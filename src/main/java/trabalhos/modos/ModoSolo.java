package trabalhos.modos;

import trabalhos.classes_robos.Robo;
import trabalhos.tabuleiro.Tabuleiro;

/**
 * MODO 1 (PrimeiraMain): um único robô, comandado pelo jogador.
 *
 * Comandos: 1 = up, 2 = down, 3 = right, 4 = left (os mesmos de Robo.mover(int)).
 * O jogo termina quando o robô encontra o alimento.
 */
public class ModoSolo extends ModoDeJogo {

    public ModoSolo(Tabuleiro tabuleiro, Robo robo) {

        super(TipoModo.SOLO, tabuleiro, robo);

        // Na PrimeiraMain o laço é "while(!r.isEncontrouAlimento(...))": se o alimento já está
        // na largada (0,0), o jogo nem começa. Reproduzimos esse comportamento aqui.
        if (estaNoAlimento(robo)) {
            robo.setIsVencedor(true);
            finalizar(robo.getCor().toUpperCase() + " ENCONTROU O ALIMENTO!",
                    "O alimento estava na posição de largada.");
        }
    }

    @Override
    public boolean isAutomatico() {
        return false;   // quem decide cada movimento é o jogador
    }

    /**
     * Executa o comando digitado/clicado pelo jogador (1 a 4).
     * Usa Robo.mover(int), então a conversão número -> direção continua só no modelo.
     */
    public ResultadoJogada jogarComando(int comando) {

        garantirJogoAtivo();

        Robo robo = getRoboDaVez();

        return concluirJogada(robo, executar(robo, r -> r.mover(comando)));
    }

    @Override
    protected ResultadoJogada concluirJogada(Robo robo, ResultadoJogada resultado) {

        if (estaNoAlimento(robo)) {
            robo.setIsVencedor(true);
            finalizar(robo.getCor().toUpperCase() + " ENCONTROU O ALIMENTO!",
                    "O robô " + robo.getCor() + " encontrou o alimento!");
            return resultado.comAlimento(true);
        }

        return resultado;   // só um robô: a vez continua sendo dele
    }
}
