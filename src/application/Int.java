package application;

public class Int {
	public int i;
	private int max;
	public Int(int i,int max){
		this.i = i;
		this.max = max;
	}
	public int increment(){
		this.i = (this.i + 1)%this.max;
		return this.i;
	}
}
