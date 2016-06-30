package geometry;

public class Point {
	private double x;
	private double y;
	public Point(double x, double y){
		this.setX(x);
		this.setY(y);
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double distanceToOrigin(){
		return (Math.sqrt((Math.pow(this.y, 2)+Math.pow(this.x, 2))));
	}
	public void update(double x, double y){
		this.setX(x);
		this.setY(y);
	}
	public String toString(){
		return "(" + this.x + "," + this.y + ")";
	}
	
	
}
