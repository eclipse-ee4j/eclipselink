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

import java.util.List;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;

/**
 * This helper is used by {@link AbstractSemanticValidator} in order to retrieve JPA information.
 * This helper allows third party adopter to write an instance of this helper that directly access
 * the JPA information without having to implement Hermes SPI, which can improve performance.
 * <p>
 * {@link GenericSemanticValidatorHelper} is a default implementation that uses Hermes SPI.
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
public interface SemanticValidatorHelper {

	/**
	 * Collects the identification variables that are defined in the <code>FROM</code> clause of the
	 * current query and from the parent queries.
	 *
	 * @param identificationVariables The {@link Map} used to store the variables
	 */
	void collectAllDeclarationIdentificationVariables(Map<String, List<IdentificationVariable>> identificationVariables);

	/**
	 * Collects the identification variables that are defined in the <code>FROM</code> clause of the
	 * current query.
	 *
	 * @param identificationVariables The {@link Map} used to store the variables
	 */
	void collectLocalDeclarationIdentificationVariables(Map<String, List<IdentificationVariable>> identificationVariables);

	/**
	 * Disposes this context, which is the current context being used by a subquery. Once it is
	 * disposed, any information retrieved will be for the subquery's parent query.
	 */
	void disposeSubqueryContext();

	/**
	 * Returns the name of the all entities that are present in the context of a persistence unit.
	 *
	 * @return The list of entity names
	 */
	String[] entityNames();

	/**
	 * Returns the constructors for the given type. All public, protected, default (package) access,
	 * and private constructors should be included.
	 * <p>
	 * If it was going through Hermes SPI, the type of the argument would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IType IType} and the return type would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IConstructor IConstructor}.
	 *
	 * @return The declared constructors
	 */
	Object[] getConstructors(Object type);

	/**
	 * Returns the ordered list of {@link JPQLQueryDeclaration}, which contain the information
	 * contained in the query's <code>FROM</code> clause.
	 *
	 * @return The list of {@link JPQLQueryDeclaration} of the current query that was parsed
	 */
	List<JPQLQueryDeclaration> getDeclarations();

	/**
	 * Retrieves the embeddable with the given type.
	 *
	 * @param type The Java type of the embeddable to retrieve
	 * @return The embeddable for the given type if it's representing an embeddable; <code>null</code>
	 * otherwise
	 */
	Object getEmbeddable(Object type);

	/**
	 * Retrieves the entity with the given entity name.
	 *
	 * @param entityName The abstract schema name of the entity to retrieve
	 * @return The entity with the given name; <code>null</code> otherwise
	 */
	Object getEntityNamed(String entityName);

	/**
	 * Returns the constant names for the given {@link Enum} type.
	 * <p>
	 * If it was going through Hermes SPI, the type of the argument would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IType IType}.
	 *
	 * @param type The {@link Enum} type
	 * @return The list of constant names
	 */
	String[] getEnumConstants(Object type);

	/**
	 * Returns the {@link JPQLGrammar} that defines how the JPQL query was parsed.
	 *
	 * @return The {@link JPQLGrammar} that was used to parse the JPQL query
	 */
	JPQLGrammar getGrammar();

	/**
	 * Returns the managed type by resolving the given {@link Expression}.
	 * <p>
	 * If it was going through Hermes SPI, the return type would be {@link org.eclipse.persistence.
	 * jpa.jpql.spi.IManagedType IManagedType}.
	 */
	Object getManagedType(Expression expression);

	/**
	 * Returns the mapping with the given name.
	 * <p>
	 * If it was going through Hermes SPI, the type of the argument would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IManagedType IManagedType} and the return type would be {@link
	 * org.eclipse.persistence.jpa.jpql.spi.IMapping IMapping}.
	 *
	 * @param managedType The managed type that has a mapping with the given name
	 * @param name The name of the mapping to retrieve
	 * @return Either the mapping or <code>null</code> if it could not be found
	 */
	Object getMappingNamed(Object managedType, String name);

	/**
	 * Returns the type of the given mapping object.
	 * <p>
	 * If it was going through Hermes SPI, the type of the argument would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IMapping IMapping} and the return type would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IType IType}.
	 *
	 * @param mapping The mapping object
	 * @return The type of the given mapping
	 */
	Object getMappingType(Object mapping);

	/**
	 * Returns the list of type declarations representing the given constructor's parameter types.
	 * If this is the default constructor, then an empty array should be returned.
	 * <p>
	 * If it was going through Hermes SPI, the type of the argument would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IConstructor IConstructor} and the return type would be {@link
	 * org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration ITypeDeclaration}.
	 *
	 * @param constructor The constructor to return its parameter types
	 * @return The list of parameter types or an empty list
	 */
	Object[] getMethodParameterTypeDeclarations(Object constructor);

	/**
	 * Returns the type by resolving the given {@link Expression}.
	 * <p>
	 * If it was going through Hermes SPI, the return type would be {@link org.eclipse.persistence.
	 * jpa.jpql.spi.IType IType}.
	 *
	 * @param expression The {@link Expression} to resolve
	 * @return The type of the given {@link Expression} or <code>null</code> if it could not be
	 * validated
	 */
	Object getType(Expression expression);

	/**
	 * Returns the type defined for the Java member.
	 * <p>
	 * If it was going through Hermes SPI, the type of the argument would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.ITypeDeclaration ITypeDeclaration} and the return type would be
	 * {@link org.eclipse.persistence.jpa.jpql.spi.IType IType}.
	 *
	 * @return The type defined for the Java member
	 */
	Object getType(Object typeDeclaration);

	/**
	 * Retrieves the class with the given fully qualified name.
	 * <p>
	 * If it was going through Hermes SPI, an {@link org.eclipse.persistence.jpa.jpql.spi.IType
	 * IType} would be returned.
	 *
	 * @param typeName The fully qualified  name of the class to retrieve
	 * @return The class to retrieve
	 */
	Object getType(String typeName);

	/**
	 * Returns the type declaration for the given {@link Expression}'s type.
	 * <p>
	 * If it was going through Hermes SPI, the type of the argument would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.ITypeDeclaration ITypeDeclaration}.
	 *
	 * @param expression The {@link Expression} to resolve
	 * @return Either the type declaration that was resolved for the given {@link Expression}
	 */
	Object getTypeDeclaration(Expression expression);

	/**
	 * Returns the helper that gives access to the most common class metadata.
	 *
	 * @return A helper containing a collection of methods related to class metadata
	 */
	ITypeHelper getTypeHelper();

	/**
	 * Returns the fully qualified class name of the given type.
	 * <p>
	 * If it was going through Hermes SPI, the type of the argument would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IType IType}.
	 *
	 * @param type The type to retrieve its name
	 * @return The name of the class represented by this one
	 */
	String getTypeName(Object type);

	/**
	 * Determines whether type 1 is an instance of type 2.
	 * <p>
	 * If it was going through Hermes SPI, the type of the arguments would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IType IType}.
	 *
	 * @param type1 The type to check if it is an instance of type 2
	 * @param type2 The type used to determine if the class represented by type 1 is an instance
	 * of with one
	 * @return <code>true</code> if type 1 is an instance of the type 2; <code>false</code> otherwise
	 */
	boolean isAssignableTo(Object type1, Object type2);

	/**
	 * Determines whether the given identification variable is defining a join or a collection member
	 * declaration expressions.
	 *
	 * @param variableName The identification variable to check for what it maps
	 * @return <code>true</code> if the given identification variable maps a collection-valued field
	 * defined in a <code>JOIN</code> or <code>IN</code> expression; <code>false</code> if it's not
	 * defined or it's mapping an abstract schema name
	 */
	boolean isCollectionIdentificationVariable(String variableName);

	/**
	 * Determines whether the given mapping is a collection type mapping.
	 * <p>
	 * If it was going through Hermes SPI, the type of the arguments would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IMapping IMapping}.
	 *
	 * @param mapping The mapping object to verify if it represents a collection mapping
	 * @return <code>true</code> if the given mapping is a collection mapping; <code>false</code>
	 * otherwise
	 */
	boolean isCollectionMapping(Object mapping);

	/**
	 * Determines whether the given type represents an {@link Enum}.
	 * <p>
	 * If it was going through Hermes SPI, the type of the argument would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IType IType}.
	 *
	 * @return <code>true</code> if the given type is an {@link Enum}; <code>false</code> otherwise
	 */
	boolean isEnumType(Object type);

	/**
	 * Determines whether an identification variable can be used in a comparison expression when the
	 * operator is either '<', '<=', '>', '>='.
	 *
	 * @param expression The {@link IdentificationVariable} that is mapped to either an entity, a
	 * singled-object value field, a collection-valued object field
	 * @return <code>true</code> if it can be used in a ordering comparison expression; <code>false</code>
	 * if it can't
	 */
	boolean isIdentificationVariableValidInComparison(IdentificationVariable expression);

	/**
	 * Determines whether the given managed type actually exists.
	 * <p>
	 * If it was going through Hermes SPI, the type of the argument would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IManagedType IManagedType}.
	 *
	 * @return <code>true</code> if the given managed type can be located; <code>false</code> if it
	 * could not be found
	 */
	boolean isManagedTypeResolvable(Object managedType);

	/**
	 * Determines whether the given mapping is a property type mapping.
	 * <p>
	 * If it was going through Hermes SPI, the type of the arguments would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IMapping IMapping}.
	 *
	 * @param mapping The mapping object to verify if it represents a property mapping
	 * @return <code>true</code> if the given mapping is a property mapping; <code>false</code> otherwise
	 */
	boolean isPropertyMapping(Object mapping);

	/**
	 * Determines whether the given mapping is a relationship type mapping.
	 * <p>
	 * If it was going through Hermes SPI, the type of the arguments would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IMapping IMapping}.
	 *
	 * @param mapping The mapping object to verify if it represents a relationship mapping
	 * @return <code>true</code> if the given mapping is a relationship mapping; <code>false</code> otherwise
	 */
	boolean isRelationshipMapping(Object mapping);

	/**
	 * Determines if the given variable is a result variable.
	 *
	 * @param variableName The variable to check if it's a result variable
	 * @return <code>true</code> if the given variable is defined as a result variable;
	 * <code>false</code> otherwise
	 */
	boolean isResultVariable(String variableName);

	/**
	 * Determines whether the given mapping is a transient attribute.
	 * <p>
	 * If it was going through Hermes SPI, the type of the arguments would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IMapping IMapping}.
	 *
	 * @param mapping The mapping object to verify if it represents a transient attribute
	 * @return <code>true</code> if the given attribute is a transient mapping; <code>false</code> otherwise
	 */
	boolean isTransient(Object mapping);

	/**
	 * Determines whether type declaration 1 is an instance of type declaration 2.
	 * <p>
	 * If it was going through Hermes SPI, the type of the arguments would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.ITypeDeclaration ITypeDeclaration}.
	 *
	 * @param typeDeclaration1 The type declaration to check if it is an instance of type declaration 2
	 * @param typeDeclaration2 The type used to determine if the class represented by type
	 * declaration 1 is an instance of with one
	 * @return <code>true</code> if type declaration 1 is an instance of the type declaration 2;
	 * <code>false</code> otherwise
	 */
	boolean isTypeDeclarationAssignableTo(Object typeDeclaration1, Object typeDeclaration2);

	/**
	 * Determines whether the given type actually exists.
	 * <p>
	 * If it was going through Hermes SPI, the type of the argument would be {@link org.eclipse.
	 * persistence.jpa.jpql.spi.IType IType}.
	 *
	 * @return <code>true</code> if the actual class exists; <code>false</code> otherwise
	 */
	boolean isTypeResolvable(Object type);

	/**
	 * Determines whether a path expression should be validated or not. This can happen in some very
	 * specific cases.
	 *
	 * @param expression The {@link StateFieldPathExpression} that might not need to be validated
	 * @return <code>true</code> to validate the given path expression; <code>false</code> otherwise
	 */
	boolean isValidatingPathExpressionAllowed(StateFieldPathExpression expression);

	/**
	 * Changes the state of this helper to use the given subquery.
	 *
	 * @param expression The parsed tree representation of the subquery that will become the current query
	 * @see #disposeSubqueryContext()
	 */
	void newSubqueryContext(SimpleSelectStatement expression);

	/**
	 * Returns the mapping for the field represented by the given {@link Expression}.
	 * <p>
	 * If it was going through Hermes SPI, the return type would be {@link org.eclipse.persistence.
	 * jpa.jpql.spi.IMapping IMapping}.
	 *
	 * @param expression The {@link Expression} representing a state field path expression or a
	 * collection-valued path expression
	 * @return Either the mapping or <code>null</code> if none exists
	 */
	Object resolveMapping(Expression expression);

	/**
	 * Returns the mapping that should be a persistence field from the entity defined by the given
	 * identification variable.
	 * <p>
	 * If it was going through Hermes SPI, the return type would be {@link org.eclipse.persistence.
	 * jpa.jpql.spi.IMapping IMapping}.
	 *
	 * @param identificationVariable The identification variable that is defined in the <code>FROM</code>
	 * clause of the query (which can be in the current subquery) or in one of the parent queries.
	 * @param name The name of the persistent field to retrieve
	 * @return The persistence field with the given name or <code>null</code> if it could not be found
	 */
	Object resolveMapping(String identificationVariable, String name);
}