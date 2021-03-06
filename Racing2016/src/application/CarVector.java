package application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import geometry.Point;
import javafx.geometry.Bounds;

public class CarVector {


	private ImageView imv;
	private Image image;
	private Map curMap;
	
	private double positionX;
	private double positionY;    
	private double accelFact = 2;
	private double brakeFact = 2;
	private double turnRadius = 1;
	private double maxSpeed = 200;
	private double maxRevSp = -100;
	private double frict;
	private int score = 100000; 
	private double timeElapsed = 0;
	private boolean grassPenalty;
	private double totalDist = 0;
	
	private double width;
	private double height;
	
    private Point pUL = new Point(0,0);
    private Point pUR = new Point(0,0);
    private Point pLL = new Point(0,0);
    private Point pLR = new Point(0,0);
    private Point[] minBounds= {this.pUL,this.pUR,this.pLL,this.pLR};
	
	private Vector velocity = new Vector(0,0);
	private Vector acceleration = new Vector(0,0);
	private Vector friction = new Vector(0,0);
	private Vector brake = new Vector(0,0);
	private double radius2 = 3;
	
	private boolean previousWinState;
	private int lapNum;

    public CarVector(Map curMap)
    {
    	this.curMap = curMap;
    	this.accelFact = 2;
    	this.brakeFact = 2;
    	this.turnRadius = 1;
    	this.maxSpeed = 200;
    	this.maxRevSp = -100;
        this.grassPenalty = false;
        positionX = 0;
        positionY = 0;    
        
    }
    public CarVector(Map curMap, double accelFact, double brakeFact, double turnRadius, double maxSpeed, double maxRevSp){
    	this.curMap = curMap;
    	this.accelFact = accelFact;
    	this.brakeFact = brakeFact;
    	this.turnRadius = turnRadius;
    	this.maxSpeed = maxSpeed;
    	this.maxRevSp = maxRevSp;
        this.grassPenalty = false;
        
        positionX = 0;
        positionY = 0;    
    }
    

    public void setImage(Image i)
    {
    	this.image = i;
        imv = new ImageView();
        imv.setImage(i);
        this.width = i.getWidth();
        this.height = i.getHeight();
    }
    
    public void reset(int playerNum){
    	acceleration.setVals(0, 0);
    	friction.setVals(0, 0);
    	brake.setVals(0, 0);
    	
		setPosition(curMap.getStartCoord()[0],curMap.getStartCoord()[1]+ 50*playerNum);
		setOrientation(180);
        velocity.setVals(0, 0);
		
		this.timeElapsed = 0;
		this.totalDist = 0;
		this.score = 100000;
		this.lapNum = 0;

    }

    public void setImage(String filename)
    {
        Image i = new Image(filename);
    	this.imv = new ImageView(i);
    	
        setImage(i);
    }

    public void setPosition(double x, double y)
    {
        positionX = x;
        positionY = y;
    }

    public void accelerate()
    {
    	double angle = imv.getRotate();

    	double cos = Math.cos(Math.toRadians(angle));
    	double sin = Math.sin(Math.toRadians(angle));
    	if(Math.abs(cos) < 0.00001){
    		cos = 0;
    	}
    	if(Math.abs(sin) < 0.00001){
    		sin = 0;
    	}

    	double xAc = cos * accelFact;
    	double yAc = sin * accelFact;
    	this.acceleration.setVals(xAc, yAc);
    	this.velocity.add(this.acceleration);
    }
    public void brake()
    
    {
    	double angle = imv.getRotate();

    	double cos = Math.cos(Math.toRadians(angle));
    	double sin = Math.sin(Math.toRadians(angle));
    	if(Math.abs(cos) <0.00001){
    		cos = 0;
    	}
    	if(Math.abs(sin) < 0.00001){
    		sin = 0;
    	}
    	this.brake.setVals(-cos * brakeFact, -sin * brakeFact);
		velocity.add(brake);
    }
    
