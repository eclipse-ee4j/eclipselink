/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.jpql;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.jpql.parser.AbstractEclipseLinkExpressionVisitor;
import org.eclipse.persistence.jpa.jpql.parser.AbstractPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.StateFieldPathExpression;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * This object scans a {@link DatabaseQuery} and checks if it's a constructor query. If it is one,
 * then the fully qualified class name and the persistent fields will be available.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public final class ConstructorQueryMappings {

	/**
	 * The fully qualified class name defined in the constructor expression if one is defined,
	 * otherwise <code>null</code>.
	 */
	private String className;

	/**
	 * The list of {@link DatabaseMapping} representing the parameter arguments if the JPQL query
	 * does contain a constructor expression.
	 */
	private List<DatabaseMapping> mappings;

	/**
	 * The {@link DatabaseQuery} for which its JPQL query was scanned to check if it's constructor
	 * query and if so, to gather the information.
	 */
	private DatabaseQuery query;

	/**
	 * Creates a new <code>ConstructorQueryMappings</code>.
	 *
	 * @param query The {@link DatabaseQuery} for which its JPQL query was scanned to check if it's
	 * constructor query and if so, to gather the information.
	 */
	ConstructorQueryMappings(DatabaseQuery query) {
		super();
		this.query = query;
		this.mappings = new LinkedList<DatabaseMapping>();
	}

	/**
	 * Returns the class name used to define the constructor expression.
	 *
	 * @return Either the fully qualified class name used in the constructor expression or
	 * <code>null</code> if the query is not a constructor query
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Returns the {@link DatabaseQuery} for which its JPQL query was scanned to check if it's
	 * constructor query and if so, to gather the information.
	 *
	 * @return The {@link DatabaseQuery} that was scanned
	 */
	public DatabaseQuery getQuery() {
		return query;
	}

	/**
	 * Determines whether the JPQL query is a constructor query, i.e. the select expression is a
	 * constructor expression.
	 * <p>
	 * Example: <code>SELECT new test.example.Employee(e.name, e.id) FROM Employee e</code>
	 *
	 * @return <code>true</code> if the <code><b>SELECT</b></code> clause has a single select item
	 * and it's a constructor expression; <code>false</code> otherwise
	 */
	public boolean isConstructorQuery() {
		return className != null;
	}

	/**
	 * Returns a non-<code>null</code> {@link Iterable} over the ordered list of {@link DatabaseMapping}
	 * objects that represents the parameter arguments defined in the constructor expression.
	 *
	 * @return The list contains the persistent fields that were passed to the constructor
	 */
	public Iterable<DatabaseMapping> mappings() {

		if (mappings.isEmpty()) {
			return Collections.emptyList();
		}

		return new LinkedList<DatabaseMapping>(mappings);
	}

	/**
	 * Populates this object by scanning the JPQL query using the given JPQL grammar.
	 *
	 * @param jpqlGrammar The {@link JPQLGrammar} how the JPQL query is parsed
	 */
	void populate(JPQLGrammar jpqlGrammar) {

		JPQLQueryContext queryContext = new JPQLQueryContext(query, jpqlGrammar);

		ConstructorVisitor visitor = new ConstructorVisitor(queryContext);
		queryContext.getJPQLExpression().accept(visitor);
	}

	/**
	 * This visitor visits the constructor items and adds the attribute type mapped to the name.
	 * <p/>
	 * Example:<br/>
	 * e.name -> "name" : String<br/>
	 * e.address.zipcode -> "zipcode" : int
	 */
	private class ConstructorItemVisitor extends AbstractEclipseLinkExpressionVisitor {

		private final JPQLQueryContext queryContext;

		ConstructorItemVisitor(JPQLQueryContext queryContext) {
			super();
			this.queryContext = queryContext;
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
		public void visit(CollectionValuedPathExpression expression) {
			visitPathExpression(expression);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(StateFieldPathExpression expression) {
			visitPathExpression(expression);
		}

		private void visitPathExpression(AbstractPathExpression expression) {

			DatabaseMapping mapping = queryContext.resolveMapping(expression);

			if (mapping != null) {
				mappings.add(mapping);
			}
		}
	}

	/**
	 * Visits the JPQL query and gather the information contained in the constructor expressions.
	 */
	private class ConstructorVisitor extends AbstractEclipseLinkExpressionVisitor {

		private final JPQLQueryContext queryContext;

		ConstructorVisitor(JPQLQueryContext queryContext) {
			super();
			this.queryContext = queryContext;
		}

		private DynamicClassLoader classLoader() {
			return (DynamicClassLoader) queryContext.getSession().getProperty(PersistenceUnitProperties.CLASSLOADER);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(ConstructorExpression expression) {

			ConstructorQueryMappings.this.className = expression.getClassName();

			// Visit the constructor items and retrieve the persistent fields
			ConstructorItemVisitor visitor = new ConstructorItemVisitor(queryContext);
			expression.getConstructorItems().accept(visitor);
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
		public void visit(SelectClause expression) {
			expression.getSelectExpression().accept(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visit(SelectStatement expression) {
			expression.getSelectClause().accept(this);
		}
	}
}