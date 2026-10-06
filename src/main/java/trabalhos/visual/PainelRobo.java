package trabalhos.visual;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import trabalhos.classes_robos.Robo;
import trabalhos.classes_robos.RoboInteligente;

/**
 * Cartão com as informações de UM robô, mostrado no painel lateral do jogo:
 * miniatura do personagem, nome, tipo, situação, posição e contagem de movimentos.
 *
 * Só LÊ dados do Robo (nunca altera). O estilo (cores, bordas) vem do CSS (jogo.css);
 * aqui só definimos os nomes das classes de estilo.
 */
public class PainelRobo extends VBox {

    private final Label rotuloSituacao = new Label();
    private final Label rotuloPosicao = new Label();
    private final Label rotuloMovimentos = new Label();

    /**
     * @param robo           o robô exibido (só para ler nome e tipo)
     * @param caminhoSprite  spritesheet do personagem, usado na miniatura
     * @param numero         1 ou 2: escolhe a cor do cartão no CSS (laranja = Goku, azul = Vegeta)
     */
    public PainelRobo(Robo robo, String caminhoSprite, int numero) {

        getStyleClass().addAll("cartao-robo", numero == 1 ? "p1" : "p2");
        setSpacing(4);

        Label rotuloNome = new Label(robo.getCor());
        rotuloNome.getStyleClass().add("cartao-nome");

        String tipo = (robo instanceof RoboInteligente) ? "Robô inteligente" : "Robô normal";
        Label rotuloTipo = new Label(tipo);
        rotuloTipo.getStyleClass().add("cartao-tipo");

        VBox textos = new VBox(1, rotuloNome, rotuloTipo);
        textos.setAlignment(Pos.CENTER_LEFT);

        HBox cabecalho = new HBox(10, criarMiniatura(caminhoSprite, robo.getCor(), numero), textos);
        cabecalho.setAlignment(Pos.CENTER_LEFT);

        rotuloSituacao.getStyleClass().add("cartao-situacao");
        rotuloPosicao.getStyleClass().add("cartao-detalhe");
        rotuloMovimentos.getStyleClass().add("cartao-detalhe");

        getChildren().addAll(cabecalho, rotuloSituacao, rotuloPosicao, rotuloMovimentos);

        atualizar(robo, "Em jogo", false);
    }

    /**
     * Atualiza os textos com o estado atual do robô.
     *
     * @param situacao texto curto ("Em jogo", "Explodiu"...) calculado pelo controller
     * @param suaVez   true destaca o cartão (borda brilhante) para mostrar de quem é a vez
     */
    public void atualizar(Robo robo, String situacao, boolean suaVez) {

        int total = robo.getMovimentosValido() + robo.getMovimentosInvalido();

        rotuloSituacao.setText(situacao);
        rotuloPosicao.setText("Posição: (" + robo.getX() + ", " + robo.getY() + ")");
        rotuloMovimentos.setText("Movimentos: " + total
                + "  (válidos " + robo.getMovimentosValido()
                + " | inválidos " + robo.getMovimentosInvalido() + ")");

        // liga/desliga a classe "ativo" sem duplicá-la
        getStyleClass().remove("ativo");
        if (suaVez) {
            getStyleClass().add("ativo");
        }
    }

    /** Recorta o primeiro frame do personagem de frente; sem PNG usa o círculo reserva. */
    private Node criarMiniatura(String caminhoSprite, String nome, int numero) {

        SpriteSheet folha = SpriteSheet.carregarDirecional(caminhoSprite);

        if (folha != null) {
            ImageView imagem = new ImageView(folha.getImagem());
            imagem.setViewport(folha.getFrame(Direcao.BAIXO, 0));
            imagem.setPreserveRatio(true);
            imagem.setSmooth(true);
            imagem.setFitWidth(64);
            imagem.setFitHeight(64);
            return imagem;
        }

        String inicial = nome.isEmpty() ? "?" : nome.substring(0, 1).toUpperCase();
        FormaReserva reserva = new FormaReserva(inicial, numero == 1 ? Color.web("#ff8a00") : Color.web("#2f6df6"));
        reserva.setTamanho(64);
        return reserva;
    }
}
