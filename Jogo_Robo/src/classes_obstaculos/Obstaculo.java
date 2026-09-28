package classes_obstaculos;
import classes_robos.Robo;
public abstract class Obstaculo {
    private int x;
    private int y;
    public Obstaculo(int x, int y){
        this.x = x;
        this.y = y;
    }
    public abstract void bater(Robo robo);
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
}
