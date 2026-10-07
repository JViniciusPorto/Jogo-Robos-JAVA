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
            do{
                System.out.println("Digite a posição x do alimento (0 a 3):");
                posicaoX = teclado.lerInteiro();
                if(posicaoX<0 || posicaoX>3){
                    System.out.println("Digite um inteiro entre [0,3]!");
                }
            }while(posicaoX<0 || posicaoX>3);

            do{
                System.out.println("Digite a posição y do alimento (0 a 3):");
                posicaoY = teclado.lerInteiro();
                if(posicaoY<0 || posicaoY>3){
                    System.out.println("Digite um inteiro entre [0,3]!");
                }
            }while(posicaoY<0 || posicaoY>3);
            if(posicaoX==0 && posicaoY==0){
                System.out.println("Você não pode colocar o alimento em (0,0)!");
            }
        }while (posicaoX==0 && posicaoY==0);
        System.out.println("==============Legenda==============");
        System.out.println("R1 - Robô Normal - "+roboNormal.getCor());
        System.out.println("R2 - Robô Inteligente - "+roboInteligente.getCor());
        System.out.println("===================================");
        esperar.aguardar(1500);
        System.out.println("Início da Partida:");
        desenhar.desenharTabuleiro(roboNormal, roboInteligente, posicaoX, posicaoY);
        
        while(jogoAtivo){
            if(robos.get(vezDe).getIsPodeMover()){

                System.out.println("Vez do "+robos.get(vezDe).getCor()+"!");
                try{
                    robos.get(vezDe).mover();
                }catch(MovimentoInvalidoException e){
                    System.out.println(e.getMessage());
                }
                if(robos.get(vezDe).isEncontrouAlimento(posicaoX, posicaoY)){
                    robos.get(vezDe).setIsPodeMover(false);
                    System.out.println("Robô "+robos.get(vezDe).getCor()+" encontrou o alimento!");
                }
                desenhar.desenharTabuleiro(roboNormal, roboInteligente, posicaoX, posicaoY);
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
