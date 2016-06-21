package application;

import java.io.Serializable;

public class Codon implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6819567294083854400L;
	private int instruction;
	private double time;

	public Codon(int instruction, double time){
		this.setInstruction(instruction);
		this.setTime(time);
	}

	public int getInstruction() {
		return instruction;
	}

	public void setInstruction(int instruction) {
		this.instruction = instruction;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}	
	public String toString(){
		return "{"+this.instruction + ", "+this.time + "}";
	}
}
