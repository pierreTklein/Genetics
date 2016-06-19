package application;
import java.util.ArrayList;
import java.util.Arrays;
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

public class MainEvolution extends Application{
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
				//changed friction from 0.9 to 3
				map = new Map(background,dimensions,startCoord,endBox, "0x606060ff",0.5,"0x398830ff",4,1,false);
			}
			if(trackNum == 2){
				//this is a racing map (track type 2)
				Image background = new Image("file:///Users/appleuser/Desktop/JavaFX tutorials/Racing2016/tracks/race_track_3.png");
				int[] dimensions = {(int)background.getWidth(),(int)background.getHeight()};
				int[] startCoord = {440, 125};
				int[][] endBox = {{415,95},{415,232}};
				map = new Map(background,dimensions,startCoord,endBox, "0x606060ff",0.5,"0x398830ff",4,2,false);
			}
			
			final int maxDistX = map.getDimensions()[0];
			final int maxDistY = map.getDimensions()[1];			
			Canvas gameCanvas = new Canvas(maxDistX,maxDistY);
			Canvas winCanvas = new Canvas(maxDistX,maxDistY);
			
			
			
			//CREATES THE VEHICLES:
			Car[] population = new Car[40];
			//
			Image blueCarImg = new Image("file:///Users/appleuser/Desktop/JavaFX tutorials/Racing2016/blueCar.png");
			//Map curMap, double accelFact, double brakeFact, double turnRadius, double maxSpeed, double maxRevSp
			
			Image redCarImg = new Image("file:///Users/appleuser/Desktop/JavaFX tutorials/Racing2016/redCar.png");

			for(int i = 0; i < population.length; i++){
				Car car = new Car(map,2,2,1,200,-100);
				car.setPosition(map.getStartCoord()[0], map.getStartCoord()[1]);
				if(i%2 == 0){
					car.setImage(blueCarImg);
				}
				else{
					car.setImage(redCarImg);

				}
				car.setRandomCode();
				population[i] = car;

			}
			
			
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
			
			
			/*
			 * read the instructions. Know which instruction based on how much time has elapsed.
			 */
			new AnimationTimer(){
				@Override
				public void handle(long now) {
					
										
					boolean win = false;
					double elapsedTime = (now-lastNanoTime.value)/1000000000.0;

					lastNanoTime.value = now;
					gc.clearRect(0, 0, maxDistX, maxDistY);

					int numberCompleted = 0;

					
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
						if(!win){
							gc.drawImage(map2.getMap(), 0, 0);
							int instruct = car.getNextInstruction();
							if(instruct == 0 && car.getCodeIndex() == 9){
								numberCompleted++;
							}
							if(instruct == 1){
								car.accelerate();
							}
							if(instruct == 2){
								car.brake();
							}
							if(instruct == 3){
								car.turnLeft();
							}
							if(instruct == 4){
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
					gc.fillText("Number of cars completed: " + numberCompleted, 400, 10*population.length+30);

					if(!win){
						wc.clearRect(0, 0, maxDistX, maxDistY);
						for(int i = 0; i < population.length; i++){
							Car car = population[i];
							boolean grassPenalty = grassPenalty(car);
							car.setGrassPenalty(grassPenalty);
							car.update(elapsedTime);
							car.render(cars);
							int[] carCenter = car.getCenter();
							gc.fillText("–––––––" + i, carCenter[0], carCenter[1]);
							String comp = "DRIVING";
							if(car.getNextInstruction() == 0 && car.getCodeIndex() == 9){
								comp = "STOPPED";
							}
							gc.fillText("Car "+ i + ": "+ car.toString() + ". "+comp,400,30+10*i);
						}
						
						
						Font font = Font.font(12);
						gc.setFont(font);
						gc.fillText("Press R to restart.", 10, 30);
					}
					
					if(numberCompleted == population.length){
						//rank cars by highest score to lowest score
						Arrays.sort(population);
						System.out.println(population[0].getScore());
						for(int i = 0; i < population.length/2; i++){
							population[population.length-i-1].setGeneCode(population[i].breed(population[i+1]));
							population[i].addMutation();
						}
						for(int j = 0; j < population.length; j++){
							population[j].reset(j);
						}
						win = false;

						
					}
				}
				
				public boolean grassPenalty(Car car){
					PixelReader pr = map2.getMap().getPixelReader();
					Bounds b = car.getBoundary();
					Color[] corners = new Color[4];
					try{
						corners[0] = pr.getColor((int) b.getMinX(), (int) b.getMinY());
						corners[1] = pr.getColor((int) (b.getMinX() + b.getWidth()), (int) car.getBoundary().getMinY());
						corners[2] = pr.getColor((int) (b.getMaxX() - b.getWidth()), (int) car.getBoundary().getMaxY());
						corners[3] = pr.getColor((int) b.getMaxX(), (int) b.getMaxY());
						int counter = 0;
						for(int i = 0; i < corners.length; i++){
							if(corners[i].toString().equals(map2.getGrassColor())){
								counter++;
							}
						}
						if(counter >=2){
							return true;
						}
						else{
							return false;
						}
					}
					catch(Exception e){
						return true;
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