    public void turnLeft()
    {
    	double angle = imv.getRotate();

    	double cos = Math.cos(Math.toRadians(angle));
    	double sin = Math.sin(Math.toRadians(angle));
    	if(Math.abs(cos) < 0.0000001){
    		cos = 0;
    	}
    	if(Math.abs(sin) < 0.0000001){
    		sin = 0;
    	}
    	
    	//acceleration = (velocity)^2/radius of circle
	    double turnAcc = Math.pow(velocity.getMagnitude(),2) / radius2;
    	//(x,y) => (-y,x)
    	Vector leftTurn = new Vector(sin * turnAcc,-cos * turnAcc);
    	velocity.add(leftTurn);
    	boolean turned = setOrientationVect(velocity);
    	if(!turned){
        	velocity.subtract(leftTurn);
    	}
    }
    public void turnRight()
    {
    	double angle = imv.getRotate();

    	double cos = Math.cos(Math.toRadians(angle));
    	double sin = Math.sin(Math.toRadians(angle));
    	if(Math.abs(cos) < 0.00001){
    		cos = 0;
    	}
    	if(Math.abs(sin) < 0.00001){
    		sin = 0;
    	}
    
    	double turnAcc = Math.pow(velocity.getMagnitude(),2) / radius2;
    	//(x,y) => (y,-x)
    	Vector rightTurn = new Vector( -sin * turnAcc, cos * turnAcc);
    	velocity.add(rightTurn);
    	boolean turned = setOrientationVect(velocity);
    	if(!turned){
        	velocity.subtract(rightTurn);
    	}
    }
    
    public boolean setOrientationVect(Vector v){
    	try{
    		double angle = v.getAngle();
    		setOrientation(angle);
    		return true;
    	}catch(Exception e){
    		return false;
    	}
    }
    
    public void setOrientation(double angle){
    	this.imv.setRotate(angle);
    }
    public void update(double time){
    	
        this.stayInMap(curMap.getDimensions()[0],curMap.getDimensions()[1]);
    	
        double angle = imv.getRotate();
        double velAngle = angle;
		try {
			velAngle = velocity.getAngle();
		} catch (Exception e) {
		}
        
    	double cos = Math.cos(Math.toRadians(angle));
    	double sin = Math.sin(Math.toRadians(angle));
    	if(Math.abs(cos) < 0.00001){
    		cos = 0;
    	}
    	if(Math.abs(sin) <0.00001){
    		sin = 0;
    	}

    	//makes sure that the velocity stays within limits of car
    	if(velAngle == angle){
    		//takes care of friction:
/*        	if(this.velocity.getMagnitude() > frict){
            	this.friction.setVals(-cos * frict, -sin * frict);
            	this.velocity.add(this.friction);
        	}
        	else{
            	this.friction.setVals(0,0);
        		this.velocity.setVals(0,0);
        	}*/
    		
    		if(velocity.getMagnitude() > maxSpeed){
    			velocity.setMagnitude(maxSpeed);
    		}
    	}
    	else{
       /* 	if(this.velocity.getMagnitude() > frict){
            	this.friction.setVals(cos * frict, sin * frict);
            	this.velocity.add(this.friction);
        	}
        	else{
            	this.friction.setVals(0,0);
        		this.velocity.setVals(0,0);
        	}*/

    		if(velocity.getMagnitude() > Math.abs(maxRevSp)){
    			velocity.setMagnitude(Math.abs(maxRevSp));
    		}
    	}

    	positionX += velocity.getX() * time;
        positionY += velocity.getY() * time;
        totalDist += Math.abs(this.velocity.getMagnitude()) * time;
        timeElapsed+=time;
        
        //this part updates the minimum bounds:
        updateBounds(angle);
        this.setScore();
        
    }
    public void updateBounds(double angle){
        Bounds b = imv.getBoundsInLocal();
        int[] center = this.getCenter();
        
		double ulx = (b.getMinX()-center[0]) * Math.cos(Math.toRadians(angle)) - (b.getMinY()-center[1]) * Math.sin(Math.toRadians(angle))+center[0];
        double uly = (b.getMinY()-center[1]) * Math.cos(Math.toRadians(angle)) + (b.getMinX()-center[0]) * Math.sin(Math.toRadians(angle))+center[1];

        this.pUL.update(ulx,uly);
        
        double urx = (b.getMaxX()-center[0]) * Math.cos(Math.toRadians(angle)) - (b.getMinY()-center[1]) * Math.sin(Math.toRadians(angle))+center[0];
        double ury = (b.getMinY()-center[1]) * Math.cos(Math.toRadians(angle)) + (b.getMaxX()-center[0]) * Math.sin(Math.toRadians(angle))+center[1];

		this.pUR.update(urx,ury);

        double llx = (b.getMinX()-center[0]) * Math.cos(Math.toRadians(angle)) - (b.getMaxY()-center[1]) * Math.sin(Math.toRadians(angle))+center[0];
        double lly = (b.getMaxY()-center[1]) * Math.cos(Math.toRadians(angle)) + (b.getMinX()-center[0]) * Math.sin(Math.toRadians(angle))+center[1];

		this.pLL.update(llx,lly);

        double lrx =(b.getMaxX()-center[0]) * Math.cos(Math.toRadians(angle)) - (b.getMaxY()-center[1]) * Math.sin(Math.toRadians(angle))+center[0];
        double lry = (b.getMaxY()-center[1]) * Math.cos(Math.toRadians(angle)) + (b.getMaxX()-center[0]) * Math.sin(Math.toRadians(angle))+center[1];
	
		this.pLR.update(lrx,lry);


    }
    
