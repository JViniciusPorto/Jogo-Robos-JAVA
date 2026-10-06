package trabalhos.visual;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Sprite de UMA imagem só, sem direções nem frames: a esfera do dragão (alimento) e a rocha.
 *
 * Se a imagem não existir, mostra o círculo reserva com uma letra.
 */
public class SpriteEstatico implements PecaVisual {

    private final StackPane no = new StackPane();
    private final ImageView imagem;       // null quando o arquivo não foi encontrado
    private final FormaReserva reserva;   // null quando a imagem foi carregada

    public SpriteEstatico(String caminhoImagem, String letraReserva, Color corReserva) {

        SpriteSheet folha = SpriteSheet.carregar(caminhoImagem, 1, 1);

        if (folha != null) {
            imagem = new ImageView(folha.getImagem());
            imagem.setPreserveRatio(true);   // nunca distorce a arte
            imagem.setSmooth(true);          // a arte é reduzida, então suavizamos
            reserva = null;
            no.getChildren().add(imagem);
        } else {
            imagem = null;
            reserva = new FormaReserva(letraReserva, corReserva);
            no.getChildren().add(reserva);
        }
    }

    @Override
    public Node getNo() {
        return no;
    }

    @Override
    public void setTamanho(double lado) {

        if (imagem != null) {
            imagem.setFitWidth(lado);
            imagem.setFitHeight(lado);
        } else {
            reserva.setTamanho(lado);
        }
    }

    /**
     * Efeito de "pulso" (cresce e volta duas vezes). Usado na esfera quando um robô a encontra.
     * É só enfeite: não muda nada no jogo.
     */
    public void destacar() {

        ScaleTransition pulso = new ScaleTransition(Duration.millis(220), no);
        pulso.setToX(1.3);
        pulso.setToY(1.3);
        pulso.setAutoReverse(true);
        pulso.setCycleCount(4);   // ida e volta, duas vezes
        pulso.play();
    }
}
