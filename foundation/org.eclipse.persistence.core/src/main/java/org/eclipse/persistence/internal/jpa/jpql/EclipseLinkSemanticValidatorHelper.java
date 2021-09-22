/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.internal.jpa.jpql;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.ITypeHelper;
import org.eclipse.persistence.jpa.jpql.JPQLQueryDeclaration;
import org.eclipse.persistence.jpa.jpql.SemanticValidatorHelper;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.querykeys.QueryKey;

/**
 * The EclipseLink implementation of {@link SemanticValidatorHelper}, which directly accesses
 * EclipseLink objects without using Hermes SPI.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
final class EclipseLinkSemanticValidatorHelper implements SemanticValidatorHelper {

    /**
     * The context used to query information about the JPQL query.
     */
    private final JPQLQueryContext queryContext;

    /**
     * Creates a new <code>EclipseLinkSemanticValidatorHelper</code>.
     *
     * @param queryContext The context used to query information about the JPQL query
     */
    EclipseLinkSemanticValidatorHelper(JPQLQueryContext queryContext) {
        super();
        this.queryContext = queryContext;
    }

    private void addIdentificationVariable(IdentificationVariable identificationVariable,
                                           Map<String, List<IdentificationVariable>> identificationVariables) {

        String variableName = (identificationVariable != null) ? identificationVariable.getVariableName() : null;

        if (ExpressionTools.stringIsNotEmpty(variableName)) {

            // Add the IdentificationVariable to the list
            List<IdentificationVariable> variables = identificationVariables.get(variableName);

            if (variables == null) {
                variables = new ArrayList<>();
                identificationVariables.put(variableName, variables);
            }

            variables.add(identificationVariable);
        }
    }

    @Override
    public void collectAllDeclarationIdentificationVariables(Map<String, List<IdentificationVariable>> identificationVariables) {

        JPQLQueryContext currentContext = queryContext.getCurrentContext();

        while (currentContext != null) {
            collectLocalDeclarationIdentificationVariables(currentContext, identificationVariables);
            currentContext = currentContext.getActualParent();
        }
    }

    private void collectLocalDeclarationIdentificationVariables(JPQLQueryContext queryContext,
                                                                Map<String, List<IdentificationVariable>> identificationVariables) {

        DeclarationResolver declarationResolver = queryContext.getDeclarationResolverImp();

        // Collect the identification variables from the declarations
        for (Declaration declaration : declarationResolver.getDeclarations()) {
            IdentificationVariable identificationVariable = declaration.identificationVariable;
            addIdentificationVariable(identificationVariable, identificationVariables);
        }

        // Collect the result variables
        for (IdentificationVariable identificationVariable : declarationResolver.getResultVariables()) {
            addIdentificationVariable(identificationVariable, identificationVariables);
        }
    }

    @Override
    public void collectLocalDeclarationIdentificationVariables(Map<String, List<IdentificationVariable>> identificationVariables) {
        collectLocalDeclarationIdentificationVariables(queryContext, identificationVariables);
    }

    @Override
    public void disposeSubqueryContext() {
        queryContext.disposeSubqueryContext();
    }

    @Override
    public String[] entityNames() {

        List<String> names = new ArrayList<>();

        for (ClassDescriptor descriptor : queryContext.getSession().getDescriptors().values()) {
            if (!descriptor.isAggregateDescriptor()) {
                String name = descriptor.getAlias();
                if (ExpressionTools.stringIsEmpty(name)) {
                    name = descriptor.getJavaClass().getSimpleName();
                }
                names.add(name);
            }
        }

        return names.toArray(new String[names.size()]);
    }

    @Override
    public List<JPQLQueryDeclaration> getAllDeclarations() {

        List<JPQLQueryDeclaration> declarations = new ArrayList<>();
        JPQLQueryContext context = queryContext.getCurrentContext();

        while (context != null) {
            declarations.addAll(context.getDeclarationResolverImp().getDeclarations());
            context = context.getActualParent();
        }

        return declarations;
    }

    @Override
    public Object[] getConstructors(Object type) {
        return (type != null) ? ((Class<?>) type).getDeclaredConstructors() : ExpressionTools.EMPTY_ARRAY;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<? extends JPQLQueryDeclaration> getDeclarations() {
        return queryContext.getDeclarations();
    }

    @Override
    public ClassDescriptor getEmbeddable(Object type) {

        ClassDescriptor descriptor = queryContext.getDescriptor((Class<?>) type);

        if ((descriptor != null) && descriptor.isAggregateDescriptor()) {
            return descriptor;
        }

        return null;
    }

    @Override
    public ClassDescriptor getEntityNamed(String entityName) {
        return queryContext.getDescriptor(entityName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String[] getEnumConstants(Object type) {

        Enum<?>[] constants = ((Class<Enum<?>>) type).getEnumConstants();
        String[] names = new String[constants.length];

        for (int index = constants.length; --index >= 0; ) {
            names[index] = constants[index].name();
        }

        return names;
    }

    @Override
    public JPQLGrammar getGrammar() {
        return queryContext.getGrammar();
    }

    @Override
    public Object getManagedType(Expression expression) {
        return queryContext.resolveDescriptor(expression);
    }

    @Override
    public Object getReferenceManagedType(Object mapping) {

        if (mapping == null) {
            return null;
        }

        return ((DatabaseMapping) mapping).getReferenceDescriptor();
    }

    @Override
    public Object getMappingNamed(Object entity, String name) {

        ClassDescriptor descriptor = (ClassDescriptor) entity;
        Object mapping = descriptor.getObjectBuilder().getMappingForAttributeName(name);

        if (mapping == null) {
            mapping = descriptor.getQueryKeyNamed(name);
        }

        return mapping;
    }

    @Override
    public Class<?> getMappingType(Object mapping) {

        if (mapping == null) {
            return null;
        }

        try {
            // We do a try/catch, it should be faster than doing an instance of
            // since a QueryKey is used a lot less than a DatabaseMapping
            return queryContext.calculateMappingType((DatabaseMapping) mapping);
        }
        catch (ClassCastException e) {
            return queryContext.calculateQueryKeyType((QueryKey) mapping);
        }
    }

    @Override
    public Type[] getMethodParameterTypeDeclarations(Object constructor) {
        return ((Constructor<?>) constructor).getGenericParameterTypes();
    }

    @Override
    public Class<?> getType(Expression expression) {
        return queryContext.getType(expression);
    }

    @Override
    public Object getType(Object typeDeclaration) {
        // Not used
        return null;
    }

    @Override
    public Class<?> getType(String className) {
        return queryContext.getType(className);
    }

    @Override
    public Object getTypeDeclaration(Expression expression) {
        // Not used
        return null;
    }

    @Override
    public ITypeHelper getTypeHelper() {
        // Not used
        return null;
    }

    @Override
    public String getTypeName(Object type) {
        return ((Class<?>) type).getName();
    }

    @Override
    public boolean isAssignableTo(Object type1, Object type2) {
        return ((Class<?>) type2).isAssignableFrom((Class<?>) type1) ;
    }

    @Override
    public boolean isCollectionIdentificationVariable(String variableName) {
        return queryContext.isCollectionIdentificationVariable(variableName);
    }

    @Override
    public boolean isCollectionMapping(Object mapping) {

        if (mapping == null) {
            return false;
        }

        try {
            // We do a try/catch, it should be faster than doing an instance of
            // since a QueryKey is used a lot less than a DatabaseMapping
            return ((DatabaseMapping) mapping).isCollectionMapping();
        }
        catch (ClassCastException e) {
            return ((QueryKey) mapping).isCollectionQueryKey();
        }
    }

    @Override
    public boolean isEmbeddableMapping(Object mapping) {
        return (mapping instanceof org.eclipse.persistence.mappings.EmbeddableMapping);
    }

    @Override
    public boolean isEnumType(Object type) {
        return (type != null) && ((Class<?>) type).isEnum();
    }

    @Override
    public boolean isIdentificationVariableValidInComparison(IdentificationVariable expression) {

        Declaration declaration = queryContext.findDeclaration(expression.getVariableName());

        if (declaration == null) {
            return false;
        }

        DatabaseMapping mapping = declaration.getMapping();

        if (mapping == null) {
            return false;
        }

        // Direct collection is not an object so it's valid
        return mapping.isDirectCollectionMapping() ||
               mapping.isAbstractCompositeDirectCollectionMapping();
    }

    @Override
    public boolean isManagedTypeResolvable(Object managedType) {
        return managedType != null;
    }

    @Override
    public boolean isPropertyMapping(Object mapping) {

        if (mapping == null) {
            return false;
        }

        try {
            // We do a try/catch, it should be faster than doing an instance of
            // since a QueryKey is used a lot less than a DatabaseMapping
            return ((DatabaseMapping) mapping).isDirectToFieldMapping();
        }
        catch (ClassCastException e) {
            return ((QueryKey) mapping).isDirectQueryKey();
        }
    }

    @Override
    public boolean isRelationshipMapping(Object mapping) {

        if (mapping == null) {
            return false;
        }

        if (mapping instanceof DatabaseMapping) {

            DatabaseMapping databaseMapping = (DatabaseMapping) mapping;

            return databaseMapping.isForeignReferenceMapping()            ||
                   databaseMapping.isAbstractCompositeCollectionMapping() ||
                   databaseMapping.isAbstractCompositeDirectCollectionMapping();
        }

        return ((QueryKey) mapping).isForeignReferenceQueryKey();
    }

    @Override
    public boolean isResultVariable(String variableName) {
        return queryContext.isResultVariable(variableName);
    }

    @Override
    public boolean isTransient(Object mapping) {
        return mapping == null;
    }

    @Override
    public boolean isTypeDeclarationAssignableTo(Object typeDeclaration1, Object typeDeclaration2) {
        // Not used
        return false;
    }

    @Override
    public boolean isTypeResolvable(Object type) {
        return type != null;
    }

    @Override
    public void newSubqueryContext(SimpleSelectStatement expression) {
        queryContext.newSubQueryContext(expression, null);
    }

    @Override
    public Object resolveMapping(Expression expression) {
        return queryContext.resolveMappingObject(expression);
    }

    @Override
    public Object resolveMapping(String variableName, String name) {

        // Find the declaration associated with the identification variable
        Declaration declaration = queryContext.findDeclaration(variableName);

        if (declaration == null) {
            return null;
        }

        // Retrieve the associated descriptor
        ClassDescriptor descriptor = declaration.getDescriptor();

        if (descriptor == null) {
            return null;
        }

        // Now, retrieve the mapping
        Object mapping = descriptor.getObjectBuilder().getMappingForAttributeName(name);

        // No mapping was found, look for a query key
        if (mapping == null) {
            mapping = descriptor.getQueryKeyNamed(name);
        }

        return mapping;
    }
}
