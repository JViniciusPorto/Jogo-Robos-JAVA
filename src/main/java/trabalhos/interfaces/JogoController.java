package trabalhos.interfaces;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import trabalhos.classes_obstaculos.Obstaculo;
import trabalhos.classes_obstaculos.ObstaculoBomba;
import trabalhos.classes_robos.Robo;
import trabalhos.modos.ConfiguracaoJogo;
import trabalhos.modos.ModoDeJogo;
import trabalhos.modos.ModoSolo;
import trabalhos.modos.ResultadoJogada;
import trabalhos.tabuleiro.Tabuleiro;
import trabalhos.visual.Direcao;
import trabalhos.visual.PainelRobo;
import trabalhos.visual.RobotSprite;
import trabalhos.visual.SaibamanSprite;
import trabalhos.visual.SpriteEstatico;
import trabalhos.visual.Sprites;
import trabalhos.visual.TabuleiroView;
import trabalhos.visual.TabuleiroView.Camada;

/**
 * Tela do jogo. É a "cola" entre três partes que não se conhecem entre si:
 *
 *   MODELO/LÓGICA  (ModoDeJogo, Robo, Tabuleiro...)  decide o que acontece;
 *   VISUAL         (TabuleiroView, sprites)          mostra o que aconteceu;
 *   TELA           (FXML + CSS)                      botões, textos, painéis.
 *
 * O fluxo de UMA jogada é sempre o mesmo, nos quatro modos:
 *   1. o modo executa a jogada e devolve um ResultadoJogada (nada de desenho aqui);
 *   2. este controller anima o resultado com os sprites;
 *   3. quando a animação termina, atualiza os painéis e decide: acabou? próxima jogada?
 *
 * Nenhuma regra do jogo é repetida aqui: o controller nunca confere bomba, rocha ou alimento.
 */
public class JogoController {

    // ---------- Tempos (em milissegundos) ----------
    private static final double ESPERA_ENTRE_JOGADAS_MS = 800;   // igual ao Tempo.aguardar(800) das Mains
    private static final double DURACAO_PASSO_MS = 500;          // quanto dura a caminhada de uma casa
    private static final double DURACAO_TREMER_MS = 350;         // balanço ao bater na parede

    // ---------- Tamanho das peças em relação à casa (1.0 = o lado da casa) ----------
    private static final double ESCALA_ROBO = 0.9;
    private static final double ESCALA_SAIBAMAN = 1.25;
    private static final double ESCALA_ROCHA = 1.1;
    private static final double ESCALA_ESFERA = 0.8;

    // ---------- Componentes do FXML (ligados pelo fx:id) ----------
    @FXML private BorderPane raiz;
    @FXML private Label rotuloModo;
    @FXML private Label rotuloStatus;
    @FXML private StackPane areaTabuleiro;      // contém o tabuleiro e, por cima, o painel de resultado
    @FXML private VBox areaRobos;               // cartões dos robôs
    @FXML private VBox painelControles;         // instruções + botões direcionais (só no modo solo)
    @FXML private ListView<String> listaEventos;
    @FXML private Button botaoPausar;

    @FXML private VBox painelResultado;         // aparece quando o jogo termina
    @FXML private Label tituloResultado;
    @FXML private Label mensagemResultado;
    @FXML private Label estatisticasResultado;

    // ---------- Estado do jogo ----------
    private ConfiguracaoJogo configuracao;      // guardada para o botão "Novo Jogo"
    private ModoDeJogo modo;
    private TabuleiroView tabuleiroView;

    // Ligações entre o modelo e o visual
    private final Map<Robo, RobotSprite> spritesDosRobos = new HashMap<>();
    private final Map<Robo, PainelRobo> paineisDosRobos = new LinkedHashMap<>();
    private final Map<Integer, SaibamanSprite> saibamansPorCasa = new HashMap<>();   // chave: x * 10 + y
    private SpriteEstatico esfera;

    private PauseTransition espera;             // a pausa de 800 ms antes de cada jogada automática
    private boolean pausado = false;
    private boolean animando = false;           // true enquanto uma jogada está sendo animada

    // =====================================================================
    //  INÍCIO
    // =====================================================================

    /**
     * Prepara e começa o jogo. É chamado pela tela de configuração (ou pelo "Novo Jogo")
     * logo depois de carregar o Jogo.fxml.
     */
    public void iniciar(ConfiguracaoJogo configuracao) {

        this.configuracao = configuracao;
        this.modo = configuracao.modo().criar(configuracao);   // cria robôs e tabuleiro do modo

        rotuloModo.setText(modo.getTipo().getTitulo());

        montarTabuleiro();
        montarPaineisDosRobos();
        configurarControles();

        registrar("Jogo iniciado: " + modo.getTipo().getTitulo() + ".");
        atualizarPaineis();

        if (modo.isTerminado()) {
            // acontece quando o alimento está na largada (0,0): o jogo termina antes de começar
            mostrarResultado();
        } else if (modo.isAutomatico()) {
            agendarProximaJogada();
        } else {
            rotuloStatus.setText("Sua vez! Mova o robô " + modo.getRoboDaVez().getCor() + ".");
        }
    }