    public Point[] getRect(){
    	return minBounds;
    }
    
    public int distanceFromFinish(){
    	int[] coords = this.getCenter();
    	int[] endBoxCenter = curMap.getCenterOfEndBox();
    	int dy = coords[1]-endBoxCenter[1];
    	int dx = coords[0]-endBoxCenter[0];
    	int distance = (int) Math.sqrt((Math.pow(dy, 2)+Math.pow(dx, 2)));
    	return distance;
    }
    
    public void render(Group group)
    {
    	if(group.getChildren().contains(imv)){
    		group.getChildren().remove(group.getChildren().indexOf(imv));
    	}
    	imv.setY(positionY);
    	imv.setX(positionX);
    	group.getChildren().add(imv);
    } 
    
    public double getVelocityX(){
    	return this.velocity.getX();
    }
    
    public void render(GraphicsContext gc)
    {
        gc.drawImage( imv.getImage(),  positionX, positionY );
    }

    public Bounds getBoundary()
    {
    	return imv.getBoundsInParent();
    }
    

    public boolean intersects(Car s)
    {
    	//TODO: COMPLETE THE INTERSECTION CODE
    	
    	
    	
        return s.getBoundary().intersects( this.getBoundary() );
    }
    
    public void stayInMap(int xMax, int yMax){
    	
    	Point[] minBound = this.getRect();
    	double maxX = 0;
    	double maxY = 0;
    	double minX = minBound[0].getX();
    	double minY = minBound[0].getY();
    	for(int i = 0; i < minBound.length; i++){
    		if(minBound[i].getX() > maxX){
    			maxX = minBound[i].getX();
    		}
    		if(minBound[i].getY() > maxY){
    			maxY = minBound[i].getY();
    		}
    		if(minBound[i].getX() < minX){
    			minX = minBound[i].getX();
    		}
    		if(minBound[i].getY() < minY){
    			minY = minBound[i].getY();
    		}
    	}
    	if(maxX> (xMax-5) && velocity.getX() > 0){
    		if(imv.getRotate() > 0){
        		imv.setRotate(360-(imv.getRotate()-180));
    		}
    		else{
    			imv.setRotate(180+imv.getRotate());
    		}
    	}
    	else if(maxY > (yMax-5) && velocity.getY() > 0){
        	imv.setRotate(360-imv.getRotate());

    	}
    	if(minX < 5 && velocity.getX() < 0){
    		if(imv.getRotate() < 180){
        		imv.setRotate(180-imv.getRotate());
    		}
    		else{
    			imv.setRotate(360-(imv.getRotate()-180));
    		}
    		
    	}
    	else if(minY < 5 && velocity.getY() < 0){

    		imv.setRotate((360-imv.getRotate())%360);

    	}
    }
    public String getVelocity(){
    	return Double.toString(velocity.getX()) + " " + Double.toString(velocity.getY());
    }
    
