package trabalhos.interfaces;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Troca de tela. Centraliza o código de "carregar um FXML e colocá-lo na janela", que antes
 * era repetido em cada Controller.
 *
 * Em vez de criar uma Scene nova a cada tela, trocamos apenas a RAIZ da Scene atual. Isso
 * mantém o tamanho da janela (inclusive se o usuário maximizou) entre as telas.
 */
public final class Navegador {

    private Navegador() {
    }

    /**
     * Carrega um FXML da pasta /fxml/, mostra na janela de "origem" e devolve o controller
     * criado, para quem chamou poder passar dados para a nova tela.
     *
     * @param origem     qualquer componente da tela atual (de onde pegamos a Scene)
     * @param arquivoFxml nome do arquivo, por exemplo "Jogo.fxml"
     */
    public static <T> T ir(Node origem, String arquivoFxml) throws IOException {

        FXMLLoader loader = new FXMLLoader(Navegador.class.getResource("/fxml/" + arquivoFxml));
        Parent novaRaiz = loader.load();

        Scene cena = origem.getScene();
        cena.setRoot(novaRaiz);

        return loader.getController();
    }

    /** Mostra/esconde um componente de verdade (some do layout, não deixa um espaço vazio). */
    public static void mostrar(Node no, boolean visivel) {
        no.setVisible(visivel);
        no.setManaged(visivel);
    }
}
