# ND ATP
## Authors
2011:
Jason Rock

## About
There should be two .jars once you extract the zip, they should be runnable.  They also have help text, but I'll be honest they probably are more of a reference than a teaching tool.

==AutomatedProver.jar==

Enter some statements that you wish to verify constancy or inconsistency in slate style  ie (and (and A B) C)  {reading arguments with more than 3 operands and parsing them as repeated operations was on my todo, but I never got around to it}

Once you have entered all of the statements type "go" and it will combine the sets from the CNFs of the statement

==StatementCommandLine.jar==

load a rulefile with "load LogicRules.txt" 
Enter a statement with slate style syntax such as "statement (and A (or B C))"
the command line will now look like {(and A (or B C))}
the "[]" represents the current working statement
"select r" would change to
(and A {(or B C)})
now we could apply a rule say commutation of or by "apply_pos_rule Commutation" or "apply_neg_rule Commutation"
for commutation neg or pos doesn't matter for others like "Idempotence_and:(and A A):A"
positive means (and A A) to A and neg means A to (and A A)
So at this point the command line should say (and A {(or C B)})
now type "up" to return to the whole command as the selected
so command line will say {(and A (or C B)}


