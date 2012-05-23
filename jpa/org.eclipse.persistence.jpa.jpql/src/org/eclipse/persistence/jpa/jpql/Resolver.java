/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * A <code>Resolver</code> is responsible to resolve a property by retrieving either the managed
 * type, the mapping, the type and the type declaration depending on the type of resolver.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class Resolver {

	/**
	 * This is only applicable to 1:1 relationships, and allows the target of the relationship to be
	 * <code>null</code> if there is no corresponding relationship in the database.
	 */
	private boolean nullAllowed;

	/**
	 * The parent of this resolver, which is never <code>null</code>.
	 */
	private final Resolver parent;

	/**
	 * The cached {@link Resolver Resolvers} mapped with a variable name.
	 */
	private Map<String, Resolver> resolvers;

	/**
	 * The cached {@link IType} of the value to resolve.
	 */
	private IType type;

	/**
	 * The cached {@link ITypeDeclaration} of the value to resolve.
	 */
	private ITypeDeclaration typeDeclaration;

	/**
	 * Creates a new <code>Resolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 * @exception NullPointerException If the parent is <code>null</code>
	 */
	protected Resolver(Resolver parent) {
		super();
		checkParent(parent);
		this.parent = parent;
	}

	/**
	 * Visits this {@link Resolver} by the given {@link ResolverVisitor visitor}.
	 *
	 * @param visitor The {@link ResolverVisitor visitor} to visit this object
	 */
	public abstract void accept(ResolverVisitor visitor);

	/**
	 * Caches the given {@link Resolver}.
	 *
	 * @param variableName The key used to cache the given {@link Resolver}
	 * @param resolver The {@link Resolver} to cache
	 */
	protected final void addChild(String variableName, Resolver resolver) {

		if (resolvers == null) {
			resolvers = new HashMap<String, Resolver>();
		}

		resolvers.put(variableName, resolver);
	}

	/**
	 * Resolves the {@link IType} of the property handled by this {@link Resolver}.
	 *
	 * @return Either the {@link IType} that was resolved by this {@link Resolver} or the {@link
	 * IType} for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	protected IType buildType() {
		return getTypeDeclaration().getType();
	}

	/**
	 * Resolves the {@link ITypeDeclaration} of the property handled by this {@link Resolver}.
	 *
	 * @return Either the {@link ITypeDeclaration} that was resolved by this {@link Resolver} or the
	 * {@link ITypeDeclaration} for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	protected abstract ITypeDeclaration buildTypeDeclaration();

	protected void checkParent(Resolver parent) {
		if (parent == null) {
			throw new NullPointerException("The parent resolver cannot be null");
		}
	}

	/**
	 * Retrieves the child of this {@link Resolver} that has the given variable name.
	 *
	 * @param variableName The name of the property that was cached
	 * @return The cached {@link Resolver} mapped with the given property name; otherwise <code>null</code>
	 */
	protected final Resolver getChild(String variableName) {
		return (resolvers != null) ? resolvers.get(variableName) : null;
	}

	/**
	 * Returns the {@link IManagedType} associated with the field handled by this {@link Resolver}.
	 * If this {@link Resolver} does not handle a field that has a {@link IManagedType}, then
	 * <code>null</code> should be returned.
	 * <p>
	 * For example: "<code><b>SELECT</b> e <b>FROM</b> Employee e</code>", the {@link Resolver} for
	 * <i>e</i> would be returning the {@link IManagedType} for <i>Employee</i>.
	 *
	 * @return Either the {@link IManagedType}, if it could be resolved; <code>null</code> otherwise
	 */
	public IManagedType getManagedType() {
		return null;
	}

	/**
	 * Returns the {@link IMapping} for the wrapped field.
	 *
	 * @return Either the {@link IMapping} or <code>null</code> if none exists
	 */
	public IMapping getMapping() {
		return null;
	}

	/**
	 * Returns the parent of this {@link Resolver}.
	 *
	 * @return The parent of this {@link Resolver}
	 */
	public Resolver getParent() {
		return parent;
	}

	/**
	 * Returns the managed type of the parent resolver.
	 *
	 * @return The managed type of the parent resolver
	 */
	protected final IManagedType getParentManagedType() {
		return parent.getManagedType();
	}

	/**
	 * Returns the {@link IMapping} of the parent resolver.
	 *
	 * @return The {@link IMapping} of the parent resolver
	 */
	protected final IMapping getParentMapping() {
		return parent.getMapping();
	}

	/**
	 * Returns the type of the parent resolver.
	 *
	 * @return The type of the parent resolver
	 */
	protected final IType getParentType() {
		return parent.getType();
	}

	/**
	 * Returns the type declaration of the parent resolver.
	 *
	 * @return The type declaration of the parent resolver
	 */
	protected final ITypeDeclaration getParentTypeDeclaration() {
		return parent.getTypeDeclaration();
	}

	/**
	 * Returns the provider of managed types.
	 *
	 * @return The container holding the managed types
	 */
	protected final IManagedTypeProvider getProvider() {
		return getQuery().getProvider();
	}

	/**
	 * Returns the external form representing the JPQL query.
	 *
	 * @return The external form of the JPQL query
	 */
	protected IQuery getQuery() {
		return parent.getQuery();
	}

	/**
	 * Returns the {@link IType} of the field handled by this {@link Resolver}.
	 *
	 * @return Either the {@link IType} that was resolved by this {@link Resolver} or the {@link IType}
	 * for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	public final IType getType() {
		if (type == null) {
			type = buildType();
		}
		return type;
	}

	/**
	 * Returns the {@link IType} of the given Java type.
	 *
	 * @param type The Java type for which its external form will be returned
	 * @return The {@link IType} representing the given Java type
	 */
	protected final IType getType(Class<?> type) {
		return getTypeRepository().getType(type);
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param typeName The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	protected final IType getType(String typeName) {
		return getTypeRepository().getType(typeName);
	}

	/**
	 * Returns the {@link ITypeDeclaration} of the field handled by this {@link Resolver}.
	 *
	 * @return Either the {@link ITypeDeclaration} that was resolved by this {@link Resolver} or the
	 * {@link ITypeDeclaration} for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	protected final ITypeDeclaration getTypeDeclaration() {
		if (typeDeclaration == null) {
			typeDeclaration = buildTypeDeclaration();
		}
		return typeDeclaration;
	}

	/**
	 * Returns a helper that gives access to the most common {@link IType types}.
	 *
	 * @return A helper containing a collection of methods related to {@link IType}
	 */
	protected final TypeHelper getTypeHelper() {
		return getTypeRepository().getTypeHelper();
	}

	/**
	 * Returns the type repository for the application.
	 *
	 * @return The repository of {@link IType ITypes}
	 */
	protected final ITypeRepository getTypeRepository() {
		return getQuery().getProvider().getTypeRepository();
	}

	/**
	 * Determines whether the {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression}
	 * to be created, which wraps the attribute or query key name allows the target of the 1:1
	 * relationship to be <code>null</code> if there is no corresponding relationship in the database.
	 *
	 * @return <code>true</code> to allow <code>null</code> if the corresponding relationship in the
	 * database does not exists; <code>false</code> otherwise
	 */
	public boolean isNullAllowed() {
		return nullAllowed;
	}

	/**
	 * Sets whether the {@link org.eclipse.persistence.jpa.jpql.parser.Expression Expression} to be
	 * created, which wraps the attribute or query key name allows the target of the 1:1 relationship
	 * to be <code>null</code> if there is no corresponding relationship in the database.
	 *
	 * @param nullAllowed <code>true</code> to allow <code>null</code> if the corresponding
	 * relationship in the database does not exists; <code>false</code> otherwise
	 */
	public void setNullAllowed(boolean nullAllowed) {
		this.nullAllowed = nullAllowed;
	}
}