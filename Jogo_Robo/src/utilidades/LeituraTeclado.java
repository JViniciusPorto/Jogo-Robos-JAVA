package utilidades;
import java.util.Scanner;
public class LeituraTeclado {
    private Scanner teclado = new Scanner(System.in);
    public int lerInteiro(){
        int numero;
        while(!teclado.hasNextInt()){
            teclado.nextLine();
            System.out.println("Digite um inteiro!");
            System.out.println("Digite novamente: ");
        }
        numero = teclado.nextInt();
        teclado.nextLine();
        return numero;
    }
    public String lerString(){
        String palavra = teclado.nextLine().replaceAll("[^a-zA-Z]", "").toLowerCase();
        return palavra;
    }
    public boolean isInteiro(){
        return teclado.hasNextInt();
    }

}
