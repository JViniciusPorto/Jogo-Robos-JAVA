package trabalhos.visual;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Desenho SIMPLES usado quando um arquivo de sprite não é encontrado (o "fallback").
 *
 * É um círculo colorido com uma letra, que se ajusta ao tamanho pedido. Todas as classes
 * de sprite reaproveitam esta mesma forma, em vez de cada uma ter o seu próprio fallback.
 */
class FormaReserva extends StackPane {

    private final Circle circulo = new Circle();
    private final Label letra = new Label();

    FormaReserva(String texto, Color cor) {
        circulo.setFill(cor);
        circulo.setStroke(Color.web("#ffffff"));
        letra.setText(texto);
        letra.setTextFill(Color.WHITE);
        getChildren().addAll(circulo, letra);
        setTamanho(60);   // tamanho provisório; o tabuleiro ajusta depois
    }

    /** Ajusta o círculo e a fonte proporcionalmente ao lado da caixa. */
    void setTamanho(double lado) {
        circulo.setRadius(lado * 0.38);
        circulo.setStrokeWidth(Math.max(1, lado * 0.03));
        letra.setFont(Font.font("SansSerif", FontWeight.BOLD, lado * 0.32));
        setMinSize(lado, lado);
        setPrefSize(lado, lado);
        setMaxSize(lado, lado);
    }

    void setCor(Color cor) {
        circulo.setFill(cor);
    }

    void setTexto(String texto) {
        letra.setText(texto);
    }
}
