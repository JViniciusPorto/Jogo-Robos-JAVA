package mains;
import classes_robos.*;
import excecoes.*;
import utilidades.*;
import java.util.List;
import java.util.ArrayList;
public class TerceiraMain {
    public static void main(String[] args) {
        Robo roboNormal = new Robo("Vermelho");
        Robo roboInteligente = new RoboInteligente("Azul");
        Desenho desenhar = new Desenho();
        LeituraTeclado teclado = new LeituraTeclado();
        Tempo esperar = new Tempo();
        List<Robo> robos = new ArrayList<>();
        robos.add(roboNormal);
        robos.add(roboInteligente);
        int vezDe = 0;
        int posicaoX;
        int posicaoY;
        boolean jogoAtivo = true;
        do{
            System.out.println("Digite a posição x do alimento (0 a 3):");
            posicaoX = teclado.lerInteiro();
        }while(posicaoX<0 || posicaoX>3);

        do{
            System.out.println("Digite a posição y do alimento (0 a 3):");
            posicaoY = teclado.lerInteiro();
        }while(posicaoY<0 || posicaoY>3);
        desenhar.desenharTabuleiro(roboNormal, roboInteligente, posicaoX, posicaoY);
        while(jogoAtivo){
            esperar.aguardar(800);

            if(robos.get(vezDe).getIsPodeMover()){
                try{
                    robos.get(vezDe).mover();
                }catch(MovimentoInvalidoException e){
                    System.out.println(e.getMessage());
                }
                desenhar.desenharTabuleiro(roboNormal, roboInteligente, posicaoX, posicaoY);
            }
            if(robos.get(vezDe).isEncontrouAlimento(posicaoX, posicaoY)){
                robos.get(vezDe).setIsPodeMover(false);
                System.out.println("Robô "+robos.get(vezDe).getCor()+" encontrou o alimento!");
            }
            vezDe = (vezDe +1)%robos.size();
            jogoAtivo = false;
            for (Robo robo : robos) {
                if(robo.getIsPodeMover()){
                    jogoAtivo = true;
                    break;
                }
            }
        }

        System.out.println("----------------- Movimentos -----------------");
        System.out.println("Robô "+roboNormal.getCor()+": "+(roboNormal.getMovimentosInvalido()+roboNormal.getMovimentosValido()));
        System.out.println("Robô "+roboInteligente.getCor()+": "+(roboInteligente.getMovimentosInvalido()+roboInteligente.getMovimentosValido()));

    }
}
