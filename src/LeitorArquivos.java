import java.io.*;
import java.util.*;

public class LeitorArquivos {

    public static Map<String, Object> lerProfessoresESalasEDisciplinas(String caminho) throws IOException {
        List<Professor> professores = new ArrayList<>();
        List<Sala> salas = new ArrayList<>();
        List<Disciplina> disciplinas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            String tipoAtual = "";
            int contador = 0;

            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;

                if (linha.startsWith("disciplinas:")) {
                    tipoAtual = "disciplinas";
                    contador = Integer.parseInt(linha.split(":")[1].trim());
                    continue;
                }
                if (linha.startsWith("professores:")) {
                    tipoAtual = "professores";
                    contador = Integer.parseInt(linha.split(":")[1].trim());
                    continue;
                }
                if (linha.startsWith("salas:")) {
                    tipoAtual = "salas";
                    contador = Integer.parseInt(linha.split(":")[1].trim());
                    continue;
                }

                // --- Disciplinas (novo formato: 7 campos)
                if (tipoAtual.equals("disciplinas") && contador > 0) {
                    String[] p = linha.split(";");
                    if (p.length != 7) {
                        throw new IllegalArgumentException("Linha de disciplina inválida: " + linha);
                    }
                    String curso = p[0];
                    int periodo = Integer.parseInt(p[1]);
                    String nome = p[2];
                    int numeroAlunos = Integer.parseInt(p[3]);
                    boolean necessitaComputador = Boolean.parseBoolean(p[4]);
                    int numeroAulasPorSemana = Integer.parseInt(p[5]);
                    String professorNome = p[6];

                    disciplinas.add(new Disciplina(
                            curso, periodo, nome, numeroAlunos,
                            necessitaComputador, numeroAulasPorSemana, professorNome
                    ));
                    contador--;
                    continue;
                }

                // --- Professores (Nome;dia:0,1;dia:2,3;...)
                if (tipoAtual.equals("professores") && contador > 0) {
                    String[] p = linha.split(";");
                    if (p.length < 2) {
                        throw new IllegalArgumentException("Linha de professor inválida: " + linha);
                    }
                    String nome = p[0];
                    Map<Integer, Set<Integer>> mapHorarios = new HashMap<>();

                    for (int i = 1; i < p.length; i++) {
                        String[] diaHor = p[i].split(":");
                        if (diaHor.length != 2) continue; // ignora formato inesperado
                        int dia = Integer.parseInt(diaHor[0]);
                        Set<Integer> horarios = new HashSet<>();
                        for (String h : diaHor[1].split(",")) {
                            if (!h.isEmpty()) horarios.add(Integer.parseInt(h));
                        }
                        // agrega se o mesmo dia aparecer mais de uma vez
                        mapHorarios.computeIfAbsent(dia, k -> new HashSet<>()).addAll(horarios);
                    }

                    professores.add(new Professor(nome, mapHorarios));
                    contador--;
                    continue;
                }

                // --- Salas (nome;capacidade;possuiComputador)
                if (tipoAtual.equals("salas") && contador > 0) {
                    String[] p = linha.split(";");
                    if (p.length != 3) {
                        throw new IllegalArgumentException("Linha de sala inválida: " + linha);
                    }
                    String nome = p[0];
                    int capacidade = Integer.parseInt(p[1]);
                    boolean possuiComputadores = Boolean.parseBoolean(p[2]);
                    salas.add(new Sala(nome, capacidade, possuiComputadores));
                    contador--;
                }
            }
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("disciplinas", disciplinas);
        resultado.put("professores", professores);
        resultado.put("salas", salas);
        return resultado;
    }
}
