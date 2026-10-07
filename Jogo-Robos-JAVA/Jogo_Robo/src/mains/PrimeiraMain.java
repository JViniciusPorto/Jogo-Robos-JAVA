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
            int posicaoY;
            do{
                do{
                    System.out.println("Qual é a posição x do alimento de (0 a 3)?");
                    posicaoX = teclado.lerInteiro();
                    if(posicaoX<0 || posicaoX>3){
                        System.out.println("Digite um inteiro entre [0,3]!");
                    }
                } while(posicaoX < 0 || posicaoX > 3);

                
                do{
                    System.out.println("Qual é a posição y do alimento de (0 a 3)?");
                    posicaoY = teclado.lerInteiro();
                    if(posicaoY<0 || posicaoY>3){
                        System.out.println("Digite um inteiro entre [0,3]!");
                    }
                } while(posicaoY < 0 || posicaoY > 3);
                if(posicaoX==0 && posicaoY==0){
                    System.out.println("Você não pode colocar o alimento em (0,0)!\n");
                }
            }while(posicaoX==0 && posicaoY==0);

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