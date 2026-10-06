package trabalhos.visual;

import java.net.URL;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

/**
 * Um spritesheet em GRADE: uma imagem grande com vários quadros (frames) de tamanho igual.
 *
 * Esta classe só sabe carregar a imagem e dizer "qual retângulo da imagem é o quadro
 * (coluna, linha)". Quem desenha o quadro é um ImageView, usando esse retângulo como viewport.
 *
 * Usamos a MESMA classe para todos os sprites do projeto:
 *   - goku.png / vegeta.png / saibaman.png : 4 colunas (frames) x 4 linhas (direções)
 *   - saibaman_ativacao.png                : 4 colunas x 1 linha
 *   - explosao.png                         : 6 colunas x 1 linha
 *   - esfera.png / rocha.png               : 1 x 1 (uma imagem só)
 * O tamanho de cada quadro é calculado dividindo a imagem pelo número de colunas e linhas,
 * então funciona com qualquer resolução.
 */
public class SpriteSheet {

    /** Quantos frames de caminhada cada direção tem. */
    public static final int FRAMES_CAMINHADA = 4;

    private final Image imagem;
    private final int colunas;
    private final int linhas;
    private final double larguraFrame;
    private final double alturaFrame;

    private SpriteSheet(Image imagem, int colunas, int linhas) {
        this.imagem = imagem;
        this.colunas = colunas;
        this.linhas = linhas;
        this.larguraFrame = imagem.getWidth() / colunas;
        this.alturaFrame = imagem.getHeight() / linhas;
    }

    /**
     * Carrega um spritesheet de src/main/resources.
     *
     * FALLBACK: se o arquivo não existir ou estiver corrompido, devolve null (e avisa no
     * console). Quem recebe null desenha uma forma simples no lugar, então um PNG ausente
     * nunca derruba o jogo.
     */
    public static SpriteSheet carregar(String caminho, int colunas, int linhas) {

        URL url = SpriteSheet.class.getResource(caminho);

        if (url == null) {
            System.err.println("[Sprites] Arquivo não encontrado: " + caminho);
            return null;
        }

        Image imagem = new Image(url.toExternalForm());

        if (imagem.isError() || imagem.getWidth() <= 0) {
            System.err.println("[Sprites] Não foi possível ler: " + caminho);
            return null;
        }

        return new SpriteSheet(imagem, colunas, linhas);
    }

    /** Atalho para os personagens: 4 frames de caminhada x 4 direções. */
    public static SpriteSheet carregarDirecional(String caminho) {
        return carregar(caminho, FRAMES_CAMINHADA, Direcao.values().length);
    }

    public Image getImagem() {
        return imagem;
    }

    public int getColunas() {
        return colunas;
    }

    public int getLinhas() {
        return linhas;
    }

    /** Retângulo da imagem que corresponde ao quadro (coluna, linha). */
    public Rectangle2D getFrame(int coluna, int linha) {

        // floorMod faz o número "dar a volta" (frame 4 volta a ser o 0) e nunca sai da grade
        int c = Math.floorMod(coluna, colunas);
        int l = Math.floorMod(linha, linhas);

        return new Rectangle2D(c * larguraFrame, l * alturaFrame, larguraFrame, alturaFrame);
    }

    /** Quadro de um personagem: a linha vem da direção e a coluna é o frame da caminhada. */
    public Rectangle2D getFrame(Direcao direcao, int frame) {
        return getFrame(frame, direcao.ordinal());
    }
}
