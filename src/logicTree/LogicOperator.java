package logicTree;

public class LogicOperator {
	public static final int NAO = -1;
	public static final int AND = 0;
	public static final int OR = 1;
	public static final int NOT = 2;
	public static final int IF = 3;
	public static final int IFF = 4;
	
	private String name;
	private boolean[] truthAssignment;
	
	public static LogicOperator AND(){
		return new LogicOperator(AND);
	}
	public static LogicOperator OR(){
		return new LogicOperator(OR);
	}
	public static LogicOperator NOT(){
		return new LogicOperator(NOT);
	}
	public static LogicOperator IF(){
		return new LogicOperator(IF);
	}
	public static LogicOperator IFF(){
		return new LogicOperator(IFF);
	}
	public static LogicOperator NAO(){
		return new LogicOperator(NAO);
	}
	
	public LogicOperator(int defined_type){
		switch(defined_type){
		case NAO:
			name = "not an operator";
			this.truthAssignment = new boolean[]{};
			break;
		case AND: 
			name = "and";
			this.truthAssignment = new boolean[]{false,false,false,true};
			break;
		case OR:
			name = "or";
			truthAssignment = new boolean[]{false,true,true,true};
			break;
		case NOT:
			name = "not";
			truthAssignment = new boolean[]{true,false};
			break;
		case IF:
			name = "if";
			truthAssignment = new boolean[]{true,false,true,true}; // look at it as 1 0
			break;								  //               1 1
		case IFF:
			name = "iff";
			truthAssignment = new boolean[]{true,false,false,true};; // 1 0
			break;								   // 0 1
		default:
			System.out.println("defined_type: "+defined_type+" Is not defined.");
			System.exit(0);
		}
	}
	
	/**
	 * 
	 * @return returns the number of operands
	 */
	public int numOperands(){
		return (int)(Math.log(truthAssignment.length)/Math.log(2));
	}
	
	/**
	 * Creates a logic operator with the truth table values in a flattened out 
	 * array, such that [1,0,0,0] would be the operator nor
	 * 
	 * @param name
	 * @param truthAssignment
	 */
	public LogicOperator(String name, boolean[] truthAssignment){
		if(truthAssignment.length != 2||truthAssignment.length != 4){
			System.err.println("I only know how to handle binary and unary operators");
			System.exit(-1);
		}
		this.truthAssignment = truthAssignment;
		this.name = name;
	}
	
	/**
	 * Equivalence determined by whether the truth table values for the operator are equal
	 * 
	 * @param right
	 * @return
	 */
	public boolean equals(LogicOperator right){
		if(truthAssignment.length != right.truthAssignment.length){
			return false;
		}
		for(int i = 0; i<truthAssignment.length; i++){
			if(truthAssignment[i] != right.truthAssignment[i]){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Performs the binary or unary operation of the logic operator.
	 * 
	 * @param left
	 * @param right
	 * @return
	 * @throws logicTreeException
	 */
	public boolean operate(LogicStatement left, LogicStatement right) throws logicTreeException{
		if(left == null){
			throw new logicTreeException("Left operand null");
		}
		if(truthAssignment.length == 2){
			return truthAssignment[left.int_val()];
		}
		if(truthAssignment.length == 4){
			if(right == null){
				throw new logicTreeException("Right operand null in binary operator");
			}
			return truthAssignment[left.int_val()*2+right.int_val()];
		}else{
			throw new logicTreeException("truthAssignment length == "+truthAssignment.length);
		}
	}
	
	public String toString(){
		return name;
	}
}
