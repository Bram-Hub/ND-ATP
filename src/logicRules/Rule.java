package logicRules;

import logicTree.LogicStatement;
import logicTree.Parser;

public interface Rule {
	
	/**
	 * This applies the logic rule one way.  For instance if we
	 * have the double negation rule, this might go from A to (not (not A))
	 * 
	 * @param ln 
	 * @param p
	 * @return Transformed logic statement
	 */
	public LogicStatement rulePosFunction(LogicStatement ln, Parser p);
	
	/**
	 * The applies the logic rule the opposite way ie. (not (not A)) to A
	 * 
	 * @param ln
	 * @param p
	 * @return Transformed Logic Statement
	 */
	public LogicStatement ruleNegFunction(LogicStatement ln, Parser p);
}
