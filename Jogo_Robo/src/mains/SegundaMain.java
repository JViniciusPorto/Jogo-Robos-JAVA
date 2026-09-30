package mains;

import classes_robos.*;
import excecoes.MovimentoInvalidoException;
import utilidades.*;

public class SegundaMain {

    public static void main(String[] args) {
        LeituraTeclado teclado = new LeituraTeclado();
        Desenho desenhar = new Desenho();
        Robo R1 = new Robo("azul");
        Robo R2 = new Robo("vermelho");
        Tempo esperar = new Tempo();
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

        desenhar.desenharTabuleiro(R1, R2, posicaoX, posicaoY);

        Robo roboDaVez = R1;
        Robo outroRobo = R2;

        while(!roboDaVez.isEncontrouAlimento(posicaoX, posicaoY)){
            esperar.aguardar(800);

            try{
                roboDaVez.mover();
            } catch(MovimentoInvalidoException e){
                System.out.println(e.getMessage());
            }

            desenhar.desenharTabuleiro(R1, R2, posicaoX, posicaoY);

            if(!roboDaVez.isEncontrouAlimento(posicaoX, posicaoY)){
                Robo trocaTemp = roboDaVez;
                roboDaVez = outroRobo;
                outroRobo = trocaTemp;
            }
        }

        System.out.println("O Robo : " + roboDaVez.getCor() + " encontrou o alimento");
        System.out.println();
        System.out.println("----------------- Movimentos -----------------");
        System.out.println("Robô " + R1.getCor() + " - válidos: " + R1.getMovimentosValido()
                + " | inválidos: " + R1.getMovimentosInvalido());
        System.out.println("Robô " + R2.getCor() + " - válidos: " + R2.getMovimentosValido()
                + " | inválidos: " + R2.getMovimentosInvalido());
    }

}
