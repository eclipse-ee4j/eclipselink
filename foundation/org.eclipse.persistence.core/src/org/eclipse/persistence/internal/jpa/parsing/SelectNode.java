/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.parsing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Represent a SELECT
 * <p><b>Responsibilities</b>:<ul>
 * <li> Hold the distinct status
 * <li> Modify a query based on the contents
 * </ul>
 *
 * <p>The SELECT statement determines the return type of an EJBQL query.
 * The SELECT may also determine the distinct state of a query
 *
 * A SELECT can be one of the following:
 * <pre>
 *  1. SELECT OBJECT(someObject)... This query will return a collection of objects
 *  2. SELECT anObject.anAttribute ... This will return a collection of anAttribute
 *  3. SELECT &lt;aggregateFunction&gt; ... This will return a single value
 *      The allowable aggregateFunctions are: AVG, COUNT, MAX, MIN, SUM
 *          SELECT AVG(emp.salary)... Returns the average of all the employees salaries
 *          SELECT COUNT(emp)... Returns a count of the employees
 *          SELECT COUNT(emp.firstName)... Returns a count of the employee's firstNames
 *          SELECT MAX(emp.salary)... Returns the maximum employee salary
 *          SELECT MIN(emp.salary)... Returns the minimum employee salary
 *          SELECT SUM(emp.salary)... Returns the sum of all the employees salaries
 * </pre>
 * @author Jon Driscoll
 * @since TopLink 5.0
 */
public class SelectNode extends QueryNode {

    private List selectExpressions = new ArrayList();
    private List identifiers = new ArrayList();

    private boolean distinct = false;
    
    public SelectNode() {
    }

    public List getSelectExpressions() {
        return selectExpressions;
    }

    public void setSelectExpressions(List exprs) {
        selectExpressions = exprs;
    }
    
