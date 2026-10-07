package classes_robos;
import java.util.ArrayList;
import java.util.Random;
import excecoes.MovimentoInvalidoException;

public class RoboInteligente extends Robo {
    private ArrayList<String> movimentosInvalidos = new ArrayList<>(); 

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
        }while(movimentosInvalidos.contains(movimento));

        try {
            mover(movimento);
            movimentosInvalidos.clear();
        } catch (MovimentoInvalidoException e) {
            movimentosInvalidos.add(movimento);
            throw e;
        }
    }
}