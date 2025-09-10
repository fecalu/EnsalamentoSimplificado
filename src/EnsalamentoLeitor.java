import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class EnsalamentoLeitor {
    public static void main(String[] args) {
        try {
            List<Professor> professores = LeitorArquivos.lerProfessores("C:\\temp\\diario-teste-eclipse\\EnsalamentoAg\\src\\dados\\teste2\\professores.txt");
            List<Sala> salas = LeitorArquivos.lerSalas("C:\\temp\\diario-teste-eclipse\\EnsalamentoAg\\src\\dados\\teste2\\salas.txt");
            List<Disciplina> disciplinas = LeitorArquivos.lerDisciplinas("C:\\temp\\diario-teste-eclipse\\EnsalamentoAg\\src\\dados\\teste2\\disciplinas.txt");

            int numHorarios = 4;
            int numDias = 5;

            AlgoritmoGenetico ag = new AlgoritmoGenetico(20, 0.15);
            ag.resolve(200, disciplinas, professores, salas, numHorarios, numDias);

            Individuo melhor = ag.getMelhorSolucao();
            System.out.println("\nMelhor solução final detalhada:");
            System.out.println(Arrays.toString(melhor.getSolucao()));
            System.out.println("Número de conflitos: " + melhor.getFitness());

        } catch (IOException e) {
            System.err.println("Erro ao ler arquivos: " + e.getMessage());
        }
    }
}
