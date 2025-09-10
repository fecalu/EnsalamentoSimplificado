// --- Classe Sala ---
class Sala {
    private String nome;
    private int capacidade;
    private boolean possuiComputador;

    public Sala(String nome, int capacidade, boolean possuiComputador) {
        this.nome = nome;
        this.capacidade = capacidade;
        this.possuiComputador = possuiComputador;
    }

    public String getNome() { return nome; }
    public int getCapacidade() { return capacidade; }
    public boolean possuiComputador() { return possuiComputador; }
}