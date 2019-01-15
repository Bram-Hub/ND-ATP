package frontend;

import java.util.Scanner;
import java.util.Stack;

import logicRules.LogicRuleSet;
import logicTree.LogicOperator;
import logicTree.LogicParserException;
import logicTree.LogicStatement;
import logicTree.Parser;

public class CommandLine {
	LogicStatement workspace;
	LogicStatement current;
	Stack<LogicStatement> parents;
	Parser parser;
	Scanner cin;
	LogicRuleSet logicRules;
	
	
	public static void main(String[] args){
		CommandLine cl = new CommandLine();
		cl.welcome();
		cl.loop();
	}
	
	public CommandLine(){
		parents = new Stack<LogicStatement>();
		workspace = null;
		current = null;
		parser = new Parser();
		cin = new Scanner(System.in);
		logicRules = null;
	}
	
	public void welcome(){
		System.out.println("Welcome\n type h for help, or start using the command line");
	}
	
	public void loop(){
		while(true){
			if(workspace!=null){
				System.out.println(workspace.commandLineToString());
				//System.out.print(current);
			}
			System.out.print("> ");
			String s = cin.nextLine();
			String[] as = s.split(" ");
			if(as[0].equals("quit")) return;
			parse_input(as);
		}
	}
	
	private void parse_input(String[] as){
		try{
		if((as[0].equals("select")||as[0].equals("s"))&&as.length==2){
			select(as[1]);
		}else if((as[0].equals("up")||as[0].equals("u"))&&as.length == 1){
			up();
		}else if((as[0].equals("load"))&&as.length == 2){
			load(as[1]);
		}else if((as[0].equals("apply")||as[0].equals("a"))&&as.length == 3){
			
		}else if(as[0].equals("list_rules")){
			System.out.println(logicRules);
		}else if(as[0].equals("list")||as[0].equals("l")){
			
		}else if(as[0].equals("and_add")){
			String s = "";
			for(int i = 1;i<as.length; i++)
				s+=as[i]+" ";
			and_to_workspace(s);
		}else if(as[0].equals("help")||as[0].equals("h")){
			help();
		}else if(as[0].equals("statement")){
			String a = "";
			for(int i =1;i<as.length;i++){
				a+=as[i]+" ";
			}
			load_string(a);
		}else if(as[0].equals("apply_pos_rule")||as[0].equals("apr")){
			apply_pos_rule(as[1]);
		}else if(as[0].equals("apply_neg_rule")||as[0].equals("anr")){
			apply_neg_rule(as[1]);
		}else{
			System.out.println("Not a command");
		}
		}catch(LogicParserException e){
			System.out.println("Error Parsing caught, please try again");
		}
	}
	
	private void load(String s){
		logicRules = new LogicRuleSet(s);
	}
	
	private void and_to_workspace(String s)throws LogicParserException{
		LogicStatement ls = parser.parse(s);
		LogicStatement nworkspace = new LogicStatement(LogicOperator.AND(),workspace,ls);
		workspace = nworkspace;
		//current = workspace;
		Stack<LogicStatement> newParents = new Stack<LogicStatement>();
		while(!parents.empty()){
			newParents.push(parents.pop());
		}
		newParents.push(nworkspace);
		while(!newParents.empty()){
			parents.push(newParents.pop());
		}
	}
	
	private void load_string(String s) throws LogicParserException{
		current = parser.parse(s);
		workspace = current;
		current.current = true;
		parents.clear();
	}
	
	private void help(){
		System.out.println("===COMMANDS ARE===");
		System.out.println("help (h)\t prints this message\n" +
				"select (s) [r,l]\t Selects the right or left child " +
				"of the current operator to work on\n" +
				"up (u)\t selects the parent operator as current\n" +
				"load [rulefile]\t loads the rulefile (try LogicRules.txt)\n" +
				"apply (a) [pos,neg] [rule]\t applys a given positive or negative rule\n" +
				"list_rules \t Lists all rules\n" +
				"statement [state]\t load statement state and replace workspace" +
				"list (l) [rule-name] \t Lists only the rules labeled as rule-name\n" +
				"and_add [statement]\t adds the statement to your workspace by anding it to the current workspace\n" +
				"apply_pos_rule [rulename]\t apply the left to right rule with name rulename\n" +
				"apply_neg_rule [rulename]\t apply the right to left rule with name rulename");				
	}
	
	private void apply_pos_rule(String rule){
		LogicStatement ncurrent = logicRules.applyPosRule(rule,current,parser);
		if(ncurrent == null){
			System.out.println("couldn't apply rule");
			return;
		}
		if(!parents.empty()){
			LogicStatement parent = parents.peek();
			if(parent.getChild(0).equals(current)){
				parent.set_left(ncurrent);
			}else{
				parent.set_right(ncurrent);
			}
		}else{
			workspace = ncurrent;
		}
		current = ncurrent;
		current.current = true;
	}
	
	private void apply_neg_rule(String rule){
		LogicStatement ncurrent = logicRules.applyNegRule(rule,current,parser);
		if(ncurrent == null){
			System.out.println("couldn't apply rule");
			return;
		}
		if(!parents.empty()){
			LogicStatement parent = parents.peek();
			if(parent.getChild(0).equals(current)){
				parent.set_left(ncurrent);
			}else{
				parent.set_right(ncurrent);
			}
		}else{
			workspace = ncurrent;
		}
		current = ncurrent;
		current.current = true;
	}
	
	private void select(String val){
		parents.push(current);
		if(val.toLowerCase().equals("right")||val.toLowerCase().equals("r")){
			current = current.getChild(1);
		}
		if(val.toLowerCase().equals("left")||val.toLowerCase().equals("l")){
			current = current.getChild(0);
		}
		if(current == null){
			current = parents.pop();
			System.out.println("you tried to switch to a null value. Correcting now.");
			return;
		}
		parents.peek().current = false;
		current.current = true;
	}
	
	private void up(){
		if(!parents.isEmpty()){
			current.current = false;
			current = parents.pop();
			current.current = true;
		}else
			System.out.println("No Parent Found");
	}
}
