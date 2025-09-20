import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class EnsalamentoLeitor {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {

            Map<String, Object> dados = LeitorArquivos.lerProfessoresESalasEDisciplinas(
                "C:\\Users\\Marcos Andre\\Documents\\AlgoritmosGeneticos\\EnsalamentoSimplificado\\src\\dados\\Cenario1.txt"
            );
            List<Disciplina> disciplinas = (List<Disciplina>) dados.get("disciplinas");
            List<Professor> professores = (List<Professor>) dados.get("professores");
            List<Sala> salas = (List<Sala>) dados.get("salas");

            int numHorarios = 4;
            int numDias = 5;

            // Cria uma instância do algoritmo genético, definindo o número de indivíduos na população (400) e a taxa de mutação (15%).
            AlgoritmoGenetico ag = new AlgoritmoGenetico(1000, 0.35,0.15); 
            // Chama o método 'resolve' do algoritmo genético, passando o número de gerações (200), as listas de dados
            // de disciplinas, professores, salas e o número de horários e dias. O algoritmo vai tentar encontrar a melhor solução.
            ag.resolve(500, disciplinas, professores, salas, numHorarios, numDias);

                    
            Individuo melhor = ag.getMelhorSolucao();
        
            System.out.println("\nMelhor solução final detalhada:");
            System.out.println(Arrays.toString(melhor.getSolucao()));
            System.out.println("Número de conflitos: " + melhor.getFitness());
            

        } catch (IOException e) {
            System.err.println("Erro ao ler arquivos: " + e.getMessage());
        }
    }
}
