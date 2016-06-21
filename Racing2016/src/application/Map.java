package application;

import javafx.scene.image.Image;
import javafx.scene.shape.Line;

public class Map {
	private Image map;
	private int[] dimensions;
	private int[] startCoord;
	private int[][] endBox;
	private String asphaltColor;
	private String grassColor;
	private double asphaltFrict;
	private double grassFrict;
	private int trackType;
	private int numLaps = 5;
	private boolean clockWiseRotation;
	public Map(){
		
	}
	
	
	public Map(Image map, int[] dimensions, int[] startCoord, int[][] endBox, String asphaltColor, double asphaltFrict, String grassColor,double grassFrict,int trackType,boolean clockWiseRotation){
		setMap(map);
		setDimensions(dimensions);
		setStartCoord(startCoord);
		setAsphaltColor(asphaltColor);
		setAsphaltFrict(asphaltFrict);
		setEndBox(endBox);
		setGrassColor(grassColor);
		setGrassFrict(grassFrict);
		setTrackType(trackType);
	}

	public Image getMap() {
		return map;
	}

	public int getTrackType() {
		return trackType;
	}


	private void setTrackType(int trackType) {
		this.trackType = trackType;
	}


	private void setMap(Image map) {
		this.map = map;
	}

	public int[] getDimensions() {
		return dimensions;
	}

	private void setDimensions(int[] dimensions) {
		this.dimensions = dimensions;
	}

	public int[] getStartCoord() {
		return startCoord;
	}

	private void setStartCoord(int[] startCoord) {
		this.startCoord = startCoord;
	}

	public String getAsphaltColor() {
		return asphaltColor;
	}

	public double getAsphaltFrict() {
		return asphaltFrict;
	}

	private void setAsphaltFrict(double asphaltFrict) {
		this.asphaltFrict = asphaltFrict;
	}

	private void setAsphaltColor(String asphaltColor) {
		this.asphaltColor = asphaltColor;
	}

	public int[][] getEndBox() {
		return endBox;
	}

	private void setEndBox(int[][] endBox) {
		this.endBox = endBox;
	}

	public String getGrassColor() {
		return grassColor;
	}

	public double getGrassFrict() {
		return grassFrict;
	}

	private void setGrassFrict(double grassFrict) {
		this.grassFrict = grassFrict;
	}

	private void setGrassColor(String grassColor) {
		this.grassColor = grassColor;
	}
	public int[] getCenterOfEndBox(){
    	int[] coordinates = new int[2];
    	coordinates[0] = (int)(((endBox[1][0]-endBox[0][0])/2)+endBox[0][0]);
    	coordinates[1] = (int)(((endBox[1][1]-endBox[0][1])/2)+endBox[0][1]);
    	return coordinates;

	}
	public boolean checkWin(Car car){
		//checks to see if car is in win box using coordinates
		if(this.trackType == 1){
			int[] upperLWin = this.getEndBox()[0];
			int[] lowerRWin = this.getEndBox()[1];
			int[] upperLCar = {(int)car.getBoundary().getMinX(),(int)car.getBoundary().getMinY()};
			int[] lowerRCar = {(int)car.getBoundary().getMaxX(),(int)car.getBoundary().getMaxY()};
			double overallSpeed = car.getOverallSpeed();
			
			if((upperLWin[0]<=upperLCar[0] && upperLWin[1]<=upperLCar[1])
			 &&(lowerRWin[0]>=lowerRCar[0] && lowerRWin[1]>=lowerRCar[1])
			 &&(overallSpeed == 0)){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			
			int[] startCoord = this.getEndBox()[0];
			int[] endCoord = this.getEndBox()[1];
			
			
			
			int[] upperLCar = {(int)car.getBoundary().getMinX(),(int)car.getBoundary().getMinY()};
			int[] lowerRCar = {(int)car.getBoundary().getMaxX(),(int)car.getBoundary().getMaxY()};

			if((startCoord[1] <= upperLCar[1] && endCoord[1] >= upperLCar[1]) && (upperLCar[0]<=startCoord[0] && lowerRCar[0] >=startCoord[0]) && ((car.getVelocityX()<=0 && !this.clockWiseRotation)||(car.getVelocityX()>=0 && this.clockWiseRotation))){
				if(!car.getPreviousWinState()){
					car.setPreviousWinState(true);
					car.setLapNum(car.getLapNum()+1);
				}
				if(car.getLapNum() == this.numLaps){
					return true;
				}
				else{
					return false;
				}
			}
			else{
				car.setPreviousWinState(false);
				return false;
			}			
		}
	}

	public boolean checkWin(CarVector car){
		//checks to see if car is in win box using coordinates
		if(this.trackType == 1){
			int[] upperLWin = this.getEndBox()[0];
			int[] lowerRWin = this.getEndBox()[1];
			int[] upperLCar = {(int)car.getBoundary().getMinX(),(int)car.getBoundary().getMinY()};
			int[] lowerRCar = {(int)car.getBoundary().getMaxX(),(int)car.getBoundary().getMaxY()};
			double overallSpeed = car.getOverallSpeed();
			
			if((upperLWin[0]<=upperLCar[0] && upperLWin[1]<=upperLCar[1])
			 &&(lowerRWin[0]>=lowerRCar[0] && lowerRWin[1]>=lowerRCar[1])
			 &&(overallSpeed == 0)){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			
			int[] startCoord = this.getEndBox()[0];
			int[] endCoord = this.getEndBox()[1];
			
			
			
			int[] upperLCar = {(int)car.getBoundary().getMinX(),(int)car.getBoundary().getMinY()};
			int[] lowerRCar = {(int)car.getBoundary().getMaxX(),(int)car.getBoundary().getMaxY()};

			if((startCoord[1] <= upperLCar[1] && endCoord[1] >= upperLCar[1]) && (upperLCar[0]<=startCoord[0] && lowerRCar[0] >=startCoord[0]) && ((car.getVelocityX()<=0 && !this.clockWiseRotation)||(car.getVelocityX()>=0 && this.clockWiseRotation))){
				if(!car.getPreviousWinState()){
					car.setPreviousWinState(true);
					car.setLapNum(car.getLapNum()+1);
				}
				if(car.getLapNum() == this.numLaps){
					return true;
				}
				else{
					return false;
				}
			}
			else{
				car.setPreviousWinState(false);
				return false;
			}			
		}
	}
	
	
}
