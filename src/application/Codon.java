package application;

public class Codon {
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
}
