package utilidades;
import java.util.Arrays;
import java.util.List;

import classes_obstaculos.Obstaculo;
import classes_obstaculos.ObstaculoBomba;
import classes_robos.*;
public class Desenho {

    public void desenharTabuleiro(Robo robozinho, int xAlimento, int yAlimento){
        for(int y = 3; y >= 0 ; y--){
            for(int x = 0; x < 4; x++){
                if(robozinho.getX() == x && robozinho.getY() == y){
                    System.out.print("R   ");
                } else {
                    if(xAlimento == x && yAlimento == y){
                            System.out.print("A   ");
                    } else{
                        System.out.print(".   ");
                    }
                }
            }
            System.out.println();
        }
    }

    public void desenharTabuleiro(Robo robo1, Robo robo2, int xAlimento, int yAlimento){
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


    public void desenharTabuleiro(Robo robo1, Robo robo2, List<Obstaculo> obstaculos, int xAlimento, int yAlimento){
        String[][] grade = new String[4][4]; //[y][x]
        for(String linha[] : grade){
            Arrays.fill(linha, ".");
        }

        for(Obstaculo obstaculo : obstaculos){
            if(obstaculo instanceof ObstaculoBomba){
                grade[obstaculo.getY()][obstaculo.getX()] = "B";
            } else {
                grade[obstaculo.getY()][obstaculo.getX()] = "P";
            }
        }

        grade[yAlimento][xAlimento] = "A";

        String simboloR1 = simboloDoRobo(robo1, "R1", xAlimento, yAlimento);
        String simboloR2 = simboloDoRobo(robo2, "R2", xAlimento, yAlimento);

        if(robo1.getX() == robo2.getX() && robo1.getY() == robo2.getY()){
            grade[robo1.getY()][robo1.getX()] = combinarSimbolos(simboloR1, simboloR2);
        } else {
            grade[robo1.getY()][robo1.getX()] = simboloR1;
            grade[robo2.getY()][robo2.getX()] = simboloR2;
        }

        for(int y = 3; y >= 0 ; y--){
            for(int x = 0; x < 4; x++){
                System.out.printf("%-6s", grade[y][x]);
            }
            System.out.println();
        }

        System.out.println();
    }

    private String simboloDoRobo(Robo robo, String rotulo, int xAlimento, int yAlimento){
        boolean explodiu = !robo.getIsPodeMover() && !robo.isEncontrouAlimento(xAlimento, yAlimento);
        return explodiu ? "X" : rotulo;
    }

    private String combinarSimbolos(String simboloR1, String simboloR2){
        boolean r1Explodiu = simboloR1.equals("X");
        boolean r2Explodiu = simboloR2.equals("X");

        if(r1Explodiu && r2Explodiu){
            return "X"; // os dois morreram na mesma célula
        } else if(r1Explodiu){
            return simboloR2; // só o vivo aparece
        } else if(r2Explodiu){
            return simboloR1; // só o vivo aparece
        } else {
            return "R1R2"; // os dois vivos, mesma célula
        }
    }
}