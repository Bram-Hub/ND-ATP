package logicRules;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;

import logicTree.LogicStatement;
import logicTree.Parser;

/**
 * Keeps a set of logic rules accessible by their string name. 
 * If there are more than one of a single name it tries to apply them in some order
 * until it works, or all have been applied unsuccessfully 
 * 
 * @author jrock
 *
 */
public class LogicRuleSet extends HashMap<String,ArrayList<Rule>> {
	private static final long serialVersionUID = 1L;
	
	public LogicRuleSet(String filename){
		try{
			FileReader fin = new FileReader(filename);
			Scanner sc = new Scanner(fin);
			readScanner(sc);
		}catch(IOException E){
			System.err.println("couldn't read file"+filename);
			System.exit(-1);
		}
	}
	
	public LogicRuleSet(Scanner sc){
		readScanner(sc);
	}
	
	public void readScanner(Scanner sc){
		while(sc.hasNext()){
			String line = sc.nextLine();
			String[] linearr = line.split(":");//rulefile should have one rule per line:  Name:stmt1:stmt2
			Rule r = new RuleFromStatements(linearr[1],linearr[2]);
			ArrayList<Rule> val = super.get(linearr[0]);
			
			if(val!=null){
				val.add(r);
			}else{
				val = new ArrayList<Rule>();
				val.add(r);
				super.put(linearr[0],val);
			}
		}
	}
	
	public LogicStatement applyPosRule(String s, LogicStatement ls, Parser p){
		LogicStatement lsnew;
		if(super.get(s)==null){
			return null;
		}
		for(Rule r: super.get(s)){
			lsnew = r.rulePosFunction(ls,p);
			if(!ls.equals(lsnew)){
				return lsnew;
			}
		}
		return null;
	}
	
	public LogicStatement applyNegRule(String s, LogicStatement ls, Parser p){
		LogicStatement lsnew;
		for(Rule r: super.get(s)){
			lsnew = r.ruleNegFunction(ls,p);
			if(!ls.equals(lsnew)){
				return lsnew;
			}
		}
		return null;
	}
	
	public String toString(){
		String ret = "";
		for(String key: super.keySet()){
			for(Rule val: super.get(key)){
				ret+=key+":"+val.toString()+"\n";
			}
		}
		return ret;
	}
}
