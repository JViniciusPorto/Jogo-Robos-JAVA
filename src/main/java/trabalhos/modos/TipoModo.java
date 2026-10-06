package trabalhos.modos;

import trabalhos.classes_robos.Robo;
import trabalhos.classes_robos.RoboInteligente;
import trabalhos.tabuleiro.Tabuleiro;

/**
 * Os quatro modos do jogo, cada um correspondendo a uma das Mains originais:
 *
 *   SOLO              -> PrimeiraMain (um robô controlado pelo jogador)
 *   DOIS_ROBOS        -> SegundaMain  (dois Robo normais, movimento aleatório)
 *   ROBO_INTELIGENTE  -> TerceiraMain (um Robo normal contra um RoboInteligente)
 *   ARENA             -> QuartaMain   (como a terceira, com bombas e rochas)
 *
 * Cada constante guarda os textos do menu e quais campos a configuração precisa mostrar.
 * O método {@link #criar} funciona como uma "fábrica": monta o modelo certo para o modo.
 */
public enum TipoModo {

    SOLO("ROBÔ SOLO", "Controle seu próprio robô até o alimento", 1, false),
    DOIS_ROBOS("DOIS ROBÔS", "Dois robôs se movimentam automaticamente", 2, false),
    ROBO_INTELIGENTE("ROBÔ INTELIGENTE", "Enfrente o robô inteligente", 2, false),
    ARENA("ARENA", "Robôs + bombas + rochas", 2, true);

    private final String titulo;
    private final String descricao;
    private final int quantidadeRobos;
    private final boolean temObstaculos;

    TipoModo(String titulo, String descricao, int quantidadeRobos, boolean temObstaculos) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.quantidadeRobos = quantidadeRobos;
        this.temObstaculos = temObstaculos;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    /** Quantos robôs participam (a tela de configuração esconde o campo do 2º nome no modo solo). */
    public int getQuantidadeRobos() {
        return quantidadeRobos;
    }

    /** true se o modo usa bombas e rochas (só a Arena). */
    public boolean temObstaculos() {
        return temObstaculos;
    }

    /**
     * Monta o tabuleiro e os robôs do modo escolhido a partir da configuração.
     * Os robôs são as classes ORIGINAIS (Robo e RoboInteligente), sem nenhuma alteração.
     */
    public ModoDeJogo criar(ConfiguracaoJogo config) {

        Tabuleiro tabuleiro = new Tabuleiro(config.alimentoX(), config.alimentoY());

        switch (this) {

            case SOLO:
                return new ModoSolo(tabuleiro, new Robo(config.nomeRobo1()));

            case DOIS_ROBOS:
                return new ModoDoisRobos(tabuleiro,
                        new Robo(config.nomeRobo1()),
                        new Robo(config.nomeRobo2()));

            case ROBO_INTELIGENTE:
                // como na TerceiraMain: o primeiro é normal e o segundo é o inteligente
                return new ModoRoboInteligente(tabuleiro,
                        new Robo(config.nomeRobo1()),
                        new RoboInteligente(config.nomeRobo2()));

            default:   // ARENA
                // bombas e rochas são colocadas ANTES de o jogo começar, como na QuartaMain
                tabuleiro.adicionarBombas(config.bombas());
                tabuleiro.adicionarRochas(config.rochas());
                return new ModoArena(tabuleiro,
                        new Robo(config.nomeRobo1()),
                        new RoboInteligente(config.nomeRobo2()));
        }
    }
}
