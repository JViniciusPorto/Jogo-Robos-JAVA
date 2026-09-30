package classes_obstaculos;
import classes_robos.Robo;
public class ObstaculoRocha extends Obstaculo{
    public ObstaculoRocha(int x, int y){
        super(x, y);
    }
    @Override
    public void bater(Robo robo) {
        int voltarX = robo.getXAnterior();
        int voltarY = robo.getYAnterior();
        robo.setXY(voltarX,voltarY);
        System.out.println("Robô "+robo.getCor()+" retornou a ("+robo.getX()+","+robo.getY()+")");
    }
}