    public boolean grassPenalty(){
		PixelReader pr = curMap.getMap().getPixelReader();
    	Point[] minBound = this.getRect();

		
		Color[] corners = new Color[4];
		try{
			corners[0] = pr.getColor((int) minBound[0].getX(), (int) minBound[0].getY());
			corners[1] = pr.getColor((int) minBound[1].getX(), (int) minBound[1].getY());
			corners[2] = pr.getColor((int) minBound[2].getX(), (int) minBound[2].getY());
			corners[3] = pr.getColor((int) minBound[3].getX(), (int) minBound[3].getY());
			int counter = 0;
			for(int i = 0; i < corners.length; i++){
				if(corners[i].toString().equals(curMap.getGrassColor())){
					counter++;
				}
			}
			if(counter >= 3){
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

    public void setGrassPenalty(double time){
    	if(this.grassPenalty()){
    		frict = curMap.getGrassFrict();
    		this.grassPenalty = true;
    	}
    	else{
    		frict = curMap.getAsphaltFrict();
    		this.grassPenalty = false;
    		
    	}
    }
    public int[] getCenter(){
    	int[] coordinates = new int[2];
    	Bounds b = imv.getBoundsInParent();
    	coordinates[0] = (int)(b.getMinX() + b.getWidth()/2);
    	coordinates[1] = (int)(b.getMinY() + b.getHeight()/2);
    	return coordinates;
    	
    }
    public void setScore(){
    	int score = 100000;
    	try{
        	this.score = (int)(score / this.distanceFromFinish() - timeElapsed*10);
        	if(this.score > score){
        		this.score =  100000;
        	}
        	if(this.score < 0){
        		this.score = 0;
        	}
    	}catch(Exception e){
    		this.score = (int)(100000 - timeElapsed*10);
        	if(this.score < 0){
        		this.score = 0;
        	}
    	}
    	/*
    	int score = 100000;
    	score /= (timeElapsed/100 + totalDist/100);
    	this.score = score;*/
    }
    public int getScore(){
    	return this.score;
    }
    public double getOverallSpeed(){
    	return this.velocity.getMagnitude();
    }
    public double getMinX(){
    	Bounds b = this.getBoundary();
    	return b.getMinX();
    }
    public double getMaxX(){
    	Bounds b = this.getBoundary();
    	return b.getMaxX();
    }
    public double getMinY(){
    	Bounds b = this.getBoundary();
    	return b.getMinY();
    }
    public double getMaxY(){
    	Bounds b = this.getBoundary();
    	return b.getMaxY();
    }
    
    public String toString()
    {
    	int[] center = getCenter();
    	double a = 0;
    	try {
			a = velocity.getAngle();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			a = imv.getRotate();
		}
    	String string = "velocity vector: "+velocity.toString() +  '\n'+ "magnitude: " +velocity.getMagnitude() + '\n'+
    					"acceler. vector: "+acceleration.toString() + '\n'+
    					"friction vector: "+friction.toString() + '\n'+
    					"brake vector:" + brake.toString() + '\n'+
    					"Velocity angle: "+a +'\n'+ 
    					 " Angle: " + imv.getRotate() + '\n' +
    					 "x:" + this.positionX + "y: "+ this.positionY;
    	
    	
    	
    	
    	
    	
    	
    	String toString = "max acceleration: " + accelFact 
    	 + '\n'+ "max brake: " + brakeFact
    	 + '\n'+"turn radius: " + turnRadius
    	 + '\n'+"max speed: " + maxSpeed
    	 + '\n'+"max reverse: " + maxRevSp
    	 + '\n' + '\n' + "current score: " + this.getScore()
         +'\n' + "overall speed: " + this.velocity.getMagnitude()
         + '\n' + "overall acceleration: " + (accelFact - frict)
         +'\n' + "lap num: "+this.lapNum
         +'\n' + "width: "+this.width + ", height: "+this.height;
    	
    	//    public Car(Map curMap, double accelFact, double brakeFact, double turnRadius, double maxSpeed, double maxRevSp){

    	/*
    	String toString = " Position: [" + positionX + "," + positionY + "]" 
        + '\n'+" Velocity: [" + velocityX + "," + velocityY + "]"
        +'\n' + "overall speed: " + overallSpeed
        + '\n' + "overall acceleration: " + (accelFact - frict)
        + '\n' + "distance travelled: "+ totalDist
        +'\n'+ " Angle: " + imv.getRotate()
        +'\n' + "cos:" + Math.cos(Math.toRadians(imv.getRotate())) + " sin:" + Math.sin(Math.toRadians(imv.getRotate()))
        + '\n' + "grass penalty: " + grassPenalty
    	+ '\n' + "current score: " + this.getScore()
    	+ '\n' + "distance from goal: " + this.distanceFromFinish();
    	if(center != null){
            toString+= '\n' + "center of mass: [" + center[0] + ","+ center[1] + "]";
    	}*/
    	return string;
    }
    
	public boolean getPreviousWinState() {
		return previousWinState;
	}
	public void setPreviousWinState(boolean previousWinState) {
		this.previousWinState = previousWinState;
	}
	public int getLapNum() {
		return lapNum;
	}
	public void setLapNum(int lapNum) {
		this.lapNum = lapNum;
	}
}
