package trabalhos.visual;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * DESENHO do tabuleiro: mostra a arena como cenário e posiciona as peças (robôs, Saibamans,
 * rochas e esfera) sobre as casas lógicas 0..3 em x e y.
 *
 * Esta classe não conhece o modelo (Robo, Tabuleiro, Obstaculo): ela só recebe pedidos como
 * "coloque esta peça na casa (2,1)". Assim a regra do jogo e o desenho ficam separados.
 *
 * COORDENADAS: o modelo usa y crescendo PARA CIMA ("up" = y+1), mas a tela cresce para
 * baixo. Por isso a casa (x, y) é desenhada na linha de tela (grade - 1 - y).
 *
 * RESPONSIVIDADE: o tabuleiro é sempre um quadrado do tamanho do menor lado da área
 * disponível. Cenário, casas e peças são recalculados toda vez que a janela muda de tamanho.
 *
 * CAMADAS (de baixo para cima): cenário < objetos (esfera, rochas, Saibamans) < robôs < efeitos.
 */
public class TabuleiroView extends Pane {

    /** Em qual camada uma peça é desenhada. */
    public enum Camada { OBJETOS, ROBOS, EFEITOS }

    // Onde fica o piso do tabuleiro dentro da imagem da arena (medido no arena.jpg quadrado):
    // começa a 13,08% da borda e ocupa 73,75% do lado. Se trocar a imagem, ajuste estes dois.
    private static final double ARENA_INICIO = 0.1308;
    private static final double ARENA_LADO = 0.7375;

    // Quando a imagem da arena não existe, desenhamos casas simples ocupando quase tudo
    private static final double RESERVA_INICIO = 0.05;
    private static final double RESERVA_LADO = 0.90;

    // Quando dois robôs dividem a casa, cada um fica menor e deslocado para o seu lado
    private static final double ESCALA_ROBOS_JUNTOS = 0.8;
    private static final double AFASTAMENTO_ROBOS_JUNTOS = 0.30;   // fração do lado da casa

    /** Uma peça colocada no tabuleiro e a casa lógica em que ela está. */
    private static class Item {
        final PecaVisual peca;
        Camada camada;
        int x;
        int y;
        final double escala;   // tamanho da caixa da peça em relação ao lado da casa

        Item(PecaVisual peca, Camada camada, int x, int y, double escala) {
            this.peca = peca;
            this.camada = camada;
            this.x = x;
            this.y = y;
            this.escala = escala;
        }
    }

    private final int grade;                       // casas por lado (4)
    private final ImageView arena;                 // null se a imagem não existe
    private final Rectangle[][] casasReserva;      // só existe se a arena não existe
    private final Group camadaObjetos = new Group();
    private final Group camadaRobos = new Group();
    private final Group camadaEfeitos = new Group();
    private final List<Item> itens = new ArrayList<>();

    // Geometria atual, recalculada em layoutChildren()
    private double ladoCelula = 0;
    private double origemX = 0;   // canto superior esquerdo da casa de cima/esquerda
    private double origemY = 0;

    private final double inicioFracao;
    private final double ladoFracao;

    public TabuleiroView(int grade) {

        this.grade = grade;

        SpriteSheet folhaArena = SpriteSheet.carregar(Sprites.ARENA, 1, 1);

        if (folhaArena != null) {
            arena = new ImageView(folhaArena.getImagem());
            arena.setSmooth(true);
            casasReserva = null;
            inicioFracao = ARENA_INICIO;
            ladoFracao = ARENA_LADO;
            getChildren().add(arena);
        } else {
            // FALLBACK: tabuleiro xadrez simples
            arena = null;
            casasReserva = new Rectangle[grade][grade];
            inicioFracao = RESERVA_INICIO;
            ladoFracao = RESERVA_LADO;

            for (int x = 0; x < grade; x++) {
                for (int y = 0; y < grade; y++) {
                    Rectangle casa = new Rectangle();
                    casa.setFill((x + y) % 2 == 0 ? Color.web("#e8e2d0") : Color.web("#d6cdb2"));
                    casasReserva[x][y] = casa;
                    getChildren().add(casa);
                }
            }
        }

        getChildren().addAll(camadaObjetos, camadaRobos, camadaEfeitos);

        // As peças são só enfeite visual: não devem capturar cliques do mouse
        camadaObjetos.setMouseTransparent(true);
        camadaRobos.setMouseTransparent(true);
        camadaEfeitos.setMouseTransparent(true);

        // Tamanho inicial e mínimo; o BorderPane/StackPane que contém esta view a estica depois
        setPrefSize(640, 640);
        setMinSize(240, 240);

        // Recorta o que ultrapassar a área (por exemplo, explosões perto da borda da janela)
        Rectangle recorte = new Rectangle();
        recorte.widthProperty().bind(widthProperty());
        recorte.heightProperty().bind(heightProperty());
        setClip(recorte);
    }

    // ---------- Gerenciamento das peças ----------

