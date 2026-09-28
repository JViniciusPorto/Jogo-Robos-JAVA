package mains;
import classes_robos.Robo;
import classes_robos.RoboInteligente;
import classes_obstaculos.ObstaculoBomba;
import classes_obstaculos.ObstaculoRocha;
import excecoes.MovimentoInvalidoException;
import java.util.Scanner;
import org.xml.sax.SAXException;

public class PrimeiraMain {
        public static void desenharTabuleiro(Robo robozinho, int xAlimento, int yAlimento){
            for(int y = 3; y >= 0 ; y--){
                for(int x = 0; x < 4; x++){
                    if(robozinho.getX() == x && robozinho.getY() == y){
                        System.out.print("R");
                    } else {
                        if(xAlimento == x && yAlimento == y){
                            System.out.print("A");
                        } else{
                            System.out.print(".");
                        }
                    }
                }
                System.out.println();
            }
        }

        public static void main(String[] args) {
            java.util.Scanner scanner = new Scanner(System.in);
            Robo r = new Robo("azul");
            
            int posicaoX;
            do{
                System.out.println("Qual é a posição x do alimento de (0 a 3)?");
                posicaoX = scanner.nextInt();

            } while(posicaoX < 0 || posicaoX > 3);

            int posicaoY;
            do{
                System.out.println("Qual é a posição y do alimento de (0 a 3)?");
                posicaoY = scanner.nextInt();

            } while(posicaoY < 0 || posicaoY > 3);

            while(!r.isEncontrouAlimento(posicaoX, posicaoY)){
                desenharTabuleiro(r, posicaoX, posicaoY);
                System.out.println("Qual movimento você quer fazer? (up, down, right,left");
                String comando = scanner.next();
                try{
                    r.mover(comando);
                } catch(MovimentoInvalidoException e){
                    System.out.println(e.getMessage());

                }


            }

            desenharTabuleiro(r, posicaoX, posicaoY);
            System.out.println("O Robo : " + r.getCor() + " encontrou o alimento");

        }
    
}