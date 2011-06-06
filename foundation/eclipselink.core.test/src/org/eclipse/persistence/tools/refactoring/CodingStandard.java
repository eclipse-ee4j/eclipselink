/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.tools.refactoring;

// no blank line here - first specify Java imports:
import java.lang.reflect.*;
// blank line - next specify all the TopLink imports:
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;
// another blank line
/**
 * <p><b>Purpose</b>: Describe the purpose of the Class.
 * <p><b>Responsibilities</b>:<ul>
 * <li> class responsibility
 * <li> class responsibility
 * </ul>
 *
 *  @author ABC
 *  @since TOPLink/Java 1.0
 */
// yet another blank line
public class CodingStandard {
// still another blank line
    /** Put one line comment describing the following variable */
    protected String giveDescriptiveNameToVariables;
    
    /** Put one line comment describing the following variable */
    protected String useTabsToIndentVariables;
    
    /** Put one line comment describing the following variable */
    protected String useSingleLineSpacingBetweenVariables;

    /** Put one line comment describing the following variable */
    protected String commentShouldBeJustBeforeVariable;

    /** Put one line comment describing the following variable */
    protected String useSpacingAfterAndBeforeClassOpenAndCloseBrackets;

    /** Put one line comment describing the following variable */
    protected String allVariablesAreProtected;
    
    protected String commentOnlyImportantVariables;
    // final blank line
    /**
     * First of all, we should not access variables directly in the methods. 
     * But if we do then always use this.attributeName notation.
     */ 
    public void accessingClassAttributes() 
    {

    }
    
    /**
     * A blank line should always be used in the following circumstances:
     *      - between logical sections inside a method - to improve readability
     *      - between the method comment and the method declaration
     */ 
    public void blankLines() 
    {
    
    }
    
    /**
     * Blank spaces should always be used in the following circumstances:
     *      - A keyword followed by a parenthesis should be separated by a space:
     *              while (true) {
     *                  ...
     *              }
     *
     *      - Note that a blank should not be used between a method name and its opening
     *        parenthesis. This helps to distinguish keywords from method calls.
     *
     *      - Blanks should appear after commas in argument lists.
     *
     *      - All binary operators should be separated from their operands by spaces.
     *
     *      - The expressions in a "for" statement should be separated by blanks. 
     *              for (expr1; expr2; expr3)
     *
     *      - Casts should be followed by a blank.
     *              (void) method((byte) aNum, (Object) x);
     *
     */
    public void blankSpaces() 
    {

    }
    
    /**
     * A boolean variable should always be prefixed with conjunctions like is, has, does, should, etc.
     * Examples: isNullAllowed, shouldMaintainCache, doesObjectExist, hasTables
     *
     * The get accessor for such variables should be the name of the variable itself and the set accessor
     * is the usual one
     *          Example isNullAllowed(), setIsNullAllowed(boolean value)
     *
     * With each boolean variable we should also provide two modifiers. 
     *          Example 
     *              allowNull()
     *              {
     *                  setIsNullAllowed(true);
     *              }
     *              
     *              dontAllowNull()
     *              {
     *                  setIsNullAllowed(true);
     *              }                   
     */ 
    public void booleanVariable() 
    {

    }
    
    /**
     * It's a good idea to declare all the varibles at the beginning of a code block as
     * variables defined in the middle of the block can be confusing.
     */ 
    public void declarationsInTheMethod(Session firstArgument, String secondArgument)
    { /* Place the starting bracket on a new line */
      /* One declaration per line is recommended since it encourages commenting */
        int level,      // indentation level
            size;       // size of table
        /* New declaration should start on new line */
        float foo,
              bar;
        /* Give one line space before coding method body */
        methodBody();       
    }
    
    /**
     * Never break for-statements into multiple lines.
     * Break down the components of the for-statement into multiple lines if necessary.
     */  
    public void forStatements()
    { 
        /* WRONG 
         * The following loop puts too much information on the same line:
         *
         * for (Enumeration mappings = getQuery().getDescriptor().getMappings().elements(); mappings.hasMoreElements(); ) {
         *      some statements;
         *  }
         *
         */
    
        /* RIGHT
         *
         *  Vector mappings = getQuery().getDescriptor().getMappings();
         *  for (Enumeration mappingsEnum = mappings.elements(); mappingsEnum.hasMoreElements(); ) {
         *      some statements;
         *  }
         *
         */
    }
    
    /**
     * Always use the braces {}. This makes the statement more clear and prevents
     * some sloppy bugs (i.e if you add a second line).
     * WRONG:
     * if (condition)
     *      doStuff();
     * else
     *      doOtherStuff();
     */  
    public void ifElseStatements()
    {
        boolean condition = true;
    
        if (condition) {
            //statements
        }
    
        if (condition) {
            //statements
        } else {
            //statements
        }

        if (condition) {
            //statements
        } else if (condition) {
            //statements
        } else {
            //statements
        }
    }
    
