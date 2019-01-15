package logicRules;

import java.util.HashMap;
import java.util.Scanner;

import logicTree.LogicParserException;
import logicTree.LogicStatement;
import logicTree.Parser;

public class RuleFromStatements implements Rule{
	private LogicStatement ls1;
	private LogicStatement ls2;
	private Parser parse_p;
	
	public RuleFromStatements(LogicStatement ls1, LogicStatement ls2){
		Parser p = new Parser();
		this.ls1 = p.duplicate_logic_statement(ls1);
		this.ls2 = p.duplicate_logic_statement(ls2);
	}
	
	public RuleFromStatements(String rule1, String rule2){
		Parser p = new Parser();
		try{
		this.ls1 = p.parse(rule1);
		this.ls2 = p.parse(rule2);
		}catch(LogicParserException e){
			System.out.println("error creating rule from statement.");
			assert(false);
		}
	}
	
	/**
	 * Trys to change head from a form of ls1 to ls2 if possible
	 * If there are any undefined (free) variables in ls2 compared
	 * to ls1, Parser p the user is prompted for input and parse p is used
	 * 
	 * @param ls
	 * @param p
	 * @return a transformed logic statement or the input if not transformable
	 */
	public LogicStatement rulePosFunction(LogicStatement ls,Parser p){
		parse_p = p;
		HashMap<Integer, LogicStatement> equiv = check_equiv(ls,ls1);
		if(equiv == null){
			return ls;
		}
		else{
			return replace_with(equiv,ls2);
		}
	}
	
	/**
	 * Trys to change head from a form of ls2 to ls1 if possible
	 * If there are any undefined (free) variables in ls1 compared
	 * to ls2, Parser p the user is prompted for input and parse p is used
	 * 
	 * @param head
	 * @param p
	 * @return a transformed logic statement or the input if not transformable
	 */
	public LogicStatement ruleNegFunction(LogicStatement head,Parser p){
		parse_p = p;
		HashMap<Integer, LogicStatement> equiv = check_equiv(head,ls2);
		if(equiv == null){
			return head;
		}
		else{
			return replace_with(equiv,ls1);
		}
	}
	
	/**
	 * trys to replace the atomics in statement with the logic statements in
	 * the equivalence map (equiv)
	 * 
	 * @param equiv
	 * @param statement
	 * @return A transformed logic statement or null
	 */
	private LogicStatement replace_with(HashMap<Integer, LogicStatement> equiv, LogicStatement statement){
		if(statement.isAtomic()){
			LogicStatement ln = equiv.get(statement.getId());
			if(ln == null){
				if(statement.getId()<0){
					return statement;
				}
				//System.err.println("Not found "+statement.toString());
				LogicStatement ls;
				while(true){
					try{
						System.out.print("Statement "+statement.toString()+" #"+statement.getId()+" undefined, define it: \n> ");
						Scanner sc = new Scanner(System.in);
						String val = sc.nextLine();
						ls = parse_p.parse(val);
						break;
					}catch(LogicParserException e){
						System.out.println("Error reading your statement please re-enter");
					}
				}
				equiv.put(statement.getId(), ls);
				return ls;
			}
			return ln;
		}else{
			LogicStatement ln = new LogicStatement();
			ln.set_op(statement.getOp());
			for(int i = 0; i < statement.getOp().numOperands(); i++){
				LogicStatement c1 = replace_with(equiv,statement.getChild(i));
				if(c1 == null){
					return null;
				}
				ln.addChild(c1);
			}
			return ln;
		}
	}
	
	/**
	 * checks if there is a mapping from ls to statement
	 * 
	 * such that if ls is (or (and A B) (not Q))
	 * and statement is (or P1 P2)
	 * 
	 * We would map
	 * P1-> (and A B)
	 * P2-> (not Q)
	 * 
	 * @param ls
	 * @param statement
	 * @return A mapping of integer values of the statements atomics to the logic statements of ls
	 */
	private HashMap<Integer,LogicStatement> check_equiv(LogicStatement ls, LogicStatement statement){
		HashMap<Integer,LogicStatement> equivs = new HashMap<Integer,LogicStatement>();
		if(statement.isAtomic()){
			if(statement.getId() < 0){
				if(ls.getId() == statement.getId()){
					equivs.put(statement.getId(), ls);
				}
				else{
					return null;
				}
			}
			else if(equivs.get(statement.getId())==null){
				equivs.put(statement.getId(),ls);
			}else{
				if(!equivs.get(statement.getId()).equals(ls)){
					return null;
				}
			}
			return equivs;
		}else if(ls.isAtomic()){
			return null;
		}else if(statement.getOp().equals(ls.getOp())){
			for(int i = 0; i<statement.getOp().numOperands(); i++){
				HashMap<Integer,LogicStatement> equiv_new = check_equiv(ls.getChild(i),statement.getChild(i));
				if(equiv_new == null){
					return null;
				}
				for(Integer key: equiv_new.keySet()){
					LogicStatement currentequiv = equivs.get(key);
					LogicStatement newEquiv = equiv_new.get(key);
					if(currentequiv == null){
						equivs.put(key, newEquiv);
					}else if(!currentequiv.equals(newEquiv)){
						return null;
					}
				}
			}
			return equivs;
		}
		return null;
	}
	
	public String toString(){
		return ls1.toString() +"<-->" + ls2.toString();
	}
	
}
