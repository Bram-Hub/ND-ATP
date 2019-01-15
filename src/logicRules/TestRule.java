package logicRules;

//import static org.junit.Assert.*;
//import logicTree2.LogicStatement;
import logicTree.Parser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestRule {
	Parser parser;
	@Before
	public void setUp() throws Exception {
		parser = new Parser();
	}

	@After
	public void tearDown() throws Exception {
		parser = null;
	}
	
	
	/*
	@Test
	public void test_demorgans(){
		RuleFromStatements rfs = new RuleFromStatements("(not (and A B))","(or (not A) (not B))");
		LogicStatement ls = parser.parse("(not (and (or A (or A A)) (if TRUE FALSE)))");
		LogicStatement lsnew = ls.ApplyPosRule(rfs);
		System.out.println(lsnew);
		System.out.println(ls);
	}*/
	
	@Test
	public void test_LogicRuleSet(){
		LogicRuleSet lrs = new LogicRuleSet("LogicRules.txt");
		System.out.println(lrs);
	}
	
}
