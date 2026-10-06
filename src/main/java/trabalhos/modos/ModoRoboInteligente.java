package trabalhos.modos;

import java.util.ArrayList;
import java.util.List;

import trabalhos.classes_robos.Robo;
import trabalhos.tabuleiro.Tabuleiro;

/**
 * MODO 3 (TerceiraMain): um Robo normal contra um RoboInteligente.
 *
 * Diferente do modo 2, o jogo NÃO acaba quando o primeiro chega: cada robô que encontra
 * o alimento deixa de poder se mover (setIsPodeMover(false)) e o outro continua até chegar
 * também. O jogo termina quando nenhum robô pode mais se mover.
 *
 * A inteligência do RoboInteligente está na própria classe dele (ele evita repetir o último
 * movimento inválido); este modo apenas chama robo.mover() como na Main original.
 */
public class ModoRoboInteligente extends ModoDeJogo {

    // Robôs na ordem em que chegaram ao alimento (o primeiro é o vencedor)
    private final List<Robo> ordemDeChegada = new ArrayList<>();

    public ModoRoboInteligente(Tabuleiro tabuleiro, Robo roboNormal, Robo roboInteligente) {
        super(TipoModo.ROBO_INTELIGENTE, tabuleiro, roboNormal, roboInteligente);
    }

    @Override
    public boolean isAutomatico() {
        return true;
    }

    @Override
    protected ResultadoJogada concluirJogada(Robo robo, ResultadoJogada resultado) {

        boolean achou = estaNoAlimento(robo);

        if (achou) {
            robo.setIsPodeMover(false);   // regra da TerceiraMain: quem chegou para de andar

            if (ordemDeChegada.isEmpty()) {
                robo.setIsVencedor(true);   // o primeiro a chegar é o vencedor
            }

            ordemDeChegada.add(robo);
        }

        passarAVez();

        if (!algumRoboPodeMover()) {
            finalizar(ordemDeChegada.get(0).getCor().toUpperCase() + " CHEGOU PRIMEIRO!",
                    montarOrdemDeChegada());
        }

        return resultado.comAlimento(achou);
    }

    /** Texto como "1º Goku  |  2º Vegeta". */
    private String montarOrdemDeChegada() {

        StringBuilder texto = new StringBuilder("Todos encontraram o alimento: ");

        for (int i = 0; i < ordemDeChegada.size(); i++) {

            if (i > 0) {
                texto.append("  |  ");
            }

            texto.append((i + 1)).append("º ").append(ordemDeChegada.get(i).getCor());
        }

        return texto.toString();
    }
}
