package classes_robos;

import java.util.Random;
import excecoes.MovimentoInvalidoException;

public class RoboInteligente extends Robo {
    private String ultimoMovimentoInvalido = null;

    public RoboInteligente(String cor) {
        super(cor);
    }

    @Override
    public void mover() throws MovimentoInvalidoException {
        String[] listaMovimentos = {"up","down","right","left"};
        Random sortear = new Random();
        String movimento;
        do{
            movimento = listaMovimentos[sortear.nextInt(4)];
        }while(movimento.equals(ultimoMovimentoInvalido));

        try {
            mover(movimento);
            ultimoMovimentoInvalido = null;
        } catch (MovimentoInvalidoException e) {
            ultimoMovimentoInvalido = movimento;
            throw e;
        }
    }
}