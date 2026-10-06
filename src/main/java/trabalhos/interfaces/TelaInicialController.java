package trabalhos.interfaces;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import trabalhos.modos.TipoModo;
import trabalhos.visual.SpriteEstatico;
import trabalhos.visual.Sprites;

/**
 * Tela inicial: o jogador escolhe um dos quatro modos.
 *
 * Cada botão apenas diz QUAL modo foi escolhido e abre a configuração dele.
 * Nenhuma regra de jogo fica aqui.
 */
public class TelaInicialController {

    @FXML
    private StackPane raiz;

    @FXML
    private HBox caixaTitulo;   // linha do título, onde colocamos duas esferas do dragão de enfeite

    @FXML
    public void initialize() {

        // Enfeite: uma esfera do dragão de cada lado do título.
        // Usa SpriteEstatico, então se o PNG faltar aparece um círculo laranja no lugar.
        SpriteEstatico esferaEsquerda = new SpriteEstatico(Sprites.ESFERA, "★", Color.web("#ff8a00"));
        SpriteEstatico esferaDireita = new SpriteEstatico(Sprites.ESFERA, "★", Color.web("#ff8a00"));
        esferaEsquerda.setTamanho(72);
        esferaDireita.setTamanho(72);

        caixaTitulo.getChildren().add(0, esferaEsquerda.getNo());
        caixaTitulo.getChildren().add(esferaDireita.getNo());
    }

    // Um método por botão (o Scene Builder liga cada botão a um deles pelo onAction)

    @FXML
    private void escolherSolo() {
        abrirConfiguracao(TipoModo.SOLO);
    }

    @FXML
    private void escolherDoisRobos() {
        abrirConfiguracao(TipoModo.DOIS_ROBOS);
    }

    @FXML
    private void escolherRoboInteligente() {
        abrirConfiguracao(TipoModo.ROBO_INTELIGENTE);
    }

    @FXML
    private void escolherArena() {
        abrirConfiguracao(TipoModo.ARENA);
    }

    /** Abre a tela de configuração já avisando qual modo foi escolhido. */
    private void abrirConfiguracao(TipoModo modo) {

        try {
            ConfiguracaoController controller = Navegador.ir(raiz, "Configuracao.fxml");
            controller.definirModo(modo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