    /** Cria o TabuleiroView e coloca nele a esfera, os obstáculos e os robôs. */
    private void montarTabuleiro() {

        Tabuleiro tabuleiro = modo.getTabuleiro();
        tabuleiroView = new TabuleiroView(tabuleiro.getTamanho());

        // índice 0 = atrás do painel de resultado, que fica por cima do tabuleiro
        areaTabuleiro.getChildren().add(0, tabuleiroView);

        // alimento: a esfera do dragão
        esfera = new SpriteEstatico(Sprites.ESFERA, "★", Color.web("#ff8a00"));
        tabuleiroView.adicionar(esfera, Camada.OBJETOS,
                tabuleiro.getAlimentoX(), tabuleiro.getAlimentoY(), ESCALA_ESFERA);

        // obstáculos: bomba vira Saibaman; rocha vira rocha
        for (Obstaculo obstaculo : tabuleiro.getObstaculos()) {

            if (obstaculo instanceof ObstaculoBomba) {
                SaibamanSprite saibaman = new SaibamanSprite();
                tabuleiroView.adicionar(saibaman, Camada.OBJETOS,
                        obstaculo.getX(), obstaculo.getY(), ESCALA_SAIBAMAN);
                saibamansPorCasa.put(chaveDaCasa(obstaculo.getX(), obstaculo.getY()), saibaman);

            } else {
                SpriteEstatico rocha = new SpriteEstatico(Sprites.ROCHA, "R", Color.GRAY);
                tabuleiroView.adicionar(rocha, Camada.OBJETOS,
                        obstaculo.getX(), obstaculo.getY(), ESCALA_ROCHA);
            }
        }

        // robôs: o primeiro é o Goku e o segundo é o Vegeta (todos largam em (0,0))
        int numero = 1;
        for (Robo robo : modo.getRobos()) {
            RobotSprite sprite = new RobotSprite(
                    caminhoDoSprite(numero), robo.getCor(), corDoJogador(numero));
            spritesDosRobos.put(robo, sprite);
            tabuleiroView.adicionar(sprite, Camada.ROBOS, robo.getX(), robo.getY(), ESCALA_ROBO);
            numero++;
        }
    }

    /** Cria um cartão de informações para cada robô no painel lateral. */
    private void montarPaineisDosRobos() {

        int numero = 1;
        for (Robo robo : modo.getRobos()) {
            PainelRobo painel = new PainelRobo(robo, caminhoDoSprite(numero), numero);
            paineisDosRobos.put(robo, painel);
            areaRobos.getChildren().add(painel);
            numero++;
        }
    }

    /** Ajusta a tela conforme o modo: controles manuais só no solo, teclado só no solo. */
    private void configurarControles() {

        Navegador.mostrar(painelControles, !modo.isAutomatico());
        Navegador.mostrar(painelResultado, false);

        // O teclado é lido pela raiz da tela. Ela precisa de foco para receber as teclas.
        raiz.addEventFilter(KeyEvent.KEY_PRESSED, this::aoPressionarTecla);
        raiz.setFocusTraversable(true);
        Platform.runLater(raiz::requestFocus);
    }

    // =====================================================================
    //  LOOP DAS JOGADAS
    // =====================================================================

    /** Modos automáticos: espera um pouco ("pensando...") e então joga. */
    private void agendarProximaJogada() {

        rotuloStatus.setText("Robô " + modo.getRoboDaVez().getCor() + " está pensando...");

        espera = new PauseTransition(Duration.millis(ESPERA_ENTRE_JOGADAS_MS));
        espera.setOnFinished(e -> jogarAutomatico());
        espera.play();

        // se o usuário pausou durante a transição entre duas jogadas, a espera nasce pausada
        if (pausado) {
            espera.pause();
        }
    }

    private void jogarAutomatico() {
        animar(modo.jogarProximaVez());
    }

    /** Modo solo: o jogador comandou um movimento (1 = up, 2 = down, 3 = right, 4 = left). */
    private void comandar(int comando) {

        if (modo.isAutomatico() || modo.isTerminado() || animando || pausado) {
            return;   // ignora comandos fora de hora (animação rodando, pausa ou fim de jogo)
        }

        // o cast é seguro: só o ModoSolo não é automático
        animar(((ModoSolo) modo).jogarComando(comando));
    }

