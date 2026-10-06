package trabalhos.modos;

import trabalhos.classes_robos.Robo;
import trabalhos.tabuleiro.Tabuleiro;

/**
 * MODO 2 (SegundaMain): dois robôs normais se movendo aleatoriamente, um de cada vez.
 *
 * O jogo termina assim que UM dos dois encontra o alimento; esse robô é o vencedor.
 */
public class ModoDoisRobos extends ModoDeJogo {

    public ModoDoisRobos(Tabuleiro tabuleiro, Robo robo1, Robo robo2) {

        super(TipoModo.DOIS_ROBOS, tabuleiro, robo1, robo2);

        // Na SegundaMain o laço verifica o robô da vez ANTES do primeiro movimento. Se o
        // alimento está em (0,0), o primeiro robô já "encontrou" e o jogo termina na hora.
        if (estaNoAlimento(robo1)) {
            robo1.setIsVencedor(true);
            finalizar(robo1.getCor().toUpperCase() + " VENCEU!",
                    "O robô " + robo1.getCor() + " encontrou o alimento na largada.");
        }
    }

    @Override
    public boolean isAutomatico() {
        return true;
    }

    @Override
    protected ResultadoJogada concluirJogada(Robo robo, ResultadoJogada resultado) {

        if (estaNoAlimento(robo)) {
            robo.setIsVencedor(true);
            finalizar(robo.getCor().toUpperCase() + " VENCEU!",
                    "O robô " + robo.getCor() + " encontrou o alimento!");
            return resultado.comAlimento(true);
        }

        passarAVez();   // ninguém achou ainda: troca o robô da vez
        return resultado;
    }
}
