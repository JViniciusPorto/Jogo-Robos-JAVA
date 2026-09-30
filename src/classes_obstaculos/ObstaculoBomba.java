package classes_obstaculos;
import classes_robos.Robo;
public class ObstaculoBomba extends Obstaculo{
    public ObstaculoBomba(int x, int y){
        super(x, y);
    }

    @Override
    public void bater(Robo robo) {
        robo.setIsPodeMover(false);
        System.out.println("Robô "+robo.getCor()+" explodiu!");
    }
}
