package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;


/**
 * This is the car object. It contains the aspects of the car (Car image, turn radius, acceleration, brake, max speed,
 * max reverse speed).
 **/
public class Car implements Comparable<Car>{
    private ImageView imv;
    private Image image;    
    private Map curMap;
    
    private double positionX;
    private double positionY;    
    private double velocityX;
    private double velocityY;
    private double accelFact = 2;
    private double brakeFact = 2;
    private double turnRadius = 1;
    private double maxSpeed = 200;
    private double maxRevSp = -100;
    private double frict;
    private double overallSpeed = 0;
    private int score = 100000; 
    private double timeElapsed = 0;
    private boolean grassPenalty;
    private double totalDist = 0;
    
    
    private Point2D pUL;
    private Point2D pUR;
    private Point2D pLL;
    private Point2D pLR;
        
    private boolean previousWinState;
    private int lapNum;
    
    private Codon[] geneCode;
    private int currentCodeIndex = 0;
    private double mutationChance = 0.10;
    

    public Car(Map curMap)
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
        velocityX = 0;
        velocityY = 0;
        
    }
    public Car(Map curMap, double accelFact, double brakeFact, double turnRadius, double maxSpeed, double maxRevSp){
    	this.curMap = curMap;
    	this.accelFact = accelFact;
    	this.brakeFact = brakeFact;
    	this.turnRadius = turnRadius;
    	this.maxSpeed = maxSpeed;
    	this.maxRevSp = maxRevSp;
        this.grassPenalty = false;
        
        positionX = 0;
        positionY = 0;    
        velocityX = 0;
        velocityY = 0;
    }
    
    //METHODS BELOW HAVE TO DO WITH GENETIC CODE AND EVOLUTION:
    
    public void setRandomCode(){
    	this.geneCode = new Codon[10];
    	for(int i = 0; i < 10; i++){
    		//1 means forward, 2, means backward, 3 means left, 4 means right.
    		int randomInstruction = ((int) (Math.random() * 4))+1;
    		double randomTime = (double) ((Math.round(Math.random() * 30.0)/10.0));
    		this.geneCode[i] = new Codon(randomInstruction,randomTime);
    	}
    }
    public void addMutation(){
    	for(int i = 0; i < 10; i++){
    		//1 means forward, 2, means backward, 3 means left, 4 means right.
    		double mutationFactor = Math.random();
    		if(mutationFactor > (1-mutationChance)){
        		int randomInstruction = ((int) (Math.random() * 4))+1;
        		double randomTime = (double) ((Math.round(Math.random() * 30.0)/10.0));
        		this.geneCode[i] = new Codon(randomInstruction,randomTime);
    		}
    	}

    }
    public Codon[] breed(Car other){
    	Codon[] offspring = new Codon[this.geneCode.length];
    	for(int i = 0; i < this.geneCode.length; i++){
    		double random = Math.random();
    		if(random < ((1-mutationChance)/2)){
    			offspring[i] = this.geneCode[i];
    		}
    		else if(random < (1-mutationChance)){
    			offspring[i] = other.geneCode[i];
    		}
    		else{
    			double randomTime = (double) ((Math.round(Math.random() * 30.0)/10.0));

    			offspring[i] = new Codon(((int) (Math.random() * 4))+1,randomTime);
    		}
    	}
    	return offspring;
    }

    
    public void setGeneCode(Codon[] geneCode){
    	this.geneCode = geneCode;
    }
    
    public Codon[] getCode(){
    	return this.geneCode;
    }
    public String getCodeString(){
    	String s = "[";
    	for(int i = 0; i < this.geneCode.length; i++){
    		s += this.geneCode[i].toString();
    	}
    	s+= "]";
    	return s;
    }
    
    public int getNextInstruction(){
    	double counter = 0;
    	for(int i = 0; i < currentCodeIndex;i++){
    		counter+=this.geneCode[i].getTime();
    	}
    	double timeOnCurCode = timeElapsed-counter;
    	if(timeOnCurCode < this.geneCode[currentCodeIndex].getTime()){
    		return this.geneCode[currentCodeIndex].getInstruction();
    	}
    	else{
    		if(currentCodeIndex == this.geneCode.length-1){
    			return 0;
    		}
    		else{
        		currentCodeIndex++;
        		return this.geneCode[currentCodeIndex].getInstruction();    		
    		}
    	}
    }
    public int getCodeIndex(){
    	return this.currentCodeIndex;
    }
    
    
    public void setImage(Image i)
    {
    	this.image = i;
        imv = new ImageView();
        imv.setImage(i);
    }
    
    public void reset(int playerNum){
    	
		setPosition(curMap.getStartCoord()[0],curMap.getStartCoord()[1]/*+ 50*playerNum*/);
		setOrientation(180);
		setOverallSpeed(0);
		
		this.timeElapsed = 0;
		this.totalDist = 0;
		this.score = 100000;
		this.lapNum = 0;
		this.currentCodeIndex = 0;

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
    	overallSpeed += (accelFact);
    	
    	if(overallSpeed > maxSpeed){
    		overallSpeed = maxSpeed;
    	}
    	
    }
    public void brake()
    
    {
    	
    	overallSpeed -= brakeFact;
    	if(overallSpeed < maxRevSp){
    		
    		overallSpeed = maxRevSp;
    	}

    }
    
    public void turnLeft()
    {
    	double angle = imv.getRotate();
    	

    	angle -= turnRadius;
    	if(angle < 0){
    		angle = 360+angle;
    	}
    	
    	if(angle > 360){
    		angle = angle % 360;
    	}
    	setOrientation(angle);
    }
    public void turnRight()
    {
    	double angle = imv.getRotate();
    	
    	angle += turnRadius;
    	if(angle < 0){
    		angle = 360-angle;
    	}
    	
    	if(angle > 360){
    		angle = angle % 360;
    	}
    	setOrientation(angle);
    }
    
    public void setOrientation(double angle){
    	this.imv.setRotate(angle);
    }
    public void setOverallSpeed(double speed){
    	this.overallSpeed = speed;
    }
    public void update(double time)
    {
        Double rotation = imv.getRotate();

    	if(overallSpeed > frict){
        	overallSpeed-=frict;
    	}
    	else if(overallSpeed < -frict){
        	overallSpeed+=frict;

    	}
    	else{
    		overallSpeed = 0;
    	}
    	
    	
    	
    	velocityX = Math.cos(Math.toRadians(rotation)) * overallSpeed;
    	velocityY = Math.sin(Math.toRadians(rotation)) * overallSpeed;
        positionX += velocityX * time;
        positionY += velocityY * time;
        totalDist += Math.abs(overallSpeed) * time;
        timeElapsed+=time;
        
        this.stayInMap(curMap.getDimensions()[0],curMap.getDimensions()[1]);
        
        //this part updates the minimum bounds:
        Bounds b = imv.getBoundsInLocal();
        int[] center = this.getCenter();
        
		double ulx = (b.getMinX()-center[0]) * Math.cos(Math.toRadians(rotation)) - (b.getMinY()-center[1]) * Math.sin(Math.toRadians(rotation))+center[0];
        double uly = (b.getMinY()-center[1]) * Math.cos(Math.toRadians(rotation)) + (b.getMinX()-center[0]) * Math.sin(Math.toRadians(rotation))+center[1];

        this.pUL = new Point2D(ulx,uly);
        
        double urx = (b.getMaxX()-center[0]) * Math.cos(Math.toRadians(rotation)) - (b.getMinY()-center[1]) * Math.sin(Math.toRadians(rotation))+center[0];
        double ury = (b.getMinY()-center[1]) * Math.cos(Math.toRadians(rotation)) + (b.getMaxX()-center[0]) * Math.sin(Math.toRadians(rotation))+center[1];

		this.pUR = new Point2D(urx,ury);

        double llx = (b.getMinX()-center[0]) * Math.cos(Math.toRadians(rotation)) - (b.getMaxY()-center[1]) * Math.sin(Math.toRadians(rotation))+center[0];
        double lly = (b.getMaxY()-center[1]) * Math.cos(Math.toRadians(rotation)) + (b.getMinX()-center[0]) * Math.sin(Math.toRadians(rotation))+center[1];

		this.pLL = new Point2D(llx,lly);

        double lrx =(b.getMaxX()-center[0]) * Math.cos(Math.toRadians(rotation)) - (b.getMaxY()-center[1]) * Math.sin(Math.toRadians(rotation))+center[0];
        double lry = (b.getMaxY()-center[1]) * Math.cos(Math.toRadians(rotation)) + (b.getMaxX()-center[0]) * Math.sin(Math.toRadians(rotation))+center[1];
	
		this.pLR = new Point2D(lrx,lry);

        this.setScore();
        
    }
    public Point2D[] getRect(){
    	Point2D[] points = {pUL,pUR,pLL,pLR};
    	return points;
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
    	return this.velocityX;
    }
    
    public void render(GraphicsContext gc)
    {
    	
        gc.drawImage( imv.getImage(), positionX, positionY );
    }

    public Bounds getBoundary()
    {
    	return imv.getBoundsInParent();
    }
    

    public boolean intersects(Car s)
    {
    	//TODO: COMPLETE THE INTERSECTION CODE, (or don't)
    	
    	
    	
        return s.getBoundary().intersects( this.getBoundary() );
    }
    
    @Override
    public int compareTo(Car other){
    	if(this.getScore() > other.getScore()){
    		return -1;
    	}
    	else if(this.getScore() < other.getScore()){
    		return 1;
    	}
    	else{
    		return 0;
    	}

    }
    
    
    public void stayInMap(int xMax, int yMax){
    	
    	Bounds box = this.getBoundary();
    	if(box.getMaxX() > (xMax-5) && velocityX > 0){
    		if(imv.getRotate() > 0){
        		imv.setRotate(360-(imv.getRotate()-180));
    		}
    		else{
    			imv.setRotate(180+imv.getRotate());
    		}
    	}
    	else if(box.getMaxY() > (yMax-5) && velocityY > 0){
        	imv.setRotate(360-imv.getRotate());

    	}
    	if(box.getMinX() < 5 && velocityX < 0){
    		if(imv.getRotate() < 180){
        		imv.setRotate(180-imv.getRotate());
    		}
    		else{
    			imv.setRotate(360-(imv.getRotate()-180));
    		}
    		
    	}
    	else if(box.getMinY() < 5 && velocityY < 0){

    		imv.setRotate((360-imv.getRotate())%360);

    	}
    }
    public String getVelocity(){
    	return Double.toString(velocityX) + " " + Double.toString(velocityY);
    }
    
    public void setGrassPenalty(boolean penalty){
    	if(penalty){
    		frict = curMap.getGrassFrict();
    	}
    	else{
    		frict = curMap.getAsphaltFrict();
    		
    	}
    	this.grassPenalty = penalty;
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
        	this.score = (int)(score / this.distanceFromFinish());// - timeElapsed*10);
        	if(this.score > score){
        		this.score =  100000;
        	}
        	if(this.score < 0){
        		this.score = 0;
        	}
    	}catch(Exception e){
    		this.score = (int)(100000);// - timeElapsed*10);
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
    	return this.overallSpeed;
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
    	String code = "[";
    	for(int i = 0; i < geneCode.length; i++){
    		code+= "{"+geneCode[i].getInstruction() + ", "+geneCode[i].getTime() + "}";
    	}
    	code+="]";
    	
    	
    	String toString = ""+this.getScore() + ", " + code;
    	
    	
    	/*
    	int[] center = getCenter();    	

    	String toString = "max acceleration: " + accelFact 
    	 + '\n'+ "max brake: " + brakeFact
    	 + '\n'+"turn radius: " + turnRadius
    	 + '\n'+"max speed: " + maxSpeed
    	 + '\n'+"max reverse: " + maxRevSp
    	 + '\n' + '\n' + "current score: " + this.getScore()
         +'\n' + "overall speed: " + overallSpeed
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
    	return toString;
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
