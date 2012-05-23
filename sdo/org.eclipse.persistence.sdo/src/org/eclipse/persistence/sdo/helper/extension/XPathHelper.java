/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.sdo.helper.extension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.XPathEngine;
import org.eclipse.persistence.exceptions.ConversionException;

/**
 * This singleton provides support for querying DataObjects
 * via xpath expression where the result is a List containing
 * one or more simple types or DataObjects.
 */
public class XPathHelper {
    private static XPathHelper instance;
    static final int GTE = 4;
    static final int LTE = 5;
    static final int GT = 6;
    static final int LT = 7;
    static final int EQ = 8;
    static final int NEQ = 9;
    static final int AND = 10;
    static final int OR = 11;
    private static final String GTE_STR = ">=";
    private static final String LTE_STR = "<=";
    private static final String GT_STR = ">";
    private static final String LT_STR = "<";
    private static final String EQ_STR = "=";
    private static final String NEQ_STR = "!=";
    private static final String AND_STR = "and";
    private static final String OR_STR = "or";

    /**
     * Returns the one and only instance of this singleton.
     */
    public static XPathHelper getInstance() {
        if (instance == null) {
            instance = new XPathHelper();
        }
        return instance;
    }

    /**
     * Create and return an XPathExpression, using the provided
     * string to create the expression.
     * 
     * @param expression
     * @return
     */
    public XPathExpression prepareExpression(String expression) {
        return new XPathExpression(expression);
    }
    
    /**
     * Evaluate an XPath expression in the specified context and return a List 
     * containing any types or DataObjects that match the search criteria.
     * 
     * @param expression
     * @param dataObject
     * @return List containing zero or more entries
     */
    public List evaluate(String expression, DataObject dataObject) {
        List results = new ArrayList();
        // call into XPathEngine until all functionality is implemented
        if (shouldCallXPathEngine(expression)) {
            return addResultsToList(XPathEngine.getInstance().get(expression, dataObject), results);
        }
        
        return evaluate(expression, dataObject, results);
    }

    /**
     * Evaluate an XPath expression in the specified context and populate 
     * the provided List with any types or DataObjects that match the 
     * search criteria.
     * 
     * @param expression
     * @param dataObject
     * @param results
     * @return
     */
    private List evaluate(String expression, DataObject dataObject, List results) {
        Object result;
        int index = expression.indexOf('/');
        if (index > -1) {
            if (index == (expression.length() - 1)) {
                return addResultsToList(processFragment(expression.substring(0, index), dataObject), results);
            } else {
                result = processFragment(expression.substring(0, index), dataObject);
                if (result instanceof DataObject) {
                    return evaluate(expression.substring(index + 1, expression.length()), (DataObject)result, results);
                } else if (result instanceof List) {
                    // loop over each result, executing the remaining portion of the expression
                    for (Iterator resultIt = ((List) result).iterator(); resultIt.hasNext();) {
                        evaluate(expression.substring(index + 1, expression.length()), (DataObject)resultIt.next(), results);
                    }
                    return results;
                } else {
                    return null;
                }
            }
        }
        return addResultsToList(processFragment(expression, dataObject), results);
    }
    
