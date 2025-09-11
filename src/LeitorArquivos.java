import java.io.*;
import java.util.*;

public class LeitorArquivos {

    public static Map<String, Object> lerProfessoresESalasEDisciplinas(String caminho) throws IOException {
        List<Professor> professores = new ArrayList<>();
        List<Sala> salas = new ArrayList<>();
        List<Disciplina> disciplinas = new ArrayList<>();
        
        BufferedReader br = new BufferedReader(new FileReader(caminho));
        String linha;
        String tipoAtual = "";
        int contador = 0;

        while ((linha = br.readLine()) != null) {
            linha = linha.trim();

            if (linha.isEmpty()) continue; // Ignora linhas vazias

            // Identifica os tipos de dados (disciplinas, professores, salas)
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

            // Processa disciplinas
            if (tipoAtual.equals("disciplinas") && contador > 0) {
                String[] partes = linha.split(";");
                String nome = partes[0];
                int numeroAlunos = Integer.parseInt(partes[1]);
                boolean necessitaComputador = Boolean.parseBoolean(partes[2]);
                int numeroAulasPorSemana = Integer.parseInt(partes[3]);
                disciplinas.add(new Disciplina(nome, numeroAlunos, necessitaComputador, numeroAulasPorSemana));
                contador--;
            }

            // Processa professores
            if (tipoAtual.equals("professores") && contador > 0) {
                String[] partes = linha.split(";");
                String nome = partes[0];
                String[] diaHorarios = partes[1].split(":");
                int dia = Integer.parseInt(diaHorarios[0]);
                Set<Integer> horarios = new HashSet<>();
                for (String h : diaHorarios[1].split(",")) {
                    horarios.add(Integer.parseInt(h));
                }
                Map<Integer, Set<Integer>> mapHorarios = new HashMap<>();
                mapHorarios.put(dia, horarios);
                professores.add(new Professor(nome, mapHorarios));
                contador--;
            }

            // Processa salas
            if (tipoAtual.equals("salas") && contador > 0) {
                String[] partes = linha.split(";");
                String nome = partes[0];
                int capacidade = Integer.parseInt(partes[1]);
                boolean possuiComputadores = Boolean.parseBoolean(partes[2]);
                salas.add(new Sala(nome, capacidade, possuiComputadores));
                contador--;
            }
        }

        br.close();

        // Retorna um Map contendo listas de disciplinas, professores e salas
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("disciplinas", disciplinas);
        resultado.put("professores", professores);
        resultado.put("salas", salas);

        return resultado;
    }
}
