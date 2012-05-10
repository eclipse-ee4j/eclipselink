/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.model.query.AbsExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AdditionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ArithmeticFactorStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AvgFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CoalesceExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CollectionValuedPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ConcatExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CountFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DateTimeStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DivisionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EntityTypeLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EnumTypeStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.FunctionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IdentificationVariableStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IndexExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.InputParameterStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LengthExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LocateExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.MaxFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ModExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.MultiplicationExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NullIfExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NumericLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SizeExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SqrtExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateFieldPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StringLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubtractionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SumFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.TypeExpressionStateObject;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This abstract definition of a builder provides the support for creating expressions defined by a
 * <code>scalar expression</code>.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"unchecked", "nls"})
public abstract class AbstractScalarExpressionStateObjectBuilder<T extends IScalarExpressionStateObjectBuilder<T>> extends AbstractStateObjectBuilder
	implements IScalarExpressionStateObjectBuilder<T> {

	/**
	 * Caches the {@link ICaseExpressionStateObjectBuilder} while it's been used.
	 */
	private ICaseExpressionStateObjectBuilder caseBuilder;

	/**
	 * The parent of the expression to build, which is only required when a JPQL fragment needs to
	 * be parsed.
	 */
	private StateObject parent;

	/**
	 * Creates a new <code>AbstractScalarExpressionStateObjectBuilder</code>.
	 *
	 * @param parent The parent of the expression to build, which is only required when a JPQL
	 * fragment needs to be parsed
	 */
	protected AbstractScalarExpressionStateObjectBuilder(StateObject parent) {
		super();
		this.parent = parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public T abs(T builder) {

		checkBuilder(builder);

		StateObject stateObject = new AbsExpressionStateObject(parent, pop());
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T add(T builder) {

		checkBuilder(builder);

		StateObject rightStateObject = pop();
		StateObject leftStateObject  = pop();

		StateObject stateObject = new AdditionExpressionStateObject(
			parent,
			leftStateObject,
			rightStateObject
		);

		add(stateObject);
		return (T) this;
	}

	protected void arithmetic(boolean plusSign) {
		StateObject stateObject = new ArithmeticFactorStateObject(parent, plusSign, pop());
		add(stateObject);
	}

	protected void avg(boolean distinct, String path) {
		StateObject stateObject = new AvgFunctionStateObject(parent, distinct, literal(path));
		add(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public T avg(String path) {
		avg(false, path);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T avgDistinct(String path) {
		avg(true, path);
		return (T) this;
	}

	protected StateObject buildCollectionPath(String path) {
		return new CollectionValuedPathExpressionStateObject(parent, path);
	}

	protected StateObject buildIdentificationVariable(String literal) {
		return new IdentificationVariableStateObject(parent, literal);
	}

	protected StateObject buildInputParameter(String parameter) {
		return new InputParameterStateObject(parent, parameter);
	}

	protected StateObject buildNumeric(Number number) {
		return new NumericLiteralStateObject(parent, number);
	}

	protected StateObject buildNumeric(String number) {
		return new NumericLiteralStateObject(parent, number);
	}

	protected StateObject buildStateFieldPath(String path) {
		return new StateFieldPathExpressionStateObject(parent, path);
	}

	protected StateObject buildStringLiteral(String literal) {
		return new StringLiteralStateObject(parent, literal);
	}

	/**
	 * {@inheritDoc}
	 */
	public T case_(ICaseExpressionStateObjectBuilder builder) {
		Assert.isEqual(caseBuilder, builder, "The Case expression builder is not the same as the current one");
		add(builder.buildStateObject());
		builder = null;
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T coalesce(T builder1, T builder2, T... builders) {

		checkBuilders(builder1, builder2);
		checkBuilders(builders);

		List<StateObject> stateObjects = new ArrayList<StateObject>();
		stateObjects.addAll(stateObjects(builders));
		stateObjects.add(0, pop());
		stateObjects.add(0, pop());

		StateObject stateObject = new CoalesceExpressionStateObject(parent, stateObjects);
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T concat(T builder1, T builder2, T... builders) {

		checkBuilders(builder1, builder2);
		checkBuilders(builders);

		List<StateObject> stateObjects = new ArrayList<StateObject>();
		stateObjects.addAll(stateObjects(builders));
		stateObjects.add(0, pop());
		stateObjects.add(0, pop());

		StateObject stateObject = new ConcatExpressionStateObject(parent, stateObjects);
		add(stateObject);
		return (T) this;
	}

	protected void count(boolean distinct, String path) {
		StateObject stateObject = new CountFunctionStateObject(parent, distinct, literal(path));
		add(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public T count(String path) {
		count(false, path);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T countDistinct(String path) {
		count(true, path);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T currentDate() {
		return date(CURRENT_DATE);
	}

	/**
	 * {@inheritDoc}
	 */
	public T currentTime() {
		return date(CURRENT_TIME);
	}

	/**
	 * {@inheritDoc}
	 */
	public T currentTimestamp() {
		return date(CURRENT_TIMESTAMP);
	}

	/**
	 * {@inheritDoc}
	 */
	public T date(String jdbcDate) {
		StateObject stateObject = new DateTimeStateObject(parent, jdbcDate);
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T divide(T builder) {

		checkBuilder(builder);

		StateObject rightStateObject = pop();
		StateObject leftStateObject  = pop();

		StateObject stateObject = new DivisionExpressionStateObject(
			parent,
			leftStateObject,
			rightStateObject
		);

		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T entityType(String entityTypeName) {
		StateObject stateObject = new EntityTypeLiteralStateObject(parent,entityTypeName);
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T enumLiteral(Enum<? extends Enum<?>> enumConstant) {
		StateObject stateObject = new EnumTypeStateObject(parent, enumConstant);
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T function(String identifier, String functionName, String... arguments) {

		StateObject stateObject = new FunctionExpressionStateObject(
			getParent(),
			identifier,
			functionName,
			literals(arguments)
		);

		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T function(String identifier, String functionName, T... arguments) {

		checkBuilders(arguments);

		StateObject stateObject = new FunctionExpressionStateObject(
			getParent(),
			identifier,
			functionName,
			stateObjects(arguments)
		);

		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public ICaseExpressionStateObjectBuilder getCaseBuilder() {
		if (caseBuilder == null) {
			caseBuilder = getParent().getQueryBuilder().buildCaseExpressionStateObjectBuilder(parent);
		}
		return caseBuilder;
	}

	/**
	 * Returns the parent of the expression to build, which is only required when a JPQL fragment
	 * needs to be parsed.
	 *
	 * @return The parent
	 */
	protected StateObject getParent() {
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public T index(String variable) {
		StateObject stateObject = new IndexExpressionStateObject(parent, variable);
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T length(T builder) {

		checkBuilder(builder);

		StateObject stateObject = new LengthExpressionStateObject(parent, pop());
		add(stateObject);
		return (T) this;
	}

	protected StateObject literal(String literal) {

		if ((literal != null) && (literal.length() > 0)) {

			char character = literal.charAt(0);

			// String literal
			if (ExpressionTools.isQuote(character)) {
				return buildStringLiteral(literal);
			}

			// Input parameter
			if (ExpressionTools.isParameter(character)) {
				return buildInputParameter(literal);
			}

			// State-field path expression
			if (literal.indexOf('.') > 0) {
				return buildStateFieldPath(literal);
			}

			// Identification variable
			return buildIdentificationVariable(literal);
		}

		// String literal
		return buildStringLiteral(literal);
	}

	protected List<StateObject> literals(String... literals) {
		List<StateObject> stateObjects = new ArrayList<StateObject>();
		for (String literal : literals) {
			stateObjects.add(literal(literal));
		}
		return stateObjects;
	}

	/**
	 * {@inheritDoc}
	 */
	public T locate(T parameter1, T parameter2) {
		return locate(parameter1, parameter2, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public T locate(T parameter1, T parameter2, T parameter3) {

		checkBuilders(parameter1, parameter2);

		if (parameter3 != null) {
			checkBuilder(parameter3);
		}

		StateObject thirdStateObject  = (parameter3 != null) ? pop() : null;
		StateObject secondStateObject = pop();
		StateObject firstStateObject  = pop();

		StateObject stateObject = new LocateExpressionStateObject(
			parent,
			firstStateObject,
			secondStateObject,
			thirdStateObject
		);

		add(stateObject);
		return (T) this;
	}

	protected void max(boolean distinct, String path) {
		StateObject stateObject = new MaxFunctionStateObject(parent, distinct, literal(path));
		add(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public T max(String path) {
		max(false, path);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T maxDistinct(String path) {
		max(true, path);
		return (T) this;
	}

	protected void min(boolean distinct, String path) {
		StateObject stateObject = new MaxFunctionStateObject(parent, distinct, literal(path));
		add(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public T min(String path) {
		min(false, path);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T minDistinct(String path) {
		min(true, path);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T minus(T builder) {
		checkBuilders(builder);
		arithmetic(false);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T mod(T parameter1, T parameter2) {

		checkBuilders(parameter1, parameter2);

		StateObject secondStateObject = pop();
		StateObject firstStateObject  = pop();

		StateObject stateObject = new ModExpressionStateObject(
			parent,
			firstStateObject,
			secondStateObject
		);

		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T multiply(T builder) {

		checkBuilders(builder);

		StateObject rightStateObject = pop();
		StateObject leftStateObject  = pop();

		StateObject stateObject = new MultiplicationExpressionStateObject(
			parent,
			leftStateObject,
			rightStateObject
		);

		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T nullIf(T builder1, T builder2) {

		checkBuilders(builder1, builder2);

		StateObject rightStateObject = pop();
		StateObject leftStateObject  = pop();

		StateObject stateObject = new NullIfExpressionStateObject(
			parent,
			leftStateObject,
			rightStateObject
		);

		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T numeric(Number number) {
		StateObject stateObject = buildNumeric(number);
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T numeric(String number) {
		StateObject stateObject = buildNumeric(number);
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T parameter(String parameter) {
		StateObject stateObject = buildInputParameter(parameter);
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T path(String path) {
		StateObject stateObject = buildStateFieldPath(path);
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T plus(T builder) {
		checkBuilders(builder);
		arithmetic(true);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T size(String path) {
		StateObject stateObject = new SizeExpressionStateObject(parent, buildCollectionPath(path));
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T sqrt(T builder) {

		checkBuilders(builder);

		StateObject stateObject = new SqrtExpressionStateObject(parent, pop());
		add(stateObject);
		return (T) this;
	}

	/**
	 * Returns a list of the {@link StateObject StateObjects} that were previously created.
	 *
	 * @param count The number of {@link StateObject StateObjects} to move to the list
	 * @return The list of {@link StateObject StateObjects} that were added to the stack
	 */
	protected List<StateObject> stateObjects(int count) {

		if (count == 0) {
			return Collections.emptyList();
		}

		List<StateObject> items = new ArrayList<StateObject>(count);

		while (count-- > 0) {
			items.add(0, pop());
		}

		return items;
	}

	/**
	 * Returns a list of the {@link StateObject StateObjects} that were previously created.
	 *
	 * @param builders The list of {@link IConditionalStateObjectBuilder builders} is used to
	 * determine how many {@link StateObject StateObjects} needs to be pulled out of the stack
	 * @return The list of {@link StateObject StateObjects} that were added to the stack
	 */
	protected List<StateObject> stateObjects(T... builders) {
		return stateObjects(builders.length);
	}

	/**
	 * {@inheritDoc}
	 */
	public T string(String literal) {
		StateObject stateObject = buildStringLiteral(literal);
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T sub(T builder) {

		checkBuilders(builder);

		StateObject stateObject = new SubExpressionStateObject(parent, pop());
		add(stateObject);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T subtract(T builder) {

		checkBuilders(builder);

		StateObject rightStateObject = pop();
		StateObject leftStateObject  = pop();

		StateObject stateObject = new SubtractionExpressionStateObject(
			parent,
			leftStateObject,
			rightStateObject
		);

		add(stateObject);
		return (T) this;
	}

	protected void sum(boolean distinct, String path) {
		StateObject stateObject = new SumFunctionStateObject(parent, distinct, literal(path));
		add(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public T sum(String path) {
		sum(false, path);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T sumDistinct(String path) {
		sum(true, path);
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	public T type(String path) {
		StateObject stateObject = new TypeExpressionStateObject(parent, path);
		add(stateObject);
		return (T) this;
	}
}