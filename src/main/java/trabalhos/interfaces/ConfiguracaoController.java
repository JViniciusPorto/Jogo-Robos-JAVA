package trabalhos.interfaces;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import trabalhos.modos.ConfiguracaoJogo;
import trabalhos.modos.TipoModo;

/**
 * Tela de configuração. Uma única tela serve aos quatro modos: os campos que não fazem
 * sentido para o modo escolhido ficam escondidos (por exemplo, o 2º robô no modo solo e
 * as bombas/rochas fora da Arena).
 *
 * Esta classe só COLETA e VALIDA os dados digitados. Ela monta um ConfiguracaoJogo e entrega
 * para o JogoController; quem cria os robôs e o tabuleiro é o TipoModo.
 */
public class ConfiguracaoController {

    private static final int TAMANHO_MAXIMO_NOME = 14;

    @FXML private StackPane raiz;
    @FXML private Label rotuloTitulo;
    @FXML private Label rotuloDescricao;
    @FXML private Label rotuloErro;

    @FXML private TextField campoNomeRobo1;
    @FXML private VBox caixaRobo2;            // some no modo solo
    @FXML private TextField campoNomeRobo2;

    @FXML private ChoiceBox<Integer> escolhaAlimentoX;
    @FXML private ChoiceBox<Integer> escolhaAlimentoY;

    @FXML private VBox caixaObstaculos;       // só aparece na Arena
    @FXML private ChoiceBox<Integer> escolhaBombas;
    @FXML private ChoiceBox<Integer> escolhaRochas;

    private TipoModo modo;

    /** Roda quando o FXML é carregado: preenche as opções das caixas de escolha. */
    @FXML
    public void initialize() {

        // O tabuleiro vai de 0 a 3 nos dois eixos
        escolhaAlimentoX.getItems().addAll(0, 1, 2, 3);
        escolhaAlimentoY.getItems().addAll(0, 1, 2, 3);
        escolhaAlimentoX.setValue(3);
        escolhaAlimentoY.setValue(3);

        // Até 5 de cada: sobram casas livres e as rochas ainda conseguem não isolar o alimento
        for (int i = 0; i <= 5; i++) {
            escolhaBombas.getItems().add(i);
            escolhaRochas.getItems().add(i);
        }
        escolhaBombas.setValue(2);
        escolhaRochas.setValue(2);

        // Nomes sugeridos: os personagens dos sprites (o jogador pode trocar)
        campoNomeRobo1.setText("Goku");
        campoNomeRobo2.setText("Vegeta");

        Navegador.mostrar(rotuloErro, false);
    }

    /** Chamado pela tela inicial logo depois de abrir esta tela. */
    public void definirModo(TipoModo modo) {

        this.modo = modo;

        rotuloTitulo.setText(modo.getTitulo());
        rotuloDescricao.setText(modo.getDescricao());

        // mostra só os campos que o modo usa
        Navegador.mostrar(caixaRobo2, modo.getQuantidadeRobos() == 2);
        Navegador.mostrar(caixaObstaculos, modo.temObstaculos());
    }

    @FXML
    private void iniciarJogo() {

        String nome1 = campoNomeRobo1.getText().trim();
        String nome2 = campoNomeRobo2.getText().trim();

        // validação: nomes preenchidos e não muito longos (para caber nos painéis)
        if (nome1.isEmpty() || (modo.getQuantidadeRobos() == 2 && nome2.isEmpty())) {
            mostrarErro("Preencha o nome de todos os robôs.");
            return;
        }
        if (nome1.length() > TAMANHO_MAXIMO_NOME || nome2.length() > TAMANHO_MAXIMO_NOME) {
            mostrarErro("Os nomes podem ter no máximo " + TAMANHO_MAXIMO_NOME + " letras.");
            return;
        }

        ConfiguracaoJogo configuracao = new ConfiguracaoJogo(
                modo,
                nome1,
                nome2,
                escolhaAlimentoX.getValue(),
                escolhaAlimentoY.getValue(),
                modo.temObstaculos() ? escolhaBombas.getValue() : 0,
                modo.temObstaculos() ? escolhaRochas.getValue() : 0);

        try {
            JogoController jogo = Navegador.ir(raiz, "Jogo.fxml");
            jogo.iniciar(configuracao);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void voltar() {

        try {
            Navegador.ir(raiz, "TelaInicial.fxml");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarErro(String mensagem) {
        rotuloErro.setText(mensagem);
        Navegador.mostrar(rotuloErro, true);
    }
}
