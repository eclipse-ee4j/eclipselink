/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.ITypeHelper;
import org.eclipse.persistence.jpa.jpql.JPQLQueryDeclaration;
import org.eclipse.persistence.jpa.jpql.SemanticValidatorHelper;
import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.tools.resolver.Declaration;
import org.eclipse.persistence.jpa.jpql.tools.resolver.DeclarationResolver;
import org.eclipse.persistence.jpa.jpql.tools.resolver.Resolver;
import org.eclipse.persistence.jpa.jpql.tools.resolver.StateFieldResolver;
import org.eclipse.persistence.jpa.jpql.tools.spi.IConstructor;
import org.eclipse.persistence.jpa.jpql.tools.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.tools.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.utility.CollectionTools;

/**
 * An implementation of {@link SemanticValidatorHelper} that uses {@link JPQLQueryContext} to return
 * the required information and Hermes SPI.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class GenericSemanticValidatorHelper implements SemanticValidatorHelper {

    private IdentificationVariableVisitor identificationVariableVisitor;

    /**
     * The context used to query information about the JPQL query.
     */
    private final JPQLQueryContext queryContext;

    /**
     * The concrete instance of {@link ITypeHelper} that simply wraps {@link TypeHelper}.
     */
    private ITypeHelper typeHelper;

    /**
     * Creates a new <code>GenericSemanticValidatorHelper</code>.
     *
     * @param queryContext The context used to query information about the JPQL query
     * @exception NullPointerException The given {@link JPQLQueryContext} cannot be <code>null</code>
     */
    public GenericSemanticValidatorHelper(JPQLQueryContext queryContext) {
        super();
        Assert.isNotNull(queryContext, "The JPQLQueryContext cannot be null");
        this.queryContext = queryContext;
    }

    protected void addIdentificationVariable(IdentificationVariable identificationVariable,
                                             Map<String, List<IdentificationVariable>> identificationVariables) {

        String variableName = (identificationVariable != null) ? identificationVariable.getVariableName() : null;

        if (ExpressionTools.stringIsNotEmpty(variableName)) {

            // Add the IdentificationVariable to the list
            List<IdentificationVariable> variables = identificationVariables.get(variableName);

            if (variables == null) {
                variables = new ArrayList<IdentificationVariable>();
                identificationVariables.put(variableName, variables);
            }

            variables.add(identificationVariable);
        }
    }

    protected IdentificationVariableVisitor buildIdentificationVariableVisitor() {
        return new IdentificationVariableVisitor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void collectAllDeclarationIdentificationVariables(Map<String, List<IdentificationVariable>> identificationVariables) {

        JPQLQueryContext currentContext = queryContext.getCurrentContext();

        while (currentContext != null) {
            collectLocalDeclarationIdentificationVariables(currentContext, identificationVariables);
            currentContext = currentContext.getParent();
        }
    }

    protected void collectLocalDeclarationIdentificationVariables(JPQLQueryContext queryContext,
                                                                  Map<String, List<IdentificationVariable>> identificationVariables) {

        DeclarationResolver declarationResolver = queryContext.getDeclarationResolverImp();

        for (Declaration declaration : declarationResolver.getDeclarations()) {

            // Register the identification variable from the base expression
            IdentificationVariable identificationVariable = declaration.getIdentificationVariable();
            addIdentificationVariable(identificationVariable, identificationVariables);

            // Register the identification variable from the JOIN expressions
            for (Join join : declaration.getJoins()) {
                IdentificationVariable joinIdentificationVariable = getIdentificationVariable(join.getIdentificationVariable());
                addIdentificationVariable(joinIdentificationVariable, identificationVariables);
            }
        }

        if (queryContext.getParent() == null) {
            for (IdentificationVariable identificationVariable : declarationResolver.getResultVariablesMap().keySet()) {
                addIdentificationVariable(identificationVariable, identificationVariables);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void collectLocalDeclarationIdentificationVariables(Map<String, List<IdentificationVariable>> identificationVariables) {
        collectLocalDeclarationIdentificationVariables(queryContext.getCurrentContext(), identificationVariables);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disposeSubqueryContext() {
        queryContext.disposeSubqueryContext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] entityNames() {

        List<String> names = new ArrayList<String>();

        for (IEntity entity : queryContext.getProvider().entities()) {
            names.add(entity.getName());
        }

        return names.toArray(new String[names.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JPQLQueryDeclaration> getAllDeclarations() {

        List<JPQLQueryDeclaration> declarations = new ArrayList<JPQLQueryDeclaration>();
        JPQLQueryContext context = queryContext.getCurrentContext();

        while (context != null) {
            declarations.addAll(context.getDeclarationResolverImp().getDeclarations());
            context = context.getParent();
        }

        return declarations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IConstructor[] getConstructors(Object type) {
        return CollectionTools.array(IConstructor.class, ((IType) type).constructors());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List getDeclarations() {
        return queryContext.getDeclarations();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IManagedType getEmbeddable(Object type) {
        return queryContext.getProvider().getEmbeddable((IType) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IEntity getEntityNamed(String entityName) {
        return queryContext.getProvider().getEntityNamed(entityName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getEnumConstants(Object type) {
        return ((IType) type).getEnumConstants();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPQLGrammar getGrammar() {
        return queryContext.getGrammar();
    }

    protected IdentificationVariable getIdentificationVariable(Expression expression) {
        IdentificationVariableVisitor visitor = getIdentificationVariableVisitor();
        expression.accept(visitor);
        try {
            return visitor.identificationVariable;
        }
        finally {
            visitor.identificationVariable = null;
        }
    }

    protected IdentificationVariableVisitor getIdentificationVariableVisitor() {
        if (identificationVariableVisitor == null) {
            identificationVariableVisitor = buildIdentificationVariableVisitor();
        }
        return identificationVariableVisitor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IManagedType getManagedType(Expression expression) {
        return queryContext.getResolver(expression).getManagedType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IMapping getMappingNamed(Object managedType, String path) {
        return ((IManagedType) managedType).getMappingNamed(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType getMappingType(Object mapping) {
        return (mapping != null) ? ((IMapping) mapping).getType() : queryContext.getTypeHelper().unknownType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITypeDeclaration[] getMethodParameterTypeDeclarations(Object constructor) {
        return ((IConstructor) constructor).getParameterTypes();
    }

    /**
     * Returns the context used to query information about the JPQL query.
     *
     * @return The context used to query information about the JPQL query
     */
    public JPQLQueryContext getQueryContext() {
        return queryContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IManagedType getReferenceManagedType(Object relationshipMapping) {

        if (relationshipMapping == null) {
            return null;
        }

        IMapping mapping = (IMapping) relationshipMapping;
        return mapping.getParent().getProvider().getManagedType(mapping.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType getType(Expression expression) {
        return queryContext.getType(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType getType(Object typeDeclaration) {
        return ((ITypeDeclaration) typeDeclaration).getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IType getType(String typeName) {
        return queryContext.getType(typeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITypeDeclaration getTypeDeclaration(Expression expression) {
        return queryContext.getTypeDeclaration(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITypeHelper getTypeHelper() {
        if (typeHelper == null) {
            typeHelper = new GenericTypeHelper(queryContext.getTypeHelper());
        }
        return typeHelper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeName(Object type) {
        return ((IType) type).getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAssignableTo(Object type1, Object type2) {
        return ((IType) type1).isAssignableTo((IType) type2) ;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCollectionIdentificationVariable(String variableName) {
        return queryContext.isCollectionIdentificationVariable(variableName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCollectionMapping(Object mapping) {
        return (mapping != null) && ((IMapping) mapping).isCollection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnumType(Object type) {
        return ((IType) type).isEnum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isIdentificationVariableValidInComparison(IdentificationVariable expression) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isManagedTypeResolvable(Object managedType) {
        return ((IManagedType) managedType).getType().isResolvable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPropertyMapping(Object mapping) {
        return (mapping != null) && ((IMapping) mapping).isProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRelationshipMapping(Object mapping) {
        return (mapping != null) && ((IMapping) mapping).isRelationship();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResultVariable(String variableName) {
        return queryContext.isResultVariable(variableName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTransient(Object mapping) {
        return (mapping != null) && ((IMapping) mapping).isTransient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTypeDeclarationAssignableTo(Object typeDeclaration1, Object typeDeclaration2) {

        ITypeDeclaration declaration1 = (ITypeDeclaration) typeDeclaration1;
        ITypeDeclaration declaration2 = (ITypeDeclaration) typeDeclaration2;

        // One is an array but not the other one
        if (declaration1.isArray() && !declaration2.isArray() ||
           !declaration1.isArray() &&  declaration2.isArray()) {

            return false;
        }

        // Check the array dimensionality
        if (declaration1.isArray()) {
            return declaration1.getDimensionality() == declaration2.getDimensionality();
        }

        return isAssignableTo(declaration1.getType(), declaration2.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTypeResolvable(Object type) {
        return ((IType) type).isResolvable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void newSubqueryContext(SimpleSelectStatement expression) {
        queryContext.newSubqueryContext(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IMapping resolveMapping(Expression expression) {
        return queryContext.getResolver(expression).getMapping();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IMapping resolveMapping(String variableName, String name) {

        Resolver parent = queryContext.getResolver(variableName);
        Resolver resolver = parent.getChild(name);

        if (resolver == null) {
            resolver = new StateFieldResolver(parent, name);
            parent.addChild(variableName, resolver);
        }

        return resolver.getMapping();
    }

    protected static class IdentificationVariableVisitor extends AbstractExpressionVisitor {

        /**
         *
         */
        protected IdentificationVariable identificationVariable;

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(IdentificationVariable expression) {
            identificationVariable = expression;
        }
    }
}
