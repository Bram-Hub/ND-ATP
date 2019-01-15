package frontend;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileReader;

import logicRules.LogicRuleSet;
import logicTree.LogicOperator;
import logicTree.LogicParserException;
import logicTree.LogicStatement;
import logicTree.Parser;


/*
 * This isn't very object oriented as is because this was
 * a fairly quick add on, and a partial rewrite of the first
 * iteration, so my design choices are half right, and some of the
 * variable names don't make a whole lot of sense because their 
 * functionality changed.  This really could use a whole 2.0 rewrite
 * with more more of the logic moved into a new object.
 */

public class CNFcmdLine {
	LogicStatement statement;
	Scanner cin;
	Parser parser;
	LogicRuleSet logicRules;
	ArrayList<String> applyRulesOrder;
	ArrayList<UnificationSet> unification_set;
	
	public static void main(String[] args){
		CNFcmdLine cl = new CNFcmdLine();
		cl.welcome();
		cl.loop();
	}
	
	CNFcmdLine(){
		cin = new Scanner(System.in);
		parser = new Parser();
		statement = null;
		logicRules = new LogicRuleSet("cnfrules.txt");
		applyRulesOrder = new ArrayList<String>();
		unification_set = new ArrayList<UnificationSet>();
		try{
			Scanner sc = new Scanner(new FileReader("cnfrules.txt"));
			while(sc.hasNext()){
				String[] s = sc.nextLine().split(":");
				applyRulesOrder.add(s[0]);
			}
		}catch(Exception e){
			System.exit(0);
		}
	}
	
	public void welcome(){
		System.out.println("Welcome to the CNF command line, please enter a statement so I can convert it to CNF\n" +
				"Commands are\n" +
				"go: checks if consistent\n" +
				"clear: clears the current statements");
	}
	
	public void loop(){
		while(true){
			print_unification();
			System.out.print("> ");
			String s = cin.nextLine();
			if(s.toLowerCase().equals("q")) return;
			else if(s.toLowerCase().equals("unify")||
					s.toLowerCase().equals("go")){
				if(unify()){
					System.out.println("Statements Are Consistent");
				}
				else{
					System.out.println("Statement Aren't Consistent");
				}
			}else if(s.toLowerCase().equals("clear")){
				unification_set.clear();
			}else{
				try{
					to_cnf(s);
					add_to_unification_set();
				}catch(LogicParserException e){
					System.out.println("Parser Exception caught, please try again");
				}
			}
		}
	}
	
	public void add_to_unification_set(){
		ArrayList<UnificationSet> alsl = cnf_to_set();
		for(UnificationSet hs:alsl){
			if(unique(hs)){
				unification_set.add(hs);
			}
		}
	}
	
	public void print_unification(){
		if(unification_set.size() == 0) return;
		for(UnificationSet hs:unification_set){
			System.out.print(hs.toString()+" ");
		}
		System.out.println();
	}
	
	public void to_cnf(String val)throws LogicParserException{
		statement = parser.parse(val);
		for(String s:applyRulesOrder){
			apply_rule_recurse(statement,null,s,true);
			//System.out.println(statement.commandLineToString());
		}
	}
	
	public boolean unify(){
		int start = 0;
		while(start < unification_set.size()){
			int end = unification_set.size();
			UnificationSet on = unification_set.get(start);
			boolean changed = false;
			for(int i = start+1; i<end; i++){
				UnificationSet new_set = UnificationSet.combine(on,unification_set.get(i));
				if(new_set.size() == 0){
					System.out.println(on+", "+ unification_set.get(i)+" combine to []");
					return false;
				}
				if(unique(new_set)){
					changed = true;
					System.out.println(on+", "+ unification_set.get(i)+" combine to "+ new_set);
					unification_set.add(new_set);
				}
			}
			if(changed)
				print_unification();
			++start;
		}
		return true;
	}
	
	public boolean unique(UnificationSet set){
		for(UnificationSet hsls: unification_set){
			if(hsls.equals(set)){
				return false;
			}
		}
		return true;
	}
	
	public ArrayList<UnificationSet> cnf_to_set(){
		ArrayList<UnificationSet> cnfset = new ArrayList<UnificationSet>();
		ArrayList<LogicStatement> cnf_list = new ArrayList<LogicStatement>();
		LogicStatement on = statement;
		while(on.getOp() != null&&on.getOp().equals(LogicOperator.AND())){
			cnf_list.add(on.getChild(1));
			on = on.getChild(0);
		}
		cnf_list.add(on);
		for(LogicStatement ls:cnf_list){
			UnificationSet nsls = new UnificationSet();
			while(!ls.isAtomic()&&ls.getOp().equals(LogicOperator.OR())){
				nsls.add(ls.getChild(1));
				ls = ls.getChild(0);
			}
			nsls.add(ls);
			cnfset.add(nsls);
		}
		return cnfset;
	}
	
	
	public void apply_rule_recurse(LogicStatement on, LogicStatement parent, String rule, boolean pos_rule){
		if(on == null||on.isAtomic()){
			return;
		}
		boolean worked = false;
		//if((lo.toString()).equals(on.getOp().toString())){
			if(pos_rule)  worked = apply_pos_rule(rule,on,parent);
			else  worked = apply_neg_rule(rule,on,parent);
		//}
		if(!on.isAtomic()){
			if(worked&&parent==null){
				apply_rule_recurse(statement,null,rule,pos_rule);
			}else if(worked){
				apply_rule_recurse(on.getChild(0),parent,rule,pos_rule);
				apply_rule_recurse(on.getChild(1),parent,rule,pos_rule);
			}else{
				apply_rule_recurse(on.getChild(0),on,rule,pos_rule);
				apply_rule_recurse(on.getChild(1),on,rule,pos_rule);	
			}
		}
	}
	
	private boolean apply_pos_rule(String rule, LogicStatement current, LogicStatement parent){
		LogicStatement ncurrent = logicRules.applyPosRule(rule,current,parser);
		if(ncurrent == null){
			//System.out.println("couldn't apply rule");
			return false;
		}
		if(parent != null){
			if(parent.getChild(0).equals(current)){
				parent.set_left(ncurrent);
			}else{
				parent.set_right(ncurrent);
			}
			current = ncurrent;
			return true;
		}else{
			statement = ncurrent;
			current = ncurrent;
			return true;
		}
	}
	
	private boolean apply_neg_rule(String rule, LogicStatement current, LogicStatement parent){
		LogicStatement ncurrent = logicRules.applyNegRule(rule,current,parser);
		if(ncurrent == null){
			//System.out.println("couldn't apply rule");
			return false;
		}
		if(parent != null){
			if(parent.getChild(0).equals(current)){
				parent.set_left(ncurrent);
			}else{
				parent.set_right(ncurrent);
			}
			current = ncurrent;
			return true;
		}else{
			statement = ncurrent;
			current = ncurrent;
			return true;
		}
	}
}