    /** 
     * Process an XPath expression fragment.  
     *
     * @param frag
     * @param dataObject
     * @return
     */
    private Object processFragment(String xpFrag, DataObject dataObject) {
        // handle self expression
        if (xpFrag.equals(".")) {
            return dataObject;
        }
        // handle containing DataObject expression
        if (xpFrag.equals("..")) {
            return dataObject.getContainer();
        }        
        
        // ignore '@'
        xpFrag = getPathWithAtRemoved(xpFrag);
        
        // handle positional '[]' expression
        int idx = xpFrag.indexOf('[');
        if (idx > -1) {
            return processBracket(xpFrag, dataObject, idx);
        }
        
        // handle non-positional expression
        Property prop = dataObject.getInstanceProperty(xpFrag);
        try {
            return dataObject.get(prop);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Process a positional or query XPath expression fragment.  This method
     * determines if the brackets represent a query or a positional path, 
     * and calls into the appropriate methods accordingly.
     * 
     * @param xpFrag
     * @param dataObject
     * @param idx
     * @return
     */
    private Object processBracket(String xpFrag, DataObject dataObject, int idx) {
        int closeIdx = xpFrag.indexOf(']');
        if (closeIdx == -1 || closeIdx < idx) {
            return null;
        }

        String contents = xpFrag.substring(idx + 1, closeIdx);
        // check for integer index
        if (contents.matches("[1-9][0-9]*")) {
            return processIndex(xpFrag, dataObject, idx, Integer.parseInt(contents) - 1); 
        }

        // check for float index
        if (contents.matches("[1-9].[0-9]*")) {
            Float num = Float.valueOf(contents);
            return processIndex(xpFrag, dataObject, idx, num.intValue() - 1); 
        }

        // check for simple/complex queries
        String reference = xpFrag.substring(0, idx);
        if (contents.indexOf(AND_STR) != -1 || contents.indexOf(OR_STR) != -1) {
            return processComplexQuery(dataObject, reference, contents);
        }
        return processSimpleQuery(dataObject, reference, contents);
    }
    
    /**
     * Process a positional XPath expression fragment.
     * 
     * @param xpFrag
     * @param dataObject
     * @param idx
     * @param idxValue
     * @return
     */
    private Object processIndex(String xpFrag, DataObject dataObject, int idx, int idxValue) {
        try {
            Property prop = dataObject.getInstanceProperty(xpFrag.substring(0, idx));
            List dataObjects = dataObject.getList(prop);
            if (idxValue < dataObjects.size()) {
                return dataObjects.get(idxValue);
            } else {
                // out of bounds                    
                throw new IndexOutOfBoundsException();
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
   
    /**
     * Evaluate the query represented by the XPath Expression fragment against
     * the DataObject. A complex query contains logical operators.
     * 
     * @param dataObject
     * @param reference
     * @param bracketContents
     * @return
     */
    private Object processComplexQuery(DataObject dataObject, String reference, String bracketContents) {
        // convert the expression to postfix notation
        OPStack opStack = new OPStack();
        List expressionParts = opStack.processExpression(bracketContents);

        ArrayList queryParts = new ArrayList();
        // first iteration, create QueryParts from the expression, keeping the
        // position of any 'and' / 'or'
        for (int i=0; i<expressionParts.size(); i++) {
            Token tok = (Token) expressionParts.get(i);
            if (tok.getName().equals(AND_STR) || tok.getName().equals(OR_STR)) {
                queryParts.add(tok.getName());
            } else {
                // assume next three entries make up a query part
                String propertyName = ((Token) expressionParts.get(i)).getName();
                String queryValue = ((Token) expressionParts.get(++i)).getName();
                int operator = getOperandFromString(((Token) expressionParts.get(++i)).getName());
                queryParts.add(new QueryPart(propertyName.trim(), queryValue, operator));
            }
        }

        // get the DataObject(s) to execute the query against
        Property prop = dataObject.getInstanceProperty(reference);
        List objects;
        if (prop.isMany()) {
            objects = dataObject.getList(prop);
        } else {
            objects = new ArrayList();
            DataObject obj = dataObject.getDataObject(prop); 
            if (obj != null) {
                objects.add(obj);
            }
        }

        List valuesToReturn = new ArrayList();

        Iterator iterObjects = objects.iterator();
        while (iterObjects.hasNext()) {
            SDODataObject cur = (SDODataObject)iterObjects.next();
            
            // this iteration, evaluate each QueryPart against the current DataObject
            ArrayList booleanValues = new ArrayList(); 
            for (int j=0; j<queryParts.size(); j++) {
                if (queryParts.get(j).equals(AND_STR) || queryParts.get(j).equals(OR_STR)) {
                    // add 'and'/'or' keeping in correct order 
                    booleanValues.add(queryParts.get(j));
                } else {
                    // assume QueryPart - evaluate and add the result
                    QueryPart qp = (QueryPart) queryParts.get(j);
                    booleanValues.add(qp.evaluate(cur));
                }
            }
            
            // at this point we have a list of boolean values with 
            // a mix of 'and'/'or' that need to be applied to get
            // the final result for this DataObject
            
            // iterate L-R, looking for 'and' / 'or' - when one is encountered,
            // apply it to the previous two booleans, repeating this until a 
            // single result is achieved
            for (int k=0; k<booleanValues.size(); k++) {
                if (booleanValues.get(k).equals(AND_STR) || booleanValues.get(k).equals(OR_STR)) {
                    if (k >= 2) {
                        Boolean b1 = (Boolean) booleanValues.get(k-1);
                        Boolean b2 = (Boolean) booleanValues.get(k-2);
                        int logicalOp = getOperandFromString((String) booleanValues.get(k));
                        booleanValues.remove(k);
                        booleanValues.remove(k-1);
                        booleanValues.set(k-2, evaluate(b1, b2, logicalOp));
                        k=0;
                    }
                }
            }
            // if there isn't a single result something went wrong...
            if (booleanValues.size() == 1) {
                if ((Boolean)booleanValues.get(0)) {
                    valuesToReturn.add(cur); 
                }
            }
        }
        return valuesToReturn;
    }
    
    private boolean evaluate(boolean b1, boolean b2, int op) {
        switch (op) {
        case AND:
            return b1 && b2;
        case OR:
            return b1 || b2;
        }
        return false;
    }

    /**
     * Evaluate the query represented by the XPath Expression fragment against
     * the DataObject. A 'simple' query has not logical operators.
     * 
     * @param dataObject
     * @param reference
     * @param query
     * @return
     */
    private Object processSimpleQuery(DataObject dataObject, String reference, String query) {
        Property prop = dataObject.getInstanceProperty(reference);
        
        List objects;
        if (prop.isMany()) {
            objects = dataObject.getList(prop);
        } else {
            objects = new ArrayList();
            DataObject obj = dataObject.getDataObject(prop); 
            if (obj != null) {
                objects.add(obj);
            }
        }

        List valuesToReturn = new ArrayList();
        QueryPart queryPart = new QueryPart(query);

        Iterator iterObjects = objects.iterator();
        while (iterObjects.hasNext()) {
            SDODataObject cur = (SDODataObject)iterObjects.next();

            if (queryPart.evaluate(cur)) {
                valuesToReturn.add(cur);
            }
        }
        return valuesToReturn;    
    }
    
    // ----------------------------- Convenience Methods ----------------------------- //
    
    /**
     * Convenience method that will add the provided object to the 'results' list 
     * if the object is non-null.  If the object represents a list, each non-null
     * entry will be added to the results list.
     * 
     * @param obj
     * @param results
     * @return
     */
    protected List addResultsToList(Object obj, List results) {
        if (obj != null) {
            if (obj instanceof List) {
                for (Iterator resultIt = ((List) obj).iterator(); resultIt.hasNext();) {
                    Object nextResult = resultIt.next();
                    if (nextResult != null) {
                        results.add(nextResult);
                    }
                }
            } else {
                results.add(obj);
            }
        }
        return results;
    }
    
    /**
     * Convenience method that strips off '@' portion, if
     * one exists.
     * 
     * @param qualifiedName
     * @return
     */
    protected String getPathWithAtRemoved(String expression) {
        int index = expression.indexOf('@');
        if (index > -1) {
            if (index > 0) {
                StringBuffer sbuf = new StringBuffer(expression.substring(0, index));
                sbuf.append(expression.substring(index + 1, expression.length()));
                return sbuf.toString();
            } 
            return expression.substring(index + 1, expression.length());
        }
        return expression;
    }
    
    /**
     * Convenience method that strips off 'ns0:' portion, if
     * one exists.
     * 
     * @param qualifiedName
     * @return
     */
    protected String getPathWithPrefixRemoved(String expression) {
        int index = expression.indexOf(':');
        if (index > -1) {
            return expression.substring(index + 1, expression.length());
        }
        return expression;
    }
    
    private int getOperandFromString(String op) {
        if (op.equals(EQ_STR)) {
            return EQ;
        }
        if (op.equals(NEQ_STR)) {
            return NEQ;
        }
        if (op.equals(GT_STR)) {
            return GT;
        }
        if (op.equals(LT_STR)) {
            return LT;
        }
        if (op.equals(GTE_STR)) {
            return GTE;
        }
        if (op.equals(LTE_STR)) {
            return LTE;
        }
        if (op.equals(AND_STR)) {
            return AND;
        }
        if (op.equals(OR_STR)) {
            return OR;
        }
        return -1;
    }
    
    private String getStringFromOperand(int op) {
        switch(op) {
        case EQ:
            return EQ_STR;
        case NEQ:
            return NEQ_STR;
        case GTE:
            return GTE_STR;
        case LTE:
            return LTE_STR;
        case GT:
            return GT_STR;
        case LT:
            return LT_STR;
        case AND:
            return AND_STR;
        case OR:
            return OR_STR;
        }
        return "";
    }
    
    /**
     * Convenience method for determining if XPathEngine should be 
     * called, i.e. the XPath expression contains functionality
     * not yet supported. 
     *  
     * @param expression
     * @return
     */
    protected boolean shouldCallXPathEngine(String expression) {
        return false;
    }
    
    // ----------------------------- Inner Classes ----------------------------- //

    /**
     * A QueryPart knows the name of the property to be queried against on a 
     * given DataObject, as well as the value to be used in the comparison.
     */
    public class QueryPart {
        String propertyName, queryValue;
        int relOperand;
        int logOperand;
        
        /**
         * This constructor breaks the provided query into 
         * property name and query value parts.
         *  
         * @param query
         */
        public QueryPart(String query) {
            processQueryContents(query);
        }

        /**
         * This constructor sets a logical operator and breaks 
         * the provided query into property name and query 
         * value parts.
         *  
         * @param query
         */
        public QueryPart(String property, String value, int op) {
            relOperand = op;
            propertyName = property;
            queryValue = formatValue(value);
        }

        /**
         * Breaks the provided query into property name and 
         * query value parts
         */
        private void processQueryContents(String query) {
            int idx = -1, operandLen = 1;
            relOperand = 1;
            
            if ((idx = query.indexOf(GTE_STR)) != -1) {
                relOperand = GTE;
                operandLen = 2;
            } else if ((idx = query.indexOf(LTE_STR)) != -1) {
                relOperand = LTE;
                operandLen = 2;
            } else if ((idx = query.indexOf(NEQ_STR)) != -1) {
                relOperand = NEQ;
                operandLen = 2;
            } else if ((idx = query.indexOf(GT_STR)) != -1) {
                relOperand = GT;
            } else if ((idx = query.indexOf(LT_STR)) != -1) {
                relOperand = LT;
            } else if ((idx = query.indexOf(EQ_STR)) != -1) {
                relOperand = EQ;
            }
            
            propertyName = query.substring(0, idx).trim();
            queryValue = formatValue(query.substring(idx + operandLen));
        }
        
        private String formatValue(String value) {
            int openQIdx = value.indexOf('\'');
            int closeQIdx = value.lastIndexOf('\'');
            if (openQIdx == -1 && closeQIdx == -1) {
                openQIdx = value.indexOf("\"");
                closeQIdx = value.lastIndexOf("\"");
            }
            if (openQIdx != -1 && closeQIdx != -1 && openQIdx < closeQIdx) {
                value = value.substring(openQIdx + 1, closeQIdx);
            } else {
                // if the value is not enclosed on quotes, trim off any whitespace
                value = value.trim();
            }
            return value;
        }
        
        /**
         * Indicate if the query represented by this QueryPart evaluates to 
         * true or false when executed on a given DataObject.
         * 
         * @param dao
         * @return
         */
        public boolean evaluate(SDODataObject dao) {
            Object queryVal = queryValue;
            Object actualVal = null;

            SDOProperty prop = dao.getInstanceProperty(propertyName);
            try {
                SDOXMLHelper sdoXMLHelper = (SDOXMLHelper) dao.getType().getHelperContext().getXMLHelper();
                queryVal = sdoXMLHelper.getXmlConversionManager().convertObject(queryValue, prop.getType().getInstanceClass());
            } catch (ConversionException e) {
                // do nothing
            }

            List values;
            if (!prop.isMany()) {
                values = new ArrayList();
                values.add(dao.get(prop));
            } else {
                values = dao.getList(prop);
            }
            
            Iterator iterValues = values.iterator();
            while (iterValues.hasNext()) {
                actualVal = iterValues.next();
                if (actualVal == null) { 
                    continue;
                }
                
                int resultOfComparison;
                try {
                    resultOfComparison = ((Comparable)actualVal).compareTo(queryVal) ;
                } catch (Exception x) {
                    continue;
                }
                
                // check the result against the logical operand
                switch (relOperand) {
                case EQ:
                    if (resultOfComparison == 0) {
                        return true;
                    }
                    break;
                case NEQ:
                    if (resultOfComparison != 0) {
                        return true;
                    }
                    break;
                case GTE:
                    if (resultOfComparison >= 0) {
                        return true;
                    }
                    break;
                case LTE:
                    if (resultOfComparison <= 0) {
                        return true;
                    }
                    break;
                case GT:
                    if (resultOfComparison > 0) {
                        return true;
                    }
                    break;
                case LT:
                    if (resultOfComparison < 0) {
                        return true;
                    }
                    break;
                }
            }
            return false;
        }

        public String toString() {
            return "QueryPart {" + propertyName + " " + getStringFromOperand(relOperand) + " " + queryValue + "}";
        }
    }
}
