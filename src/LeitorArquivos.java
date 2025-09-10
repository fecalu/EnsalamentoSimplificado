import java.io.*;
import java.util.*;

public class LeitorArquivos {

    public static List<Professor> lerProfessores(String caminho) throws IOException {
        List<Professor> professores = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(caminho));
        String linha;
        while ((linha = br.readLine()) != null) {
            if (linha.trim().isEmpty()) continue;
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
        }
        br.close();
        return professores;
    }

    public static List<Sala> lerSalas(String caminho) throws IOException {
        List<Sala> salas = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(caminho));
        String linha;
        while ((linha = br.readLine()) != null) {
            if (linha.trim().isEmpty()) continue;
            String[] partes = linha.split(";");
            String nome = partes[0];
            int capacidade = Integer.parseInt(partes[1]);
            boolean possuiComputadores = Boolean.parseBoolean(partes[2]);
            salas.add(new Sala(nome, capacidade, possuiComputadores));
        }
        br.close();
        return salas;
    }

    public static List<Disciplina> lerDisciplinas(String caminho) throws IOException {
        List<Disciplina> disciplinas = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(caminho));
        String linha;
        while ((linha = br.readLine()) != null) {
            if (linha.trim().isEmpty()) continue;
            String[] partes = linha.split(";");
            String nome = partes[0];
            int alunos = Integer.parseInt(partes[1]);
            boolean precisaComputador = Boolean.parseBoolean(partes[2]);
            int aulasPorSemana = Integer.parseInt(partes[3]);
            disciplinas.add(new Disciplina(nome, alunos, precisaComputador, aulasPorSemana));
        }
        br.close();
        return disciplinas;
    }
}