    /**
     * Coloca uma peça na casa (x, y).
     *
     * @param escala tamanho da caixa da peça em relação ao lado da casa
     *               (1.0 = do tamanho da casa; 0.6 = 60% da casa)
     */
    public void adicionar(PecaVisual peca, Camada camada, int x, int y, double escala) {

        itens.add(new Item(peca, camada, x, y, escala));
        camadaDe(camada).getChildren().add(peca.getNo());
        posicionarTodos();
    }

    /** Muda a casa lógica de uma peça e a reposiciona (a animação de deslizar é feita pelo sprite). */
    public void mover(PecaVisual peca, int x, int y) {

        Item item = itemDe(peca);

        if (item != null) {
            item.x = x;
            item.y = y;
            posicionarTodos();
        }
    }

    /** Tira a peça do tabuleiro (usado quando o Saibaman termina de explodir). */
    public void remover(PecaVisual peca) {

        Item item = itemDe(peca);

        if (item != null) {
            itens.remove(item);
            camadaDe(item.camada).getChildren().remove(peca.getNo());
            posicionarTodos();
        }
    }

    /** Leva uma peça para a camada de efeitos, para ser desenhada por cima dos robôs. */
    public void trazerParaEfeitos(PecaVisual peca) {

        Item item = itemDe(peca);

        if (item != null) {
            item.camada = Camada.EFEITOS;
            // adicionar a um novo pai remove automaticamente do pai antigo
            camadaEfeitos.getChildren().add(peca.getNo());
            posicionarTodos();
        }
    }

    /** Lado de uma casa em pixels (usado pelo controller para calcular o quanto o sprite desliza). */
    public double getLadoCelula() {
        return ladoCelula;
    }

    public void pausarTudo() {
        for (Item item : itens) {
            item.peca.pausar();
        }
    }

    public void continuarTudo() {
        for (Item item : itens) {
            item.peca.continuar();
        }
    }

    public void pararTudo() {
        for (Item item : itens) {
            item.peca.parar();
        }
    }

    // ---------- Layout ----------

    /** Chamado pelo JavaFX sempre que a view muda de tamanho: recalcula tudo. */
    @Override
    protected void layoutChildren() {

        double largura = getWidth();
        double altura = getHeight();
        double lado = Math.min(largura, altura);          // o tabuleiro é um quadrado
        double deslocX = (largura - lado) / 2;            // centraliza no espaço que sobrar
        double deslocY = (altura - lado) / 2;

        if (arena != null) {
            arena.setFitWidth(lado);
            arena.setFitHeight(lado);
            arena.setLayoutX(deslocX);
            arena.setLayoutY(deslocY);
        }

        origemX = deslocX + inicioFracao * lado;
        origemY = deslocY + inicioFracao * lado;
        ladoCelula = ladoFracao * lado / grade;

        if (casasReserva != null) {
            for (int x = 0; x < grade; x++) {
                for (int y = 0; y < grade; y++) {
                    Rectangle casa = casasReserva[x][y];
                    casa.setX(origemX + x * ladoCelula);
                    casa.setY(origemY + (grade - 1 - y) * ladoCelula);   // y do modelo cresce para cima
                    casa.setWidth(ladoCelula);
                    casa.setHeight(ladoCelula);
                }
            }
        }

        posicionarTodos();
    }

    /** Calcula e aplica a posição e o tamanho de todas as peças. */
    private void posicionarTodos() {

        if (ladoCelula <= 0) {
            return;   // ainda não houve o primeiro layout
        }

        for (Item item : itens) {

            double escala = item.escala;
            double afastamento = 0;

            // Se há mais de um robô na mesma casa, cada um fica menor e de um lado
            if (item.camada == Camada.ROBOS) {

                List<Item> companheiros = robosNaCasa(item.x, item.y);

                if (companheiros.size() > 1) {
                    int posicao = companheiros.indexOf(item);
                    escala = item.escala * ESCALA_ROBOS_JUNTOS;
                    // posições simétricas em torno do centro: ..., -1, 0, +1, ... (metade)
                    afastamento = (posicao - (companheiros.size() - 1) / 2.0)
                            * ladoCelula * AFASTAMENTO_ROBOS_JUNTOS;
                }
            }

            double lado = ladoCelula * escala;
            double centroX = origemX + (item.x + 0.5) * ladoCelula + afastamento;
            double centroY = origemY + (grade - 1 - item.y + 0.5) * ladoCelula;   // y do modelo cresce para cima

            item.peca.setTamanho(lado);
            item.peca.getNo().setLayoutX(centroX - lado / 2);
            item.peca.getNo().setLayoutY(centroY - lado / 2);
        }
    }

    // ---------- Apoio ----------

    private List<Item> robosNaCasa(int x, int y) {

        List<Item> lista = new ArrayList<>();

        for (Item item : itens) {
            if (item.camada == Camada.ROBOS && item.x == x && item.y == y) {
                lista.add(item);
            }
        }

        return lista;
    }

    private Item itemDe(PecaVisual peca) {

        for (Item item : itens) {
            if (item.peca == peca) {
                return item;
            }
        }

        return null;
    }

    private Group camadaDe(Camada camada) {

        switch (camada) {
            case OBJETOS:
                return camadaObjetos;
            case ROBOS:
                return camadaRobos;
            default:
                return camadaEfeitos;
        }
    }
}
