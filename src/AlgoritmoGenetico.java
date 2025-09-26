import db.ConnectionDb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.*;

class AlgoritmoGenetico {
	private int tamanhoPopulacao;
	private double taxaMutacao;
	private List<Individuo> populacao;
	private Individuo melhorSolucao;

	private int numSalas, numHorarios, numDias;

	private final Connection connection = ConnectionDb.getConn();

	// cache pra fitness/impressão
	private Map<String, Professor> profByName;

	private double taxaElitismo; // Variável para elitismo

	// <> NOVO: Contador de execuções da função objetivo como variável da classe.
	private int numExecObjetiva = 0;


	public AlgoritmoGenetico(int tamanhoPopulacao, double taxaMutacao, double taxaElitismo) {
		this.tamanhoPopulacao = tamanhoPopulacao;
		this.taxaMutacao = taxaMutacao;
		this.taxaElitismo = taxaElitismo; // Inicializando elitismo
		this.populacao = new ArrayList<>();
	}

	public void resolve(int numeroGeracoes, List<Disciplina> disciplinas, List<Professor> professores, List<Sala> salas,

						int numHorarios, int numDias) {

		// <> NOVO: Zera o contador no início de cada resolução.
		this.numExecObjetiva = 0;

		Instant agora = Instant.now();

		this.numSalas = salas.size();
		this.numHorarios = numHorarios;
		this.numDias = numDias;

		// monta lookup de professores por nome
		this.profByName = new HashMap<>();
		for (Professor p : professores) {
			this.profByName.put(p.getNome(), p);
		}

		int totalAulas = 0;
		for (Disciplina d : disciplinas)
			totalAulas += d.getNumeroAulasPorSemana();

		inicializaPopulacao(totalAulas, disciplinas, salas);

		List<String> historico = new ArrayList<>();

		for (int g = 0; g < numeroGeracoes; g++) {
			List<Individuo> novaPopulacao = new ArrayList<>();

			// Adiciona o elitismo: calcula o número de indivíduos elitistas com base na taxa
			int elitismoCount = (int) (tamanhoPopulacao * taxaElitismo);
			for (int i = 0; i < elitismoCount; i++) {
				novaPopulacao.add(new Individuo(populacao.get(i))); // Mantém as melhores soluções
			}

			// Preenche o restante da população
			while (novaPopulacao.size() < tamanhoPopulacao) {
				Individuo pai1 = torneio();
				Individuo pai2 = torneio();
				Individuo filho = crossover(pai1, pai2);
				mutacao(filho);
				filho.calcularFitness(disciplinas, profByName, salas);
				this.numExecObjetiva++; // <> ALTERAÇÃO: Incrementa o contador aqui.
				novaPopulacao.add(filho);
			}

			// Atualiza a população com a nova geração
			populacao = novaPopulacao;
			populacao.sort(Comparator.comparingDouble(Individuo::getFitness));

			// Atualiza a melhor solução
			if (populacao.get(0).getFitness() < melhorSolucao.getFitness()) {
				melhorSolucao = populacao.get(0);
			}

			historico.add("Geração " + (g + 1) + ": Fitness: " + melhorSolucao.getFitness());

			// numExecObjetiva += 1; // <> REMOVIDO: Linha antiga que contava errado.
		}

		System.out.println("Histórico das soluções:");
		historico.forEach(System.out::println);

		System.out.println("\nMelhor solução final encontrada:");
		imprimirGrade(melhorSolucao, disciplinas, salas);
		System.out.println("Número de conflitos: " + melhorSolucao.getFitness()); // FITNESS

		// Captura o tempo final após a execução do algoritmo
		Instant tempoFinal = Instant.now();

		// Calcula a duração entre o tempo inicial e o tempo final
		Duration duracao = Duration.between(agora, tempoFinal);

		// TEMPO
		double tempoQuebrado = duracao.toMillis() / 1000.0;

		// <> ATENÇÃO: Verifique se o nome da tabela está correto. Na query antiga era "testes_algoritmos_1"
		String sql = "INSERT INTO testes_algoritmos_1 (nome_instancia, fitness_ag_java, tempo_ag_java, melhor_individuo_java, num_exec_objetiva) VALUES (?, ?, ?, ?, ?)";

		try(PreparedStatement stmt = connection.prepareStatement(sql)){
			stmt.setString(1, "Cenário 1");
			stmt.setDouble(2, melhorSolucao.getFitness());
			stmt.setDouble(3, BigDecimal.valueOf(tempoQuebrado).setScale(2, RoundingMode.HALF_UP).doubleValue());
			stmt.setString(4, Arrays.toString(melhorSolucao.getSolucao()));
			stmt.setInt(5, this.numExecObjetiva); // <> ALTERAÇÃO: Usa o contador correto da classe.

			stmt.executeUpdate();
		} catch (SQLException e){
			throw new RuntimeException(e);
		}

		// Exibe a duração total de execução em milissegundos
		System.out.println("Duração: " + duracao.getSeconds() + " segundos");
		System.out.println("Duração em segundos quebrados: " + BigDecimal.valueOf(tempoQuebrado).setScale(2, RoundingMode.HALF_UP) + " segundos");
		// <> NOVO: Imprime o número correto de execuções.
		System.out.println("Total de execuções da função objetivo: " + this.numExecObjetiva);
	}