    /**
     * Avoid long statements. Maintainence is a nightmare.
     */ 
    public void longStatements()
    {
        /*
         * WRONG:
         * setTableDefinition(getTableDefinitionNamed(((DatabaseTable)getMapping().getDescriptor().getTables().firstElement()).getName()));
         *
         * RIGHT:
         * The right approach to this statement would be to break it up into smaller statements. This would mean few more
         * lines and local variables, but will be easy to understand
         *
         * DatabaseTable table = (DatabaseTable)getMapping().getDescriptor().getTables().firstElement();
         * String tableName = table.getName();
         * TableDefinition tableDefinition = getTableDefinitionNamed(tableName);
         * setTableDefinition(tableDefinition);
         */
    }
    
    /**
     * NOTE:
     * A compound statement is a statement that contains several other statements enclosed in braces "{}".
     *  The enclosed statements should be indented one more level than the compound statement.
     *
     *  The opening left brace should be at the end of the line beginning the compound statement,
     *  and the closing right brace should begin a line and be indented to match the beginning of the 
     *  compound statement.
     *
     *  Braces are used around all statements, even single statements, when they are part of a control 
     *  structure, such as an "if-else" or "for" statement. This makes it easier to add or delete 
     *  statements without accidentally introducing bugs due to forgetting to add braces.
     */  
    public void methodBody()
    {
        longStatements();
        ifElseStatements();
        forStatements();
        whileStatements();
        tryCatchStatements();
    }
    
    /**
     * INTERNAL:
     * Give a full description of the user callable method. Make sure it is javadoc compatible.
     * - blank line -
     * @param argument1 This is optional, give full description only if neccessary.
     * @exception InvocationTargetException This is optional, give full description only if neccessary.
     * @return String This is optional, give full description only if neccessary.
     */
    public String methodCommentsForNonAPI(String argument1, String argument2)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        String descriptiveVariable = "The variable should be descriptive enough to convey its meaning.";
    
        return descriptiveVariable;
    }
    
    /**
     * PUBLIC:
     * Give a full description of the user callable method. Make sure it is javadoc compatible.
     *  - blank line -
     * @param argument1 This is optional, give full description only if neccessary.
     * @exception InvocationTargetException This is optional, give full description only if neccessary.
     * @return String This is optional, give full description only if neccessary.
     */
    public String methodCommentsForUserAPI(String argument1, String argument2)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        String descriptiveVariable = "The variable should be descriptive enough to convey its meaning.";
    
        return descriptiveVariable;
    }
    
    /**
     * method comments
     */
    public void methodDeclarationIsLong(String argument1, String argument2)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        // do something
    }
    
    /**
     * method Comments
     */
    public void methodDeclarationIsReallyLong(
            String argument1,
            String argument2,
            String argument3,
            String ifYouHaveMoreArgumentsThenCheckYourMethod)
        throws
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException
    {
        // do something
    }
    
    /**
     * Instead of prefixing the parameter "useTabsToIndentVariable" with an article (e.g. a, an, the)
     * and then assigning to the class attribute we can do the following assignment.
     *
     * NOTE:
     * Prefixing parameters in non accessor methods makes sense because we can differentiate between
     * local variables and passed parameters.
     */
    public void setAccessor(String useTabsToIndentVariable)
    {
        this.useTabsToIndentVariables = useTabsToIndentVariable;    
    }
    
    /**
     * TopLink exceptions are all runtime exceptions - so they do not have to be caught.
     * If the method throws the exception then it must be declared in the "throws" clause.
     * The one exception is if a runtime exception is caught for cleanup purposes,
     * then it should not be re-thrown.
     * A method that calls a method that throws a TopLink exception should not also throw it,
     * the exception to this is database and optimistic lock exceptions which must always
     * be thrown up to the user callable methods.
     */  
    public void toplinkExceptions () throws DatabaseException, QueryException
    {
        /*
        Cursor cursor = prepareCuror();
        try {
            if (cursor.isEmpty()) {
                throw new QueryException("This must not be empty because ...", getQuery());
            }
            doStuff(cursor);    
        } catch (RuntimeException exception) {
            cursor.close();
            throw exception;
        }   
        */  
    }
    
    public void tryCatchStatements ()
    {
        /*
        try {
            statements;
        } catch (Exception exception) {
            statements;
        }   
        */  
    }
    
    public void whileStatements ()
    {
    /*
     * A while statement should have the following form:
     *              while (condition) {
     *                  statement;
     *              }
     *
     * An empty while statment should have the following form:
     *              while (condition);
     */
    }
}
