/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ExpressionRegistry;
import org.eclipse.persistence.jpa.jpql.parser.IdentifierRole;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLQueryBNF;
import org.eclipse.persistence.jpa.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.jpql.spi.IEmbeddable;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IMappedSuperclass;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;

/**
 * An abstract class that provides helper methods for quick access to the {@link JPQLQueryContext}'s
 * methods.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractVisitor extends AnonymousExpressionVisitor {

	/**
	 * This visitor determines whether the visited {@link Expression} is the {@link CollectionExpression}.
	 */
	private CollectionExpressionVisitor collectionExpressionVisitor;

	/**
	 * The context used to query information about the JPQL query.
	 */
	protected final JPQLQueryContext context;

	/**
	 * This visitor determines whether the visited {@link Expression} is the {@link NullExpression}.
	 */
	private NullExpressionVisitor nullExpressionVisitor;

	/**
	 * Creates a new <code>AbstractVisitor</code>.
	 *
	 * @param context The context used to query information about the JPQL query
	 * @exception NullPointerException The {@link JPQLQueryContext} cannot be <code>null</code>
	 */
	protected AbstractVisitor(JPQLQueryContext context) {
		super();
		Assert.isNotNull(context, "The JPQLQueryContext cannot be null");
		this.context = context;
		initialize();
	}

	/**
	 * Creates a visitor that collects the {@link CollectionExpression} if it's been visited.
	 *
	 * @return A new {@link CollectionExpressionVisitor}
	 */
	protected CollectionExpressionVisitor buildCollectionExpressionVisitor() {
		return new CollectionExpressionVisitor();
	}

	/**
	 * Creates a visitor that collects the {@link NullExpression} if it's been visited.
	 *
	 * @return A new {@link NullExpressionVisitor}
	 */
	protected NullExpressionVisitor buildNullExpressionVisitor() {
		return new NullExpressionVisitor();
	}

	/**
	 * Disposes of the internal data.
	 */
	public void dispose() {
	}

	/**
	 * Casts the given {@link Expression} to a {@link CollectionExpression} if it is actually an
	 * object of that type.
	 *
	 * @param expression The {@link Expression} to cast
	 * @return The given {@link Expression} if it is a {@link CollectionExpression} or <code>null</code>
	 * if it is any other object
	 */
	protected CollectionExpression getCollectionExpression(Expression expression) {
		CollectionExpressionVisitor visitor = getCollectionExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression;
		}
		finally {
			visitor.expression = null;
		}
	}

	/**
	 * Returns the visitor that collects the {@link CollectionExpression} if it's been visited.
	 *
	 * @return The {@link CollectionExpressionVisitor}
	 * @see #buildCollectionExpressionVisitor()
	 */
	protected CollectionExpressionVisitor getCollectionExpressionVisitor() {
		if (collectionExpressionVisitor == null) {
			collectionExpressionVisitor = buildCollectionExpressionVisitor();
		}
		return collectionExpressionVisitor;
	}

	/**
	 * Retrieves the {@link IEmbeddable} for the given {@link IType} if one exists.
	 *
	 * @param type The {@link IType} that is mapped as an embeddable
	 * @return The given {@link IType} mapped as an embeddable; <code>null</code> if none exists or
	 * if it's mapped as a different managed type
	 */
	protected IEmbeddable getEmbeddable(IType type) {
		return getProvider().getEmbeddable(type);
	}

	/**
	 * Retrieves the {@link IEntity} for the given {@link IType} if one exists.
	 *
	 * @param type The {@link IType} that is mapped as an entity
	 * @return The given {@link IType} mapped as an entity; <code>null</code> if none exists or if
	 * it's mapped as a different managed type
	 */
	protected IEntity getEntity(IType type) {
		return getProvider().getEntity(type);
	}

	/**
	 * Retrieves the entity with the given abstract schema name, which can also be the entity class
	 * name.
	 *
	 * @param entityName The abstract schema name, which can be different from the entity class name
	 * but by default, it's the same
	 * @return The managed type that has the given name or <code>null</code> if none could be found
	 */
	protected IManagedType getEntityNamed(String entityName) {
		return getProvider().getEntityNamed(entityName);
	}

	/**
	 * Returns the registry containing the {@link JPQLQueryBNF JPQLQueryBNFs} and the {@link
	 * org.eclipse.persistence.jpa.jpql.parser.ExpressionFactory ExpressionFactories} that are used
	 * to properly parse a JPQL query.
	 *
	 * @return The registry containing the information related to the JPQL grammar
	 */
	protected ExpressionRegistry getExpressionRegistry() {
		return getQueryContext().getExpressionRegistry();
	}

	/**
	 * Returns the JPQL grammar that will be used to define how to parse a JPQL query.
	 *
	 * @return The grammar that was used to parse this {@link Expression}
	 */
	protected JPQLGrammar getGrammar() {
		return getQueryContext().getGrammar();
	}

	/**
	 * Retrieves the role of the given identifier. A role helps to describe the purpose of the
	 * identifier in a query.
	 *
	 * @param identifier The identifier for which its role is requested
	 * @return The role of the given identifier
	 */
	protected IdentifierRole getIdentifierRole(String identifier) {
		return getExpressionRegistry().getIdentifierRole(identifier);
	}

	/**
	 * Retrieves the JPA version in which the identifier was first introduced.
	 *
	 * @return The version in which the identifier was introduced
	 */
	protected JPAVersion getIdentifierVersion(String identifier) {
		return getExpressionRegistry().getIdentifierVersion(identifier);
	}

	/**
	 * Returns the version of the Java Persistence this entity for which it was defined.
	 *
	 * @return The version of the Java Persistence being used
	 */
	protected JPAVersion getJPAVersion() {
		return getQueryContext().getJPAVersion();
	}

	/**
	 * Returns the parsed tree representation of the JPQL query.
	 *
	 * @return The parsed tree representation of the JPQL query
	 */
	protected JPQLExpression getJPQLExpression() {
		return context.getJPQLExpression();
	}

	/**
	 * Retrieves the entity for the given type.
	 *
	 * @param type The type that is used as a managed type
	 * @return The managed type for the given type, if one exists, <code>null</code> otherwise
	 */
	protected IManagedType getManagedType(IType type) {
		return getProvider().getManagedType(type);
	}

	/**
	 * Retrieves the {@link IMappedSuperclass} for the given {@link IType} if one exists.
	 *
	 * @param type The {@link IType} that is mapped as an embeddable
	 * @return The given {@link IType} mapped as an mapped super class; <code>null</code> if none
	 * exists or if it's mapped as a different managed type
	 */
	protected IMappedSuperclass getMappedSuperclass(IType type) {
		return getProvider().getMappedSuperclass(type);
	}

	/**
	 * Returns the {@link IMapping} for the field represented by the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} representing a state field path expression or a
	 * collection-valued path expression
	 * @return Either the {@link IMapping} or <code>null</code> if none exists
	 */
	protected IMapping getMapping(Expression expression) {
		return context.getMapping(expression);
	}

	/**
	 * Returns the visitor that collects the {@link NullExpression} if it's been visited.
	 *
	 * @return The {@link NullExpressionVisitor}
	 * @see #buildNullExpressionVisitor()
	 */
	protected NullExpressionVisitor getNullExpressionVisitor() {
		if (nullExpressionVisitor == null) {
			nullExpressionVisitor = buildNullExpressionVisitor();
		}
		return nullExpressionVisitor;
	}

	/**
	 * Retrieves the provider of managed types.
	 *
	 * @return The object that has access to the application's managed types.
	 */
	protected IManagedTypeProvider getProvider() {
		return getQuery().getProvider();
	}

	/**
	 * Returns the external form of the JPQL query.
	 *
	 * @return The external form of the JPQL query
	 */
	protected IQuery getQuery() {
		return context.getQuery();
	}

	/**
	 * Retrieves the BNF object that was registered for the given unique identifier.
	 *
	 * @param queryBNFID The unique identifier of the {@link JPQLQueryBNF} to retrieve
	 * @return The {@link JPQLQueryBNF} representing a section of the grammar
	 */
	protected JPQLQueryBNF getQueryBNF(String queryBNFId) {
		return getExpressionRegistry().getQueryBNF(queryBNFId);
	}

	/**
	 * Returns the {@link JPQLQueryContext} that is used by this visitor.
	 *
	 * @return The {@link JPQLQueryContext} holding onto the JPQL query and the cached information
	 */
	protected JPQLQueryContext getQueryContext() {
		return context;
	}

	/**
	 * Returns the string representation of the JPQL query.
	 *
	 * @return A non-<code>null</code> string representation of the JPQL query
	 */
	protected String getQueryExpression() {
		return getQuery().getExpression();
	}

	/**
	 * Creates or retrieved the cached {@link Resolver} for the given {@link Expression}. The
	 * {@link Resolver} can return the {@link IType} and {@link ITypeDeclaration} of the {@link
	 * Expression} and either the {@link IManagedType} or the {@link IMapping}.
	 *
	 * @param expression The {@link Expression} for which its {@link Resolver} will be retrieved
	 * @return {@link Resolver} for the given {@link Expression}
	 */
	protected Resolver getResolver(Expression expression) {
		return context.getResolver(expression);
	}

	/**
	 * Retrieves the external type for the given Java type.
	 *
	 * @param type The Java type to wrap with an external form
	 * @return The external form of the given type
	 */
	protected IType getType(Class<?> type) {
		return getTypeRepository().getType(type);
	}

	/**
	 * Returns the {@link IType} of the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} for which its type will be calculated
	 * @return Either the {@link IType} that was resolved by this {@link Resolver} or the
	 * {@link IType} for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	protected IType getType(Expression expression) {
		return context.getType(expression);
	}

	/**
	 * Retrieves the external class for the given fully qualified class name.
	 *
	 * @param typeName The fully qualified class name of the class to retrieve
	 * @return The external form of the class to retrieve
	 */
	protected IType getType(String name) {
		return getTypeRepository().getType(name);
	}

	/**
	 * Returns the {@link ITypeDeclaration} of the field handled by this {@link Resolver}.
	 *
	 * @param expression The {@link Expression} for which its type declaration will be calculated
	 * @return Either the {@link ITypeDeclaration} that was resolved by this {@link Resolver} or the
	 * {@link ITypeDeclaration} for {@link IType#UNRESOLVABLE_TYPE} if it could not be resolved
	 */
	protected ITypeDeclaration getTypeDeclaration(Expression expression) {
		return context.getTypeDeclaration(expression);
	}

	/**
	 * Returns a helper that gives access to the most common {@link IType types}.
	 *
	 * @return A helper containing a collection of methods related to {@link IType}
	 */
	protected TypeHelper getTypeHelper() {
		return getTypeRepository().getTypeHelper();
	}

	/**
	 * Returns the type repository for the application.
	 *
	 * @return The repository of {@link IType ITypes}
	 */
	protected ITypeRepository getTypeRepository() {
		return getProvider().getTypeRepository();
	}

	/**
	 * Initializes this visitor.
	 */
	protected void initialize() {
	}

	/**
	 * Determines whether the given {@link Expression} is the {@link NullExpression}.
	 *
	 * @param expression The {@link Expression} to visit
	 * @return <code>true</code> if the given {@link Expression} is {@link NullExpression};
	 * <code>false</code> otherwise
	 */
	protected boolean isNull(Expression expression) {
		NullExpressionVisitor visitor = getNullExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression != null;
		}
		finally {
			visitor.expression = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected abstract void visit(Expression expression);

	/**
	 * This visitor retrieves the {@link CollectionExpression} if it is visited.
	 */
	protected static class CollectionExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link CollectionExpression} if it is the {@link Expression} that was visited.
		 */
		protected CollectionExpression expression;

		/**
		 * Creates a new <code>CollectionExpressionVisitor</code>.
		 */
		protected CollectionExpressionVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(CollectionExpression expression) {
			this.expression = expression;
		}
	}

	/**
	 * This visitor checks to see if the visited expression is {@link NullExpression}.
	 */
	protected static class NullExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link NullExpression} if it is the {@link Expression} that was visited.
		 */
		protected NullExpression expression;

		/**
		 * Creates a new <code>NullExpressionVisitor</code>.
		 */
		protected NullExpressionVisitor() {
			super();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullExpression expression) {
			this.expression = expression;
		}
	}
}