/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.jpql.tools.resolver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.LiteralType;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSchemaName;
import org.eclipse.persistence.jpa.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.jpql.parser.DeleteStatement;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SimpleFromClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SubExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.parser.UpdateStatement;
import org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.tools.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;

/**
 * This {@link Resolver} is responsible to visit the current query (which is either the top-level
 * query or a subquery) and gathers the information from the declaration clause. For a <b>SELECT</b>
 * or <b>DELETE</b> clause, the information will be retrieved from the <b>FROM</b> clause. For an
 * <code>UDPATE</code> clause, it will be retrieved from the unique identification range variable
 * declaration.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
public class DeclarationResolver extends Resolver {

    /**
     * The {@link Declaration Declarations} of the current query that was parsed.
     */
    private List<Declaration> declarations;

    /**
     * This visitor is responsible to visit the current query's declaration and populate this
     * resolver with the list of declarations.
     */
    private DeclarationVisitor declarationVisitor;

    /**
     * This visitor is responsible to convert the abstract schema name into a path expression.
     */
    private QualifyRangeDeclarationVisitor qualifyRangeDeclarationVisitor;

    /**
     * The context used to query information about the query.
     */
    private JPQLQueryContext queryContext;

    /**
     * The identification variable names mapped to their resolvers.
     */
    private Map<String, Resolver> resolvers;

    /**
     * The variables identifying the select expressions, if any was defined or an empty set if none
     * were defined.
     */
    private Map<IdentificationVariable, String> resultVariables;

    /**
     * This visitor resolves the "root" object represented by the given {@link Expression}. This will
     * also handle using a subquery in the <code><b>FROM</b></code> clause. This is only supported by
     * EclipseLink.
     */
    private RootObjectExpressionVisitor rootObjectExpressionVisitor;

    /**
     * Creates a new <code>DeclarationResolver</code>.
     *
     * @param parent The parent resolver if this is used for a subquery or null if it's used for the
     * top-level query
     * @param queryContext The context used to query information about the query
     */
    public DeclarationResolver(DeclarationResolver parent, JPQLQueryContext queryContext) {
        super(parent);
        initialize(queryContext);
    }

    /**
     * Adds the given {@link Declaration} at the end of the list.
     *
     * @param declaration The {@link Declaration} representing a single variable declaration
     */
    protected final void addDeclaration(Declaration declaration) {
        declarations.add(declaration);
    }

    /**
     * Registers a range variable declaration that will be used when a JPQL fragment is parsed.
     *
     * @param entityName The name of the entity to be accessible with the given variable name
     * @param variableName The identification variable used to navigate to the entity
     */
    public void addRangeVariableDeclaration(String entityName, String variableName) {

        // Always make the identification variable be upper case since it's
        // case insensitive, the get will also use upper case
        String internalVariableName = variableName.toUpperCase();

        // Make sure the identification variable was not declared more than once,
        // this could cause issues when trying to resolve it
        if (!resolvers.containsKey(internalVariableName)) {

            // Resolve the expression and map it with the identification variable
            Resolver resolver = new EntityResolver(this, entityName);
            resolver = new IdentificationVariableResolver(resolver, variableName);
            resolvers.put(internalVariableName, resolver);

            RangeDeclaration declaration = new RangeDeclaration();
            declaration.rootPath               = entityName;
            declaration.identificationVariable = new IdentificationVariable(null, variableName);
            declarations.add(declaration);
        }
    }

    protected DeclarationVisitor buildDeclarationVisitor() {
        return new DeclarationVisitor();
    }

    protected RootObjectExpressionVisitor buildRootObjectExpressionVisitor() {
        return new RootObjectExpressionVisitor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IType buildType() {
        return getTypeHelper().unknownType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ITypeDeclaration buildTypeDeclaration() {
        return getTypeHelper().unknownTypeDeclaration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkParent(Resolver parent) {
        // Don't do anything, this is the root
    }

    /**
     * Converts the given {@link Declaration} from being set as a range variable declaration to
     * a path expression declaration.
     * <p>
     * In this query "<code>UPDATE Employee SET firstName = 'MODIFIED' WHERE (SELECT COUNT(m) FROM
     * managedEmployees m) {@literal >} 0</code>" <em>managedEmployees</em> is an unqualified
     * collection-valued path expression (<code>employee.managedEmployees</code>).
     *
     * @param declaration The {@link Declaration} that was parsed to range over an abstract schema
     * name but is actually ranging over a path expression
     * @param outerVariableName The identification variable coming from the parent identification
     * variable declaration
     */
    public void convertUnqualifiedDeclaration(Declaration declaration, String outerVariableName) {

        QualifyRangeDeclarationVisitor visitor = qualifyRangeDeclarationVisitor();

        try {
            visitor.oldDeclaration    = declaration;
            visitor.outerVariableName = outerVariableName;

            declaration.declarationExpression.accept(visitor);
        }
        finally {
            visitor.oldDeclaration    = null;
            visitor.newDeclaration    = null;
            visitor.outerVariableName = null;
        }
    }

    /**
     * Disposes the internal data.
     */
    public void dispose() {
        resolvers      .clear();
        declarations   .clear();
        resultVariables.clear();
    }

    /**
     * Retrieves the {@link Declaration} for which the given variable name is used to navigate to the
     * "root" object.
     *
     * @param variableName The name of the identification variable that is used to navigate a "root" object
     * @return The {@link Declaration} containing the information about the identification variable declaration
     * @since 2.5
     */
    public Declaration getDeclaration(String variableName) {

        for (Declaration declaration : declarations) {
            if (declaration.getVariableName().equalsIgnoreCase(variableName)) {
                return declaration;
            }
        }

        return null;
    }

    /**
     * Returns the ordered list of {@link Declaration Declarations}.
     *
     * @return The {@link Declaration Declarations} of the current query that was parsed
     */
    public List<Declaration> getDeclarations() {
        return declarations;
    }

    protected DeclarationVisitor getDeclarationVisitor() {
        if (declarationVisitor == null) {
            declarationVisitor = buildDeclarationVisitor();
        }
        return declarationVisitor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DeclarationResolver getParent() {
        return (DeclarationResolver) super.getParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IQuery getQuery() {
        return queryContext.getQuery();
    }

    /**
     * Returns the {@link JPQLQueryContext} that is used by this visitor.
     *
     * @return The {@link JPQLQueryContext} holding onto the JPQL query and the cached information
     */
    protected JPQLQueryContext getQueryContext() {
        return queryContext;
    }

    /**
     * Retrieves the {@link Resolver} mapped with the given identification variable. If the
     * identification is not defined in the declaration traversed by this resolver, than the search
     * will traverse the parent hierarchy.
     *
     * @param variableName The identification variable that maps a {@link Resolver}
     * @return The {@link Resolver} mapped with the given identification variable
     */
    public Resolver getResolver(String variableName) {

        variableName = variableName.toUpperCase();
        Resolver resolver = getResolverImp(variableName);

        if ((resolver == null) && (getParent() != null)) {
            resolver = getParent().getResolver(variableName);
        }

        if (resolver == null) {
            resolver = new NullResolver(this);
            resolvers.put(variableName, resolver);
        }

        return resolver;
    }

    /**
     * Retrieves the {@link Resolver} mapped with the given identification variable. The search does
     * not traverse the parent hierarchy.
     *
     * @param variableName The identification variable that maps a {@link Resolver}
     * @return The {@link Resolver} mapped with the given identification variable
     */
    protected Resolver getResolverImp(String variableName) {
        return resolvers.get(variableName);
    }

    /**
     * Returns the variables that got defined in the select expression. This only applies to JPQL
     * queries built for JPA 2.0 or later.
     *
     * @return The variables identifying the select expressions, if any was defined or an empty set
     * if none were defined
     */
    public Set<String> getResultVariables() {
        return new HashSet<String>(resultVariables.values());
    }

    /**
     * Returns the map of result variables that got used to define a select expression. This only
     * applies to JPQL queries built for JPA 2.0.
     *
     * @return The variables identifying the select expressions, if any was defined or an empty map
     * if none were defined
     */
    public Map<IdentificationVariable, String> getResultVariablesMap() {
        return resultVariables;
    }

    protected RootObjectExpressionVisitor getRootObjectExpressionVisitor() {
        if (rootObjectExpressionVisitor == null) {
            rootObjectExpressionVisitor = buildRootObjectExpressionVisitor();
        }
        return rootObjectExpressionVisitor;
    }

    /**
     * Determines whether the JPQL expression has <b>JOIN</b> expressions.
     *
     * @return <code>true</code> if the query or subquery being traversed contains <b>JOIN</b>
     * expressions; <code>false</code> otherwise
     */
    public boolean hasJoins() {

        for (Declaration declaration : declarations) {
            if (declaration.hasJoins()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Initializes this <code>DeclarationResolver</code>.
     *
     * @param queryContext The context used to query information about the query
     */
    protected void initialize(JPQLQueryContext queryContext) {
        this.queryContext    = queryContext;
        this.resolvers       = new HashMap<String, Resolver>();
        this.declarations    = new LinkedList<Declaration>();
        this.resultVariables = new HashMap<IdentificationVariable, String>();
    }

    /**
     * Determines whether the given identification variable is defining a <b>JOIN</b> expression or
     * in a <code>IN</code> expressions for a collection-valued field. If the search didn't find the
     * identification in this resolver, then it will traverse the parent hierarchy.
     *
     * @param variableName The identification variable to check for what it maps
     * @return <code>true</code> if the given identification variable maps a collection-valued field
     * defined in a <code>JOIN</code> or <code>IN</code> expression; <code>false</code> otherwise
     */
    public boolean isCollectionIdentificationVariable(String variableName) {

        boolean result = isCollectionIdentificationVariableImp(variableName);

        if (!result && (getParent() != null)) {
            result = getParent().isCollectionIdentificationVariableImp(variableName);
        }

        return result;
    }

    /**
     * Determines whether the given identification variable is defining a <b>JOIN</b> expression or
     * in a <code>IN</code> expressions for a collection-valued field. The search does not traverse
     * the parent hierarchy.
     *
     * @param variableName The identification variable to check for what it maps
     * @return <code>true</code> if the given identification variable maps a collection-valued field
     * defined in a <code>JOIN</code> or <code>IN</code> expression; <code>false</code> otherwise
     */
    protected boolean isCollectionIdentificationVariableImp(String variableName) {

        for (Declaration declaration : declarations) {

            switch (declaration.getType()) {

                case COLLECTION: {
                    if (declaration.getVariableName().equalsIgnoreCase(variableName)) {
                        return true;
                    }
                    return false;
                }

                case RANGE:
                case DERIVED: {

                    AbstractRangeDeclaration rangeDeclaration = (AbstractRangeDeclaration) declaration;

                    // Check the JOIN expressions
                    for (Join join : rangeDeclaration.getJoins()) {

                        String joinVariableName = queryContext.literal(
                            join.getIdentificationVariable(),
                            LiteralType.IDENTIFICATION_VARIABLE
                        );

                        if (joinVariableName.equalsIgnoreCase(variableName)) {
                            // Make sure the JOIN expression maps a collection mapping
                            Resolver resolver = getResolver(variableName);
                            IMapping mapping = (resolver != null) ? resolver.getMapping() : null;
                            return (mapping != null) && mapping.isCollection();
                        }
                    }
                }

                default:
                    continue;
            }
        }

        return false;
    }

    /**
     * Determines whether the given variable name is an identification variable name mapping an
     * entity. The search traverses the parent resolver if it is not found in this resolver, which
     * represents the current JPQL query.
     *
     * @param variableName The name of the variable to verify if it's defined in a range variable
     * declaration in the current query or any parent query
     * @return <code>true</code> if the variable name is mapping an abstract schema name; <code>false</code>
     * if it's defined in a collection member declaration
     */
    public boolean isRangeIdentificationVariable(String variableName) {

        boolean result = isRangeIdentificationVariableImp(variableName);

        if (!result && (getParent() != null)) {
            result = getParent().isRangeIdentificationVariableImp(variableName);
        }

        return result;
    }

    /**
     * Determines whether the given variable name is an identification variable name mapping an
     * entity. The search only searches in this resolver, which represents the current JPQL query.
     *
     * @param variableName The name of the variable to verify if it's defined in a range variable
     * declaration in the current query or any parent query
     * @return <code>true</code> if the variable name is mapping an abstract schema name; <code>false</code>
     * if it's defined in a collection member declaration
     */
    protected boolean isRangeIdentificationVariableImp(String variableName) {

        for (Declaration declaration : declarations) {

            if (declaration.getType().isRange() &&
                declaration.getVariableName().equalsIgnoreCase(variableName)) {

                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the given variable is a result variable.
     *
     * @param variable The variable to check if it's a result variable
     * @return <code>true</code> if the given variable is defined as a result variable; <code>false</code>
     * otherwise
     */
    public boolean isResultVariable(String variable) {
        return resultVariables.containsValue(variable.toUpperCase());
    }

    /**
     * Visits the current query (which is either the top-level query or a subquery) and gathers the
     * information from the declaration clause.
     *
     * @param expression The {@link Expression} to visit in order to retrieve the information
     * contained in the given query's declaration
     */
    public void populate(Expression expression) {
        DeclarationVisitor visitor = getDeclarationVisitor();
        try {
            expression.accept(visitor);
        }
        finally {
            visitor.currentDeclaration = null;
        }
    }

    protected QualifyRangeDeclarationVisitor qualifyRangeDeclarationVisitor() {
        if (qualifyRangeDeclarationVisitor == null) {
            qualifyRangeDeclarationVisitor = new QualifyRangeDeclarationVisitor();
        }
        return qualifyRangeDeclarationVisitor;
    }

    /**
     * Resolves the "root" object represented by the given {@link Expression}. This will also handle
     * using a subquery in the <code><b>FROM</b></code> clause. This is only supported by EclipseLink.
     *
     * @param expression The {@link Expression} to visit, which represents the "root" object of a
     * declaration
     * @return The {@link Resolver} for the given {@link Expression}
     */
    protected Resolver resolveRootObject(Expression expression) {
        RootObjectExpressionVisitor visitor = getRootObjectExpressionVisitor();
        try {
            expression.accept(visitor);
            return visitor.resolver;
        }
        finally {
            visitor.resolver = null;
        }
    }

    protected String visitDeclaration(Expression expression, Expression identificationVariable) {

        // Visit the identification variable expression and retrieve the identification variable name
        String variableName = queryContext.literal(
            identificationVariable,
            LiteralType.IDENTIFICATION_VARIABLE
        );

        // If it's not empty, then we can create a Resolver
        if (variableName != ExpressionTools.EMPTY_STRING) {

            // Always make the identification variable be upper case since it's
            // case insensitive, the get will also use upper case
            String internalVariableName = variableName.toUpperCase();

            // Make sure the identification variable was not declared more than once,
            // this could cause issues when trying to resolve it
            if (!resolvers.containsKey(internalVariableName)) {

                // Resolve the expression and map it with the identification variable
                Resolver resolver = resolveRootObject(expression);
                resolver = new IdentificationVariableResolver(resolver, variableName);
                resolvers.put(internalVariableName, resolver);
            }

            return variableName;
        }

        return ExpressionTools.EMPTY_STRING;
    }

    protected class DeclarationVisitor extends AbstractExpressionVisitor {

        /**
         * This flag is used to determine what to do in {@link #visit(SimpleSelectStatement)}.
         */
        protected boolean buildingDeclaration;

        /**
         * Flag used to determine if the {@link IdentificationVariable} to visit is a result variable.
         */
        protected boolean collectResultVariable;

        /**
         * The {@link Declaration} being populated.
         */
        protected Declaration currentDeclaration;

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(AbstractSchemaName expression) {
            currentDeclaration = new RangeDeclaration();
            currentDeclaration.rootPath = expression.getText();
            declarations.add(currentDeclaration);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionExpression expression) {
            expression.acceptChildren(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionMemberDeclaration expression) {

            CollectionDeclaration declaration = new CollectionDeclaration();
            declaration.declarationExpression = expression;
            declaration.baseExpression        = expression.getCollectionValuedPathExpression();
            declaration.rootPath              = declaration.baseExpression.toActualText();
            declarations.add(declaration);

            // Retrieve the IdentificationVariable
            Expression identificationVariable = expression.getIdentificationVariable();
            String variableName = visitDeclaration(expression, identificationVariable);

            if (variableName != ExpressionTools.EMPTY_STRING) {
                declaration.identificationVariable = (IdentificationVariable) identificationVariable;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionValuedPathExpression expression) {
            currentDeclaration = new DerivedDeclaration();
            currentDeclaration.rootPath = expression.toActualText();
            declarations.add(currentDeclaration);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(DeleteClause expression) {

            try {
                buildingDeclaration = true;
                expression.getRangeVariableDeclaration().accept(this);
                buildingDeclaration = false;

                currentDeclaration.declarationExpression = expression;
            }
            finally {
                currentDeclaration = null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(DeleteStatement expression) {
            expression.getDeleteClause().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(FromClause expression) {
            expression.getDeclaration().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IdentificationVariable expression) {
            if (collectResultVariable) {
                resultVariables.put(expression, expression.getText().toUpperCase());
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IdentificationVariableDeclaration expression) {

            try {
                expression.getRangeVariableDeclaration().accept(this);
                expression.getJoins().accept(this);
                currentDeclaration.declarationExpression = expression;
            }
            finally {
                currentDeclaration = null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(Join expression) {

            AbstractRangeDeclaration rangeDeclaration = (AbstractRangeDeclaration) currentDeclaration;
            Expression identificationVariable = expression.getIdentificationVariable();
            visitDeclaration(expression, identificationVariable);
            rangeDeclaration.addJoin(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(JPQLExpression expression) {
            expression.getQueryStatement().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(NullExpression expression) {
            if (buildingDeclaration) {
                currentDeclaration = new UnknownDeclaration();
                currentDeclaration.baseExpression = expression;
                currentDeclaration.rootPath       = ExpressionTools.EMPTY_STRING;
                declarations.add(currentDeclaration);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(RangeVariableDeclaration expression) {

            // Visit the "root" object, which will create the right Declaration
            Expression rootObject = expression.getRootObject();
            buildingDeclaration = true;
            rootObject.accept(this);
            buildingDeclaration = false;

            // Set the base expression
            currentDeclaration.baseExpression = expression;

            // Set the identification variable
            Expression identificationVariable = expression.getIdentificationVariable();
            String variableName = visitDeclaration(rootObject, identificationVariable);

            if (variableName != ExpressionTools.EMPTY_STRING) {
                currentDeclaration.identificationVariable = (IdentificationVariable) identificationVariable;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(ResultVariable expression) {
            collectResultVariable = true;
            expression.getResultVariable().accept(this);
            collectResultVariable = false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SelectClause expression) {
            expression.getSelectExpression().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SelectStatement expression) {
            expression.getFromClause().accept(this);
            expression.getSelectClause().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleFromClause expression) {
            expression.getDeclaration().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleSelectClause expression) {
            expression.getSelectExpression().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleSelectStatement expression) {
            expression.getFromClause().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UpdateClause expression) {

            try {
                buildingDeclaration = true;
                expression.getRangeVariableDeclaration().accept(this);
                buildingDeclaration = false;

                currentDeclaration.declarationExpression = expression;
            }
            finally {
                currentDeclaration = null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(UpdateStatement expression) {
            expression.getUpdateClause().accept(this);
        }
    }

    protected static class QualifyRangeDeclarationVisitor extends AbstractExpressionVisitor {

        /**
         * The new {@link Declaration}.
         */
        protected Declaration newDeclaration;

        /**
         * The {@link Declaration} being modified.
         */
        protected Declaration oldDeclaration;

        /**
         * The identification variable coming from the parent identification variable declaration.
         */
        protected String outerVariableName;

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(CollectionValuedPathExpression expression) {
            newDeclaration.rootPath       = expression.toActualText();
            newDeclaration.baseExpression = expression;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IdentificationVariableDeclaration expression) {
            expression.getRangeVariableDeclaration().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(RangeVariableDeclaration expression) {

            newDeclaration = new DerivedDeclaration();

            expression.setVirtualIdentificationVariable(outerVariableName, newDeclaration.rootPath);
            expression.getRootObject().accept(this);
        }
    }

    /**
     * This visitor takes care to support a subquery defined as a "root" object.
     */
    protected class RootObjectExpressionVisitor extends AnonymousExpressionVisitor {

        /**
         * The {@link Resolver} of the "root" object.
         */
        protected Resolver resolver;

        /**
         * {@inheritDoc}
         */
        @Override
        protected void visit(Expression expression) {
            resolver = queryContext.getResolver(expression);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SimpleSelectStatement expression) {
            resolver = new FromSubqueryResolver(
                DeclarationResolver.this,
                DeclarationResolver.this.queryContext,
                expression
            );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(SubExpression expression) {
            expression.getExpression().accept(this);
        }
    }
}
