package application;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
				//file:///Users/appleuser/Desktop/JavaFX tutorials/Racing2016/
				Image background = new Image("file:Images/tracks/race_track_2.png");
				int[] dimensions = {(int)background.getWidth(),(int)background.getHeight()};
				int[] startCoord = {1100, 160};
				int[][] endBox = {{165,737},{209,815}};
				//changed friction from 0.9 to 5
				map = new Map(background,dimensions,startCoord,endBox, "0x606060ff",0.5,"0x398830ff",5,1,false);
			}
			if(trackNum == 2){
				//this is a racing map (track type 2)
				Image background = new Image("file:Images/tracks/race_track_3.png");
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
			Car[] population = new Car[48];
			
			//Creates the images:
			//file:///Users/appleuser/Desktop/JavaFX tutorials/Racing2016
			Image blueCarImg = new Image("file:Images/Cars/blueCar.png");
			Image redCarImg = new Image("file:Images/Cars/redCar.png");
			Image purpCarImg = new Image("file:Images/Cars/purpCar.png");
			Image yelCarImg = new Image("file:Images/Cars/yelCar.png");

			stage.getIcons().add(blueCarImg);

			//method that reads the gene codes.
			boolean continueProgress = true;
			if(continueProgress){
				try{
				    FileInputStream fis = new FileInputStream("save-files/saveFile.tmp");
					ObjectInputStream codeReader = new ObjectInputStream(fis);
					for(int i = 0; i < population.length; i++){
						//Map curMap, double accelFact, double brakeFact, double turnRadius, double maxSpeed, double maxRevSp
						Car car = new Car(map,2,2,1,200,-100);
						car.setPosition(map.getStartCoord()[0], map.getStartCoord()[1]);
						if(i%4 == 0){
							car.setImage(blueCarImg);
						}
						else if(i%3 == 0){
							car.setImage(redCarImg);
						}
						else if(i%2 == 0){
							car.setImage(purpCarImg);
						}
						else{
							car.setImage(yelCarImg);
						}
						Codon[] curCode = (Codon[]) codeReader.readObject();
						car.setGeneCode(curCode);
						population[i] = car;
					}
					codeReader.close();
					fis.close();
					System.out.println("gene pool loaded successfully.");


				}catch(IOException e){
					for(int i = 0; i < population.length; i++){
						Car car = new Car(map,2,2,1,200,-100);
						car.setPosition(map.getStartCoord()[0], map.getStartCoord()[1]);
						if(i%4 == 0){
							car.setImage(blueCarImg);
						}
						else if(i%3 == 0){
							car.setImage(redCarImg);
						}
						else if(i%2 == 0){
							car.setImage(purpCarImg);
						}
						else{
							car.setImage(yelCarImg);
						}
						car.setRandomCode();
						population[i] = car;
					}
					System.out.println("gene pool failed to load.");
				}
			}
			else{
				for(int i = 0; i < population.length; i++){
					Car car = new Car(map,2,2,1,200,-100);
					car.setPosition(map.getStartCoord()[0], map.getStartCoord()[1]);
					if(i%4 == 0){
						car.setImage(blueCarImg);
					}
					else if(i%3 == 0){
						car.setImage(redCarImg);
					}
					else if(i%2 == 0){
						car.setImage(purpCarImg);
					}
					else{
						car.setImage(yelCarImg);
					}
					car.setRandomCode();
					population[i] = car;
				}
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
							
							Image winScreen = new Image("file:Images/winScreen.png");
							wc.drawImage(winScreen, 0, 0);
							Font font = Font.font(20);
							wc.setFont(font);
							wc.fillText("Press R to restart.", 500, 30);
							wc.fillText("Genetic Code successful! Press O to replay. Press I to restart from scratch", 700, 500);
							if(cmds.contains("O")){
								for(int j = 0; j < population.length; j++){
									population[j].reset(j);
								}
								win = false;
							}
							if(cmds.contains("I")){
								for(int j = 0; j < population.length; j++){
									population[j].reset(j);
									population[j].setRandomCode();
								}
								win = false;
							}
							break;
						}
						if(!win){
							gc.drawImage(map2.getMap(), 0, 0);
							int instruct = car.getNextInstruction();
							if(instruct == 0 && car.getCodeIndex()==car.getCode().length-1 && car.getOverallSpeed() == 0){
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
							//reset simulation
							if(cmds.contains("I")){
								for(int j = 0; j < population.length; j++){
									population[j].reset(j);
									population[j].setRandomCode();
								}
								try{
									FileWriter highScore = new FileWriter("save-files/highScores.txt",false);
									highScore.write("");
									highScore.close();
								}catch(IOException e){
									
								}
								win = false;
							}
							//restart round
							if(cmds.contains("O")){
								for(int j = 0; j < population.length; j++){
									population[j].reset(j);
								}
								win = false;

							}
							//this is to save a current gene code:
							if(cmds.contains("P")){
								try {
									FileOutputStream fos;
									ObjectOutputStream oos;
									FileWriter fWrite;
									
									//checking if saveFile already exists
									if(new File("save-files/saveFile.tmp").isFile()){
									    fos = new FileOutputStream("save-files/saveFile.tmp");
									    oos = new ObjectOutputStream(fos);
									}
									else{
									    fos = new FileOutputStream(new File("save-files/saveFile.tmp"));
									    oos = new ObjectOutputStream(fos);
									}	
									
									if(new File("save-files/geneCode.txt").isFile()){
										fWrite = new FileWriter("save-files/geneCode.txt");
									}
									else{
										fWrite = new FileWriter(new File("save-files/geneCode.txt"));

									}
									for(int j = 0; j < population.length; j++){
										fWrite.write(population[j].getCodeString() + '\n');
										oos.writeObject(population[j].getCode());
									}
									gc.fillText("Save successful.", 10, 70);
									
									fWrite.flush();
									fWrite.close();
									
									oos.flush();
									oos.close();
									
									fos.flush();
									fos.close();
									
								} catch (IOException e) {
									e.printStackTrace();
									gc.fillText("Unable to save.", 10, 70);
								}
							}
						}
					}	
					gc.fillText("Number of cars completed: " + numberCompleted, 10, 10);

					if(!win){
						wc.clearRect(0, 0, maxDistX, maxDistY);
						for(int i = 0; i < population.length; i++){
							Car car = population[i];
							car.setGrassPenalty(elapsedTime);
							car.update(elapsedTime);
							car.render(cars);
							int[] carCenter = car.getCenter();
							gc.fillText("–––––––" + i + "," + car.getScore(), carCenter[0], carCenter[1]);
							
							/**this is for debugging:**/
							
						/*	String comp = "DRIVING";
							if(car.getNextInstruction() == 0 && car.getCodeIndex() == 9){
								comp = "STOPPED";
							}
							gc.fillText("Car "+ i + ": "+ car.toString() + ". "+comp,400,30+10*i);*/
						}
						
						
						Font font = Font.font(12);
						gc.setFont(font);
						gc.fillText("Press I to reset progress.", 10, 30);
						gc.fillText("Press O to restart generation", 10, 40);
						gc.fillText("Press P to save progress.", 10, 50);
						
					}
					
					if(numberCompleted == population.length){
						/**rank cars by highest score to lowest score**/
						Arrays.sort(population);
						String s = Integer.toString(population[0].getScore());
						for(int i = 0; i < population.length/2; i++){
							/**breed new cars based on genetic code of best cars **/
							population[population.length-i-1].setGeneCode(population[i].breed(population[i+1]));
							population[i].addMutation();
						}
						for(int j = 0; j < population.length; j++){
							population[j].reset(j);
						}
						/**saves current progress:**/
						try {
						    FileOutputStream fos = new FileOutputStream("saveFile.tmp");
						    ObjectOutputStream oos = new ObjectOutputStream(fos);
						   
							FileWriter currentGenes = new FileWriter("geneCode.txt");
							FileWriter highScore = new FileWriter("highScores.txt",true);
							highScore.write(s + '\t');
							for(int j = 0; j < population.length; j++){
								currentGenes.write(population[j].getCodeString() + '\n');
								oos.writeObject(population[j].getCode());
							}
							gc.fillText("Save successful.", 10, 70);
							highScore.flush();
							highScore.close();
							
							currentGenes.flush();
							currentGenes.close();
							
							oos.flush();
							oos.close();
							
							fos.flush();
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
							gc.fillText("Unable to save.", 10, 70);
						}
						win = false;
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
	
	//a little easter egg:
	public static void keka(){
		int bunny = 500000;
		bunny = bunny * 2;
		System.out.println(bunny + "bunnies bunnies everywhere!!");
	}
	
}



