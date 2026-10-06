package trabalhos.tabuleiro;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import trabalhos.classes_obstaculos.Obstaculo;
import trabalhos.classes_obstaculos.ObstaculoBomba;
import trabalhos.classes_obstaculos.ObstaculoRocha;

/**
 * MODELO do tabuleiro: uma grade fixa de 4x4 casas (coordenadas 0 a 3 em x e y).
 *
 * Esta classe guarda apenas DADOS DO JOGO: onde está o alimento e onde estão os
 * obstáculos. Ela não sabe nada de JavaFX, sprites ou animações.
 *
 * Decisões de projeto:
 *  - O alimento é escolhido pelo jogador (como nas Mains originais), por isso o
 *    construtor recebe a posição dele.
 *  - Bombas e rochas são sorteadas em casas livres (a tela de configuração só
 *    pergunta a QUANTIDADE, como pedido).
 *  - As rochas nunca isolam o alimento: o sorteio refaz a posição de uma rocha se
 *    ela deixasse o alimento inalcançável. Assim o jogo sempre pode terminar.
 */
public class Tabuleiro {

    // Lado do tabuleiro: a área de locomoção é um quadrado de 4 unidades (enunciado do trabalho)
    private static final int TAMANHO = 4;

    // Quantas posições diferentes tentamos para uma rocha antes de desistir dela
    private static final int MAX_TENTATIVAS_ROCHA = 50;

    // obstaculos[x][y] guarda o obstáculo da casa (x,y), ou null se a casa está vazia
    private final Obstaculo[][] obstaculos = new Obstaculo[TAMANHO][TAMANHO];

    private final int alimentoX;
    private final int alimentoY;

    private final Random random = new Random();

    /**
     * Cria o tabuleiro com o alimento na posição escolhida pelo jogador.
     *
     * @throws IllegalArgumentException se a posição estiver fora de 0..3
     */
    public Tabuleiro(int alimentoX, int alimentoY) {

        if (!dentroDoTabuleiro(alimentoX, alimentoY)) {
            throw new IllegalArgumentException("O alimento precisa estar entre 0 e 3 nos dois eixos.");
        }

        this.alimentoX = alimentoX;
        this.alimentoY = alimentoY;
    }

    // ---------- Colocação de obstáculos ----------

    /** Coloca a quantidade pedida de bombas em casas livres sorteadas. */
    public void adicionarBombas(int quantidade) {

        for (int i = 0; i < quantidade; i++) {

            int[] posicao = sortearPosicaoLivre();

            // sem casas livres: não há mais onde colocar
            if (posicao == null) {
                return;
            }

            obstaculos[posicao[0]][posicao[1]] = new ObstaculoBomba(posicao[0], posicao[1]);
        }
    }

    /**
     * Coloca a quantidade pedida de rochas em casas livres sorteadas,
     * garantindo que o alimento continue alcançável a partir de (0,0).
     */
    public void adicionarRochas(int quantidade) {

        for (int i = 0; i < quantidade; i++) {

            // tenta algumas posições até achar uma que não isole o alimento
            for (int tentativa = 0; tentativa < MAX_TENTATIVAS_ROCHA; tentativa++) {

                int[] posicao = sortearPosicaoLivre();

                if (posicao == null) {
                    return;
                }

                obstaculos[posicao[0]][posicao[1]] = new ObstaculoRocha(posicao[0], posicao[1]);

                if (alimentoAlcancavel()) {
                    break;   // posição boa, passa para a próxima rocha
                }

                // essa rocha fecharia o caminho: desfaz e sorteia outra posição
                obstaculos[posicao[0]][posicao[1]] = null;
            }
        }
    }

    /**
     * Remove um obstáculo do tabuleiro. Usado quando uma bomba explode:
     * o enunciado diz que "ao explodir a bomba desaparece do tabuleiro".
     */
    public void removerObstaculo(Obstaculo obstaculo) {

        if (obstaculos[obstaculo.getX()][obstaculo.getY()] == obstaculo) {
            obstaculos[obstaculo.getX()][obstaculo.getY()] = null;
        }
    }

    // ---------- Consultas ----------

    /** Obstáculo da casa (x,y), ou null se a casa está vazia. */
    public Obstaculo getObstaculo(int x, int y) {
        return obstaculos[x][y];
    }

    /** Lista com todos os obstáculos atuais (a camada visual usa isto para criar os sprites). */
    public List<Obstaculo> getObstaculos() {

        List<Obstaculo> lista = new ArrayList<>();

        for (int x = 0; x < TAMANHO; x++) {
            for (int y = 0; y < TAMANHO; y++) {
                if (obstaculos[x][y] != null) {
                    lista.add(obstaculos[x][y]);
                }
            }
        }

        return lista;
    }

    public int getTamanho() {
        return TAMANHO;
    }

    public int getAlimentoX() {
        return alimentoX;
    }

    public int getAlimentoY() {
        return alimentoY;
    }

    // ---------- Apoio interno ----------

    private boolean dentroDoTabuleiro(int x, int y) {
        return x >= 0 && x < TAMANHO && y >= 0 && y < TAMANHO;
    }

    /**
     * Sorteia uma casa livre. Uma casa é livre se não for a largada dos robôs (0,0),
     * não for a casa do alimento e não tiver obstáculo. Retorna null se não houver nenhuma.
     * (Montamos a lista de candidatas em vez de sortear às cegas para nunca entrar em laço infinito.)
     */
    private int[] sortearPosicaoLivre() {

        List<int[]> livres = new ArrayList<>();

        for (int x = 0; x < TAMANHO; x++) {
            for (int y = 0; y < TAMANHO; y++) {

                boolean largada = (x == 0 && y == 0);
                boolean alimento = (x == alimentoX && y == alimentoY);

                if (!largada && !alimento && obstaculos[x][y] == null) {
                    livres.add(new int[]{x, y});
                }
            }
        }

        if (livres.isEmpty()) {
            return null;
        }

        return livres.get(random.nextInt(livres.size()));
    }

    /**
     * Busca em largura a partir de (0,0) andando pelas 4 direções e SEM atravessar rochas.
     * Bombas contam como passáveis (elas matam o robô, mas não bloqueiam o caminho).
     * Retorna true se o alimento puder ser alcançado.
     */
    private boolean alimentoAlcancavel() {

        boolean[][] visitado = new boolean[TAMANHO][TAMANHO];
        ArrayDeque<int[]> fila = new ArrayDeque<>();

        fila.add(new int[]{0, 0});
        visitado[0][0] = true;

        int[][] passos = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        while (!fila.isEmpty()) {

            int[] atual = fila.poll();

            if (atual[0] == alimentoX && atual[1] == alimentoY) {
                return true;
            }

            for (int[] passo : passos) {

                int nx = atual[0] + passo[0];
                int ny = atual[1] + passo[1];

                if (dentroDoTabuleiro(nx, ny)
                        && !visitado[nx][ny]
                        && !(obstaculos[nx][ny] instanceof ObstaculoRocha)) {

                    visitado[nx][ny] = true;
                    fila.add(new int[]{nx, ny});
                }
            }
        }

        return false;
    }
}
