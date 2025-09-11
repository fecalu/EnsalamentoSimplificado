import java.io.IOException;
import java.util.*;

public class EnsalamentoLeitor {
    @SuppressWarnings("unchecked") //suprimir avisos de compilação. Não podem ser verificadas em tempo de compilação
	public static void main(String[] args) {
        try {
            Map<String, Object> dados = LeitorArquivos.lerProfessoresESalasEDisciplinas("C:\\temp\\diario-teste-eclipse\\EnsalamentoAg\\src\\dados\\problema_ensalamento1.txt");
            //
            List<Disciplina> disciplinas = (List<Disciplina>) dados.get("disciplinas");
            List<Professor> professores = (List<Professor>) dados.get("professores");
            List<Sala> salas = (List<Sala>) dados.get("salas");
            //
            int numHorarios = 4;  // Horários por dia
            int numDias = 5;      // Segunda a sexta

            // Inicializa o algoritmo genético
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