	private void inicializaPopulacao(int totalAulas, List<Disciplina> disciplinas, List<Sala> salas) {
		populacao.clear();
		for (int i = 0; i < tamanhoPopulacao; i++) {
			Individuo ind = new Individuo(totalAulas, numSalas, numHorarios, numDias);
			ind.calcularFitness(disciplinas, profByName, salas);
			this.numExecObjetiva++; // <> ALTERAÇÃO: Incrementa o contador aqui também.
			populacao.add(ind);
		}
		populacao.sort(Comparator.comparingDouble(Individuo::getFitness));
		melhorSolucao = populacao.get(0);
	}

	private Individuo torneio() {
		Individuo i1 = populacao.get((int) (Math.random() * tamanhoPopulacao));
		Individuo i2 = populacao.get((int) (Math.random() * tamanhoPopulacao));
		return (i1.getFitness() <= i2.getFitness()) ? i1 : i2;
	}

	private Individuo crossover(Individuo pai1, Individuo pai2) {
		Individuo filho = new Individuo(pai1);
		for (int i = 0; i < pai1.getSolucao().length; i++) {
			if (Math.random() < 0.5)
				filho.getSolucao()[i] = pai2.getSolucao()[i];
		}
		return filho;
	}

	private void mutacao(Individuo ind) {
		if (Math.random() < taxaMutacao) {
			int pos = (int) (Math.random() * ind.getSolucao().length);
			ind.getSolucao()[pos] = (int) (Math.random() * (numSalas * numDias * numHorarios));
		}
	}

