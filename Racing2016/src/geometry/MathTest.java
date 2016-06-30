package geometry;

public class MathTest {
	public static void main(String[] args){
		
		Point a = new Point(0,0);
		Point b = new Point(0,2);
		Point c = new Point(2,2);
		Point d = new Point(2,0);

		Point[] abcd = {c,a,b,d};
		Polygon p = new Polygon(abcd);

		Point e = new Point(10,10);
		Point f = new Point(0,3);
		Point g = new Point(4,3);
		Point h = new Point(3,3);

		Point[] efgh = {e,f,g,h};
		Polygon q = new Polygon(efgh);

		System.out.println(p.intersects(q));
	}
}
