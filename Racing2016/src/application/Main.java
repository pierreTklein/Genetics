package application;
	
import java.util.ArrayList;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Main extends Application {
	@Override
	public void start(Stage stage){
		try{
			Group root = new Group();
			Group cars = new Group();
			Scene scene = new Scene(root);
			
			stage.setScene(scene);
		
			//creates the map
			int trackNum = 1;
			Map map = new Map();
			if(trackNum == 1){
				//this is a parking map (track type 1)
				Image background = new Image("file:///Users/appleuser/Desktop/JavaFX tutorials/Racing2016/tracks/race_track_2.png");
				int[] dimensions = {(int)background.getWidth(),(int)background.getHeight()};
				int[] startCoord = {1100, 160};
				int[][] endBox = {{165,737},{209,815}};
				map = new Map(background,dimensions,startCoord,endBox, "0x606060ff",0.5,"0x398830ff",0.9,1,false);
			}
			if(trackNum == 2){
				//this is a racing map (track type 2)
				Image background = new Image("file:///Users/appleuser/Desktop/JavaFX tutorials/Racing2016/tracks/race_track_3.png");
				int[] dimensions = {(int)background.getWidth(),(int)background.getHeight()};
				int[] startCoord = {440, 125};
				int[][] endBox = {{415,95},{415,232}};
				map = new Map(background,dimensions,startCoord,endBox, "0x606060ff",0.5,"0x398830ff",0.9,2,false);
			}
			
			final int maxDistX = map.getDimensions()[0];
			final int maxDistY = map.getDimensions()[1];			
			Canvas gameCanvas = new Canvas(maxDistX,maxDistY);
			Canvas winCanvas = new Canvas(maxDistX,maxDistY);
			
			
			
			//CREATES THE VEHICLES:
			Car[] population = new Car[2];
			//
			Image blueCarImg = new Image("file:///Users/appleuser/Desktop/JavaFX tutorials/Racing2016/blueCar.png");
			//Map curMap, double accelFact, double brakeFact, double turnRadius, double maxSpeed, double maxRevSp
			Car defaultCar = new Car(map,2,2,1,200,-100);
			defaultCar.setImage(blueCarImg);
			
			Image redCarImg = new Image("file:///Users/appleuser/Desktop/JavaFX tutorials/Racing2016/redCar.png");
			Car redCar = new Car(map,2,2,1,200,-100);
			redCar.setImage(redCarImg);
			
			

			
			population[0] = defaultCar;
			population[1] = redCar;
			
			root.getChildren().add(gameCanvas);
			root.getChildren().add(cars);
			root.getChildren().add(winCanvas);
			
			GraphicsContext gc = gameCanvas.getGraphicsContext2D();
			GraphicsContext wc = winCanvas.getGraphicsContext2D();
			
			
			gc.drawImage(map.getMap(), 0, 0);
			
			//this is the relay between the code and the input from user
			ArrayList<String> cmds = new ArrayList<String>();
			scene.setOnKeyPressed(
					new EventHandler<KeyEvent>(){

						@Override
						public void handle(KeyEvent event) {
							String cmd = event.getCode().toString();
							if(!cmds.contains(cmd)){
								cmds.add(cmd);
							}
						}
						
					}
			);
			scene.setOnKeyReleased(
					new EventHandler<KeyEvent>(){
						public void handle(KeyEvent event){
							String cmd = event.getCode().toString();
							if(cmds.contains(cmd)){
								cmds.remove(cmd);
							}
						}
					}
			);
			
			for(int j = 0; j < population.length; j++){
				population[j].reset(j);
			}
			final Map map2 = map;
			final Time lastNanoTime = new Time(System.nanoTime());
			new AnimationTimer(){
				@Override
				public void handle(long now) {
					
										
					boolean win = false;
					double elapsedTime = (now-lastNanoTime.value)/1000000000.0;
					
					Car raceCar = defaultCar;
					
					lastNanoTime.value = now;
					gc.clearRect(0, 0, maxDistX, maxDistY);
					
					for(int i = 0; i < population.length; i++){
						Car car = population[i];
						if(map2.checkWin(car)){
							win = true;
							wc.clearRect(0, 0,maxDistX, maxDistY);
							int score = car.getScore();
							
							Image winScreen = new Image("file:///Users/appleuser/Desktop/JavaFX tutorials/Racing2016/winScreen.png");
							wc.drawImage(winScreen, 0, 0);
							Font font = Font.font(30);
							wc.setFont(font);
							wc.fillText("Press R to restart.", 500, 30);
							wc.fillText("SCORE: " + score + ". Player "+(i+1)+" won.", 700, 500);
							if(cmds.contains("R")){
								for(int j = 0; j < population.length; j++){
									population[j].reset(j);
								}
								win = false;
							}
							break;
						}
						if(!win && i == 0){
							gc.drawImage(map2.getMap(), 0, 0);

							if(cmds.contains("UP")){
								car.accelerate();
							}
							if(cmds.contains("DOWN")){
								car.brake();
							}
							if(cmds.contains("LEFT")){
								car.turnLeft();
							}
							if(cmds.contains("RIGHT")){
								car.turnRight();
							}
							if(cmds.contains("R")){
								for(int j = 0; j < population.length; j++){
									population[j].reset(j);
								}
								win = false;
							}
						}
						if(!win && i == 1){
							gc.drawImage(map2.getMap(), 0, 0);

							if(cmds.contains("W")){
								car.accelerate();
							}
							if(cmds.contains("S")){
								car.brake();
							}
							if(cmds.contains("A")){
								car.turnLeft();
							}
							if(cmds.contains("D")){
								car.turnRight();
							}
							if(cmds.contains("R")){
								for(int j = 0; j < population.length; j++){
									population[j].reset(j);
								}
								win = false;
							}

						}

					}					
					if(!win){
						wc.clearRect(0, 0, maxDistX, maxDistY);
						
						for(int i = 0; i < population.length; i++){
							Car car = population[i];
							car.update(elapsedTime);
							car.setGrassPenalty(elapsedTime);
							car.render(cars);
							Point[] p = car.getRect();
							

							gc.fillRect(p[0].getX()-3, p[0].getY()-3, 5, 5);
							gc.fillRect(p[1].getX()-3, p[1].getY()-3, 5, 5);
							gc.fillRect(p[2].getX()-3, p[2].getY()-3, 5, 5);
							gc.fillRect(p[3].getX()-3, p[3].getY()-3, 5, 5);
							
						}
						
				//		gc.fillText(raceCar.toString(),400,30);
						
						Font font = Font.font(12);
						gc.setFont(font);
						gc.fillText("Press R to restart.", 10, 30);
						gc.fillText(raceCar.toString(), 10, 50);
					}
				}
							
			}.start();
			stage.setTitle("Racers");
			stage.show();
			
			
		}
		catch(Exception e){
			e.printStackTrace();
			
		}
	}
	
	
	
	public static void main(String[] args){
		launch(args);
	}
	
	public static void keka(){
		int bunny = 500000;
		bunny = bunny * 2;
		System.out.println(bunny + "bunnies bunnies everywhere!!");
	}
	
}


/*

for(int i = 0; i < population.length; i++){
	Random randomNum = new Random();
	double accelFact = Math.abs(randomNum.nextGaussian() + 4);
	double brakeFact = Math.abs(randomNum.nextGaussian() + 4);
	double turnRadius = Math.abs(randomNum.nextGaussian() + 1);
	double maxSpeed = Math.abs(randomNum.nextGaussian()*10 + 100);
	double maxRevSp = -1 * maxSpeed / 2;
	
	Car car = new Car(race_track_2, accelFact, brakeFact, turnRadius, maxSpeed, maxRevSp);
	car.setImage(blueCarImg);
	population[i] = car;
}
*/
//creates a car:
//Map curMap, double accelFact, double brakeFact, double turnRadius, double maxSpeed, double maxRevSp


/*Car raceCar = new Car(race_track_2, 2, 2, 1, 100,-50);
Image rcImg = new Image("file:///Users/appleuser/Desktop/JavaFX tutorials/Racing2016/blueCar.png");
raceCar.setImage(rcImg);
raceCar.reset();
*/