    /**
     * Anima o resultado de uma jogada. O modelo JÁ decidiu o que aconteceu; aqui só
     * escolhemos qual animação mostrar para cada tipo de desfecho.
     */
    private void animar(ResultadoJogada resultado) {

        animando = true;
        registrar(resultado.mensagem());
        rotuloStatus.setText(resultado.mensagem());

        RobotSprite sprite = spritesDosRobos.get(resultado.robo());
        Runnable aoTerminar = () -> terminarJogada(resultado);

        switch (resultado.tipo()) {

            case BATEU_NA_PAREDE:
                sprite.tremer(Duration.millis(DURACAO_TREMER_MS), aoTerminar);
                break;

            case BATEU_NA_ROCHA:
                // o robô avança em direção à rocha e volta (o modelo já o devolveu à casa anterior)
                sprite.bater(direcaoDoMovimento(resultado),
                        tabuleiroView.getLadoCelula() * 0.4,
                        Duration.millis(DURACAO_PASSO_MS), aoTerminar);
                break;

            case EXPLODIU:
                // primeiro caminha até a casa da bomba; quando chega, o Saibaman explode
                andar(sprite, resultado, () -> explodirSaibaman(resultado, aoTerminar));
                break;

            default:   // MOVEU
                andar(sprite, resultado, aoTerminar);
                break;
        }
    }

    /** Move o sprite para a casa de destino e toca a caminhada. */
    private void andar(RobotSprite sprite, ResultadoJogada resultado, Runnable aoTerminar) {

        // 1) o tabuleiro coloca o sprite na casa nova (posição lógica)
        tabuleiroView.mover(sprite, resultado.xDepois(), resultado.yDepois());

        // 2) o sprite desliza da casa antiga até ela. O eixo y é invertido: o modelo cresce
        //    para cima e a tela cresce para baixo.
        double casa = tabuleiroView.getLadoCelula();
        double deslocX = (resultado.xDepois() - resultado.xAntes()) * casa;
        double deslocY = -(resultado.yDepois() - resultado.yAntes()) * casa;

        sprite.andar(deslocX, deslocY, direcaoDoMovimento(resultado),
                Duration.millis(DURACAO_PASSO_MS), aoTerminar);
    }

    /** Toca a ativação e a explosão do Saibaman que estava na casa onde o robô pisou. */
    private void explodirSaibaman(ResultadoJogada resultado, Runnable aoTerminar) {

        SaibamanSprite saibaman = saibamansPorCasa.remove(
                chaveDaCasa(resultado.xDepois(), resultado.yDepois()));
        RobotSprite sprite = spritesDosRobos.get(resultado.robo());

        if (saibaman == null) {
            // não deveria acontecer, mas se não houver sprite o jogo segue normalmente
            sprite.setExplodido(true);
            aoTerminar.run();
            return;
        }

        // desenha o Saibaman por cima dos robôs enquanto explode
        tabuleiroView.trazerParaEfeitos(saibaman);

        saibaman.explodir(
                () -> sprite.setExplodido(true),            // no impacto: o robô cai, cinza
                () -> {                                     // no fim: o Saibaman some do tabuleiro
                    tabuleiroView.remover(saibaman);
                    aoTerminar.run();
                });
    }

    /** Roda quando a animação da jogada acabou: atualiza a tela e decide o que vem depois. */
    private void terminarJogada(ResultadoJogada resultado) {

        animando = false;

        if (resultado.achouAlimento()) {
            registrar("Robô " + resultado.robo().getCor() + " encontrou o alimento!");
            esfera.destacar();
        }

        atualizarPaineis();

        if (modo.isTerminado()) {
            mostrarResultado();
        } else if (modo.isAutomatico()) {
            agendarProximaJogada();
        } else {
            rotuloStatus.setText("Sua vez! Mova o robô " + modo.getRoboDaVez().getCor() + ".");
        }
    }

    // =====================================================================
    //  FIM DE JOGO E PAINÉIS
    // =====================================================================

    /** Mostra o painel de vitória/empate com as estatísticas dos movimentos. */
    private void mostrarResultado() {

        tituloResultado.setText(modo.getTituloFinal());
        mensagemResultado.setText(modo.getMensagemFinal());
        estatisticasResultado.setText(String.join("\n", modo.getEstatisticas()));

        // o CSS pinta o título de dourado (vitória) ou de vermelho (empate)
        painelResultado.getStyleClass().removeAll("vitoria", "empate");
        painelResultado.getStyleClass().add(modo.getVencedor() != null ? "vitoria" : "empate");

        Navegador.mostrar(painelResultado, true);
        rotuloStatus.setText(modo.getTituloFinal());
        botaoPausar.setDisable(true);
    }

