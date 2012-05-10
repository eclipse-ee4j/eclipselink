/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeVisitor;
import org.eclipse.persistence.jpa.jpql.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.spi.IMappingType;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.ITypeDeclaration;
import org.eclipse.persistence.jpa.jpql.util.iterator.CloneIterator;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableIterator;

import static org.eclipse.persistence.jpa.jpql.LiteralType.*;

/**
 * This {@link Resolver} wraps a subquery that is used as the "root" object in a query's declaration.
 * <p>
 * Example:
 * <p>
 * <pre><code> SELECT e.firstName
 * FROM Employee e, (SELECT count(e2), e2.firstName FROM Employee e2) e3
 * WHERE e.firstName = e3.firstName</code></pre>
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class FromSubqueryResolver extends Resolver {

	/**
	 * The virtual {@link IManagedType} representing the subquery.
	 */
	private IManagedType managedType;

	/**
	 * The {@link JPQLQueryContext} is used to query information about the application metadata and
	 * cached information.
	 */
	private JPQLQueryContext queryContext;

	/**
	 * The subquery being defined as a "root" object.
	 */
	private SimpleSelectStatement subquery;

	/**
	 * Creates a new <code>FromSubqueryResolver</code>.
	 *
	 * @param parent The parent of this resolver, which is never <code>null</code>
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 * @param subquery
	 * @exception NullPointerException If the parent is <code>null</code>
	 */
	public FromSubqueryResolver(Resolver parent,
	                            JPQLQueryContext queryContext,
	                            SimpleSelectStatement subquery) {

		super(parent);
		this.subquery     = subquery;
		this.queryContext = queryContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(ResolverVisitor visitor) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IType buildType() {
		return getManagedType().getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ITypeDeclaration buildTypeDeclaration() {
		return getType().getTypeDeclaration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType getManagedType() {
		if (managedType == null) {
			managedType = new VirtualManagedType();
		}
		return managedType;
	}

	/**
	 * This {@link IManagedType} represents a virtual managed type where its content will be derived
	 * from the subquery.
	 */
	protected class VirtualManagedType implements IManagedType {

		/**
		 * The virtual {@link IMapping mappings} representing what is selected.
		 */
		private Map<String, IMapping> mappings;

		/**
		 * {@inheritDoc}
		 */
		public void accept(IManagedTypeVisitor visitor) {
		}

		/**
		 * {@inheritDoc}
		 */
		public int compareTo(IManagedType managedType) {
			return getType().getName().compareTo(managedType.getType().getName());
		}

		/**
		 * {@inheritDoc}
		 */
		public IMapping getMappingNamed(String name) {
			initializeMappings();
			return mappings.get(name);
		}

		/**
		 * {@inheritDoc}
		 */
		public IManagedTypeProvider getProvider() {
			return FromSubqueryResolver.this.getProvider();
		}

		/**
		 * {@inheritDoc}
		 */
		public IType getType() {
			return getProvider().getTypeRepository().getType(IType.UNRESOLVABLE_TYPE);
		}

		private void initializeMappings() {

			if (mappings == null) {
				mappings = new HashMap<String, IMapping>();

				// Create virtual mappings that wraps the select items
				VirtualMappingBuilder builder = new VirtualMappingBuilder();
				builder.parent   = this;
				builder.mappings = mappings;

				subquery.accept(builder);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public IterableIterator<IMapping> mappings() {
			initializeMappings();
			return new CloneIterator<IMapping>(mappings.values());
		}
	}

	/**
	 * This virtual {@link IMapping} wraps one of the select items.
	 */
	protected class VirtualMapping implements IMapping {

		private String name;
		private IManagedType parent;
		private Resolver resolver;

		protected VirtualMapping(IManagedType parent, String name, Resolver resolver) {
			super();
			this.name     = name;
			this.parent   = parent;
			this.resolver = resolver;
		}

		/**
		 * {@inheritDoc}
		 */
		public int compareTo(IMapping mapping) {
			return getName().compareTo(mapping.getName());
		}

		/**
		 * {@inheritDoc}
		 */
		public int getMappingType() {
			IMapping mapping = resolver.getMapping();
			return (mapping != null) ? mapping.getMappingType() : IMappingType.TRANSIENT;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getName() {
			return name;
		}

		/**
		 * {@inheritDoc}
		 */
		public IManagedType getParent() {
			return parent;
		}

		/**
		 * {@inheritDoc}
		 */
		public IType getType() {
			return resolver.getType();
		}

		/**
		 * {@inheritDoc}
		 */
		public ITypeDeclaration getTypeDeclaration() {
			return resolver.getTypeDeclaration();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
			// TODO: Can we do this???
			return getType().hasAnnotation(annotationType);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isCollection() {
			IMapping mapping = resolver.getMapping();
			return (mapping != null) ? mapping.isCollection() : false;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isProperty() {
			IMapping mapping = resolver.getMapping();
			return (mapping != null) ? mapping.isProperty() : false;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isRelationship() {
			IMapping mapping = resolver.getMapping();
			return (mapping != null) ? mapping.isRelationship() : false;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isTransient() {
			IMapping mapping = resolver.getMapping();
			return (mapping != null) ? mapping.isTransient() : false;
		}
	}

	/**
	 * This visitor will traverse the <code><b>SELECT</b></code> clause and create virtual mappings
	 * for the state field path expressions and any expression aliased with a result variable.
	 */
	protected class VirtualMappingBuilder extends AbstractEclipseLinkExpressionVisitor {

		/**
		 * The virtual {@link IMapping} that wraps a select item mapped with the result variable if
		 * one was defined otherwise the name will be generated based on the type of select item.
		 */
		private Map<String, IMapping> mappings;

		/**
		 * The virtual {@link IManagedType}.
		 */
		protected IManagedType parent;

		protected IMapping buildMapping(String name, Resolver resolver) {
			return new VirtualMapping(parent, name, resolver);
		}

		protected String literal(Expression expression, LiteralType literalType) {
			return queryContext.literal(expression, literalType);
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
		public void visit(ResultVariable expression) {

			String name = literal(expression, RESULT_VARIABLE);

			if (ExpressionTools.stringIsNotEmpty(name)) {
				Resolver resolver = queryContext.getResolver(expression.getSelectExpression());
				mappings.put(name, buildMapping(name, resolver));
			}
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
			expression.getSelectClause().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {

			if (!expression.startsWithDot()) {
				String name = literal(expression, PATH_EXPRESSION_LAST_PATH);

				if (ExpressionTools.stringIsNotEmpty(name)) {
					Resolver resolver = queryContext.getResolver(expression);
					mappings.put(name, buildMapping(name, resolver));
				}
			}
		}
	}
}