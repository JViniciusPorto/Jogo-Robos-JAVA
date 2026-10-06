package trabalhos.visual;

import javafx.scene.Node;

/**
 * Qualquer coisa que o TabuleiroView consegue colocar e dimensionar numa casa:
 * robôs, Saibamans, rochas e a esfera do dragão.
 *
 * O tabuleiro só precisa de poucas coisas de cada peça: o nó JavaFX para exibir, um método
 * para ajustar o tamanho quando a janela muda e três controles de animação. Isso mantém o
 * TabuleiroView independente dos tipos de sprite.
 */
public interface PecaVisual {

    /** Nó JavaFX que será adicionado ao tabuleiro. */
    Node getNo();

    /** Define o lado (em pixels) da "caixa" quadrada onde a peça é desenhada. */
    void setTamanho(double lado);

    /** Pausa a animação em andamento (peças sem animação não precisam fazer nada). */
    default void pausar() {
    }

    /** Retoma uma animação que foi pausada. */
    default void continuar() {
    }

    /** Interrompe qualquer animação e volta ao estado parado. */
    default void parar() {
    }
}
