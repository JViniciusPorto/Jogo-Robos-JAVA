package trabalhos.visual;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Parte VISUAL de um robô (Goku ou Vegeta): guarda a direção, o frame atual e as animações.
 *
 * Esta classe NÃO conhece Robo nem as regras do jogo. Quem manda é o JogoController, que
 * diz "ande para lá" ou "bata aqui" depois que o modelo já decidiu o que aconteceu.
 *
 * A animação de caminhada troca os 4 frames da direção atual enquanto o sprite desliza de
 * uma casa para outra. Quando o movimento termina, volta ao frame parado e nenhum timer
 * continua rodando (o personagem parado não gasta animação).
 *
 * Se o PNG não for encontrado, aparece um círculo colorido com a inicial do nome (fallback).
 */
public class RobotSprite implements PecaVisual {

    // Frame mostrado quando o personagem está parado (o primeiro da caminhada)
    private static final int FRAME_PARADO = 0;

    // Group é um contêiner sem layout próprio: o TabuleiroView posiciona ele com layoutX/Y
    private final Group no = new Group();

    private final SpriteSheet folha;          // null se o PNG não existe
    private final ImageView imagem;           // null no modo fallback
    private final FormaReserva reserva;       // null quando o PNG foi carregado
    private final Color corReserva;

    private Direcao direcao = Direcao.BAIXO;  // começa virado para baixo (de frente)
    private Animation animacaoAtual;          // null quando o personagem está parado

    public RobotSprite(String caminhoSprite, String nome, Color corReserva) {

        this.corReserva = corReserva;
        this.folha = SpriteSheet.carregarDirecional(caminhoSprite);

        if (folha != null) {
            imagem = new ImageView(folha.getImagem());
            imagem.setPreserveRatio(true);   // nunca distorce o personagem
            imagem.setSmooth(true);          // a arte é reduzida na tela, então suavizamos
            reserva = null;
            no.getChildren().add(imagem);
        } else {
            String inicial = nome.isEmpty() ? "?" : nome.substring(0, 1).toUpperCase();
            imagem = null;
            reserva = new FormaReserva(inicial, corReserva);
            no.getChildren().add(reserva);
        }

        mostrarFrame(FRAME_PARADO);
    }

    @Override
    public Node getNo() {
        return no;
    }

    /** Define o lado da caixa quadrada do sprite; a proporção do desenho é sempre preservada. */
    @Override
    public void setTamanho(double lado) {

        if (imagem != null) {
            imagem.setFitWidth(lado);
            imagem.setFitHeight(lado);
        } else {
            reserva.setTamanho(lado);
        }
    }

    // ---------- Animações ----------

    /**
     * Anda de uma casa para a vizinha.
     *
     * O TabuleiroView já colocou o sprite na casa de DESTINO. Então aqui o sprite começa
     * deslocado para trás (-deslocX, -deslocY) e desliza até a posição final (0, 0),
     * trocando os 4 frames durante o trajeto.
     *
     * @param deslocX    pixels de tela entre a casa antiga e a nova no eixo horizontal
     * @param deslocY    idem no eixo vertical (positivo = para baixo na tela)
     * @param aoTerminar executado quando a animação acaba
     */
    public void andar(double deslocX, double deslocY, Direcao novaDirecao,
                      Duration duracao, Runnable aoTerminar) {

        parar();
        direcao = novaDirecao;

        no.setTranslateX(-deslocX);
        no.setTranslateY(-deslocY);

        TranslateTransition deslizar = new TranslateTransition(duracao, no);
        deslizar.setToX(0);
        deslizar.setToY(0);
        deslizar.setInterpolator(Interpolator.LINEAR);   // velocidade constante, como uma caminhada

        iniciar(new ParallelTransition(deslizar, criarAnimacaoDeFrames(duracao)), aoTerminar);
    }

    /**
     * Encosta numa rocha: avança um pedaço na direção dela e volta.
     * (O modelo já devolveu o robô à casa anterior; isto só mostra a "batida".)
     */
    public void bater(Direcao novaDirecao, double distancia, Duration duracao, Runnable aoTerminar) {

        parar();
        direcao = novaDirecao;

        Duration metade = duracao.divide(2);

        TranslateTransition ida = new TranslateTransition(metade, no);
        ida.setToX(novaDirecao.getDxTela() * distancia);
        ida.setToY(novaDirecao.getDyTela() * distancia);

        TranslateTransition volta = new TranslateTransition(metade, no);
        volta.setToX(0);
        volta.setToY(0);

        iniciar(new ParallelTransition(
                new SequentialTransition(ida, volta),
                criarAnimacaoDeFrames(duracao)), aoTerminar);
    }

