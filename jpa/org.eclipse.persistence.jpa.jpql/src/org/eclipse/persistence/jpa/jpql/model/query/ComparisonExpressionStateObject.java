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
package org.eclipse.persistence.jpa.jpql.model.query;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.ComparisonExpressionBNF;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * Only the values of like types are permitted to be compared. A type is like another type if they
 * correspond to the same Java language type, or if one is a primitive Java language type and the
 * other is the wrapped Java class type equivalent (e.g., int and Integer are like types in this
 * sense).
 * <p>
 * There is one exception to this rule: it is valid to compare numeric values for which the rules of
 * numeric promotion apply. Conditional expressions attempting to compare non-like type values are
 * disallowed except for this numeric case.
 * <p>
 * Note that the arithmetic operators and comparison operators are permitted to be applied to
 * state-fields and input parameters of the wrapped Java class equivalents to the primitive numeric
 * Java types. Two entities of the same abstract schema type are equal if and only if they have the
 * same primary key value. Only equality/inequality comparisons over enumeration constants are
 * required to be supported.
 * <p>
 * <b>JPA 1.0 - BNF:</b>
 * <pre><code>comparison_expression ::= string_expression comparison_operator {string_expression | all_or_any_expression} |
 *                          boolean_expression {=|<>} {boolean_expression | all_or_any_expression} |
 *                          enum_expression {=|<>} {enum_expression | all_or_any_expression} |
 *                          datetime_expression comparison_operator {datetime_expression | all_or_any_expression} |
 *                          entity_expression {=|<>} {entity_expression | all_or_any_expression} |
 *                          arithmetic_expression comparison_operator {arithmetic_expression | all_or_any_expression}</code></pre>
 *
 * <b>JPA 2.0 - BNF:</b>
 * <pre><code>comparison_expression ::= string_expression comparison_operator {string_expression | all_or_any_expression} |
 *                          boolean_expression {=|<>} {boolean_expression | all_or_any_expression} |
 *                          enum_expression {=|<>} {enum_expression | all_or_any_expression} |
 *                          datetime_expression comparison_operator {datetime_expression | all_or_any_expression} |
 *                          entity_expression {=|<>} {entity_expression | all_or_any_expression} |
 *                          arithmetic_expression comparison_operator {arithmetic_expression | all_or_any_expression} |
 *                          <b>entity_type_expression {=|<>} entity_type_expression}</b></code></pre>
 *
 * @see ComparisonExpression
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class ComparisonExpressionStateObject extends CompoundExpressionStateObject {

	/**
	 * The comparison identifier, which is either <, <=, =, >=, <>.
	 */
	private String identifier;

	/**
	 * Notifies the identifier property has changed.
	 */
	public static final String IDENTIFIER_PROPERTY = "identifier";

	/**
	 * Creates a new <code>ComparisonExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param leftStateObject The {@link StateObject} representing the left expression
	 * @param identifier The comparison identifier, either <, <=, =, >=, <>
	 * @param rightStateObject The {@link StateObject} representing the right expression
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ComparisonExpressionStateObject(StateObject parent,
	                                       StateObject leftStateObject,
	                                       String identifier,
	                                       StateObject rightStateObject) {

		super(parent, leftStateObject, rightStateObject);
		validateIdentifier(identifier);
		this.identifier = identifier;
	}

	/**
	 * Creates a new <code>ComparisonExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param identifier The comparison identifier, either <, <=, =, >=, <>
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ComparisonExpressionStateObject(StateObject parent, String identifier) {
		super(parent);
		validateIdentifier(identifier);
		this.identifier = identifier;
	}

	/**
	 * Creates a new <code>ComparisonExpressionStateObject</code>.
	 *
	 * @param parent The parent of this state object, which cannot be <code>null</code>
	 * @param leftJpqlFragment The string representation of the left expression to parse and to
	 * convert into a {@link StateObject}
	 * @param identifier The comparison identifier, either <, <=, =, >=, <>
	 * @param rightJpqlFragment The string representation of the right expression to parse and to
	 * convert into a {@link StateObject}
	 * @exception NullPointerException The given parent cannot be <code>null</code>
	 */
	public ComparisonExpressionStateObject(StateObject parent,
	                                       String leftJpqlFragment,
	                                       String identifier,
	                                       String rightJpqlFragment) {

		super(parent, leftJpqlFragment, rightJpqlFragment);
		validateIdentifier(identifier);
		this.identifier = identifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public void accept(StateObjectVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComparisonExpression getExpression() {
		return (ComparisonExpression) super.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getLeftQueryBNFId() {
		return ComparisonExpressionBNF.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getRightQueryBNFId() {
		return ComparisonExpressionBNF.ID;
	}

	/**
	 * Keeps a reference of the {@link ComparisonExpression parsed object} object, which should only
	 * be done when this object is instantiated during the conversion of a parsed JPQL query into
	 * {@link StateObject StateObjects}.
	 *
	 * @param expression The {@link ComparisonExpression parsed object} representing a comparison
	 * expression
	 */
	public void setExpression(ComparisonExpression expression) {
		super.setExpression(expression);
	}

	/**
	 * Sets the comparison identifier to one of the following: <, <=, =, >=, <>.
	 *
	 * @param identifier The new comparison identifier, either <, <=, =, >=, <>
	 */
	public void setIdentifier(String identifier) {
		validateIdentifier(identifier);
		String oldIdentifier = this.identifier;
		this.identifier = identifier;
		firePropertyChanged(IDENTIFIER_PROPERTY, oldIdentifier, identifier);
	}

	protected void validateIdentifier(String identifier) {
		Assert.isValid(
			identifier,
			"The comparison identifier must be either <, <=, =, >=, <>.",
			LOWER_THAN, LOWER_THAN_OR_EQUAL, EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, DIFFERENT
		);
	}
}