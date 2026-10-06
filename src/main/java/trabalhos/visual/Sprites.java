package trabalhos.visual;

/**
 * Caminhos de todos os arquivos de imagem do jogo, num lugar só.
 * Para trocar uma arte basta substituir o PNG em src/main/resources/sprites/
 * (mantendo o nome e a organização em grade) ou mudar o caminho aqui.
 */
public final class Sprites {

    /** Goku: 4 colunas (frames) x 4 linhas (CIMA, BAIXO, ESQUERDA, DIREITA). */
    public static final String GOKU = "/sprites/goku.png";

    /** Vegeta: mesma organização do Goku. */
    public static final String VEGETA = "/sprites/vegeta.png";

    /** Saibaman parado (a "bomba"): 4 colunas x 4 linhas, mesma ordem de direções. */
    public static final String SAIBAMAN = "/sprites/saibaman.png";

    /** Saibaman se ativando: 4 frames em uma linha (o último é a silhueta vermelha). */
    public static final String SAIBAMAN_ATIVACAO = "/sprites/saibaman_ativacao.png";

    /** Explosão: 6 frames em uma linha (do clarão até a cratera). */
    public static final String EXPLOSAO = "/sprites/explosao.png";

    /** Esfera do dragão (o alimento): imagem única. */
    public static final String ESFERA = "/sprites/esfera.png";

    /** Rocha: imagem única. */
    public static final String ROCHA = "/sprites/rocha.png";

    /** Cenário da arena (quadrado, com o piso do tabuleiro no centro). */
    public static final String ARENA = "/sprites/arena.jpg";

    // Classe só de constantes: não deve ser instanciada
    private Sprites() {
    }
}
