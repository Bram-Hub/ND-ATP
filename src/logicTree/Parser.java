package logicTree;
import java.util.HashMap;

/**
 * 
 * @author jrock
 * Parser keeps track of seen atoms, and keeps AtomicLogicNodes for each.
 * This lets us compare 
 * (not (and A B)) to (not (or (not A) (not B)))
 * because A and B always have the same AtomicLogicNode
 */

public class Parser {
	LogicStatement head;
	HashMap<String,LogicStatement> variables;
	int atomic_count;
	
	public Parser(){
		atomic_count = 0;
		variables = new HashMap<String, LogicStatement>();
		variables.put("TRUE",LogicStatement.TAUT);
		variables.put("FALSE",LogicStatement.NEG);
	}
	
	/**
	 * duplicates the logic statement using this parser, so any atoms are changed to this parsers
	 * atomic id's
	 * 
	 * @param to_dup
	 * @return
	 */
	public LogicStatement duplicate_logic_statement(LogicStatement to_dup){
		try{
			return parse(to_dup.toString());
		}catch(LogicParserException e){
			System.out.println("This should never occur");
			assert(false);
			return null;
		}
	}
	
	/**
	 * read the string as a logic statement and return the logic statement result or 
	 * throw an exception
	 * 
	 * @param s
	 * @return
	 */
	public LogicStatement parse(String s) throws LogicParserException{
		s = s.replaceAll("[(]","( ");
		s = s.replaceAll("[)]"," )");
		s = s.replaceAll("\\s+", " ");
		s = s.replaceAll("^\\s+", "");
		//System.out.println(s);
		String[] ss = s.split(" ");
		if(!validate(ss)){
			throw new LogicParserException("Parenthesis Not Matched");
		}
		if(ss[0].equals("("))
			head = recurseRead(s.split(" "),1);
		else{
			if(variables.get(ss[0])!=null){
				head = variables.get(ss[0]);
			}else{
				head = new LogicStatement(atomic_count++,ss[0]);
				variables.put(ss[0],head);
			}
		}
		return head;
	}
	
	private boolean validate(String[] s){
		int parens = 0;
		for(int i = 0; i<s.length; i++){
			if(parens<0) return false;
			if(s[i].equals("(")) ++parens;
			if(s[i].equals(")")) --parens;
		}
		if(parens != 0) return false;
		return true;
	}
	
	private LogicStatement recurseRead(String[] s,int index){
		/*
		 * Start following an open paren, so our next has to be a logic op
		 * 
		 * Read until an open paren
		 * call recurseRead
		 * goto closing paren
		 * repeat
		 * 
		 */
		int num_parens=0;
		boolean read_through = false;
		
		LogicOperator op = getLogicOp(s[index]);
		LogicStatement ret = new LogicStatement();
		ret.set_op(op);
		
		for(int i = index+1; i<s.length; i++){
			if(!read_through){
				if(s[i].equals(")")){
					return ret;
				}else if(s[i].equals("(")){
					num_parens++;
					LogicStatement n = recurseRead(s,i+1);
					ret.addChild(n);
					read_through = true;
				}else{
					LogicStatement var = variables.get(s[i]);
					if(var == null){
						var = new LogicStatement(atomic_count++,s[i]);
						variables.put(s[i], var);
					}
					ret.addChild(var);
				}
			}else{
				if(s[i].equals("(")){
					num_parens++;
				}
				if(s[i].equals(")")){
					num_parens--;
				}
				if(num_parens == 0){
					read_through = false;
				}
			}
		}
		return ret;
	}
	
	private LogicOperator getLogicOp(String s){
		if(s.toLowerCase().equals("and")){
			return new LogicOperator(LogicOperator.AND);
		}
		if(s.toLowerCase().equals("or")){
			return new LogicOperator(LogicOperator.OR);
		}
		if(s.toLowerCase().equals("not")){
			return new LogicOperator(LogicOperator.NOT);
		}
		if(s.toLowerCase().equals("if")){
			return new LogicOperator(LogicOperator.IF);
		}
		if(s.toLowerCase().equals("iff")){
			return new LogicOperator(LogicOperator.IFF);
		}
		return null;
	}
}

