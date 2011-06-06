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
package org.eclipse.persistence.jpa.internal.jpql;

import org.eclipse.persistence.jpa.internal.jpql.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.AnonymousExpressionVisitor;
import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.jpql.TypeHelper;
import org.eclipse.persistence.jpa.jpql.spi.IEmbeddable;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IJPAVersion;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeVisitor;
import org.eclipse.persistence.jpa.jpql.spi.IMappedSuperclass;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IPlatform;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.spi.ITypeRepository;

/**
 * An abstract class that provides helper methods for quick access to the {@link JPQLQueryContext}'s
 * methods.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractVisitor extends AnonymousExpressionVisitor {

	/**
	 * This visitor determines whether the visited {@link Expression} is the {@link CollectionExpression}.
	 */
	private CollectionExpressionVisitor collectionExpressionVisitor;

	/**
	 * This visitor simply determines what type of {@link IManagedType} that was visited.
	 */
	private ManagedTypeVisitor managedTypeVisitor;

	/**
	 * This visitor determines whether the visited {@link Expression} is the {@link NullExpression}.
	 */
	private NullExpressionVisitor nullExpressionVisitor;

	/**
	 * The context used to query information about the query.
	 */
	protected final JPQLQueryContext queryContext;

	/**
	 * Creates a new <code>AbstractVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the query
	 */
	protected AbstractVisitor(JPQLQueryContext queryContext) {
		super();
		this.queryContext = queryContext;
		initialize();
	}

	/**
	 * Casts the given {@link Expression} to a {@link CollectionExpression} if it is actually an
	 * object of that type.
	 *
	 * @param expression The {@link Expression} to cast
	 * @return The given {@link Expression} if it is a {@link CollectionExpression} or <code>null</code>
	 * if it is any other object
	 */
	protected final CollectionExpression collectionExpression(Expression expression) {
		CollectionExpressionVisitor visitor = collectionExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression;
		}
		finally {
			visitor.expression = null;
		}
	}

	private CollectionExpressionVisitor collectionExpressionVisitor() {
		if (collectionExpressionVisitor == null) {
			collectionExpressionVisitor = new CollectionExpressionVisitor();
		}
		return collectionExpressionVisitor;
	}

	/**
	 * Disposes of the internal data.
	 */
	public void dispose() {
	}

	/**
	 * Returns the given {@link IManagedType} if it is an {@link IEmbeddable}.
	 *
	 * @param managedType The {@link IManagedType} to check if it's an {@link IEmbeddable}
	 * @return The given {@link IManagedType} cast as {@link IEmbeddable} or <code>null</code> if it
	 * is not an {@link IEmbeddable}
	 */
	protected IEmbeddable embeddable(IManagedType managedType) {

		if (managedType == null) {
			return null;
		}

		ManagedTypeVisitor visitor = managedTypeVisitor();

		try {
			managedType.accept(visitor);
			return visitor.embeddable;
		}
		finally {
			visitor.embeddable       = null;
			visitor.entity           = null;
			visitor.mappedSuperclass = null;
		}
	}

	/**
	 * Returns the given {@link IManagedType} if it is an {@link IEntity}.
	 *
	 * @param managedType The {@link IManagedType} to check if it's an {@link IEntity}
	 * @return The given {@link IManagedType} cast as {@link IEntity} or <code>null</code> if it is
	 * not an {@link IEntity}
	 */
	protected IEntity entity(IManagedType managedType) {

		if (managedType == null) {
			return null;
		}

		ManagedTypeVisitor visitor = managedTypeVisitor();

		try {
			managedType.accept(visitor);
			return visitor.entity;
		}
		finally {
			visitor.embeddable       = null;
			visitor.entity           = null;
			visitor.mappedSuperclass = null;
		}
	}

	/**
	 * Returns the version of the Java Persistence this entity for which it was defined.
	 *
	 * @return The version of the Java Persistence being used
	 */
	protected IJPAVersion getJPAVersion() {
		return getProvider().getVersion();
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
	 * Retrieves the entity with the given abstract schema name, which can also be the entity class
	 * name.
	 *
	 * @param abstractSchemaName The abstract schema name, which can be different from the entity
	 * class name but by default, it's the same
	 * @return The managed type that has the given name or <code>null</code> if none could be found
	 */
	protected IManagedType getManagedType(String abstractSchemaName) {
		return getProvider().getManagedType(abstractSchemaName);
	}

	/**
	 * Returns the {@link IMapping} for the field represented by the given {@link Expession}.
	 *
	 * @param expression The {@link Expression} representing a state field path expression or a
	 * collection-valued path expression
	 * @return Either the {@link IMapping} or <code>null</code> if none exists
	 */
	protected IMapping getMapping(Expression expression) {
		return queryContext.getMapping(expression);
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
		return queryContext.getQuery();
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
		return queryContext.getResolver(expression);
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
		return queryContext.getType(expression);
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
		return queryContext.getTypeDeclaration(expression);
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
	 * Initializes this class.
	 */
	protected void initialize() {
	}

	/**
	 * Determines whether the platform is EclipseLink.
	 *
	 * @return <code>true</code> if the platform is EclipseLink; <code>false</code> if it's pure JPA
	 * (generic)
	 */
	protected boolean isEclipseLinkPlatform() {
		return getProvider().getPlatform() == IPlatform.ECLIPSE_LINK;
	}

	/**
	 * Determines whether the given {@link IManagedType} is an {@link IEmbeddable}.
	 *
	 * @param managedType The {@link IManagedType} to check if it's an {@link IEmbeddable}
	 * @return <code>true</code> if the given {@link IManagedType} is an {@link IEmbeddable};
	 * <code>false</code> otherwise
	 */
	protected boolean isEmbeddable(IManagedType managedType) {
		return embeddable(managedType) != null;
	}

	/**
	 * Determines whether the given {@link IManagedType} is an {@link IEntity}.
	 *
	 * @param managedType The {@link IManagedType} to check if it's an {@link IEntity}
	 * @return <code>true</code> if the given {@link IManagedType} is an {@link IEntity};
	 * <code>false</code> otherwise
	 */
	protected boolean isEntity(IManagedType managedType) {
		return entity(managedType) != null;
	}

	/**
	 * Determines whether the platform is pure JPA (generic).
	 *
	 * @return <code>true</code> if the platform is pure JPA; <code>false</code> if it's EclipseLink
	 */
	protected boolean isJavaPlatform() {
		return getProvider().getPlatform() == IPlatform.JAVA;
	}

	/**
	 * Determines whether the given {@link IManagedType} is an {@link IMappedSuperclass}.
	 *
	 * @param managedType The {@link IManagedType} to check if it's an {@link IMappedSuperclass}
	 * @return <code>true</code> if the given {@link IManagedType} is an {@link IMappedSuperclass};
	 * <code>false</code> otherwise
	 */
	protected boolean isMappedSuperclass(IManagedType managedType) {
		return mappedSuperclass(managedType) != null;
	}

	/**
	 * Determines whether the given {@link Expression} is the {@link NullExpression}.
	 *
	 * @param expression The {@link Expression} to visit
	 * @return <code>true</code> if the given {@link Expression} is {@link NullExpression};
	 * <code>false</code> otherwise
	 */
	protected final boolean isNull(Expression expression) {
		NullExpressionVisitor visitor = nullExpressionVisitor();
		try {
			expression.accept(visitor);
			return visitor.expression != null;
		}
		finally {
			visitor.expression = null;
		}
	}

	private ManagedTypeVisitor managedTypeVisitor() {
		if (managedTypeVisitor == null) {
			managedTypeVisitor = new ManagedTypeVisitor();
		}
		return managedTypeVisitor;
	}

	/**
	 * Returns the given {@link IManagedType} if it is an {@link IMappedSuperclass}.
	 *
	 * @param managedType The {@link IManagedType} to check if it's an {@link IMappedSuperclass}
	 * @return The given {@link IManagedType} cast as {@link IMappedSuperclass} or <code>null</code>
	 * if it is not an {@link IMappedSuperclass}
	 */
	protected IMappedSuperclass mappedSuperclass(IManagedType managedType) {

		if (managedType == null) {
			return null;
		}

		ManagedTypeVisitor visitor = managedTypeVisitor();

		try {
			managedType.accept(visitor);
			return visitor.mappedSuperclass;
		}
		finally {
			visitor.embeddable       = null;
			visitor.entity           = null;
			visitor.mappedSuperclass = null;
		}
	}

	private NullExpressionVisitor nullExpressionVisitor() {
		if (nullExpressionVisitor == null) {
			nullExpressionVisitor = new NullExpressionVisitor();
		}
		return nullExpressionVisitor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected abstract void visit(Expression expression);

	/**
	 * This visitor retrieves the {@link CollectionExpression} if it is visited.
	 */
	private static class CollectionExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * The {@link CollectionExpression} if it is the {@link Expression} that was visited.
		 */
		CollectionExpression expression;

		/**
		 * Creates a new <code>CollectionExpressionVisitor</code>.
		 */
		public CollectionExpressionVisitor() {
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

	private static class ManagedTypeVisitor implements IManagedTypeVisitor {

		/**
		 * The {@link IEmbeddable} that won't be <code>null</code> if it was visited.
		 */
		IEmbeddable embeddable;

		/**
		 * The {@link IEntity} that won't be <code>null</code> if it was visited.
		 */
		IEntity entity;

		/**
		 * The {@link IMappedSuperclass} that won't be <code>null</code> if it was visited.
		 */
		IMappedSuperclass mappedSuperclass;

		/**
		 * {@inheritDoc}
		 */
		public void visit(IEmbeddable embeddable) {
			this.embeddable = embeddable;
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(IEntity entity) {
			this.entity = entity;
		}

		/**
		 * {@inheritDoc}
		 */
		public void visit(IMappedSuperclass mappedSuperclass) {
			this.mappedSuperclass = mappedSuperclass;
		}
	}

	/**
	 * This visitor checks to see if the visited expression is {@link NullExpression}.
	 */
	private static class NullExpressionVisitor extends AbstractExpressionVisitor {

		/**
		 * <code>true</code> if the visited expression is {@link NullExpression}.
		 */
		NullExpression expression;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(NullExpression expression) {
			this.expression = expression;
		}
	}
}