	// Métodos auxiliares para repetição de caracteres e formatação de células
	private String rep(String s, int n) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < n; i++)
			b.append(s);
		return b.toString();
	}

	private String cell(String s, int w) { // corta e preenche à direita
		if (s == null)
			s = "";
		if (s.length() > w)
			return s.substring(0, w - 1) + "…";
		return String.format("%-" + w + "s", s);
	}

	private void imprimirGrade(Individuo ind, List<Disciplina> disciplinas, List<Sala> salas) {
		// --- configuração das colunas (ajuste se quiser) ---
		int W_CURSO = 28, W_PERIODO = 8, W_DIA = 10, W_HOR = 10, W_DISC = 30, W_PROF = 22, W_SALA = 30;

		boolean USE_ASCII = false; // mude pra true se o terminal não renderizar box-drawing
		String H = USE_ASCII ? "-" : "─";
		String V = USE_ASCII ? "|" : "│";
		String TL = USE_ASCII ? "+" : "┌";
		String TR = USE_ASCII ? "+" : "┐";
		String BL = USE_ASCII ? "+" : "└";
		String BR = USE_ASCII ? "+" : "┘";
		String TJ = USE_ASCII ? "+" : "┬";
		String MJ = USE_ASCII ? "+" : "┼";
		String BJ = USE_ASCII ? "+" : "┴";

		String[] dias = { "Segunda", "Terça", "Quarta", "Quinta", "Sexta" };

		// --- cria estrutura para as linhas ---
		class Row {
			String curso, periodo, dia, horario, disciplina, professor, sala;

			Row(String curso, int periodo, int diaIdx, int horIdx, String disc, String prof, String sala) {
				this.curso = curso;
				this.periodo = periodo + "ºP";
				this.dia = dias[diaIdx];
				this.horario = "Horário " + (horIdx + 1);
				this.disciplina = disc;
				this.professor = prof;
				this.sala = sala;
			}
		}
		List<Row> rows = new ArrayList<>();

		int aulaIndex = 0;
		for (Disciplina d : disciplinas) {
			String profNome = d.getProfessorNome();
			for (int a = 0; a < d.getNumeroAulasPorSemana(); a++) {
				int codigo = ind.getSolucao()[aulaIndex];
				int salaIndex = codigo / (numDias * numHorarios);
				int resto = codigo % (numDias * numHorarios);
				int diaIndex = resto / numHorarios;
				int horarioIndex = resto % numHorarios;

				String profBadge = profByName.containsKey(profNome) ? profNome : profNome + " *";
				rows.add(new Row(d.getCurso(), d.getPeriodo(), diaIndex, horarioIndex, d.getNome(), profBadge,
						salas.get(salaIndex).getNome()));
				aulaIndex++;
			}
		}

		// --- ordena as linhas: curso, período, dia, horário, disciplina ---
		rows.sort(Comparator.comparing((Row r) -> r.curso).thenComparing(r -> r.periodo)
				.thenComparing(r -> Arrays.asList(dias).indexOf(r.dia))
				.thenComparing(r -> Integer.parseInt(r.horario.replaceAll("\\D+", "")))
				.thenComparing(r -> r.disciplina));

		// largura total
		int total = 1 + 1 + W_CURSO + 1 + 1 + W_PERIODO + 1 + 1 + W_DIA + 1 + 1 + W_HOR + 1 + 1 + W_DISC + 1 + 1
				+ W_PROF + 1 + 1 + W_SALA + 1;

		// --- título ---
		String titulo = "MELHOR SOLUÇÃO ENCONTRADA - ALOCAÇÃO DE AULAS";
		int inner = total - 2;
		int left = Math.max(0, (inner - titulo.length()) / 2);
		int right = Math.max(0, inner - titulo.length() - left);
		System.out.println(TL + rep(H, inner) + TR);
		System.out.println(V + rep(" ", left) + titulo + rep(" ", right) + V);

		// --- topo do cabeçalho ---
		System.out.println(MJ + rep(H, W_CURSO + 2) + MJ + rep(H, W_PERIODO + 2) + MJ + rep(H, W_DIA + 2) + MJ
				+ rep(H, W_HOR + 2) + MJ + rep(H, W_DISC + 2) + MJ + rep(H, W_PROF + 2) + MJ + rep(H, W_SALA + 2) + MJ);

		// --- cabeçalho ---
		System.out.println(V + " " + cell("CURSO", W_CURSO) + " " + V + " " + cell("PERÍODO", W_PERIODO) + " " + V + " "
				+ cell("DIA", W_DIA) + " " + V + " " + cell("HORÁRIO", W_HOR) + " " + V + " "
				+ cell("DISCIPLINA", W_DISC) + " " + V + " " + cell("PROFESSOR", W_PROF) + " " + V + " "
				+ cell("SALA", W_SALA) + " " + V);

		// --- separador entre cabeçalho e dados ---
		System.out.println(MJ + rep(H, W_CURSO + 2) + TJ + rep(H, W_PERIODO + 2) + TJ + rep(H, W_DIA + 2) + TJ
				+ rep(H, W_HOR + 2) + TJ + rep(H, W_DISC + 2) + TJ + rep(H, W_PROF + 2) + TJ + rep(H, W_SALA + 2) + MJ);

		// --- linhas ---
		for (Row r : rows) {
			System.out.println(V + " " + cell(r.curso, W_CURSO) + " " + V + " " + cell(r.periodo, W_PERIODO) + " " + V
					+ " " + cell(r.dia, W_DIA) + " " + V + " " + cell(r.horario, W_HOR) + " " + V + " "
					+ cell(r.disciplina, W_DISC) + " " + V + " " + cell(r.professor, W_PROF) + " " + V + " "
					+ cell(r.sala, W_SALA) + " " + V);
		}

		// --- base ---
		System.out.println(BL + rep(H, inner) + BR);

		// dica de legenda
		if (rows.stream().anyMatch(r -> r.professor.endsWith("*"))) {
			System.out.println("* professor referenciado na disciplina não cadastrado na lista de professores");
		}
	}

	public Individuo getMelhorSolucao() {
		return melhorSolucao;
	}
}