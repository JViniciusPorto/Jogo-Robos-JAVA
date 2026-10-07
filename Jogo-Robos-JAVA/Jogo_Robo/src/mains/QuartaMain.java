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
        int posicaoY;
        int quantidadeBombas;
        int quantidadeRochas;
        int xBomba;
        int yBomba;
        int xRocha;
        int yRocha;

        boolean posicaoValida;

        while(true){
            System.out.println("Digite a posição x do alimento (0 a 3):");
            posicaoX = teclado.lerInteiro();
            if(posicaoX<0 || posicaoX>3){
                System.out.println("Digite um inteiro entre [0,3]!");
                continue;
            }
            System.out.println("Digite a posição y do alimento (0 a 3):");
            posicaoY = teclado.lerInteiro();
            if(posicaoY<0 || posicaoY>3){
                System.out.println("Digite um inteiro entre [0,3]!");
                continue;
            }
            if(posicaoX==0 && posicaoY==0){
                System.out.println("Você não pode colocar o alimento em (0,0)!");
                continue;
            }
            break;
        }

        do{
            System.out.println("\nQuantas bombas você quer colocar no tabuleiro ? (0 a 4)");
            quantidadeBombas = teclado.lerInteiro();
            if(quantidadeBombas<0 || quantidadeBombas>4){
                System.out.println("Digite uma quantidade entre [0,4]!");
            }
        }while(quantidadeBombas<0 || quantidadeBombas>4);

        for(int i = 0; i < quantidadeBombas; i++){
            do{
                posicaoValida = true;
                xBomba = lerPosicaoObstaculo(teclado, "x", i+1, "bomba");
                yBomba = lerPosicaoObstaculo(teclado, "y", i+1, "bomba");
                if(xBomba==posicaoX && yBomba==posicaoY){
                    System.out.println("Você não pode colocar a bomba no mesmo lugar do alimento!");
                    posicaoValida = false;
                }else if(xBomba==0 && yBomba==0){
                    System.out.println("Você não pode colocar uma bomba em (0,0)!");
                    posicaoValida = false;
                }else if(existeObstaculo(obstaculos, xBomba, yBomba)){
                    System.out.println("Já existe um obstáculo nessa posição!");
                    posicaoValida = false;
                }
            }while(!posicaoValida);
            obstaculos.add(new ObstaculoBomba(xBomba, yBomba));
        }

        do{
            System.out.println("\nQuantas rochas você quer colocar no tabuleiro? (0 a 4)");
            quantidadeRochas = teclado.lerInteiro();
            if(quantidadeRochas<0|| quantidadeRochas>4){
                System.out.println("Digite uma quantidade entre [0,4]!");
            }
        }while(quantidadeRochas<0 || quantidadeRochas>4);

        for(int i = 0; i < quantidadeRochas; i++){
            do{
                posicaoValida = true;
                xRocha = lerPosicaoObstaculo(teclado, "x", i+1, "rocha");
                yRocha = lerPosicaoObstaculo(teclado, "y", i+1, "rocha");
                if(xRocha==posicaoX && yRocha==posicaoY){
                    System.out.println("Você não pode colocar a rocha no mesmo lugar do alimento!");
                    posicaoValida = false;
                }else if(xRocha==0 && yRocha==0){
                    System.out.println("Você não pode colocar a rocha em (0,0)!");
                    posicaoValida = false;
                }else if(existeObstaculo(obstaculos, xRocha, yRocha)){
                    System.out.println("Já existe um obstáculo nessa posição!");
                    posicaoValida = false;
                }
            }while(!posicaoValida);
            obstaculos.add(new ObstaculoRocha(xRocha, yRocha));
        }

        System.out.println("\n==============Legenda==============");
        System.out.println("R1 - Robô Normal - "+roboNormal.getCor());
        System.out.println("R2 - Robô Inteligente - "+roboInteligente.getCor());
        System.out.println("B - Bomba");
        System.out.println("P - Rocha");
        System.out.println("X - Robô que explodiu");
        System.out.println("===================================");
        esperar.aguardar(2000);
        System.out.println("Início da Partida:");
        desenhar.desenharTabuleiro(roboNormal, roboInteligente, obstaculos, posicaoX, posicaoY);

        int vezDe = 0;
        boolean alguemAchou = false;
        boolean ambosExplodiram = false;

        while(!alguemAchou && !ambosExplodiram){
            Robo roboDaVez = robos.get(vezDe);

            if(roboDaVez.getIsPodeMover()){
                esperar.aguardar(800);
                System.out.println("Vez do "+roboDaVez.getCor()+"!");
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
            if(valor <0 || valor >3){
                System.out.println("Digite um inteiro entre [0,3]!");
            }
        }while(valor < 0 || valor > 3);
        return valor;
    }

    private static boolean existeObstaculo(List<Obstaculo> obstaculos,int x, int y){
        for (Obstaculo obstaculo : obstaculos) {
            if(obstaculo.getX()==x && obstaculo.getY()==y){
                return true;
            }
        }
        return false;
    }
}