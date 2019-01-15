package frontend;

import java.util.HashSet;
import logicTree.LogicStatement;


public class UnificationSet {
	private HashSet<LogicStatement> positive;
	private HashSet<LogicStatement> negative;
	
	public UnificationSet(){
		positive = new HashSet<LogicStatement>();
		negative = new HashSet<LogicStatement>();
	}
	
	public void addAll(HashSet<LogicStatement> inp){
		for(LogicStatement ls:inp){
			if(ls.getOp() == null){
				positive.add(ls);
			}
			else if(ls.getOp().toString().equals("not")){
				negative.add(ls.getChild(0));
			}
			else{
				positive.add(ls);
			}
		}
	}
	
	public void add(LogicStatement ls){
		if(ls.getOp() == null){
			positive.add(ls);
		}
		else if(ls.getOp().toString().equals("not")){
			negative.add(ls.getChild(0));
		}
		else{
			positive.add(ls);
		}
	}
	
	public int contains(LogicStatement ls){
		if(positive.contains(ls)){
			return 1;
		}
		if(negative.contains(ls)){
			return -1;
		}
		return 0;
	}
	
	public int size(){
		return positive.size()+negative.size();
	}
	
	public String toString(){
		String s = "[";
		for(LogicStatement ls: positive){
			s+=ls.toString()+", ";
		}
		for(LogicStatement ls: negative){
			s+="~"+ls.toString()+", ";
		}
		s = s.substring(0,s.length()-2);
		s+="]";
		return s;
	}
	
	public boolean equals(UnificationSet left){
		for(LogicStatement ls:positive){
			if(!left.positive.contains(ls)){
				return false;
			}
		}
		for(LogicStatement ls:negative){
			if(!left.negative.contains(ls)){
				return false;
			}
		}
		for(LogicStatement ls:left.positive){
			if(!positive.contains(ls)){
				return false;
			}
		}
		for(LogicStatement ls:left.negative){
			if(!negative.contains(ls)){
				return false;
			}
		}
		return true;
	}
	
	public static UnificationSet combine(UnificationSet right, UnificationSet left){
		UnificationSet ret = new UnificationSet();
		for(LogicStatement ls:right.positive){
			if(!left.negative.contains(ls)){
				ret.positive.add(ls);
			}
		}
		for(LogicStatement ls:right.negative){
			if(!left.positive.contains(ls)){
				ret.negative.add(ls);
			}
		}
		for(LogicStatement ls:left.positive){
			if(!right.negative.contains(ls)){
				ret.positive.add(ls);
			}
		}
		for(LogicStatement ls:left.negative){
			if(!right.positive.contains(ls)){
				ret.negative.add(ls);
			}
		}
		return ret;
	}
}