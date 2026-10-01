package mains;
import  classes_robos.*;
import classes_obstaculos.*;
import excecoes.*;
import utilidades.*;
import java.util.List;
import java.util.ArrayList;

public class QuartaMain {

    public static void main(String[] args) {
        Robo roboNormal = new Robo("Vermelho");
        Robo roboInteligente = new RoboInteligente("Azul");
        Desenho desenhar = new Desenho();
        LeituraTeclado teclado = new LeituraTeclado();
        Tempo esperar = new Tempo();
        List<Robo> robos = new ArrayList<>();
        robos.add(roboNormal);
        robos.add(roboInteligente);
        List<Obstaculo> obstaculos = new ArrayList<>();

        int posicaoX;
        do{
            System.out.println("Digite a posição x do alimento");
            posicaoX = teclado.lerInteiro();
        } while(posicaoX < 0 || posicaoX > 3);

        int posicaoY;
        do{
            System.out.println("Digite a posição y do alimento");
            posicaoY = teclado.lerInteiro();
        } while(posicaoY < 0 || posicaoY > 3);

        System.out.println("Quantas bombas você quer colocar no tabuleiro ?");
        int quantidadeBombas = teclado.lerInteiro();

        for(int i = 0; i < quantidadeBombas; i++){
            int xBomba = lerPosicaoObstaculo(teclado, "x", i+1, "bomba");
            int yBomba = lerPosicaoObstaculo(teclado, "y", i+1, "bomba");
            obstaculos.add(new ObstaculoBomba(xBomba, yBomba));
        }

        System.out.println("Quantas rochas você quer colocar no tabuleiro?");
        int quantidadeRochas = teclado.lerInteiro();
        for(int i = 0; i < quantidadeRochas; i++){
            int xRocha = lerPosicaoObstaculo(teclado, "x", i+1, "rocha");
            int yRocha = lerPosicaoObstaculo(teclado, "y", i+1, "rocha");
            obstaculos.add(new ObstaculoRocha(xRocha, yRocha));
        }

        desenhar.desenharTabuleiro(roboNormal, roboInteligente, obstaculos, posicaoX, posicaoY);

        int vezDe = 0;
        boolean alguemAchou = false;
        boolean ambosExplodiram = false;

        while(!alguemAchou && !ambosExplodiram){
            Robo roboDaVez = robos.get(vezDe);

            if(roboDaVez.getIsPodeMover()){
                esperar.aguardar(800);

            try{
                roboDaVez.mover();

                 Obstaculo obstaculoAtingido = null;
                    for(Obstaculo obstaculo : obstaculos){
                        if(obstaculo.getX() == roboDaVez.getX() && obstaculo.getY() == roboDaVez.getY()){
                            obstaculoAtingido = obstaculo;
                            break;
                        }
                    }

                    if(obstaculoAtingido != null){
                        obstaculoAtingido.bater(roboDaVez);
                        if(obstaculoAtingido instanceof ObstaculoBomba){
                            obstaculos.remove(obstaculoAtingido);
                        }
                    }

            } catch(MovimentoInvalidoException e) {
                System.out.println(e.getMessage());
            }

            desenhar.desenharTabuleiro(roboNormal, roboInteligente, obstaculos, posicaoX, posicaoY);

             if(roboDaVez.getIsPodeMover() && roboDaVez.isEncontrouAlimento(posicaoX, posicaoY)){
                    alguemAchou = true;
                    System.out.println("Robô "+roboDaVez.getCor()+" encontrou o alimento!");
                }
            }

            vezDe = (vezDe + 1) % robos.size();

            ambosExplodiram = true;
            for(Robo robo : robos){
                if(robo.getIsPodeMover()){
                    ambosExplodiram = false;
                    break;
                }
            }
        }

        if(ambosExplodiram){
            System.out.println("Os dois robôs explodiram, nenhum dos dois acharam o alimento");
        }

        System.out.println();
        System.out.println("----------------- Movimentos -----------------");
        System.out.println("Robô "+roboNormal.getCor()+": "+(roboNormal.getMovimentosValido()+roboNormal.getMovimentosInvalido()));
        System.out.println("Robô "+roboInteligente.getCor()+": "+(roboInteligente.getMovimentosValido()+roboInteligente.getMovimentosInvalido()));
    }

    private static int lerPosicaoObstaculo(LeituraTeclado teclado, String eixo, int numero, String tipo){
        int valor;
        do{
            System.out.println("Digite a posição "+eixo+" da "+tipo+" "+numero+" (0 a 3):");
            valor = teclado.lerInteiro();
        }while(valor < 0 || valor > 3);
        return valor;
    }
}




    
