/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tests;

import org.eclipse.persistence.jpa.jpql.ExpressionTools;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeVisitor;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;

/**
 * The concrete implementation of {@link IEntity} that is wrapping the runtime representation of a
 * JPA entity.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class JavaEntity extends JavaManagedType
                       implements IEntity {

	private String name;
	private Map<String, IQuery> queries;

	/**
	 * Creates a new <code>JavaEntity</code>.
	 *
	 * @param provider The provider of JPA managed types
	 * @param type The {@link IType} wrapping the Java type
	 */
	JavaEntity(IManagedTypeProvider provider, JavaType type) {
		super(provider, type);
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(IManagedTypeVisitor visitor) {
		visitor.visit(this);
	}

	private void addQuery(Map<String, IQuery> queries, NamedQuery namedQuery) {

		try {
			Method nameMethod  = namedQuery.getClass().getMethod("name");
			Method queryMethod = namedQuery.getClass().getMethod("query");

			String name = (String) nameMethod.invoke(namedQuery);
			String query = (String) queryMethod.invoke(namedQuery);

			queries.put(name, new JavaQuery(getProvider(), query));
		}
		catch (Exception e) {
			// Ignore
		}
	}

	private String buildName() {

		Class<?> type = getType().getType();
		Entity entity = type.getAnnotation(Entity.class);
		String name = null;

		try {
			Method nameMethod = entity.getClass().getMethod("name");
			name = (String) nameMethod.invoke(entity);
		}
		catch (Exception e) {
			// Ignore
		}
		finally {
			if (ExpressionTools.stringIsEmpty(name)) {
				name = type.getSimpleName();
			}
		}

		return name;
	}

	private Map<String, IQuery> buildQueries() {

		Map<String, IQuery> queries = new HashMap<String, IQuery>();

		try {
			Class<?> type = getType().getType();

			if (type.isAnnotationPresent(NamedQueries.class)) {
				NamedQueries namedQueries = type.getAnnotation(NamedQueries.class);
				Method valueMethod = namedQueries.getClass().getMethod("value");
				NamedQuery[] namedQueryList = (NamedQuery[]) valueMethod.invoke(namedQueries);

				for (NamedQuery query : namedQueryList) {
					addQuery(queries, query);
				}
			}
			else {
				NamedQuery namedQuery = type.getAnnotation(NamedQuery.class);
				addQuery(queries, namedQuery);
			}
		}
		catch (Exception e) {
			// Ignore
		}

		return queries;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		if (name == null) {
			name = buildName();
		}
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	public IQuery getNamedQuery(String queryName) {
		initializeQueries();
		return queries.get(queryName);
	}

	private void initializeQueries() {
		if (queries == null) {
			queries = buildQueries();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getName();
	}
}