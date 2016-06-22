package application;

public class MathTest {
	public static void main(String[] args){
		for(int i = 0; i < 10; i++){
			
			
    		double randomTime = (double) ((Math.round(Math.random() * 30.0)/10.0));
    		System.out.println(randomTime);
		}

	}
}
