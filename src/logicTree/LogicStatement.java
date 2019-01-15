package logicTree;

import java.util.HashMap;


public class LogicStatement {
	private LogicOperator op;
	private LogicStatement right;
	private LogicStatement left;
	private boolean truth_assignment;
	private HashMap<String, LogicStatement> Atomics;
	
	public boolean current;
	
	//only set if this is an atomic statement
	private boolean is_atomic;
	private int symbol_id;
	private String name;
	
	public static final LogicStatement TAUT = new LogicStatement(-1,"True",true);
	public static final LogicStatement NEG = new LogicStatement(-2,"False",false);
	
	/**
	 * Non Atomic initialization
	 * 
	 * @param op : operator
	 * @param left : left child
	 * @param right : right child
	 */
	public LogicStatement(LogicOperator op, LogicStatement left, LogicStatement right){
		this.op = op;
		this.left = left;
		this.right = right;
		is_atomic = false;
		current = false;
	}
	
	/**
	 * Void initialization non atomic
	 * 
	 */
	public LogicStatement(){
		is_atomic = false;
		current = false;
	}
	/**
	 * Atomic Initialization,
	 * 
	 * @param symbol_id : identification for the symbol needs to be unique and non negative
	 * @param name : name for the symbol for printing purposse
	 */
	public LogicStatement(int symbol_id, String name){
		this.symbol_id = symbol_id;
		is_atomic = true;
		this.name = name;
		set_truth_val(false);
		current = false;
	}
	/**
	 * Atomic Initialization with a truth value for the atomic
	 * 
	 * @param symbol_id
	 * @param name
	 * @param val
	 */
	public LogicStatement(int symbol_id, String name, boolean val){
		this.symbol_id = symbol_id;
		is_atomic = true;
		this.name = name;
		set_truth_val(val);
		current = false;
	}
	
	/**
	 * Set the truth value of an atomic crash if non atomic
	 * 
	 * @param val
	 */
	public void set_truth_val(boolean val){
		if(is_atomic){
			truth_assignment = val;
		}else{
			assert(false);
		}
	}
	
	/**
	 * Sets the right subchild if non atomic
	 * crash if atomic
	 * 
	 * @param right
	 */
	public void set_right(LogicStatement right){
		if(!is_atomic) this.right = right;
		else assert(false);
	}
	
	/**
	 * Set the left subchild if non atomic
	 * crash if atomic
	 * 
	 * @param left
	 */
	public void set_left(LogicStatement left){
		if(!is_atomic) this.left = left;
		else assert(false);
	}
	
	/**
	 * Add a child if non atomic and at least one child is null
	 * crash otherwise
	 * 
	 * @param ls
	 */
	public void addChild(LogicStatement ls){
		if(is_atomic) assert(false);
		if(left == null) left = ls;
		else if(right == null) right = ls;
		else assert(false);
	}
	/**
	 * Set the operator if non atomic
	 * 
	 * @param op
	 */
	public void set_op(LogicOperator op){
		if(!is_atomic) this.op = op;
		else assert(false);
	}
	
	/**
	 * 
	 * @return the value of this statement
	 * @throws logicTreeException
	 */
	public boolean bool_val() throws logicTreeException{
		if(is_atomic){
			return truth_assignment;
		}
		truth_assignment = op.operate(left,right);
		return truth_assignment;
	}

	/**
	 * 
	 * @return the value of this statement as an integer (1 true, 0 false)
	 * @throws logicTreeException
	 */
	public int int_val() throws logicTreeException{
		if(is_atomic){
			if(truth_assignment) return 1;
			else return 0;
		}
		
		truth_assignment = op.operate(left,right);
		if(truth_assignment) return 1;
		else return 0;
	}
	
	/**
	 * equal if you have the same left and right as rhs.  Including the same
	 * atoms.  (and A B) != (and C D)
	 * @param rhs
	 * @return
	 */
	public boolean equals(LogicStatement rhs){
		if(is_atomic){
			if(rhs.is_atomic){
				return rhs.symbol_id == symbol_id;
			}
			return false;
		}
		if(rhs.is_atomic){
			return false;
		}
		boolean rightequals = false;
		if(right == null||rhs.right == null){
			if(right == null&&rhs.right == null){
				rightequals = true;
			}
			else{
				return false;
			}
		}else{
			rightequals = right.equals(rhs.right);
		}
		boolean leftequals = false;
		if(left == null||rhs.left == null){
			if(left == null&&rhs.left == null){
				leftequals = true;
			}
			else{
				return false;
			}
		}else{
			leftequals = left.equals(rhs.left);
		}
		return rightequals&&leftequals;
	}
	
	public String commandLineToString(){
		String ret = "";
		if(current){
			ret = "[";
			ret+=toString();
			ret+="]";
		}else{
			if(is_atomic){
				return name;
			}
			ret = "(";
			ret+=op.toString();
			if(left!= null) ret+= " "+left.commandLineToString();
			if(right!=null)ret+=" "+right.commandLineToString();
			ret+=")";
		}
		return ret;
	}

	public String toString(){
		if(is_atomic){
			return name;
		}
		String ret = "(";
		ret+=op.toString();
		
		if(left != null) ret+=" "+left.toString();
		if(right!=null) ret+=" "+right.toString();
		
		ret+=")";
		return ret;
	}

	/**
	 * 
	 * @return All the atomics in this statement
	 */
	public HashMap<String, LogicStatement> learn_atomics(){
		Atomics = new HashMap<String, LogicStatement>();
		if(is_atomic){
			Atomics.put(name,this);
		}else{
			HashMap<String,LogicStatement> rightAtoms = right.learn_atomics();
			HashMap<String,LogicStatement> leftAtoms = left.learn_atomics();
			Atomics.putAll(rightAtoms);
			Atomics.putAll(leftAtoms);
		}
		return Atomics;
	}
	/**
	 * 
	 * @return a string list of the atomics in the statement
	 */
	public String list_atomics(){
		String ret = "";
		for(String s: Atomics.keySet()){
			ret+= s+" ";
		}
		return ret;
	}
	
	/**
	 * true iff this statement is an atomic statement
	 * 
	 * @return
	 */
	public boolean isAtomic(){
		return is_atomic;
	}
	public LogicOperator getOp(){ return op; }
	/**
	 * returns atomic id or crashes if not an atomic statement
	 * @return the atomic id
	 */
	public int getId(){
		if(is_atomic){ return symbol_id; }
		assert(false);
		return -1000;
	}
	/**
	 * gets the ith child
	 * @param i should be 0 or 1
	 * @return the ith child 
	 */
	public LogicStatement getChild(int i){
		if(i == 0 && left != null){
			return left;
		}else if(i == 1 && right != null){
			return right;
		}
		assert(false);
		return null;
	}
}
