package mains;
import classes_robos.Robo;
import excecoes.MovimentoInvalidoException;
import utilidades.*;
public class PrimeiraMain {
        public static void main(String[] args) {
            LeituraTeclado teclado = new LeituraTeclado();
            Robo r = new Robo("azul");
            Desenho desenhar = new Desenho();
            int posicaoX;
            do{
                System.out.println("Qual é a posição x do alimento de (0 a 3)?");
                posicaoX = teclado.lerInteiro();

            } while(posicaoX < 0 || posicaoX > 3);

            int posicaoY;
            do{
                System.out.println("Qual é a posição y do alimento de (0 a 3)?");
                posicaoY = teclado.lerInteiro();

            } while(posicaoY < 0 || posicaoY > 3);

            while(!r.isEncontrouAlimento(posicaoX, posicaoY)){
                desenhar.desenharTabuleiro(r, posicaoX, posicaoY);
                System.out.println("Qual movimento você quer fazer? (up/1, down/2, right/3,left/4)");
                if(teclado.isInteiro()){
                    int comandoInteiro = teclado.lerInteiro();
                    try{
                        r.mover(comandoInteiro);
                    } catch(MovimentoInvalidoException e){
                        System.out.println(e.getMessage());
                    }
                }else{
                   String comandoString = teclado.lerString(); 
                   try{
                        r.mover(comandoString);
                    } catch(MovimentoInvalidoException e){
                        System.out.println(e.getMessage());

                    }
                }


            }

            desenhar.desenharTabuleiro(r, posicaoX, posicaoY);
            System.out.println("O Robô " + r.getCor() + " encontrou o alimento!");

        }
    
}