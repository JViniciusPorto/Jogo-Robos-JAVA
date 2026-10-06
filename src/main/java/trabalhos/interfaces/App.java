package trabalhos.interfaces;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Ponto de entrada do JavaFX. Só abre a janela com a tela inicial (menu dos quatro modos).
 *
 * Daqui em diante a navegação entre telas é feita pela classe Navegador, que troca o
 * conteúdo da MESMA janela (assim o tamanho e a posição da janela são preservados).
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TelaInicial.fxml"));
        Parent raiz = loader.load();

        // A cena é criada uma única vez; depois só trocamos a raiz dela (ver Navegador)
        Scene cena = new Scene(raiz, 1180, 760);

        stage.setTitle("Jogo dos Robôs");
        stage.setMinWidth(980);    // abaixo disso o tabuleiro e o painel lateral não cabem juntos
        stage.setMinHeight(680);
        stage.setScene(cena);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
