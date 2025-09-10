import java.util.Map;
import java.util.Set;

class Professor {
    private String nome;
    private Map<Integer, Set<Integer>> horariosDisponiveis; // dia -> horários

    public Professor(String nome, Map<Integer, Set<Integer>> horariosDisponiveis) {
        this.nome = nome;
        this.horariosDisponiveis = horariosDisponiveis;
    }

    public String getNome() { return nome; }
    public Map<Integer, Set<Integer>> getHorariosDisponiveis() { return horariosDisponiveis; }
}