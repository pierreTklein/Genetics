package geometry;

public class Line {
	private Point[] points = new Point[2];
	private double length;
	private double slope;
	
	public Line(Point[] points){
		this.setPoints(points);
	}
	public Line(Point a, Point b){
		this.points[0] = a;
		this.points[1] = b;
		this.setPoints(this.points);
	}
	public Point[] getPoints() {
		return points;
	}
	public void setPoints(Point[] points) {
		this.points = points;
		this.length = (Math.sqrt((Math.pow(this.getDy(), 2)+Math.pow(this.getDx(), 2))));
		this.slope = this.getDy() / this.getDx();
	}
	public void setPoints(Point a, Point b){
		this.points[0] = a;
		this.points[1] = b;
		this.setPoints(this.points);
	}

	public double getLength() {
		return length;
	}
	public double getSlope() {
		return slope;
	}
	public double getDy() {
		return this.points[0].getY()-this.points[1].getY();
	}
	public double getDx() {
		return this.points[0].getX()-this.points[1].getX();
	}
	
	public boolean intersects(Line other){
		if(this.getOrientationABC(other.getPoints()[0])!= this.getOrientationABC(other.getPoints()[1]) &&
				other.getOrientationABC(this.getPoints()[0]) != other.getOrientationABC(this.getPoints()[1])){
			return true;
		}
		else{
			return false;
		}
	}
	
	//gives the orientation of the current line and a point
	//points[0] = a, points[1] = b
	//Clockwise = -1, Counter-Clockwise = 1, Co-linear = 0.
	public int getOrientationABC(Point c) {
		Line lineBC = new Line(this.points[1],c);
		double result = this.getDy()*lineBC.getDx() - lineBC.getDy()*this.getDx();
		if(result > 0){
			return -1;
		}
		else if(result < 0){
			return 1;
		}
		else{
			return 0;
		}
	}
	
	//Clockwise = -1, Counter-Clockwise = 1, Co-linear = 0.
	public int getOrientationBAC(Point c) {
		Line lineAC = new Line(this.points[0],c);
		double result = this.getDy()*lineAC.getDx() - lineAC.getDy()*this.getDx();
		if(result > 0){
			return -1;
		}
		else if(result < 0){
			return 1;
		}
		else{
			return 0;
		}
	}
	public String toString(){
		return "p1 = " + points[0].toString() + ", p2 =" + points[1].toString() + ", slope =" +this.slope + ", length = "+ this.length;
	}

	
}
