import java.util.*;

public class EnsalamentoSimplificado {
    public static void main(String[] args) {
        // --- Professores e suas disponibilidades (dia -> horários) ---
        Map<Integer, Set<Integer>> horarios1 = new HashMap<>();
        horarios1.put(1, new HashSet<>(Arrays.asList(0,1,2,3))); // Terça
        Professor prof1 = new Professor("Alysson", horarios1);

        Map<Integer, Set<Integer>> horarios2 = new HashMap<>();
        horarios2.put(2, new HashSet<>(Arrays.asList(0,1))); // Quarta
        Professor prof2 = new Professor("Roberto", horarios2);

        Map<Integer, Set<Integer>> horarios3 = new HashMap<>();
        horarios3.put(2, new HashSet<>(Arrays.asList(2,3))); // Quarta
        Professor prof3 = new Professor("Ricardo", horarios3);

        Map<Integer, Set<Integer>> horarios4 = new HashMap<>();
        horarios4.put(3, new HashSet<>(Arrays.asList(0,1))); // Quinta
        Professor prof4 = new Professor("Carlos", horarios4);

        List<Professor> professores = Arrays.asList(prof1, prof2, prof3, prof4);

        // --- Salas ---
        Sala sala1 = new Sala("Sala 1", 30, true);


        List<Sala> salas = Arrays.asList(sala1);

        // --- Horários e dias ---
        int numHorarios = 4;  // Horários por dia
        int numDias = 5;      // Segunda a sexta

        // --- Disciplinas (com número de aulas por semana) ---
        Disciplina d1 = new Disciplina("Adm De Dados", 25, true, 4);          // 4 aulas/semana
        Disciplina d2 = new Disciplina("Empreendedorismo", 30, true, 2);     // 2 aulas/semana
        Disciplina d3 = new Disciplina("Arq OO", 20, true, 2);                 // 2 aulas/semana
        Disciplina d4 = new Disciplina("Gestao De Conhecimento", 30, true, 2);// 2 aulas/semana

        List<Disciplina> disciplinas = Arrays.asList(d1, d2, d3, d4);

        // --- Algoritmo Genético ---
        AlgoritmoGenetico ag = new AlgoritmoGenetico(20, 0.15);  // 20 indivíduos, 15% mutação
        ag.resolve(200, disciplinas, professores, salas, numHorarios, numDias);  // 200 gerações

        // --- Melhor solução ---
        Individuo melhor = ag.getMelhorSolucao();
        System.out.println("\nMelhor solução final detalhada:");
        System.out.println(Arrays.toString(melhor.getSolucao()));
        System.out.println("Número de conflitos: " + melhor.getFitness());
    }
}
