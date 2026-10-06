package trabalhos.modos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import trabalhos.classes_obstaculos.Obstaculo;
import trabalhos.classes_obstaculos.ObstaculoBomba;
import trabalhos.classes_robos.Robo;
import trabalhos.excecoes.MovimentoInvalidoException;
import trabalhos.tabuleiro.Tabuleiro;

/**
 * LÓGICA COMUM aos quatro modos. Esta classe NÃO tem nada de JavaFX.
 *
 * O que é igual em todos os modos fica aqui, sem repetição:
 *   - guardar o tabuleiro e os robôs;
 *   - controlar de quem é a vez;
 *   - executar um movimento tratando a exceção e os obstáculos (código que antes
 *     estava dentro da QuartaMain);
 *   - guardar se o jogo acabou e a mensagem final.
 *
 * O que muda de um modo para outro é só a regra de FIM DE JOGO, e ela fica numa única
 * pergunta abstrata: {@link #concluirJogada}. (Padrão "método template".)
 */
public abstract class ModoDeJogo {

    /**
     * Um movimento qualquer que será aplicado a um robô: aleatório (robo.mover()) ou
     * comandado (robo.mover(int)). Existe para o método executar() servir aos dois casos.
     */
    protected interface Movimento {
        void executar(Robo robo) throws MovimentoInvalidoException;
    }

    private final TipoModo tipo;
    private final Tabuleiro tabuleiro;
    private final List<Robo> robos;

    private int indiceDaVez = 0;
    private boolean terminado = false;
    private String tituloFinal = "";
    private String mensagemFinal = "";

    protected ModoDeJogo(TipoModo tipo, Tabuleiro tabuleiro, Robo... robos) {
        this.tipo = tipo;
        this.tabuleiro = tabuleiro;
        this.robos = new ArrayList<>(Arrays.asList(robos));
    }

    // ---------- O que cada modo precisa definir ----------

    /** true se os robôs se movem sozinhos (modos 2, 3 e 4); false se o jogador comanda (modo 1). */
    public abstract boolean isAutomatico();

    /**
     * Regra de fim de jogo do modo. Chamada logo depois de cada movimento.
     * Aqui o modo decide se alguém venceu, se o jogo acabou e de quem é a próxima vez.
     *
     * @param robo      quem acabou de jogar
     * @param resultado o que aconteceu no movimento (ainda sem a informação do alimento)
     * @return o mesmo resultado, marcado com achouAlimento quando for o caso
     */
    protected abstract ResultadoJogada concluirJogada(Robo robo, ResultadoJogada resultado);

    // ---------- Jogada automática ----------

    /**
     * Faz o robô da vez se mover sozinho (Robo.mover() sem parâmetros: sorteia a direção).
     * Para o RoboInteligente isso chama a versão dele, que evita repetir o movimento inválido.
     */
    public final ResultadoJogada jogarProximaVez() {
        garantirJogoAtivo();
        Robo robo = getRoboDaVez();
        return concluirJogada(robo, executar(robo, r -> r.mover()));
    }

    // ---------- Núcleo compartilhado ----------

    /**
     * Aplica um movimento ao robô e resolve o que existe na casa de destino.
     * É o mesmo fluxo da QuartaMain original:
     *   1. tenta mover (a exceção vira "bateu na parede");
     *   2. procura um obstáculo na nova posição;
     *   3. chama obstaculo.bater(robo) (a bomba/rocha decide o efeito);
     *   4. se era uma bomba, ela é removida do tabuleiro.
     */
    protected ResultadoJogada executar(Robo robo, Movimento movimento) {

        int xAntes = robo.getX();
        int yAntes = robo.getY();

        try {
            movimento.executar(robo);

        } catch (MovimentoInvalidoException e) {
            // movimento recusado: o Robo não saiu do lugar e já contou o movimento inválido
            return new ResultadoJogada(robo, ResultadoJogada.Tipo.BATEU_NA_PAREDE,
                    xAntes, yAntes, xAntes, yAntes, false,
                    robo.getCor() + " bateu na parede (" + e.getMessage() + ")");
        }

        // onde o mover() deixou o robô (antes de bomba ou rocha agirem)
        int xDepois = robo.getX();
        int yDepois = robo.getY();

        Obstaculo obstaculo = tabuleiro.getObstaculo(xDepois, yDepois);

        if (obstaculo == null) {
            return new ResultadoJogada(robo, ResultadoJogada.Tipo.MOVEU,
                    xAntes, yAntes, xDepois, yDepois, false,
                    robo.getCor() + " foi para (" + xDepois + "," + yDepois + ")");
        }

        // quem decide o efeito é a classe do obstáculo (bomba desativa o robô, rocha o devolve)
        obstaculo.bater(robo);

        if (obstaculo instanceof ObstaculoBomba) {
            tabuleiro.removerObstaculo(obstaculo);   // "ao explodir a bomba desaparece do tabuleiro"
            return new ResultadoJogada(robo, ResultadoJogada.Tipo.EXPLODIU,
                    xAntes, yAntes, xDepois, yDepois, false,
                    robo.getCor() + " pisou numa bomba em (" + xDepois + "," + yDepois + ") e explodiu!");
        }

        return new ResultadoJogada(robo, ResultadoJogada.Tipo.BATEU_NA_ROCHA,
                xAntes, yAntes, xDepois, yDepois, false,
                robo.getCor() + " bateu numa rocha em (" + xDepois + "," + yDepois
                        + ") e voltou para (" + robo.getX() + "," + robo.getY() + ")");
    }