    public List getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List identifiers) {
        this.identifiers = identifiers;
    }

    public boolean usesDistinct() {
        return distinct;
    }
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * Returns a DatabaseQuery instance representing the owning
     * ParseTree. This implementation returns a ReadAllQuery for simple SELECT
     * queries and a ReportQuery otherwise.
     */ 
    public DatabaseQuery createDatabaseQuery(ParseTreeContext context) {
        
        // TODO: This optimization needs to be revisited because it causes GlassFish issues: 2084 and 2171
        // These issues have been solve in GlassFish by always generating a ReportQuery
        // The same fix has not been made in Oracle TopLink because it disables some advanced JPA Query Hints
        ObjectLevelReadQuery query;
        if (isReadAllQuery(context)) {
            query = new ReadAllQuery();
        } else {
            query = new ReportQuery();
        }
        query.dontUseDistinct(); //gf bug 1395- prevents using distinct unless user specified
        return query;
    }

    /** 
     * Returns true if the SELECT clause consists of a single expression
     * returning the base identification variable of the query and if the base
     * variable is defined as a range variable w/o FETCH JOINs.
     */
    private boolean isReadAllQuery(ParseTreeContext context) {
        if (!isSingleSelectExpression()) {
            // multiple expressions in the select clause => ReportQuery
            return false;
        }
        
        Node node = getFirstSelectExpressionNode();
        if (!node.isVariableNode()) {
            // Does not select an identification variable (e.g. projection or
            // aggregate function) =>  ReportQuery
            return false;
        }
        String variable = ((VariableNode)node).getCanonicalVariableName();
        
        // Note, the base variable in ParseTreeContext is not yet set =>
        // calculate it
        String baseVariable = getParseTree().getFromNode().getFirstVariable();
        if (!context.isRangeVariable(baseVariable)) {
            // Query's base variable is not a range variable.
            return false;
        }

        // Bug 393470
        // Use ReportQuery for GROUP BY / HAVING clauses in ANTLR
        if (getParseTree().hasGroupBy() || getParseTree().hasHaving()) {
            return false;
        }
        
        // Use ReadAllQuery if the variable of the SELECT clause expression is
        // the base variable
        return baseVariable.equals(variable);
    }

    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(DatabaseQuery theQuery, GenerationContext context) {
        ObjectLevelReadQuery readQuery = (ObjectLevelReadQuery)theQuery;
        if (selectExpressions.isEmpty()) {
            return;
        }

        //set the distinct state
        //BUG 3168673: Don't set distinct state if we're using Count
        if (!(isSingleSelectExpression() && getFirstSelectExpressionNode().isCountNode())) {
            // Set the distinct state for the query
            if (usesDistinct()) {
                getParseTree().setDistinctState(ObjectLevelReadQuery.USE_DISTINCT);
                readQuery.setDistinctState(ObjectLevelReadQuery.USE_DISTINCT);
            }
        }

        if (readQuery instanceof ReportQuery) {
            ReportQuery reportQuery = (ReportQuery)readQuery;
            reportQuery.returnWithoutReportQueryResult();
            if (isSingleSelectExpression()) {
                reportQuery.returnSingleAttribute();
            }
        }
        SelectGenerationContext selectContext = (SelectGenerationContext)context;
        for (int i=0;i<selectExpressions.size();i++){
            Node node = (Node)selectExpressions.get(i);
            if (selectingRelationshipField(node, context)) {
                selectContext.useOuterJoins();
            }
            if (node.isAliasableNode() && identifiers != null){
                String alias = (String)identifiers.get(i);
                if (alias != null){
                    ((AliasableNode)node).setAlias(alias);
                }
            }
            node.applyToQuery(readQuery, context); 
            selectContext.dontUseOuterJoins();
        }

        //indicate on the query if "return null if primary key null"
        //This means we want nulls returned if we expect an outer join
        readQuery.setShouldBuildNullForNullPk(this.hasOneToOneSelected(context));

    }

    public boolean hasOneToOneSelected(GenerationContext context) {
        // Iterate the select expression and return true if one of it has a
        // oneToOne selected.
        for (Iterator i = selectExpressions.iterator(); i.hasNext();) {
            Node node = (Node)i.next();
            if (hasOneToOneSelected(node, context)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Answer true if there is a one-to-one relationship selected.
     * This includes a chain of relationships.
     * True: SELECT employee.address FROM ..... //Simple 1:1
     * True: SELECT a.b.c.d FROM ..... //where a->b, b->c and c->d are all 1:1.
     * False: SELECT OBJECT(employee) FROM ..... //simple SELECT
     * False: SELECT phoneNumber.areaCode FROM ..... //direct-to-field
     */
    private boolean hasOneToOneSelected(Node node, GenerationContext context) {
        //BUG 3240484: Not SELECTing 1:1 if it's in a COUNT
        if (node.isCountNode()) {
            return false;
        }

        if (node.isAggregateNode()) {
            // delegate to aggregate expression
            return hasOneToOneSelected(node.getLeft(), context);
        }
         
        if (node.isVariableNode()){
            return !nodeRefersToObject(node, context);
        }

        if (node.isConstructorNode()) {
            List args = ((ConstructorNode)node).getConstructorItems();
            for (Iterator i = args.iterator(); i.hasNext();) {
                Node arg = (Node)i.next();
                if (hasOneToOneSelected(arg, context)) {
                    return true;
                }
            }
            return false;
        }
      
        // check whether it is a direct-to-field mapping
        return !selectingDirectToField(node, context);
    }

    /**
     * Verify that the selected alias is a valid alias. If it's not valid,
     * an Exception will be thrown, likely JPQLException.aliasResolutionException.
     *
     * Valid: SELECT OBJECT(emp) FROM Employee emp WHERE ...
     * Invalid: SELECT OBJECT(badAlias) FROM Employee emp WHERE ...
     */
    public void verifySelectedAlias(GenerationContext context) {
        for (Iterator i = selectExpressions.iterator(); i.hasNext();) {
            Node node = (Node)i.next();
            //if the node is a DotNode, there is no selected alias
            if (node.isDotNode()) {
                return;
            }
            node.resolveClass(context);
        }
    }

    /**
     * Answer true if the variable name given as argument is SELECTed.
     *
     * True: "SELECT OBJECT(emp) ...." & variableName = "emp"
     * False: "SELECT OBJECT(somethingElse) ..." & variableName = "emp"
     */
    public boolean isSelected(String variableName) {
        for (Iterator i = selectExpressions.iterator(); i.hasNext();) {
            Node node = (Node)i.next();
            //Make sure we've SELECted a VariableNode
            if (node.isVariableNode() && 
                ((VariableNode)node).getCanonicalVariableName().equals(variableName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSelectNode() {
        return true;
    }

    /**
     * Check the select expression nodes for a path expression starting with a
     * unqualified field access and if so, replace it by a qualified field
     * access. 
     */
    public Node qualifyAttributeAccess(ParseTreeContext context) {
        for (int i = 0; i < selectExpressions.size(); i++) {
            Node item = (Node)selectExpressions.get(i);
            selectExpressions.set(i, item.qualifyAttributeAccess(context));
        }
        return this;
    }

    /**
     * Validate node.
     */
    public void validate(ParseTreeContext context) {
        for (Iterator i = selectExpressions.iterator(); i.hasNext(); ) {
            Node item = (Node)i.next();
            item.validate(context);
        }
    }

    /**
     * Answer the class associated with my left node.
     */
    public Class resolveClass(GenerationContext context) {
        return getReferenceClass(context);
    }
    
    /**
     * Return a EclipseLink expression generated using the left node.
     */
    public Expression generateExpression(GenerationContext context) {
        return null;
    }

    /**
     * Compute the Reference class for this query.
     * @return the class this query is querying for
     */
    public Class getReferenceClass(GenerationContext context) {
        return getClassOfFirstVariable(context);
    }
    
    private Class getClassOfFirstVariable(GenerationContext context) {
        Class clazz = null;
        String variable = getParseTree().getFromNode().getFirstVariable();
        ParseTreeContext parseTreeContext = context.getParseTreeContext();
        if (parseTreeContext.isRangeVariable(variable)) {
            String schema = parseTreeContext.schemaForVariable(variable);
            // variables is defines in a range variable declaration, so there
            // is a schema name for this variable
            clazz = parseTreeContext.classForSchemaName(schema, context);
        } else {
            // variable is defined in a JOIN clause, so there is a a defining
            // node for the variable
            Node path = parseTreeContext.pathForVariable(variable);
            clazz = path.resolveClass(context);
        }
        return clazz;
    }

    /**
     * Answer true if a variable in the IN clause is SELECTed
     */
    public boolean isVariableInINClauseSelected(GenerationContext context) {
        for (Iterator i = selectExpressions.iterator(); i.hasNext();) {
            Node node = (Node)i.next();
        
            if (node.isVariableNode()) {
                String variableNameForLeft = ((VariableNode)node).getCanonicalVariableName();
                if (!context.getParseTreeContext().isRangeVariable(variableNameForLeft)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Answer true if this node refers to an object described later in the EJBQL
     * True: SELECT p FROM Project p
     * False: SELECT p.id FROM Project p
     */
    public boolean nodeRefersToObject(Node node, GenerationContext context) {
        if (!node.isVariableNode()){
            return false;
        }
        String name = ((VariableNode)node).getCanonicalVariableName();
        String alias = context.getParseTreeContext().schemaForVariable(name);
        if (alias != null){
            ClassDescriptor descriptor = context.getSession().getDescriptorForAlias(alias);
            if (descriptor != null){
                return true;
            }
        }
        return false;
    }

    private boolean selectingRelationshipField(Node node, GenerationContext context) {
        if ((node == null) || !node.isDotNode()) {
            return false;
        }
        TypeHelper typeHelper = context.getParseTreeContext().getTypeHelper();
        Node path = node.getLeft();
        AttributeNode attribute = (AttributeNode)node.getRight();
        return typeHelper.isRelationship(path.getType(), 
                                         attribute.getAttributeName());
    }

    /**
     * Answer true if the SELECT ends in a direct-to-field.
     * true: SELECT phone.areaCode
     * false: SELECT employee.address
     */
    private boolean selectingDirectToField(Node node, GenerationContext context) {

        if ((node == null) || !node.isDotNode()) {     
            return false;
        }
        return ((DotNode)node).endsWithDirectToField(context);
    }

    /** 
     * Returns the first select expression node.
     */
    private Node getFirstSelectExpressionNode() {
        return selectExpressions.size() > 0 ? 
            (Node)selectExpressions.get(0) : null;
    }

    private boolean isSingleSelectExpression() {
        return selectExpressions.size() == 1;
    }    
    
}
