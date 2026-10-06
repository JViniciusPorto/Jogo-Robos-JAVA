package trabalhos.visual;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Representação VISUAL de uma bomba: o Saibaman.
 *
 * Quem decide que a bomba explode é a classe ObstaculoBomba (e o ModoDeJogo). Este sprite
 * só sabe TOCAR a sequência visual quando o controller manda:
 *
 *   Saibaman parado -> ativação (4 frames) -> explosão (6 frames) -> some (fade)
 *
 * Ele é composto por três imagens sobrepostas, mostradas uma de cada vez:
 *   - parado    : o Saibaman de frente (saibaman.png);
 *   - ativacao  : o Saibaman se enchendo de energia (saibaman_ativacao.png);
 *   - explosao  : a explosão, maior que a casa (explosao.png).
 *
 * Qualquer PNG ausente é ignorado sem quebrar o jogo; se o Saibaman parado faltar,
 * aparece um círculo verde com "S".
 */
public class SaibamanSprite implements PecaVisual {

    // Quanto tempo cada frame da ativação/explosão fica na tela
    private static final double MS_POR_FRAME = 110;

    // A explosão é desenhada maior que a casa para dar impacto (1.6 = 160% do lado da peça)
    private static final double ESCALA_EXPLOSAO = 1.6;

    private final Group no = new Group();

    private final SpriteSheet folhaParado;
    private final SpriteSheet folhaAtivacao;
    private final SpriteSheet folhaExplosao;

    private final ImageView parado;       // cada um pode ser null se o PNG não existe
    private final ImageView ativacao;
    private final ImageView explosao;
    private final FormaReserva reserva;   // só existe se o Saibaman parado não foi encontrado

    private Animation animacaoAtual;

    public SaibamanSprite() {

        folhaParado = SpriteSheet.carregarDirecional(Sprites.SAIBAMAN);
        folhaAtivacao = SpriteSheet.carregar(Sprites.SAIBAMAN_ATIVACAO, 4, 1);
        folhaExplosao = SpriteSheet.carregar(Sprites.EXPLOSAO, 6, 1);

        parado = criarImagem(folhaParado);
        ativacao = criarImagem(folhaAtivacao);
        explosao = criarImagem(folhaExplosao);

        if (parado != null) {
            // "parado" usa o Saibaman virado para BAIXO (de frente), primeiro frame
            parado.setViewport(folhaParado.getFrame(Direcao.BAIXO, 0));
            reserva = null;
        } else {
            reserva = new FormaReserva("S", Color.web("#6fbf3b"));
        }

        // Ordem de empilhamento: o que é adicionado depois fica por cima
        if (reserva != null) {
            no.getChildren().add(reserva);
        } else {
            no.getChildren().add(parado);
        }
        // ativação e explosão só aparecem durante explodir(); começam escondidas.
        // (Fica aqui, e não em setTamanho, porque setTamanho é chamado a cada novo layout,
        // inclusive no meio da animação.)
        if (ativacao != null) {
            ativacao.setVisible(false);
            no.getChildren().add(ativacao);
        }
        if (explosao != null) {
            explosao.setVisible(false);
            no.getChildren().add(explosao);
        }
    }

    /** Cria o ImageView de uma folha (ou null se a folha não existe), já escondido se for efeito. */
    private ImageView criarImagem(SpriteSheet folha) {

        if (folha == null) {
            return null;
        }

        ImageView imagem = new ImageView(folha.getImagem());
        imagem.setPreserveRatio(true);
        imagem.setSmooth(true);
        return imagem;
    }

    @Override
    public Node getNo() {
        return no;
    }

    @Override
    public void setTamanho(double lado) {

        if (parado != null) {
            parado.setFitWidth(lado);
            parado.setFitHeight(lado);
        } else {
            reserva.setTamanho(lado);
        }

        if (ativacao != null) {
            ativacao.setFitWidth(lado);
            ativacao.setFitHeight(lado);
        }

        if (explosao != null) {
            double ladoExplosao = lado * ESCALA_EXPLOSAO;
            explosao.setFitWidth(ladoExplosao);
            explosao.setFitHeight(ladoExplosao);
            // centraliza a explosão (maior) sobre a caixa da peça
            explosao.setLayoutX((lado - ladoExplosao) / 2);
            explosao.setLayoutY((lado - ladoExplosao) / 2);
        }
    }

    /**
     * Toca a sequência de explosão.
     *
     * @param noImpacto  executado no instante em que a explosão começa (o controller usa
     *                   para deixar o robô "deitado"/cinza exatamente nessa hora)
     * @param aoTerminar executado quando o Saibaman terminou de sumir
     */
    public void explodir(Runnable noImpacto, Runnable aoTerminar) {

        parar();

        Timeline quadros = new Timeline();
        int framesAtivacao = 4;
        int framesExplosao = 6;

        // 1) ativação: o Saibaman troca por cada um dos 4 frames de energia
        for (int i = 0; i < framesAtivacao; i++) {
            final int indice = i;
            quadros.getKeyFrames().add(new KeyFrame(
                    Duration.millis(i * MS_POR_FRAME),
                    e -> mostrarAtivacao(indice)));
        }

        // 2) impacto: some o Saibaman e começa a explosão
        quadros.getKeyFrames().add(new KeyFrame(
                Duration.millis(framesAtivacao * MS_POR_FRAME),
                e -> {
                    esconderSaibaman();
                    mostrarExplosao(0);
                    if (noImpacto != null) {
                        noImpacto.run();
                    }
                }));

        // 3) explosão: frames 2 a 6 (o 1º já foi mostrado no impacto)
        for (int k = 1; k < framesExplosao; k++) {
            final int indice = k;
            quadros.getKeyFrames().add(new KeyFrame(
                    Duration.millis((framesAtivacao + k) * MS_POR_FRAME),
                    e -> mostrarExplosao(indice)));
        }

        // marca o fim da linha do tempo (o último frame fica visível por mais um passo)
        quadros.getKeyFrames().add(new KeyFrame(
                Duration.millis((framesAtivacao + framesExplosao) * MS_POR_FRAME)));

        // 4) a cratera/fumaça se desfaz suavemente: o Saibaman "desaparece"
        FadeTransition sumir = new FadeTransition(Duration.millis(300), no);
        sumir.setToValue(0);

        SequentialTransition sequencia = new SequentialTransition(quadros, sumir);

        sequencia.setOnFinished(e -> {
            animacaoAtual = null;
            no.setVisible(false);
            if (aoTerminar != null) {
                aoTerminar.run();
            }
        });

        animacaoAtual = sequencia;
        sequencia.play();
    }

    // ---------- Quadros ----------

    private void mostrarAtivacao(int frame) {

        if (ativacao != null) {
            esconderSaibaman();
            ativacao.setViewport(folhaAtivacao.getFrame(frame, 0));
            ativacao.setVisible(true);
        }
    }

    private void mostrarExplosao(int frame) {

        if (ativacao != null) {
            ativacao.setVisible(false);
        }

        if (explosao != null) {
            explosao.setViewport(folhaExplosao.getFrame(frame, 0));
            explosao.setVisible(true);
        }
    }

    private void esconderSaibaman() {

        if (parado != null) {
            parado.setVisible(false);
        }
        if (reserva != null) {
            reserva.setVisible(false);
        }
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
            animacaoAtual.setOnFinished(null);
            animacaoAtual.stop();
            animacaoAtual = null;
        }
    }
}
