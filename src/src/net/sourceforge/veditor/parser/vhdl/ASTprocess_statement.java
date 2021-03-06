/**
 * 
 * This file is based on the VHDL parser originally developed by
 * (c) 1997 Christoph Grimm,
 * J.W. Goethe-University Frankfurt
 * Department for computer engineering
 *
 **/
package net.sourceforge.veditor.parser.vhdl;



/* Generated By:JJTree: Do not edit this line. ASTprocess_statement.java */
/* JJT: 0.3pre1 */

public class ASTprocess_statement extends SimpleNode {
  ASTprocess_statement(int id) {
    super(id);
  }

  public String getIdentifier(){
	  if(jjtGetChild(0) instanceof ASTidentifier){
		  return ((ASTidentifier)jjtGetChild(0)).name;
	  }
	  return null;
  }  

  /**
   * semantic-checks for process_statement
   * - identifiers at beginning and end must match
   * - ...
   */
  public void Check()
  {
	if(jjtGetChild(0) instanceof ASTidentifier){
		String s1 = ((ASTidentifier)jjtGetChild(0)).name;
	    int i = jjtGetNumChildren()-1;
	    if (jjtGetChild(i).toString() == "identifier")
	    {
	      if ( s1.compareToIgnoreCase(((ASTidentifier)jjtGetChild(i)).name) != 0)
	    	  getErrorHandler().Error("identifiers don't match: "+s1+"/="+
	             ((ASTidentifier)jjtGetChild(i)).name,null);
	    }
	    CheckSIWGLevel1();
	}
  }
}
