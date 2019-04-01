/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.JPQLException;

/**
 * INTERNAL
 * <p><b>Purpose</b>: The ParseTreeContext holds and manages context information for the parse tree for validation.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Associate schema names with variables
 * <li> Associate identifier with nodes
 * <li> Answer an alias for a variable name
 * <li> Answer a class for a variable name
 * <li> Answer a class loader
 * <li> Answer true if there is a class for a variable name
 * <li> Answer a node for a given identifier
 * <li> Print the context on a string
 * </ul>
 * @see ParseTree
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class ParseTreeContext {
    private Map variableDecls;
    private String baseVariable;
    private int currentScope;
    private Set outerScopeVariables;
    private Map fetchJoins;
    private TypeHelper typeHelper;
    private Map parameterTypes;
    private List parameterNames;
    private NodeFactory nodeFactory;
    private String queryInfo;

    /**
     * INTERNAL
     * Return a new initialized ParseTreeContext
     */
    public ParseTreeContext(NodeFactory nodeFactory, String queryInfo) {
        super();
        variableDecls = new HashMap();
        currentScope = 0;
        fetchJoins = new HashMap();
        typeHelper = null;
        parameterTypes = new HashMap();
        parameterNames = new ArrayList();
        this.nodeFactory = nodeFactory;
        this.queryInfo = queryInfo;
    }

    /**
     * INTERNAL
     * Associate the given schema with the given variable.
     */
    public void registerSchema(String variable, String schema, int line, int column) {
        VariableDecl decl = (VariableDecl)variableDecls.get(variable);
        if (decl == null) {
            decl = new VariableDecl(variable, schema);
            variableDecls.put(variable, decl);
        } else {
            String text = decl.isRangeVariable ? decl.schema : decl.path.getAsString();
            throw JPQLException.multipleVariableDeclaration(
                getQueryInfo(), line, column, variable, text);
        }
    }

    /**
     * INTERNAL
     * Associate the given path with the given variable.
     */
    public void registerJoinVariable(String variable, Node path, int line, int column) {
        VariableDecl decl = (VariableDecl)variableDecls.get(variable);
        if (decl == null) {
            decl = new VariableDecl(variable, path);
            variableDecls.put(variable, decl);
        } else {
            String text = decl.isRangeVariable ? decl.schema : decl.path.getAsString();
            throw JPQLException.multipleVariableDeclaration(
                getQueryInfo(), line, column, variable, text);
        }
    }
    /**
     * INTERNAL
     */
    public void unregisterVariable(String variable) {
        variableDecls.remove(variable);
    }

    /**
     * INTERNAL
     * Returns true if the specified string denotes a variable.
     */
    public boolean isVariable(String variable) {
        VariableDecl decl = (VariableDecl)variableDecls.get(variable);
        return decl != null;
    }

    /**
     * INTERNAL
     * Returns true if the specified string denotes a range variable.
     */
    /** */
    public boolean isRangeVariable(String variable) {
        VariableDecl decl = (VariableDecl)variableDecls.get(variable);
        return (decl != null) && decl.isRangeVariable;
    }

    /**
     * INTERNAL
     * Returns the abstract schema name if the specified string denotes a
     * range variable.
     */
    public String schemaForVariable(String variable) {
        VariableDecl decl = (VariableDecl)variableDecls.get(variable);
        return (decl != null) ? decl.schema : null;
    }

    /**
     * INTERNAL
     * Answer the class associated with the provided schema name
     */
    public Class classForSchemaName(String schemaName, GenerationContext context) {
        ClassDescriptor descriptor = context.getSession().getDescriptorForAlias(schemaName);
        if (descriptor == null) {
            throw JPQLException.entityTypeNotFound(getQueryInfo(), schemaName);
        }
        Class theClass = descriptor.getJavaClass();
        if (theClass == null) {
            throw JPQLException.resolutionClassNotFoundException(getQueryInfo(), schemaName);
        }
        return theClass;
    }

    /**
     * INTERNAL
     * getVariableNameForClass():
     *        Answer the name mapped to the specified class. Answer null if none found.
     * SELECT OBJECT (emp) FROM Employee emp
     *   getVariableNameForClass(Employee.class) =&gt; "emp"
     */
    public String getVariableNameForClass(Class theClass, GenerationContext context) {
        for (Iterator i = variableDecls.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry)i.next();
            String nextVariable = (String)entry.getKey();
            VariableDecl decl = (VariableDecl)entry.getValue();
            if ((decl.schema != null) &&
                (theClass == this.classForSchemaName(decl.schema, context))) {
                return nextVariable;
            }
        }
        return null;
    }

    /**
     * INTERNAL
     * Returns the path if the specified string denotes a join or collection
     * member variable.
     */
    public Node pathForVariable(String variable) {
        VariableDecl decl = (VariableDecl)variableDecls.get(variable);
        return (decl != null) ? decl.path : null;
    }

    /** */
    public String getBaseVariable() {
        return baseVariable;
    }

    /** */
    public void setBaseVariable(String variable) {
        this.baseVariable = variable;
    }

    /** */
    public NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    /** */
    public String getQueryInfo() {
        return queryInfo;
    }

    /**
     * INTERNAL
     * Returns true if the specified string denotes a variable declared in an
     * outer scope.
     */
    public boolean isDeclaredInOuterScope(String variable) {
        VariableDecl decl = (VariableDecl)variableDecls.get(variable);
        return (decl != null) ? (decl.scope < currentScope) : false;
    }

    /**
     * INTERNAL
     * Sets the scope of the specified variable to the current scope.
     */
    public void setScopeOfVariable(String variable) {
        VariableDecl decl = (VariableDecl)variableDecls.get(variable);
        if (decl != null) {
            decl.scope = currentScope;
        }
    }

    /**
     * INTERNAL
     * Enters a new scope. This initializes the set of outer scope variables.
     */
    public void enterScope() {
        currentScope++;
        resetOuterScopeVariables();
    }

    /**
     * INTERNAL
     * Leaves the current scope.
     */
    public void leaveScope() {
        currentScope--;
    }

    /**
     * INTERNAL
     * Adds the specified variable to the set of outer scope variables.
     */
    public void registerOuterScopeVariable(String variable) {
        outerScopeVariables.add(variable);
    }

    /**
     * INTERNAL
     * Returns the set of outer scope variables.
     */
    public Set getOuterScopeVariables() {
        return outerScopeVariables;
    }

    /**
     * INTERNAL
     * Resets the set of outer scope variables.
     */
    public void resetOuterScopeVariables() {
        outerScopeVariables = new HashSet();
    }

    /**
     * INTERNAL
     * Resets the set of outer scope variables.
     */
    public void resetOuterScopeVariables(Set variables) {
        outerScopeVariables = variables;
    }

    /**
     * Associate the given variableName with the given node representing a
     * JOIN FETCH node.
     */
    public void registerFetchJoin(String variableName, Node node) {
        List joins = (List)fetchJoins.get(variableName);
        if (joins == null) {
            joins = new ArrayList();
            fetchJoins.put(variableName, joins);
        }
        joins.add(node);
    }

    /** Returns a list of FETCH JOIN nodes for the specified attached to the
     * specified variable. */
    public List getFetchJoins(String variableName) {
        return (List)fetchJoins.get(variableName);
    }

    /** Mark the specified variable as used if it is declared in the current
     * scope. */
    public void usedVariable(String variable) {
        VariableDecl decl = (VariableDecl)variableDecls.get(variable);
        if ((decl != null) && (decl.scope == currentScope)) {
            decl.used = true;
        }
    }

    /** Returns s set of variables that are declared in the current scope,
     * but not used in the query. */
    public Set getUnusedVariables() {
        Set unused = new HashSet();
        for (Iterator i = variableDecls.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry)i.next();
            String variable = (String)entry.getKey();
            VariableDecl decl = (VariableDecl)entry.getValue();
            if ((decl.scope == currentScope) && !decl.used) {
                unused.add(variable);
            }
        }
        return unused;
    }

    //answer true if two or more variables are mapped to the same type name in variableTypes
    //true => "SELECT OBJECT (emp1) FROM Employee emp1, Employee emp2 WHERE ..."
    //false => "SELECT OBJECT (emp) FROM Employee emp WHERE ..."
    public boolean hasMoreThanOneVariablePerType() {
        Map typeNamesToVariables = new HashMap();
        int nrOfRangeVariables = 0;
        //Map the Aliases to the variable names, then check the count
        for (Iterator i = variableDecls.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry)i.next();
            String variable = (String)entry.getKey();
            VariableDecl decl = (VariableDecl)entry.getValue();
            if (decl.isRangeVariable) {
                nrOfRangeVariables++;
                typeNamesToVariables.put(decl.schema, variable);
            }
        }
        return typeNamesToVariables.size() != nrOfRangeVariables;
    }

    //answer true if two or more aliases are involved in the FROM (different types)
    //true => "SELECT OBJECT (emp1) FROM Employee emp1, Address addr1 WHERE ..."
    //false => "SELECT OBJECT (emp) FROM Employee emp WHERE ..."
    //false => "SELECT OBJECT (emp1) FROM Employee emp1, Employee emp2 WHERE ..."
    public boolean hasMoreThanOneAliasInFrom() {
        Map typeNamesToVariables = new HashMap();
        for (Iterator i = variableDecls.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry)i.next();
            String variable = (String)entry.getKey();
            VariableDecl decl = (VariableDecl)entry.getValue();
            if (decl.isRangeVariable) {
                typeNamesToVariables.put(decl.schema, variable);
            }
        }
        return typeNamesToVariables.size() > 1;
    }

    /**
     * INTERNAL
     * Returns the type helper stored in this context.
     */
    public TypeHelper getTypeHelper() {
        return typeHelper;
    }

    /**
     * INTERNAL
     * Stores the specified type helper in this context.
     */
    public void setTypeHelper(TypeHelper typeHelper) {
        this.typeHelper = typeHelper;
    }

    /**
     * INTERNAL
     * Add a parameter.
     */
    public void addParameter(String parameterName) {
        if (!parameterNames.contains(parameterName)){
            parameterNames.add(parameterName);
        }
    }

    /**
     * INTERNAL
     * Defines the type of the parameter with the specified name.
     */
    public void defineParameterType(String parameterName, Object parameterType,
        int line, int column) {
        if (parameterTypes.containsKey(parameterName)) {
            // existing entry
            Object oldType = parameterTypes.get(parameterName);
            if (typeHelper.isAssignableFrom(oldType, parameterType)) {
                // OK
            } else if (typeHelper.isAssignableFrom(parameterType, oldType)) {
                // new parameter type is more general
                parameterTypes.put(parameterName, parameterType);
            } else {
                // error case old usage and new usage do not match type
                throw JPQLException.invalidMultipleUseOfSameParameter(
                    getQueryInfo(), line, column, parameterName,
                    typeHelper.getTypeName(oldType),
                    typeHelper.getTypeName(parameterType));
            }
        } else {
            // new entry
            parameterTypes.put(parameterName, parameterType);
        }
    }

    /**
     * INTERNAL
     * Returns true if the query has at least one parameter.
     */
    public boolean hasParameters() {
        return !parameterNames.isEmpty();
    }

    /**
     * INTERNAL
     * Return the type of the specified parameter.
     */
    public Object getParameterType(String parameter) {
        return parameterTypes.get(parameter);
    }

    /**
     * INTERNAL
     * Return the parameter names.
     */
    public List getParameterNames() {
        return parameterNames;
    }

    /**
     * INTERNAL
     * Class defining the type of the values the variableDecls map.
     * It holds the following values:
     * variable - the name of the variable
     * isRangeVariable - true if the variable is declared as range variable
     * schema - the abstract for a range variable
     * path - the path for join or collection member variable
     * scope - the scope of the variable
     * used - true if the variable is used in any of the clauses
     */
    static class VariableDecl {
        public final String variable;
        public final boolean isRangeVariable;
        public final String schema;
        public final Node path;
        public int scope;
        public boolean used;
        public VariableDecl(String variable, String schema) {
            this.variable = variable;
            this.isRangeVariable = true;
            this.schema = schema;
            this.path = null;
            this.used = false;
        }
        public VariableDecl(String variable, Node path) {
            this.variable = variable;
            this.isRangeVariable = false;
            this.schema = null;
            this.path = path;
            this.used = false;
        }
    }
}
