class Disciplina {
    private String curso;
    private int periodo;
    private String nome;
    private int numeroAlunos;
    private boolean necessitaComputador;
    private int numeroAulasPorSemana;
    private String professorNome; // <- chave da relação

    public Disciplina(String curso, int periodo, String nome,
                      int numeroAlunos, boolean necessitaComputador,
                      int numeroAulasPorSemana, String professorNome) {
        this.curso = curso;
        this.periodo = periodo;
        this.nome = nome;
        this.numeroAlunos = numeroAlunos;
        this.necessitaComputador = necessitaComputador;
        this.numeroAulasPorSemana = numeroAulasPorSemana;
        this.professorNome = professorNome;
    }

    public String getCurso() { return curso; }
    public int getPeriodo() { return periodo; }
    public String getNome() { return nome; }
    public int getNumeroAlunos() { return numeroAlunos; }
    public boolean isNecessitaComputador() { return necessitaComputador; }
    public int getNumeroAulasPorSemana() { return numeroAulasPorSemana; }
    public String getProfessorNome() { return professorNome; }
}