    /**
     * Passa a vez para o próximo robô que ainda pode se mover.
     * Robôs que explodiram (ou que já acharam o alimento, no modo 3) são pulados, o que
     * equivale ao "if (getIsPodeMover())" das Mains originais, só que sem esperar à toa.
     */
    protected void passarAVez() {

        for (int i = 0; i < robos.size(); i++) {

            indiceDaVez = (indiceDaVez + 1) % robos.size();

            if (robos.get(indiceDaVez).getIsPodeMover()) {
                return;
            }
        }
    }

    /** true se pelo menos um robô ainda consegue se mover. */
    protected boolean algumRoboPodeMover() {

        for (Robo robo : robos) {
            if (robo.getIsPodeMover()) {
                return true;
            }
        }

        return false;
    }

    /** Encerra o jogo guardando o texto que a interface vai mostrar. */
    protected void finalizar(String titulo, String mensagem) {
        this.terminado = true;
        this.tituloFinal = titulo;
        this.mensagemFinal = mensagem;
    }

    /** Protege contra jogar depois do fim (seria um erro de programação na interface). */
    protected void garantirJogoAtivo() {
        if (terminado) {
            throw new IllegalStateException("O jogo já terminou.");
        }
    }

    // ---------- Consultas para a interface ----------

    public TipoModo getTipo() {
        return tipo;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    /** Lista dos robôs, somente leitura (a interface não deve adicionar nem remover robôs). */
    public List<Robo> getRobos() {
        return Collections.unmodifiableList(robos);
    }

    /** Robô que fará a próxima jogada. */
    public Robo getRoboDaVez() {
        return robos.get(indiceDaVez);
    }

    public boolean isTerminado() {
        return terminado;
    }

    /** Título curto do resultado, por exemplo "GOKU VENCEU!" ou "EMPATE!". */
    public String getTituloFinal() {
        return tituloFinal;
    }

    /** Frase explicando como o jogo terminou. */
    public String getMensagemFinal() {
        return mensagemFinal;
    }

    /** Robô marcado como vencedor, ou null (empate ou jogo em andamento). */
    public Robo getVencedor() {

        for (Robo robo : robos) {
            if (robo.getIsVencedor()) {
                return robo;
            }
        }

        return null;
    }

    /**
     * Estatísticas de movimentos de cada robô, uma linha por robô.
     * Reúne o que a SegundaMain (válidos/inválidos) e a Terceira/Quarta (total) mostravam.
     */
    public List<String> getEstatisticas() {

        List<String> linhas = new ArrayList<>();

        for (Robo robo : robos) {
            int total = robo.getMovimentosValido() + robo.getMovimentosInvalido();
            linhas.add(robo.getCor() + ": " + total + " movimentos"
                    + " (válidos: " + robo.getMovimentosValido()
                    + " | inválidos: " + robo.getMovimentosInvalido() + ")");
        }

        return linhas;
    }

    /** true se o robô está sobre o alimento neste momento. */
    public final boolean estaNoAlimento(Robo robo) {
        return robo.isEncontrouAlimento(tabuleiro.getAlimentoX(), tabuleiro.getAlimentoY());
    }
}
