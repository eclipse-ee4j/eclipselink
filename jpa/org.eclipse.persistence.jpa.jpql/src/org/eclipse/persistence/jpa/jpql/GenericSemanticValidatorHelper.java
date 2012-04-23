/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.DeclarationResolver.Declaration;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.spi.IConstructor;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.util.CollectionTools;

/**
 * An implementation of {@link SemanticValidatorHelper} that uses {@link JPQLQueryContext} to return
 * the required information and Hermes SPI.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class GenericSemanticValidatorHelper implements SemanticValidatorHelper {

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

	private void addIdentificationVariable(IdentificationVariable identificationVariable,
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

	/**
	 * {@inheritDoc}
	 */
	public void collectAllDeclarationIdentificationVariables(Map<String, List<IdentificationVariable>> identificationVariables) {

		JPQLQueryContext currentContext = queryContext.getCurrentContext();

		while (currentContext != null) {
			collectLocalDeclarationIdentificationVariables(currentContext, identificationVariables);
			currentContext = currentContext.getParent();
		}
	}

	private void collectLocalDeclarationIdentificationVariables(JPQLQueryContext queryContext,
	                                                            Map<String, List<IdentificationVariable>> identificationVariables) {

		for (Declaration declaration : queryContext.getActualDeclarationResolver().getDeclarations()) {

			// Register the identification variable from the base expression
			IdentificationVariable identificationVariable = declaration.identificationVariable;
			addIdentificationVariable(identificationVariable, identificationVariables);

			// Register the identification variable from the JOIN expressions
			for (IdentificationVariable joinIdentificationVariable : declaration.joins.values()) {
				addIdentificationVariable(joinIdentificationVariable, identificationVariables);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void collectLocalDeclarationIdentificationVariables(Map<String, List<IdentificationVariable>> identificationVariables) {
		collectLocalDeclarationIdentificationVariables(queryContext, identificationVariables);
	}

	/**
	 * {@inheritDoc}
	 */
	public void disposeSubqueryContext() {
		queryContext.disposeSubqueryContext();
	}

	/**
	 * {@inheritDoc}
	 */
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
	public IConstructor[] getConstructors(Object type) {
		return CollectionTools.array(IConstructor.class, ((IType) type).constructors());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getDeclarations() {
		return queryContext.getDeclarations();
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getEmbeddable(Object type) {
		return queryContext.getProvider().getEmbeddable((IType) type);
	}

	/**
	 * {@inheritDoc}
	 */
	public IEntity getEntityNamed(String entityName) {
		return queryContext.getProvider().getEntityNamed(entityName);
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getEnumConstants(Object type) {
		return ((IType) type).getEnumConstants();
	}

	/**
	 * {@inheritDoc}
	 */
	public JPQLGrammar getGrammar() {
		return queryContext.getGrammar();
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedType getManagedType(Expression expression) {
		return queryContext.getResolver(expression).getManagedType();
	}

	/**
	 * {@inheritDoc}
	 */
	public IMapping getMappingNamed(Object managedType, String path) {
		return ((IManagedType) managedType).getMappingNamed(path);
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getMappingType(Object mapping) {
		return (mapping != null) ? ((IMapping) mapping).getType() : queryContext.getTypeHelper().unknownType();
	}

	/**
	 * {@inheritDoc}
	 */
	public ITypeDeclaration[] getMethodParameterTypeDeclarations(Object constructor) {
		return ((IConstructor) constructor).getParameterTypes();
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType(Expression expression) {
		return queryContext.getType(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType(Object typeDeclaration) {
		return ((ITypeDeclaration) typeDeclaration).getType();
	}

	/**
	 * {@inheritDoc}
	 */
	public IType getType(String typeName) {
		return queryContext.getType(typeName);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITypeDeclaration getTypeDeclaration(Expression expression) {
		return queryContext.getTypeDeclaration(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITypeHelper getTypeHelper() {
		if (typeHelper == null) {
			typeHelper = new GenericTypeHelper(queryContext.getTypeHelper());
		}
		return typeHelper;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTypeName(Object type) {
		return ((IType) type).getName();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAssignableTo(Object type1, Object type2) {
		return ((IType) type1).isAssignableTo((IType) type2) ;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCollectionIdentificationVariable(String variableName) {
		return queryContext.isCollectionIdentificationVariable(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCollectionMapping(Object mapping) {
		return (mapping != null) && ((IMapping) mapping).isCollection();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEnumType(Object type) {
		return ((IType) type).isEnum();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isIdentificationVariableValidInComparison(IdentificationVariable expression) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isManagedTypeResolvable(Object managedType) {
		return ((IManagedType) managedType).getType().isResolvable();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPropertyMapping(Object mapping) {
		return (mapping != null) && ((IMapping) mapping).isProperty();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRelationshipMapping(Object mapping) {
		return (mapping != null) && ((IMapping) mapping).isRelationship();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isResultVariable(String variableName) {
		return queryContext.isResultVariable(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTransient(Object mapping) {
		return (mapping != null) && ((IMapping) mapping).isTransient();
	}

	/**
	 * {@inheritDoc}
	 */
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
	public boolean isTypeResolvable(Object type) {
		return ((IType) type).isResolvable();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValidatingPathExpressionAllowed(StateFieldPathExpression expression) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void newSubqueryContext(SimpleSelectStatement expression) {
		queryContext.newSubqueryContext(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public IMapping resolveMapping(Expression expression) {
		return queryContext.getResolver(expression).getMapping();
	}

	/**
	 * {@inheritDoc}
	 */
	public IMapping resolveMapping(String variableName, String name) {

		Resolver parent = queryContext.getResolver(variableName);
		Resolver resolver = parent.getChild(name);

		if (resolver == null) {
			resolver = new StateFieldResolver(parent, name);
			parent.addChild(variableName, resolver);
		}

		return resolver.getMapping();
	}
}