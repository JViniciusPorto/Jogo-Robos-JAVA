package classes_robos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import excecoes.MovimentoInvalidoException;

public class RoboInteligente extends Robo {
    public RoboInteligente(String cor) {
        super(cor);
    }

    @Override
    public void mover(int comando) throws MovimentoInvalidoException {
        List<String> disponiveis = new ArrayList<>(List.of("up", "down", "right", "left"));
        Random random = new Random();

        while (!disponiveis.isEmpty()) {
            String movimento = disponiveis.get(random.nextInt(disponiveis.size()));

            try {
                mover(movimento);
                return; // movimento válido, terminou a jogada
            } catch (MovimentoInvalidoException e) {
                disponiveis.remove(movimento); // não sorteia esse de novo
            }
        }
    }
}