package trabalhos.modos;

import trabalhos.classes_robos.Robo;
import trabalhos.tabuleiro.Tabuleiro;

/**
 * MODO 4 (QuartaMain): Robo normal e RoboInteligente numa arena com bombas e rochas.
 *
 * Os obstáculos já estão no tabuleiro; o tratamento deles (bater, remover a bomba) é feito
 * por ModoDeJogo.executar(), igual à QuartaMain. Aqui ficam só as regras de fim de jogo:
 *   - se um robô (que ainda pode se mover) chega ao alimento, ele vence;
 *   - se os dois explodem, o jogo termina empatado.
 */
public class ModoArena extends ModoDeJogo {

    public ModoArena(Tabuleiro tabuleiro, Robo roboNormal, Robo roboInteligente) {
        super(TipoModo.ARENA, tabuleiro, roboNormal, roboInteligente);
    }

    @Override
    public boolean isAutomatico() {
        return true;
    }

    @Override
    protected ResultadoJogada concluirJogada(Robo robo, ResultadoJogada resultado) {

        // "roboDaVez.getIsPodeMover() && ..." da QuartaMain: um robô que explodiu não conta
        boolean achou = robo.getIsPodeMover() && estaNoAlimento(robo);

        if (achou) {
            robo.setIsVencedor(true);
            finalizar(robo.getCor().toUpperCase() + " VENCEU!",
                    "O robô " + robo.getCor() + " encontrou o alimento!");
            return resultado.comAlimento(true);
        }

        passarAVez();

        if (!algumRoboPodeMover()) {
            finalizar("EMPATE!", "Os dois robôs explodiram, nenhum dos dois achou o alimento.");
        }

        return resultado;
    }
}
