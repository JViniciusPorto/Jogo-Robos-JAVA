package utilidades;
import java.util.Arrays;
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
}
