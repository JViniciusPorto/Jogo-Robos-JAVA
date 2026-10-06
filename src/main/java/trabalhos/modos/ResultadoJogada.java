package trabalhos.modos;

import trabalhos.classes_robos.Robo;

/**
 * Relatório de UMA jogada, devolvido pelo modo de jogo para a interface.
 *
 * Por que existe: o modelo (Robo, Tabuleiro, Obstáculos) decide o que acontece; a camada
 * visual só precisa SABER o que aconteceu para animar. Este objeto é a ponte entre os dois
 * lados, e assim a interface nunca repete regras do jogo.
 *
 * É um "record": uma classe imutável só com dados (Java gera construtor e getters).
 *
 * @param robo            quem jogou
 * @param tipo            o que aconteceu (ver {@link Tipo})
 * @param xAntes          posição do robô ANTES de se mover
 * @param yAntes          idem, eixo y
 * @param xDepois         posição para onde o mover() levou o robô, ANTES de qualquer obstáculo agir
 * @param yDepois         idem, eixo y
 * @param achouAlimento   true se, nesta jogada, o robô chegou ao alimento
 * @param mensagem        texto pronto para a lista de eventos
 */
public record ResultadoJogada(
        Robo robo,
        Tipo tipo,
        int xAntes,
        int yAntes,
        int xDepois,
        int yDepois,
        boolean achouAlimento,
        String mensagem) {

    /** Os quatro desfechos possíveis de uma jogada. */
    public enum Tipo {
        MOVEU,             // andou normalmente para uma casa livre
        BATEU_NA_PAREDE,   // MovimentoInvalidoException: tentou sair do tabuleiro
        BATEU_NA_ROCHA,    // andou até a rocha e foi devolvido à casa anterior
        EXPLODIU           // andou até uma bomba e explodiu
    }

    /**
     * Devolve uma cópia deste resultado marcando se o robô achou o alimento.
     * (O record é imutável, então "alterar" significa criar uma cópia.)
     */
    public ResultadoJogada comAlimento(boolean achou) {
        return new ResultadoJogada(robo, tipo, xAntes, yAntes, xDepois, yDepois, achou, mensagem);
    }
}
