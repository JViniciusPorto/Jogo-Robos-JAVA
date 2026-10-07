package classes_robos;
import excecoes.MovimentoInvalidoException;
import java.util.Random;
public class Robo {
    private String cor;
    private int x;
    private int y;
    private int xAnterior;
    private int yAnterior;
    private boolean isPodeMover = true;
    private int movimentosInvalido = 0;
    private int movimentosValidos = 0;
    public Robo(String cor){
        this.cor = cor;
        this.x=0;
        this.y=0;
        this.xAnterior = 0;
        this.yAnterior = 0;
    }
    
    public void mover(String comando) throws MovimentoInvalidoException{
        switch (comando) {
            case "up":
                if(this.y+1>3){
                    this.movimentosInvalido++;
                    throw new MovimentoInvalidoException("O robô "+getCor()+" fez um movimento Inválido: Up");
                }
                this.xAnterior = this.x;
                this.yAnterior = this.y;
                this.y += 1;
                this.movimentosValidos++;
                System.out.println("Posição Atual do Robô "+getCor()+": ("+this.x+","+this.y+")");
                break;
            case "down":
                if(this.y-1<0){
                    this.movimentosInvalido++;
                    throw new MovimentoInvalidoException("O robô "+getCor()+" fez um movimento Inválido: Down");
                }
                this.xAnterior = this.x;
                this.yAnterior = this.y;
                this.y -= 1;
                this.movimentosValidos++;
                System.out.println("Posição Atual do Robô "+getCor()+": ("+this.x+","+this.y+")");
                break;
            case "right":
                if(this.x+1>3){
                    this.movimentosInvalido++;
                    throw new MovimentoInvalidoException("O robô "+getCor()+" fez um movimento Inválido: Right");
                }
                this.xAnterior = this.x;
                this.yAnterior = this.y;
                this.x += 1;
                this.movimentosValidos++;
                System.out.println("Posição Atual do Robô "+getCor()+": ("+this.x+","+this.y+")");
                break;
            case "left":
                if(this.x-1<0){
                    this.movimentosInvalido++;
                    throw new MovimentoInvalidoException("O robô "+getCor()+" fez um movimento Inválido: Left");
                }
                this.xAnterior = this.x;
                this.yAnterior = this.y;
                this.x -= 1;
                this.movimentosValidos++;
                System.out.println("Posição Atual do Robô "+getCor()+": ("+this.x+","+this.y+")");
                break;
            default:
                this.movimentosInvalido++;
                throw new MovimentoInvalidoException("O robô "+getCor()+" fez um movimento Inválido: Comando inválido - "+comando);
        }
    }

    public void mover(int comando)throws MovimentoInvalidoException{
        switch (comando) {
            case 1:
                mover("up");
                break;
            case 2:
                mover("down");
                break;
            case 3:
                mover("right");
                break;
            case 4:
                mover("left");
                break;
            default:
                throw new MovimentoInvalidoException("O robô "+getCor()+" fez um movimento Inválido: Comando inválido - "+comando);
        }
    }

    public void mover() throws MovimentoInvalidoException {
        String[] movimentos = { "up", "down", "right", "left" };
        Random random = new Random();
        String movimento = movimentos[random.nextInt(4)];
        mover(movimento);
    }

    public int getMovimentosInvalido(){
        return this.movimentosInvalido;
    }

    public int getMovimentosValido(){
        return this.movimentosValidos;
    }

    public boolean isEncontrouAlimento(int xAlimento, int yAlimento){
        return this.x==xAlimento && this.y==yAlimento;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public int getXAnterior(){
        return this.xAnterior;
    }

    public int getYAnterior(){
        return this.yAnterior;
    }
    public String getCor(){
        return this.cor;
    }

    public boolean getIsPodeMover(){
        return this.isPodeMover;
    }

    public void setIsPodeMover(boolean estado){
        this.isPodeMover = estado;
    }
    
    public void setXY(int x, int y){
        this.x = x;
        this.y = y;
    }
}
