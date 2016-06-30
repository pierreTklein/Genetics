package geometry;

public class Polygon {
	private Point[] vertexes;
	private Line[] edges;
	private Point anchor;
	
	public Polygon(Point[] vertexes){
		this.vertexes = vertexes;
		this.order();
		this.edges = new Line[vertexes.length];
		this.createEdges();
	}
	public void updatePoints(Point[] vertexes){
		this.vertexes = vertexes;
		this.order();
		this.updateEdges();
	}
	private void createEdges(){
		for(int i = 0; i < this.edges.length; i++){
			this.edges[i] = new Line(this.vertexes[i],this.vertexes[(i+1)%this.edges.length]);
		}
	}
	private void updateEdges(){
		for(int i = 0; i < this.edges.length; i++){
			this.edges[i].setPoints(this.vertexes[i],this.vertexes[(i+1)%this.edges.length]);
		}
	}
	
	private void order(){
		findAnchor();
		Point[] tmp = new Point[vertexes.length];
		mergeSort(this.vertexes,tmp,1,this.vertexes.length-1);
	}
	
	private void findAnchor(){
		int index = 0;
		for(int i = 1; i < this.vertexes.length;i++){
			double d1 = this.vertexes[index].distanceToOrigin();
			double d2 = this.vertexes[i].distanceToOrigin();
			if(d1 > d2){
				index = i;
				
			}
		}
		Point tmp = this.vertexes[0];
		this.anchor = this.vertexes[index];
		this.vertexes[index] = tmp;
		this.vertexes[0] = this.anchor;
	}
	
	private void mergeSort(Point[] v2, Point [] tmp, int left, int right)
	{
		if( left < right )
		{
			int center = (left + right) / 2;
			mergeSort(v2, tmp, left, center);
			mergeSort(v2, tmp, center + 1, right);
			merge(v2, tmp, left, center + 1, right);
		}
	}


    private void merge(Point[] v2, Point[] tmp, int left, int right, int rightEnd )
    {
        int leftEnd = right - 1;
        int k = left;
        int num = rightEnd - left + 1;
    	Line ab = new Line(anchor,anchor);
        while(left <= leftEnd && right <= rightEnd){
        	ab.setPoints(anchor,v2[left]);
            if(ab.getOrientationABC(v2[right]) <= 0)
                tmp[k++] = v2[left++];
            else
                tmp[k++] = v2[right++];
        }
        while(left <= leftEnd)    // Copy rest of first half
            tmp[k++] = v2[left++];

        while(right <= rightEnd)  // Copy rest of right half
            tmp[k++] = v2[right++];

        // Copy tmp back
        for(int i = 0; i < num; i++, rightEnd--)
        	v2[rightEnd] = tmp[rightEnd];
    }
    
    public boolean intersects(Polygon other){
    	for(int i = 0; i < this.edges.length; i++){
    		for(int j = 0; j < other.getEdges().length; j++){
    			if(this.edges[i].intersects(other.getEdges()[j])){
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    public Line[] getEdges(){
    	return this.edges;
    }
    public double getArea(){
    	double area = 0;
    	for(int i = 0; i < this.vertexes.length;i++){
    		area+=(this.vertexes[i].getX() * this.vertexes[(i+1)%this.vertexes.length].getY()) - (this.vertexes[i].getY() * this.vertexes[(i+1)%this.vertexes.length].getX());
    	}
    	area=Math.abs(area/=2);
    	return area;
    }
    public String toString(){
    	String s = "";
    	/*
    	for(int i = 0; i < edges.length; i++){
    		s+= "Line "+ (i+1) + ": " + edges[i].toString() + ", " + '\n';
    	}*/
    	for(int i = 0; i < vertexes.length; i++){
    		s+= vertexes[i].toString() + " ";
    	}
    	return s;
    }
	
}
