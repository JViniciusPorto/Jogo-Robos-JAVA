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
        boolean estaNaLista = false;
        do{
            movimento = listaMovimentos[sortear.nextInt(4)];
            for (String historico : movimentosInvalidos) {
                if(historico.equals(movimento)){
                    estaNaLista = true;
                    break;
                }
            }
        }while(estaNaLista);

        try {
            mover(movimento);
        } catch (MovimentoInvalidoException e) {
            movimentosInvalidos.add(movimento);
            if(movimentosInvalidos.size()==4){
                movimentosInvalidos.remove(0);
                movimentosInvalidos.remove(1);
                movimentosInvalidos.remove(2);
            }
            throw e;
        }
    }
}