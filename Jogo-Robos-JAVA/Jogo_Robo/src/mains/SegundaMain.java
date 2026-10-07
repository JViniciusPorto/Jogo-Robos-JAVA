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

        System.out.println("==============Legenda==============");
        System.out.println("R1 - "+R1.getCor());
        System.out.println("R2 - "+R2.getCor());
        System.out.println("===================================");
        esperar.aguardar(1500);
        System.out.println("Início da Partida:");
        desenhar.desenharTabuleiro(R1, R2, posicaoX, posicaoY);

        Robo roboDaVez = R1;
        Robo outroRobo = R2;

        while(!roboDaVez.isEncontrouAlimento(posicaoX, posicaoY)){
            esperar.aguardar(800);
            System.out.println("Vez do "+roboDaVez.getCor()+"!");
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

        System.out.println("O Robô " + roboDaVez.getCor() + " encontrou o alimento!");
        System.out.println();
        System.out.println("----------------- Movimentos -----------------");
        System.out.println("Robô " + R1.getCor() + " - válidos: " + R1.getMovimentosValido()
                + " | inválidos: " + R1.getMovimentosInvalido());
        System.out.println("Robô " + R2.getCor() + " - válidos: " + R2.getMovimentosValido()
                + " | inválidos: " + R2.getMovimentosInvalido());
    }

}