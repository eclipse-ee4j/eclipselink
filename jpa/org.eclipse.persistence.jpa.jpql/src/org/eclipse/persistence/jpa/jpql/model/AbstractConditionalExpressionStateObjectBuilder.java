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
package org.eclipse.persistence.jpa.jpql.model;

import java.util.Collections;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.model.query.AllOrAnyExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AndExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.BetweenExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CollectionMemberExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ComparisonExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ConcatExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EmptyCollectionComparisonExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ExistsExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.InExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.KeywordExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LikeExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LowerExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NullComparisonExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.OrExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleSelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubstringExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.TrimExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpperExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression.Specification;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The abstract implementation of {@link IConditionalStateObjectBuilder} that supports the creation
 * of the conditional expression based on the JPQL grammar defined in JPA 2.0.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("unchecked")
public abstract class AbstractConditionalExpressionStateObjectBuilder<T extends IAbstractConditionalExpressionStateObjectBuilder<T>>
	extends AbstractScalarExpressionStateObjectBuilder<T>
   implements IAbstractConditionalExpressionStateObjectBuilder<T> {

	/**
	 * Creates a new <code>AbstractStateObjectBuilder</code>.
	 *
	 * @param parent The parent of the expression to build, which is only required when a JPQL
	 * fragment needs to be parsed
	 */
	public AbstractConditionalExpressionStateObjectBuilder(StateObject parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public T all(SimpleSelectStatementStateObject subquery) {
		allOrAny(ALL, subquery);
		return (T) this;
	}

	protected void allOrAny(String identifier, SimpleSelectStatementStateObject subquery) {
		StateObject stateObject = new AllOrAnyExpressionStateObject(getParent(), identifier, subquery);
		add(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public T and(T builder) {

		checkBuilder(builder);

		StateObject rightStateObject = pop();
		StateObject leftStateObject  = pop();

		StateObject stateObject = new AndExpressionStateObject(
			getParent(),
			leftStateObject,
			rightStateObject
		);

		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T any(SimpleSelectStatementStateObject subquery) {
		allOrAny(ANY, subquery);
		return (T) this;
	}

	protected void between(boolean not) {

		StateObject upperBoundStateObject  = pop();
		StateObject lowerBoundStateObject  = pop();
		StateObject firstStateObject       = pop();

		StateObject stateObject = new BetweenExpressionStateObject(
			getParent(),
			firstStateObject,
			not,
			lowerBoundStateObject,
			upperBoundStateObject
		);

		add(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public T between(T lowerBoundExpression, T upperBoundExpression) {
		checkBuilders(lowerBoundExpression, upperBoundExpression);
		between(false);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T collectionPath(String path) {
		StateObject stateObject = buildCollectionPath(path);
		add(stateObject);
		return (T) this;
	}

	protected void comparison(String identifier) {
		comparison(identifier, pop());
	}

	protected void comparison(String identifier, StateObject rightStateObject) {

		StateObject leftStateObject = pop();

		StateObject stateObject = new ComparisonExpressionStateObject(
			getParent(),
			leftStateObject,
			identifier,
			rightStateObject
		);

		add(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public T concat(T parameter1, T parameter2) {
		checkBuilders(parameter1, parameter2);
		StateObject stateObject = new ConcatExpressionStateObject(getParent(), stateObjects(2));
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T different(Number number) {
		comparison(DIFFERENT, buildNumeric(number));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T different(String literal) {
		comparison(DIFFERENT, literal(literal));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T different(T builder) {
		checkBuilder(builder);
		comparison(DIFFERENT);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T equal(Number number) {
		comparison(EQUAL, buildNumeric(number));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T equal(String literal) {
		comparison(EQUAL, literal(literal));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T equal(T builder) {
		checkBuilder(builder);
		comparison(EQUAL);
		return (T) this;
	}

	protected void exists(boolean not, SimpleSelectStatementStateObject subquery) {
		StateObject stateObject = new ExistsExpressionStateObject(getParent(), not, subquery);
		add(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public T exists(SimpleSelectStatementStateObject subquery) {
		exists(false, subquery);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T FALSE() {
		keyword(FALSE);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T greaterThan(Number number) {
		comparison(GREATER_THAN, buildNumeric(number));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T greaterThan(String literal) {
		comparison(GREATER_THAN, literal(literal));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T greaterThan(T builder) {
		checkBuilder(builder);
		comparison(GREATER_THAN);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T greaterThanOrEqual(Number number) {
		comparison(GREATER_THAN_OR_EQUAL, buildNumeric(number));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T greaterThanOrEqual(String literal) {
		comparison(GREATER_THAN_OR_EQUAL, literal(literal));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T greaterThanOrEqual(T builder) {
		checkBuilder(builder);
		comparison(GREATER_THAN_OR_EQUAL);
		return (T) this;
	}

	protected void in(boolean not, List<StateObject> inItems) {

		StateObject stateFieldPath = pop();

		StateObject stateObject = new InExpressionStateObject(
			getParent(),
			stateFieldPath,
			not,
			inItems
		);

		add(stateObject);
	}

	protected void in(boolean not, String... inItems) {
		in(not, literals(inItems));
	}

	protected void in(boolean not, T... inItems) {
		in(false, stateObjects(inItems));
	}

	/**
	 * {@inheritDoc}
	 */
	public T in(SimpleSelectStatementStateObject subquery) {
		in(false, Collections.<StateObject>singletonList(subquery));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T in(String... inItems) {
		in(false, inItems);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T in(T... inItems) {
		checkBuilders(inItems);
		in(false, inItems);
		return (T) this;
	}

	protected void isEmpty(boolean not, String path) {

		StateObject stateObject = new EmptyCollectionComparisonExpressionStateObject(
			getParent(),
			not,
			path
		);

		add(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public T isEmpty(String path) {
		isEmpty(false, path);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T isNotEmpty(String path) {
		isEmpty(true, path);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T isNotNull(String path) {
		isNull(true, path);
		return (T) this;
	}

	protected void isNull(boolean not, String path) {

		StateObject stateObject;

		if (ExpressionTools.isParameter(path.charAt(0))) {
			stateObject = buildInputParameter(path);
		}
		else {
			stateObject = buildStateFieldPath(path);
		}

		stateObject = new NullComparisonExpressionStateObject(
			getParent(),
			not,
			stateObject
		);

		add(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public T isNull(String path) {
		isNull(false, path);
		return (T) this;
	}

	protected void keyword(String identifier) {
		StateObject stateObject = new KeywordExpressionStateObject(getParent(), identifier);
		add(stateObject);
	}

	protected void like(boolean not, String escapeCharacter) {

		StateObject patternValue = pop();
		StateObject string       = pop();

		StateObject stateObject = new LikeExpressionStateObject(
			getParent(),
			string,
			not,
			patternValue,
			escapeCharacter
		);

		add(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public T like(String patternValue) {
		like(string(patternValue));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T like(T patternValue) {
		checkBuilder(patternValue);
		like(false, null);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T like(T patternValue, String escapeCharacter) {
		checkBuilder(patternValue);
		like(false, escapeCharacter);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T lower(T builder) {
		checkBuilder(builder);
		StateObject stateObject = new LowerExpressionStateObject(getParent(), pop());
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T lowerThan(Number number) {
		comparison(LOWER_THAN, buildNumeric(number));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T lowerThan(String literal) {
		comparison(LOWER_THAN, literal(literal));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T lowerThan(T builder) {
		checkBuilder(builder);
		comparison(LOWER_THAN);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T lowerThanOrEqual(Number number) {
		comparison(LOWER_THAN_OR_EQUAL, buildNumeric(number));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T lowerThanOrEqual(String literal) {
		comparison(LOWER_THAN_OR_EQUAL, literal(literal));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T lowerThanOrEqual(T builder) {
		checkBuilder(builder);
		comparison(LOWER_THAN_OR_EQUAL);
		return (T) this;
	}

	protected void member(boolean not, boolean of, String collectionValuedPathExpression) {

		StateObject entity = pop();

		StateObject stateObject = new CollectionMemberExpressionStateObject(
			getParent(),
			entity,
			not,
			of,
			collectionValuedPathExpression
		);

		add(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public T member(String path) {
		member(false, false, path);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T memberOf(String path) {
		member(false, true, path);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T notBetween(T lowerBoundExpression, T upperBoundExpression) {
		checkBuilders(lowerBoundExpression, upperBoundExpression);
		between(true);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T notExists(SimpleSelectStatementStateObject subquery) {
		exists(true, subquery);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T notIn(SimpleSelectStatementStateObject subquery) {
		in(true, Collections.<StateObject>singletonList(subquery));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T notIn(String... inItems) {
		in(true, inItems);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T notIn(T... inItems) {
		checkBuilders(inItems);
		in(true, inItems);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T notLike(String patternValue) {
		notLike(string(patternValue));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T notLike(T builder) {
		checkBuilder(builder);
		like(true, null);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T notLike(T builder, String escapeCharacter) {
		checkBuilder(builder);
		like(true, escapeCharacter);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T notMember(String path) {
		member(true, false, path);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T notMemberOf(String path) {
		member(true, true, path);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T NULL() {
		keyword(NULL);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T or(T builder) {

		checkBuilder(builder);

		StateObject rightStateObject = pop();
		StateObject leftStateObject  = pop();

		StateObject stateObject = new OrExpressionStateObject(
			getParent(),
			leftStateObject,
			rightStateObject
		);

		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T some(SimpleSelectStatementStateObject subquery) {
		allOrAny(SOME, subquery);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T sub(StateObject stateObject) {
		stateObject = new SubExpressionStateObject(getParent(), stateObject);
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T substring(T parameter1, T parameter2, T parameter3) {

		checkBuilders(parameter1, parameter2, parameter3);

		StateObject thirdStateObject  = pop();
		StateObject secondStateObject = pop();
		StateObject firstStateObject  = pop();

		StateObject stateObject = new SubstringExpressionStateObject(
			getParent(),
			firstStateObject,
			secondStateObject,
			thirdStateObject
		);

		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T trim(Specification specification, String trimCharacter, T builder) {

		checkBuilder(builder);
		StateObject stateObject = pop();

		stateObject = new TrimExpressionStateObject(
			getParent(),
			specification,
			ExpressionTools.stringIsNotEmpty(trimCharacter) ? literal(trimCharacter) : null,
			stateObject
		);

		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T trim(Specification specification, T builder) {
		return trim(specification, null, builder);
	}

	/**
	 * {@inheritDoc}
	 */
	public T TRUE() {
		keyword(TRUE);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T upper(T builder) {
		checkBuilder(builder);
		StateObject stateObject = new UpperExpressionStateObject(getParent(), pop());
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T variable(String variable) {
		StateObject stateObject = buildIdentificationVariable(variable);
		add(stateObject);
		return (T) this;
	}
}