    /**
     * Balança de um lado para o outro sem sair do lugar.
     * Usado quando o robô tenta sair do tabuleiro (MovimentoInvalidoException).
     */
    public void tremer(Duration duracao, Runnable aoTerminar) {

        parar();

        Timeline balancar = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(no.translateXProperty(), 0)),
                new KeyFrame(duracao.multiply(0.2), new KeyValue(no.translateXProperty(), -6)),
                new KeyFrame(duracao.multiply(0.4), new KeyValue(no.translateXProperty(), 6)),
                new KeyFrame(duracao.multiply(0.6), new KeyValue(no.translateXProperty(), -4)),
                new KeyFrame(duracao.multiply(0.8), new KeyValue(no.translateXProperty(), 4)),
                new KeyFrame(duracao, new KeyValue(no.translateXProperty(), 0)));

        iniciar(balancar, aoTerminar);
    }

    /**
     * Linha do tempo que mostra os 4 frames da caminhada, um depois do outro, dividindo a
     * duração do movimento em 4 partes iguais (frame 1 -> 2 -> 3 -> 4).
     */
    private Timeline criarAnimacaoDeFrames(Duration duracao) {

        Timeline frames = new Timeline();
        int total = SpriteSheet.FRAMES_CAMINHADA;

        for (int i = 0; i < total; i++) {
            final int indice = i;   // lambda precisa de uma variável que não muda
            frames.getKeyFrames().add(new KeyFrame(
                    duracao.multiply((double) i / total),
                    e -> mostrarFrame(indice)));
        }

        return frames;
    }

    /** Guarda a animação, configura o que fazer no fim e dá o play. */
    private void iniciar(Animation animacao, Runnable aoTerminar) {

        animacaoAtual = animacao;

        animacao.setOnFinished(e -> {
            // terminou: volta ao frame parado e esquece a animação (nada mais fica rodando)
            no.setTranslateX(0);
            no.setTranslateY(0);
            mostrarFrame(FRAME_PARADO);
            animacaoAtual = null;

            if (aoTerminar != null) {
                aoTerminar.run();
            }
        });

        mostrarFrame(FRAME_PARADO);
        animacao.play();
    }

    // ---------- Controle (PecaVisual) ----------

    @Override
    public void pausar() {
        if (animacaoAtual != null && animacaoAtual.getStatus() == Animation.Status.RUNNING) {
            animacaoAtual.pause();
        }
    }

    @Override
    public void continuar() {
        if (animacaoAtual != null && animacaoAtual.getStatus() == Animation.Status.PAUSED) {
            animacaoAtual.play();
        }
    }

    @Override
    public void parar() {

        if (animacaoAtual != null) {
            animacaoAtual.setOnFinished(null);   // não chama o "aoTerminar" de uma animação cancelada
            animacaoAtual.stop();
            animacaoAtual = null;
        }

        no.setTranslateX(0);
        no.setTranslateY(0);
        mostrarFrame(FRAME_PARADO);
    }

    /**
     * Aparência de robô que explodiu: acinzentado, meio transparente e deitado (girado 90°).
     * É só visual; quem decide que o robô não anda mais é o Robo (setIsPodeMover(false)).
     */
    public void setExplodido(boolean explodido) {

        no.setRotate(explodido ? 90 : 0);

        if (imagem != null) {
            if (explodido) {
                ColorAdjust cinza = new ColorAdjust();
                cinza.setSaturation(-1);
                cinza.setBrightness(-0.3);
                imagem.setEffect(cinza);
                imagem.setOpacity(0.75);
            } else {
                imagem.setEffect(null);
                imagem.setOpacity(1);
            }
        } else {
            reserva.setCor(explodido ? Color.DIMGRAY : corReserva);
        }
    }

    /** Mostra o quadro (direção atual, frame) do spritesheet. Sem PNG não há o que trocar. */
    private void mostrarFrame(int frame) {
        if (imagem != null) {
            imagem.setViewport(folha.getFrame(direcao, frame));
        }
    }
}
