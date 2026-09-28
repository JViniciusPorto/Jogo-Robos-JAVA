package classes_robos;
import excecoes.MovimentoInvalidoException;
public class Robo {
    private String cor;
    private int x;
    private int y;
    private int xAnterior;
    private int yAnterior;
    private boolean isVencedor = false;
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
                    throw new MovimentoInvalidoException("Movimento Inválido: Up");
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
                    throw new MovimentoInvalidoException("Movimento Inválido: Down");
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
                    throw new MovimentoInvalidoException("Movimento Inválido: Right");
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
                    throw new MovimentoInvalidoException("Movimento Inválido: Left");
                }
                this.xAnterior = this.x;
                this.yAnterior = this.y;
                this.x -= 1;
                this.movimentosValidos++;
                System.out.println("Posição Atual do Robô "+getCor()+": ("+this.x+","+this.y+")");
                break;
            default:
                System.out.println("Comando Inválido!");
                break;
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
                System.out.println("Comando Inválido!");
                break;
        }
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

    public boolean getIsVencedor(){
        return this.isVencedor;
    }

    public boolean getIsPodeMover(){
        return this.isPodeMover;
    }
    public void setIsVencedor(boolean estado){
        this.isVencedor = estado;
    }

    public void setIsPodeMover(boolean estado){
        this.isPodeMover = estado;
    }
    
    public void setXY(int x, int y){
        this.x = x;
        this.y = y;
    }
}
