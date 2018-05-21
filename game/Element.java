package game;

public class Element {
	public int variable; //wrap with getters and setters
	public int coefficient;
	public int power;
	
	private final boolean IS_OPERATOR;
	private Operators operator;
	
	public Element(int variable, int coefficient, int power){
		this.variable = variable;
		this.coefficient = coefficient;
		this.power = power;
		this.IS_OPERATOR = false;
	}
	
	public Element(Operators op){
		this.variable = 0;
		this.coefficient = 0;
		this.power = 0;
		this.IS_OPERATOR = true;
		this.operator = op;
		
	}
	
	public boolean isOperator(){
		return IS_OPERATOR;
	}
	
	public Operators getOperator(){
		if(IS_OPERATOR){
			return operator;
		}
		return null;
	}
	
	public int getVariable(){
		return variable;
	}
	
	public void setVariable(int variable){
		this.variable = variable;
	}
	
	public int getPower(){
		return power;
	}
	
	public void setPower(int power){
		this.power = power;
	}
	
	public int getCoefficient(){
		return coefficient;
	}
	
	public void setCoefficient(int coefficient){
		this.coefficient = coefficient;
	}
	
	
}
