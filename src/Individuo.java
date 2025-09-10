import java.util.*;

class Individuo {
    private int[] solucao; // cada posição = uma aula (gene)
    private double fitness; // A qualidade da solução (fitness)

    private int numSalas, numHorarios, numDias;

    public Individuo(int totalAulas, int numSalas, int numHorarios, int numDias) {
    	//Necessários para que o algoritmo possa "entender" o contexto e realizar as alocações corretamente.
    	
    	//totalAulas: O número de aulas que precisam ser alocadas.
    	//numSalas: O número de salas disponíveis.
    	//numHorarios: O número de horários disponíveis por dia.
    	//numDias: O número de dias úteis para distribuir as aulas.
        this.numSalas = numSalas;
        this.numHorarios = numHorarios;
        this.numDias = numDias;

        solucao = new int[totalAulas];
        int totalComb = numSalas * numDias * numHorarios;
        
        // A solução inicial (array solucao[]) é gerada aleatoriamente. 
        // Cada índice de solucao[] recebe um número aleatório entre 0 e o total de combinações possíveis (baseado nas salas x dias x horários).
        for (int i = 0; i < totalAulas; i++) {
            solucao[i] = (int) (Math.random() * totalComb);
        }
    }

    // Construtor de cópia
    public Individuo(Individuo outro) {
        this.solucao = Arrays.copyOf(outro.solucao, outro.solucao.length);
        this.fitness = outro.fitness;
        this.numSalas = outro.numSalas;
        this.numHorarios = outro.numHorarios;
        this.numDias = outro.numDias;
    }

    
    //Fitness baixo = melhor solução (menos conflitos)
    //Fitness alto = pior solução (mais conflitos)
    public void calcularFitness(List<Disciplina> disciplinas, List<Professor> professores, List<Sala> salas) {
        int conflitos = 0;

        Map<Integer, Set<Integer>> professorHorariosOcupados = new HashMap<>();
        Map<Integer, Set<Integer>> salaHorariosOcupados = new HashMap<>();

        int aulaIndex = 0;
        for (int d = 0; d < disciplinas.size(); d++) {
            Disciplina disc = disciplinas.get(d);
            Professor prof = professores.get(d);

            for (int a = 0; a < disc.getNumeroAulasPorSemana(); a++) {
                int codigo = solucao[aulaIndex];

                int salaIndex = codigo / (numDias * numHorarios);
                int resto = codigo % (numDias * numHorarios);
                int diaIndex = resto / numHorarios;
                int horarioIndex = resto % numHorarios;

                // Conflito professor
                if (!prof.getHorariosDisponiveis().getOrDefault(diaIndex, new HashSet<>()).contains(horarioIndex)) {
                    conflitos++;
                }
                professorHorariosOcupados.putIfAbsent(d, new HashSet<>());
                if (!professorHorariosOcupados.get(d).add(diaIndex * numHorarios + horarioIndex)) {
                    conflitos++; // professor já ocupado
                }

                // Conflito sala
                salaHorariosOcupados.putIfAbsent(salaIndex, new HashSet<>());
                if (!salaHorariosOcupados.get(salaIndex).add(diaIndex * numHorarios + horarioIndex)) {
                    conflitos++; // sala já ocupada
                }

                // Capacidade e recursos
                Sala sala = salas.get(salaIndex);
                if (sala.getCapacidade() < disc.getNumeroAlunos()) conflitos++;
                if (disc.isNecessitaComputador() && !sala.possuiComputador()) conflitos++;

                aulaIndex++;
            }
        }

        this.fitness = conflitos;
    }

    public int[] getSolucao() { return solucao; }
    public double getFitness() { return fitness; }
}