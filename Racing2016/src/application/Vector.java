package application;

public class Vector {
	private double[] vector = new double[2];
	
	
	
	public Vector(double[] vector){
		this.vector = vector;
	}
	
	public Vector(double xVal, double yVal){
		double[] v  ={xVal,yVal};
		this.vector = v;
	}
	
	public void setVals(double xVal, double yVal){
		this.vector[0] = xVal;
		this.vector[1] = yVal;

	}
	
	public double getAngle() throws Exception{
		double x = this.vector[0];
		double y = this.vector[1];
		double angle = 0;
    	if(x > 0 && y > 0){
    		angle = (90-Math.toDegrees(Math.atan(x/y)));
    		
    	}
    	else if(x < 0 && y > 0){
    		angle = (-Math.toDegrees(Math.atan(x/y))+90);
    	}
    	else if(x > 0 && y < 0){
    		angle = (Math.toDegrees(Math.atan(x/y))+360);
    	}
    	else if(x < 0 && y < 0){
    		angle = (Math.toDegrees(Math.atan(x/y))+180);
    	}
    	else if(x == 0 && y != 0){
    		if(y > 0){
    			angle = (90);
    		}
    		if(y < 0){
    			angle = (270);
    		}
    	}
    	else if(x != 0 && y == 0){
    		if(x > 0){
    			angle = (0);
    		}
    		if(x < 0){
    			angle = (180);
    		}
    	}
    	else {	//if(x == 0 && y == 0){
    		throw new Exception();
    	}
		return angle;


	}
	
	public void add(Vector other){
		this.vector[0] += other.getX(); 
		this.vector[1] += other.getY();
	}
	public void subtract(Vector other){
		this.vector[0] -= other.getX(); 
		this.vector[1] -= other.getY();

	}
	
	public void scalarMult(double scalar){
		for(int i = 0; i < this.vector.length; i++){
			vector[i] *= scalar;
		}
	}
	
	public double getMagnitude(){
		double magnitude = Math.sqrt((Math.pow(this.getX(), 2)+(Math.pow(this.getY(), 2))));
		return magnitude;
	}
	public void setMagnitude(double num){
		double scalar = num/this.getMagnitude();
		this.scalarMult(scalar);
	}

	public double getX(){
		return vector[0];
	}
	
	public double getY(){
		return vector[1];
	}

	public double[] getVals(){
		return vector;
	}
	
	public String toString(){
		return "[ "+this.getX() + " , " + this.getY()+" ]";
	}
}
