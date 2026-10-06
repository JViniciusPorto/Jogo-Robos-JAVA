package trabalhos.modos;

/**
 * Tudo o que o jogador escolhe na tela de configuração, reunido num único objeto.
 *
 * Assim a tela de configuração e a tela do jogo conversam por um único parâmetro
 * (em vez de uma lista longa), e o botão "Novo Jogo" consegue recomeçar com as
 * mesmas escolhas apenas guardando este objeto.
 *
 * @param modo       qual dos quatro modos será jogado
 * @param nomeRobo1  nome do primeiro robô (Goku)
 * @param nomeRobo2  nome do segundo robô (Vegeta); ignorado no modo solo
 * @param alimentoX  coluna do alimento (0 a 3)
 * @param alimentoY  linha do alimento (0 a 3)
 * @param bombas     quantidade de bombas (só o modo Arena usa)
 * @param rochas     quantidade de rochas (só o modo Arena usa)
 */
public record ConfiguracaoJogo(
        TipoModo modo,
        String nomeRobo1,
        String nomeRobo2,
        int alimentoX,
        int alimentoY,
        int bombas,
        int rochas) {
}
