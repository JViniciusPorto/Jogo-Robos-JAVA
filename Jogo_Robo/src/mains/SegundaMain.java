package mains;

import classes_robos.*;
import excecoes.MovimentoInvalidoException;
import java.util.Arrays;
import java.util.Scanner;

public class SegundaMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Robo R1 = new Robo("azul");
        Robo R2 = new Robo("vermelho");

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

        desenharTabuleiro(R1, R2, posicaoX, posicaoY);

        Robo roboDaVez = R1;
        Robo outroRobo = R2;

        while(!roboDaVez.isEncontrouAlimento(posicaoX, posicaoY)){
            aguardar(800);

            try{
                roboDaVez.mover();
            } catch(MovimentoInvalidoException e){
                System.out.println(e.getMessage());
            }

            desenharTabuleiro(R1, R2, posicaoX, posicaoY);

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
    
    public static void desenharTabuleiro(Robo robo1, Robo robo2, int xAlimento, int yAlimento){
        String[][] grade = new String[4][4]; // grade[y][x]
        for (String[] linha : grade) {
            Arrays.fill(linha, ".");
        }

        grade[yAlimento][xAlimento] = "A";

        if (robo1.getX() == robo2.getX() && robo1.getY() == robo2.getY()) {
            grade[robo1.getY()][robo1.getX()] = "R1R2";
        } else {
            grade[robo1.getY()][robo1.getX()] = "R1"; // sobrescreve o "A" se coincidir
            grade[robo2.getY()][robo2.getX()] = "R2";
        }

        for(int y = 3; y >= 0 ; y--){
            for(int x = 0; x < 4; x++){
                System.out.printf("%-6s", grade[y][x]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void aguardar(int millisegundos) {
        try {
            Thread.sleep(millisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
