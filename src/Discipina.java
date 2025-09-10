class Disciplina {
    private String nome;
    private int numeroAlunos;
    private boolean necessitaComputador;
    private int numeroAulasPorSemana; 

    public Disciplina(String nome, int numeroAlunos, boolean necessitaComputador, int numeroAulasPorSemana) {
        this.nome = nome;
        this.numeroAlunos = numeroAlunos;
        this.necessitaComputador = necessitaComputador;
        this.numeroAulasPorSemana = numeroAulasPorSemana;
    }

    public String getNome() { return nome; }
    public int getNumeroAlunos() { return numeroAlunos; }
    public boolean isNecessitaComputador() { return necessitaComputador; }
    public int getNumeroAulasPorSemana() { return numeroAulasPorSemana; }
}
