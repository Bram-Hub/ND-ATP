package logicTree;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestParser {

	Parser parser;
	@Before
	public void setUp() throws Exception {
		parser = new Parser();
	}

	@After
	public void tearDown() throws Exception {
		parser = null;
	}
	
	@Test
	public void testParse()throws Exception{
		LogicStatement ls = parser.parse("(and (or A C) (if B TRUE))");
		assertEquals("(and (or A C) (if B True))", ls.toString());
		assertEquals(false,ls.bool_val());
		ls.learn_atomics();
		System.out.println(ls.list_atomics()+" == A B C True (in any order)");
	}
}
