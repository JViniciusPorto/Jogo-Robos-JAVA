package trabalhos.visual;

/**
 * Direção em que um personagem está virado NA TELA.
 *
 * É um conceito só visual: o modelo (Robo) continua falando em "up", "down", "left" e "right".
 * Esta enumeração traduz o que o modelo fez (quanto x e y mudaram) para algo que o sprite entende.
 *
 * ATENÇÃO À ORDEM: a ordem das constantes (CIMA, BAIXO, ESQUERDA, DIREITA) é a mesma ordem
 * das LINHAS nos spritesheets goku.png, vegeta.png e saibaman.png. Por isso o número da linha
 * de uma direção é simplesmente direcao.ordinal(). Não reordene sem refazer os PNGs.
 */
public enum Direcao {

    CIMA(0, -1),
    BAIXO(0, 1),
    ESQUERDA(-1, 0),
    DIREITA(1, 0);

    // Vetor unitário em coordenadas de TELA (na tela o y cresce para BAIXO)
    private final int dxTela;
    private final int dyTela;

    Direcao(int dxTela, int dyTela) {
        this.dxTela = dxTela;
        this.dyTela = dyTela;
    }

    public int getDxTela() {
        return dxTela;
    }

    public int getDyTela() {
        return dyTela;
    }

    /**
     * Descobre a direção a partir de quanto as coordenadas do Robo mudaram (depois - antes).
     * No modelo, "up" é y+1, "right" é x+1, "down" é y-1 e "left" é x-1.
     */
    public static Direcao deDeslocamento(int dx, int dy) {

        if (dx > 0) {
            return DIREITA;   // "right"
        }
        if (dx < 0) {
            return ESQUERDA;  // "left"
        }
        if (dy > 0) {
            return CIMA;      // "up"
        }
        return BAIXO;         // "down" (também serve quando não houve deslocamento)
    }
}
