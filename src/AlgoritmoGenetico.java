import java.util.*;

class AlgoritmoGenetico {
    private int tamanhoPopulacao;
    private double taxaMutacao;
    private List<Individuo> populacao;
    private Individuo melhorSolucao;

    private int numSalas, numHorarios, numDias;

    public AlgoritmoGenetico(int tamanhoPopulacao, double taxaMutacao) {
        this.tamanhoPopulacao = tamanhoPopulacao;
        this.taxaMutacao = taxaMutacao;
        this.populacao = new ArrayList<>();
    }
    


    public void resolve(int numeroGeracoes, List<Disciplina> disciplinas, List<Professor> professores, List<Sala> salas, int numHorarios, int numDias) {
        this.numSalas = salas.size();
        this.numHorarios = numHorarios;
        this.numDias = numDias;

        int totalAulas = 0;
        for (Disciplina d : disciplinas) totalAulas += d.getNumeroAulasPorSemana();

        
        
        //Soluções iniciais (indivíduos). Cada solução é uma tentativa de alocar as aulas nas salas com horários, baseado nas aulas e professores. 
        //São soluções aleatórias no começo. Confira o método mais em baixo
        
        inicializaPopulacao(totalAulas, disciplinas, professores, salas);

        List<String> historico = new ArrayList<>();

        for (int g = 0; g < numeroGeracoes; g++) {
            List<Individuo> novaPopulacao = new ArrayList<>();
            novaPopulacao.add(new Individuo(melhorSolucao)); // elitismo

            while (novaPopulacao.size() < tamanhoPopulacao) { // Aqui é tipo uma competição para escolher o melhor 
                Individuo pai1 = torneio(); //Como se estivesse fazendo uma competição para escolher os melhores "candidatos". 
                                            //Cada indivíduo "luta" contra outro para determinar quem vai passar para a próxima fase.
                Individuo pai2 = torneio();
                Individuo filho = crossover(pai1, pai2); //A cada geração, vai selecionar dois indivíduos (soluções) que vão "fazer cruzamento" para gerar um novo indivíduo.
                mutacao(filho);
                filho.calcularFitness(disciplinas, professores, salas);
                novaPopulacao.add(filho);
            }

            populacao = novaPopulacao;
            populacao.sort(Comparator.comparingDouble(Individuo::getFitness));
            if (populacao.get(0).getFitness() < melhorSolucao.getFitness()) {
                melhorSolucao = populacao.get(0);
            }

            historico.add("Geração " + (g + 1) + ": Fitness: " + melhorSolucao.getFitness());
        }

        System.out.println("Histórico das soluções:");
        historico.forEach(System.out::println);

        System.out.println("\nMelhor solução final encontrada:");
        imprimirGrade(melhorSolucao, disciplinas, professores, salas);
        System.out.println("Número de conflitos: " + melhorSolucao.getFitness());
    }

    private void inicializaPopulacao(int totalAulas, List<Disciplina> disciplinas, List<Professor> professores, List<Sala> salas) {
        populacao.clear();
        for (int i = 0; i < tamanhoPopulacao; i++) {
            Individuo ind = new Individuo(totalAulas, numSalas, numHorarios, numDias);
            ind.calcularFitness(disciplinas, professores, salas);
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
            if (Math.random() < 0.5) filho.getSolucao()[i] = pai2.getSolucao()[i];
        }
        return filho;
    }

    private void mutacao(Individuo ind) {
        if (Math.random() < taxaMutacao) {
            int pos = (int) (Math.random() * ind.getSolucao().length);
            ind.getSolucao()[pos] = (int) (Math.random() * (numSalas * numDias * numHorarios));
        }
    }

    private void imprimirGrade(Individuo ind, List<Disciplina> disciplinas, List<Professor> professores, List<Sala> salas) {
        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta"};
        int aulaIndex = 0;

        for (int d = 0; d < disciplinas.size(); d++) {
            Disciplina disc = disciplinas.get(d);
            Professor prof = professores.get(d);

            for (int a = 0; a < disc.getNumeroAulasPorSemana(); a++) {
                int codigo = ind.getSolucao()[aulaIndex];
                int salaIndex = codigo / (numDias * numHorarios);
                int resto = codigo % (numDias * numHorarios);
                int diaIndex = resto / numHorarios;
                int horarioIndex = resto % numHorarios;

                System.out.println(dias[diaIndex] + " | Horário " + (horarioIndex + 1) + " | Disciplina: " + disc.getNome() +
                        " | Professor: " + prof.getNome() + " | Sala: " + salas.get(salaIndex).getNome());
                aulaIndex++;
            }
        }
    }
    
    public Individuo getMelhorSolucao() {
        return melhorSolucao;
    }
}