/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

import org.eclipse.persistence.utils.jpa.query.parser.DeleteStatement;
import org.eclipse.persistence.utils.jpa.query.parser.JPQLExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SelectClause;
import org.eclipse.persistence.utils.jpa.query.parser.SelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.SubstringExpression;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateStatement;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;

/**
 * Calculates the type of the query result of the JPQL query.
 * <p>
 * The type of the query result specified by the <b>SELECT</b> clause of a query
 * is an entity abstract schema type, a state field type, the result of a scalar
 * expression, the result of an aggregate function, the result of a construction
 * operation, or some sequence of these.
 * <p>
 * The result type of the <b>SELECT</b> clause is defined by the the result
 * types of the select expressions contained in it. When multiple select
 * expressions are used in the SELECT clause, the elements in this result
 * correspond in order to the order of their specification in the <b>SELECT</b>
 * clause and in type to the result types of each of the select expressions.
 * <p>
 * <ul>
 * <li>The result type of an <code>identification_variable</code> is the type of
 * the entity object or embeddable object to which the identification variable
 * corresponds. The type of an <code>identification_variable</code> that refers
 * to an entity abstract schema type is the type of the entity to which that
 * identification variable corresponds or a subtype as determined by the
 * object/relational mapping.</li>
 *
 * <li>The result type of a <code>single_valued_path_expression</code> that is a
 * <code>state_field_path_expression</code> is the same type as the
 * corresponding state field of the entity or embeddable class. If the state
 * field of the entity is a primitive type, the result type is the corresponding
 * object type.</li>
 *
 * <li>The result type of a <code>single_valued_path_expression</code> that is a
 * <code>single_valued_object_path_expression</code> is the type of the entity
 * object or embeddable object to which the path expression corresponds. A
 * <code>single_valued_object_path_expression</code> that results in an entity
 * object will result in an entity of the type of the relationship field or the
 * subtype of the relationship field of the entity object as determined by the
 * object/relational mapping.</li>
 *
 * <li>The result type of a <code>single_valued_path_expression</code> that is
 * an identification_variable to which the <code>KEY</code> or
 * <code>VALUE</code> function has been applied is determined by the type of the
 * map key or value respectively, as defined by the above rules.</li>
 *
 * <li>The result type of a <code>single_valued_path_expression</code> that is
 * an identification_variable to which the <code>ENTRY</code> function has been
 * applied is {@link java.util.Map#Entry}, where the key and value types of the
 * map entry are determined by the above rules as applied to the map key and map
 * value respectively.</li>
 *
 * <li>The result type of a <code>scalar_expression</code> is the type of the
 * scalar value to which the expression evaluates.</li>
 * <li>The result type of an <code>entity_type_expression</code> scalar
 * expression is the Java class to which the resulting abstract schema type
 * corresponds.</li>
 *
 * <li>The result type of a <code>constructor_expression</code> is the type of
 * the class for which the constructor is defined. The types of the arguments to
 * the constructor are defined by the above rules.</li>
 * </ul>
 *
 * @return The result type of the JPQL query if it could accurately be
 * calculated or the {@link MWClass} for <code>Object</code> if it could not be
 * calculated
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class SelectQueryTypeVisitor extends TypeVisitor
{
	/**
	 * Creates a new <code>SelectQueryTypeVisitor</code>.
	 *
	 * @param query The model object representing the JPA named query
	 */
	SelectQueryTypeVisitor(IQuery query)
	{
		super(query);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression)
	{
		setResolver(buildClassTypeResolver(Object.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression)
	{
		expression.getQueryStatement().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression)
	{
		// visit(CollectionExpression) iterates through the children but for a
		// SELECT clause, a CollectionExpression means the result type is Object[]
		CollectionExpressionVisitor visitor = new CollectionExpressionVisitor();
		expression.getSelectExpression().accept(visitor);

		if (visitor.expression != null)
		{
			setResolver(buildClassTypeResolver(Object[].class));
		}
		else
		{
			expression.getSelectExpression().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression)
	{
		expression.getSelectClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateStatement expression)
	{
		setResolver(buildClassTypeResolver(Object.class));
	}
}