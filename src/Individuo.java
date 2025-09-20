import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Individuo {
    private int[] solucao;
    private double fitness;

    private int numSalas, numHorarios, numDias;

    public Individuo(int totalAulas, int numSalas, int numHorarios, int numDias) {
        this.numSalas = numSalas;
        this.numHorarios = numHorarios;
        this.numDias = numDias;

        solucao = new int[totalAulas];
        int totalComb = numSalas * numDias * numHorarios;
        for (int i = 0; i < totalAulas; i++) {
            solucao[i] = (int) (Math.random() * totalComb);
        }
    }

    public Individuo(Individuo outro) {
        this.solucao = Arrays.copyOf(outro.solucao, outro.solucao.length);
        this.fitness = outro.fitness;
        this.numSalas = outro.numSalas;
        this.numHorarios = outro.numHorarios;
        this.numDias = outro.numDias;
    }

    // >>> NOVA ASSINATURA: usa lookup por nome de professor
    public void calcularFitness(List<Disciplina> disciplinas,
                                Map<String, Professor> profByName,
                                List<Sala> salas) {
        int conflitos = 0;

        // chaveia a agenda por NOME do professor
        Map<String, Set<Integer>> professorHorariosOcupados = new HashMap<>();
        Map<Integer, Set<Integer>> salaHorariosOcupados = new HashMap<>();

        int aulaIndex = 0;
        for (Disciplina disc : disciplinas) {
            String profNome = disc.getProfessorNome();
            Professor prof = profByName.get(profNome); // pode ser null se não cadastrado

            for (int a = 0; a < disc.getNumeroAulasPorSemana(); a++) {
                int codigo = solucao[aulaIndex];

                int salaIndex = codigo / (numDias * numHorarios);
                int resto = codigo % (numDias * numHorarios);
                int diaIndex = resto / numHorarios;
                int horarioIndex = resto % numHorarios;
                int slotKey = diaIndex * numHorarios + horarioIndex;

                // Conflito professor (sem cadastro conta como conflito forte)
                if (prof == null) {
                    conflitos += 2; // penaliza mais se a disciplina aponta pra professor inexistente
                } else {
                    // indisponibilidade
                    if (!prof.getHorariosDisponiveis()
                            .getOrDefault(diaIndex, Collections.emptySet())
                            .contains(horarioIndex)) {
                        conflitos++;
                    }
                    // choque de horário do mesmo professor
                    professorHorariosOcupados
                            .computeIfAbsent(profNome, k -> new HashSet<>());
                    if (!professorHorariosOcupados.get(profNome).add(slotKey)) {
                        conflitos++; // professor já ocupado neste slot
                    }
                }

                // Conflito sala (choque de uso)
                salaHorariosOcupados
                        .computeIfAbsent(salaIndex, k -> new HashSet<>());
                if (!salaHorariosOcupados.get(salaIndex).add(slotKey)) {
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