    /** Atualiza os cartões dos robôs com o estado atual do modelo. */
    private void atualizarPaineis() {

        for (Map.Entry<Robo, PainelRobo> entrada : paineisDosRobos.entrySet()) {

            Robo robo = entrada.getKey();
            boolean suaVez = !modo.isTerminado() && robo == modo.getRoboDaVez();

            entrada.getValue().atualizar(robo, descreverSituacao(robo), suaVez);
        }
    }

    /** Texto curto da situação do robô, deduzido do estado do modelo. */
    private String descreverSituacao(Robo robo) {

        if (robo.getIsVencedor()) {
            return "★ Venceu";
        }
        if (!robo.getIsPodeMover() && modo.estaNoAlimento(robo)) {
            return "Chegou ao alimento";
        }
        if (!robo.getIsPodeMover()) {
            return "Explodiu";
        }
        return "Em jogo";
    }

    // =====================================================================
    //  BOTÕES E TECLADO
    // =====================================================================

    @FXML
    private void alternarPausa() {

        if (modo.isTerminado()) {
            return;
        }

        pausado = !pausado;

        if (pausado) {
            if (espera != null) {
                espera.pause();
            }
            tabuleiroView.pausarTudo();
            botaoPausar.setText("Continuar");
            rotuloStatus.setText("Jogo pausado.");

        } else {
            botaoPausar.setText("Pausar");
            tabuleiroView.continuarTudo();

            // só retoma a espera se ela estava mesmo pausada (se já terminou, não reinicia)
            if (espera != null && espera.getStatus() == javafx.animation.Animation.Status.PAUSED) {
                espera.play();
            }
            if (!animando && !modo.isAutomatico()) {
                rotuloStatus.setText("Sua vez! Mova o robô " + modo.getRoboDaVez().getCor() + ".");
            }
        }
    }

    /** Recomeça com as MESMAS configurações (os obstáculos são sorteados de novo). */
    @FXML
    private void novoJogo() {

        pararTudo();

        try {
            JogoController novo = Navegador.ir(raiz, "Jogo.fxml");
            novo.iniciar(configuracao);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Volta ao menu dos quatro modos. */
    @FXML
    private void voltar() {

        pararTudo();

        try {
            Navegador.ir(raiz, "TelaInicial.fxml");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Botões direcionais do modo solo. Os números são os mesmos de Robo.mover(int).

    @FXML
    private void comandarCima() {
        comandar(1);   // up
    }

    @FXML
    private void comandarBaixo() {
        comandar(2);   // down
    }

    @FXML
    private void comandarDireita() {
        comandar(3);   // right
    }

    @FXML
    private void comandarEsquerda() {
        comandar(4);   // left
    }

    /** Teclas do modo solo: setas, W A S D ou os números 1 a 4. */
    private void aoPressionarTecla(KeyEvent evento) {

        if (modo.isAutomatico()) {
            return;
        }

        int comando;

        switch (evento.getCode()) {
            case UP: case W: case DIGIT1: case NUMPAD1:
                comando = 1;
                break;
            case DOWN: case S: case DIGIT2: case NUMPAD2:
                comando = 2;
                break;
            case RIGHT: case D: case DIGIT3: case NUMPAD3:
                comando = 3;
                break;
            case LEFT: case A: case DIGIT4: case NUMPAD4:
                comando = 4;
                break;
            default:
                return;   // outra tecla: ignora
        }

        evento.consume();   // evita que as setas também movam o foco entre os botões
        comandar(comando);
    }

    // =====================================================================
    //  APOIO
    // =====================================================================

    /** Para tudo que está rodando antes de sair desta tela (evita animações "fantasmas"). */
    private void pararTudo() {

        if (espera != null) {
            espera.stop();
        }
        tabuleiroView.pararTudo();
    }

    /** Direção visual do movimento, deduzida de quanto as coordenadas mudaram. */
    private Direcao direcaoDoMovimento(ResultadoJogada resultado) {
        return Direcao.deDeslocamento(
                resultado.xDepois() - resultado.xAntes(),
                resultado.yDepois() - resultado.yAntes());
    }

    /** Escreve na lista de eventos (a mais recente fica no topo). */
    private void registrar(String mensagem) {
        listaEventos.getItems().add(0, mensagem);
    }

    /** Identifica uma casa por um único número: x * 10 + y (funciona porque x e y vão de 0 a 3). */
    private int chaveDaCasa(int x, int y) {
        return x * 10 + y;
    }

    /** Jogador 1 usa o spritesheet do Goku; jogador 2, o do Vegeta. */
    private String caminhoDoSprite(int numero) {
        return numero == 1 ? Sprites.GOKU : Sprites.VEGETA;
    }

    /** Cor do círculo reserva (se o PNG faltar): laranja para o Goku, azul para o Vegeta. */
    private Color corDoJogador(int numero) {
        return numero == 1 ? Color.web("#ff8a00") : Color.web("#2f6df6");
    }
}
