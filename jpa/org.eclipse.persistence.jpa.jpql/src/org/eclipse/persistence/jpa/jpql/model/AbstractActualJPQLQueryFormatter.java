/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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

import java.util.ListIterator;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.model.query.AbsExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractDoubleEncapsulatedExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractFromClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractIdentificationVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractModifyStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractRangeVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractSchemaNameStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractSelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractSingleEncapsulatedExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractTripleEncapsulatedExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AdditionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AggregateFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AllOrAnyExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AndExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ArithmeticFactorStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AvgFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.BadExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.BetweenExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CaseExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CoalesceExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CollectionMemberDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CollectionMemberExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CollectionValuedPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ComparisonExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CompoundExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ConcatExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ConstructorExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.CountFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DateTimeStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DeleteClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DeleteStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DerivedPathIdentificationVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DerivedPathVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.DivisionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EmptyCollectionComparisonExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EncapsulatedIdentificationVariableExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EntityTypeLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EntryExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.EnumTypeStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ExistsExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.FromClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.FunctionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.GroupByClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.HavingClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IdentificationVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IdentificationVariableStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.InExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IndexExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.InputParameterStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JoinStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.KeyExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.KeywordExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LengthExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LikeExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ListHolderStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LocateExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.LowerExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.MaxFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.MinFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ModExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.MultiplicationExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NotExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NullComparisonExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NullIfExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.NumericLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ObjectExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.OrExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.OrderByClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.OrderByItemStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.RangeVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ResultVariableStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleFromClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleSelectClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleSelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SimpleStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SizeExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SqrtExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateFieldPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StringLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubstringExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubtractionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SumFunctionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.TreatExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.TrimExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.TypeExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UnknownExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpdateClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpdateItemStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpdateStatementStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.UpperExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.ValueExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.WhenClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.WhereClauseStateObject;
import org.eclipse.persistence.jpa.jpql.parser.AbstractDoubleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractFromClause;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSingleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractTripleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AggregateFunction;
import org.eclipse.persistence.jpa.jpql.parser.BetweenExpression;
import org.eclipse.persistence.jpa.jpql.parser.CaseExpression;
import org.eclipse.persistence.jpa.jpql.parser.CoalesceExpression;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.CollectionMemberExpression;
import org.eclipse.persistence.jpa.jpql.parser.CompoundExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConcatExpression;
import org.eclipse.persistence.jpa.jpql.parser.ConstructorExpression;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.eclipse.persistence.jpa.jpql.parser.DeleteClause;
import org.eclipse.persistence.jpa.jpql.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.EncapsulatedIdentificationVariableExpression;
import org.eclipse.persistence.jpa.jpql.parser.ExistsExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.FunctionExpression;
import org.eclipse.persistence.jpa.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.jpql.parser.HavingClause;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.KeywordExpression;
import org.eclipse.persistence.jpa.jpql.parser.LikeExpression;
import org.eclipse.persistence.jpa.jpql.parser.NotExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;
import org.eclipse.persistence.jpa.jpql.parser.TrimExpression;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.jpql.parser.WhenClause;
import org.eclipse.persistence.jpa.jpql.parser.WhereClause;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This {@link IJPQLQueryFormatter} is used to generate a string representation of a {@link
 * StateObject} based on how it was parsed, which means this formatter can only be used when the
 * {@link StateObject} was created by parsing a JPQL query because it needs to retrieve parsing
 * information from the corresponding {@link Expression}.
 * <p>
 * It is possible to partially match the JPQL query that was parsed, the value of <em>exactMatch</em>
 * will determine whether the string representation of any given {@link StateObject} should reflect
 * the exact string that was parsed. <code>true</code> will use every bit of information contained
 * in the corresponding {@link Expression} to perfectly match what was parsed (case of JPQL
 * identifiers and the presence of whitespace); <code>false</code> will only match the case
 * sensitivity of the JPQL identifiers.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("null")
public abstract class AbstractActualJPQLQueryFormatter extends BaseJPQLQueryFormatter {

	/**
	 * Determines whether the string representation of any given {@link StateObject} should reflect
	 * the exact string that was parsed: <code>true</code> will use every bit of information
	 * contained in the corresponding {@link Expression} to perfectly match what was parsed;
	 * <code>false</code> will only match the case sensitivity of the JPQL identifiers.
	 */
	protected final boolean exactMatch;

	/**
	 * Creates a new <code>AbstractActualJPQLQueryFormatter</code>.
	 *
	 * @param exactMatch Determines whether the string representation of any given {@link StateObject}
	 * should reflect the exact string that was parsed: <code>true</code> will use every bit of
	 * information contained in the corresponding {@link Expression} to perfectly match what was
	 * parsed (case of JPQL identifiers and the presence of whitespace); <code>false</code> will only
	 * match the case sensitivity of the JPQL identifiers
	 */
	protected AbstractActualJPQLQueryFormatter(boolean exactMatch) {
		super(IdentifierStyle.UPPERCASE);
		this.exactMatch = exactMatch;
	}

	/**
	 * Creates a new <code>AbstractActualJPQLQueryFormatter</code>.
	 *
	 * @param exactMatch Determines whether the string representation of any given {@link StateObject}
	 * should reflect the exact string that was parsed: <code>true</code> will use every bit of
	 * information contained in the corresponding {@link Expression} to perfectly match what was
	 * parsed (case of JPQL identifiers and the presence of whitespace); <code>false</code> will only
	 * match the case sensitivity of the JPQL identifiers
	 * @param style Determines how the JPQL identifiers are written out, which is used if the
	 * {@link StateObject} was modified after its creation
	 * @exception NullPointerException The IdentifierStyle cannot be <code>null</code>
	 */
	protected AbstractActualJPQLQueryFormatter(boolean exactMatch, IdentifierStyle style) {
		super(style);
		this.exactMatch = exactMatch;
	}

	/**
	 * Appends the given actual identifier if it's not an empty string, otherwise the second
	 * identifier will be appended.
	 *
	 * @param actualIdentifier The actual JPQL identifier to append to the writer if it's not an
	 * empty string
	 * @param identifier The uppercase constant of the JPQL identifier to append if the actual one is
	 * an empty string
	 */
	protected void appendIdentifier(String actualIdentifier, String identifier) {

		if (!exactMatch || ExpressionTools.stringIsEmpty(actualIdentifier)) {
			actualIdentifier = formatIdentifier(identifier);
		}

		writer.append(actualIdentifier);
	}

	/**
	 * Determines whether the string representation of any given {@link StateObject} should reflect
	 * the exact string that was parsed.
	 *
	 * @return <code>true</code> will use every bit of information contained in the corresponding
	 * {@link Expression} to perfectly match what was parsed; <code>false</code> will only match the
	 * case sensitivity of the JPQL identifiers
	 */
	public boolean isUsingExactMatch() {
		return exactMatch;
	}

	protected boolean shouldOutput(Expression expression) {
		return !exactMatch || (expression == null);
	}

	protected void toStringAggregateFunction(AggregateFunctionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			AggregateFunction expression = stateObject.getExpression();

			// Identifier
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : stateObject.getIdentifier(), stateObject.getIdentifier());

			// '('
			if (shouldOutput(expression) || expression.hasLeftParenthesis()) {
				writer.append(formatIdentifier(LEFT_PARENTHESIS));
			}

			// 'DISTINCT'
			if (stateObject.hasDistinct()) {
				appendIdentifier((expression != null) ? expression.getActualDistinctIdentifier() : DISTINCT, DISTINCT);

				if (shouldOutput(expression) || expression.hasSpaceAfterDistinct()) {
					writer.append(SPACE);
				}
			}

			// Encapsulated expression
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}

			// ')'
			if (shouldOutput(expression) || expression.hasRightParenthesis()) {
				writer.append(formatIdentifier(RIGHT_PARENTHESIS));
			}
		}
	}

	protected void toStringChildren(ListHolderStateObject<? extends StateObject> stateObject,
	                                boolean comma) {

		for (ListIterator<? extends StateObject> iter = stateObject.items(); iter.hasNext(); ) {
			iter.next().accept(this);
			if (iter.hasNext()) {
				writer.append(comma ? COMMA_SPACE : SPACE);
			}
		}
	}

	protected void toStringCompound(CompoundExpressionStateObject stateObject, String identifier) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			CompoundExpression expression = stateObject.getExpression();

			// Left expression
			if (stateObject.hasLeft()) {
				stateObject.getLeft().accept(this);
				writer.append(SPACE);
			}

			// Identifier
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : identifier, identifier);

			if (shouldOutput(expression) || expression.hasSpaceAfterIdentifier()) {
				writer.append(SPACE);
			}

			// Right expression
			if (stateObject.hasRight()) {
				stateObject.getRight().accept(this);
			}
		}
	}

	protected void toStringDoubleEncapsulated(AbstractDoubleEncapsulatedExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			AbstractDoubleEncapsulatedExpression expression = stateObject.getExpression();

			// Identifier
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : stateObject.getIdentifier(), stateObject.getIdentifier());

			// '('
			if (shouldOutput(expression) || expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}
			else if (expression.hasSpaceAfterIdentifier()) {
				writer.append(COMMA);
			}

			// First expression
			if (stateObject.hasFirst()) {
				stateObject.getFirst().accept(this);
			}

			// ','
			if (shouldOutput(expression) || expression.hasComma()) {
				writer.append(COMMA);
			}

			if (shouldOutput(expression) || expression.hasSpaceAfterComma()) {
				writer.append(SPACE);
			}

			// Second expression
			if (stateObject.hasSecond()) {
				stateObject.getSecond().accept(this);
			}

			// ')'
			if (shouldOutput(expression) || expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	protected void toStringEncapsulatedIdentificationVariable(EncapsulatedIdentificationVariableExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			EncapsulatedIdentificationVariableExpression expression = stateObject.getExpression();

			// Identifier
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : stateObject.getIdentifier(), stateObject.getIdentifier());

			// '('
			if (shouldOutput(expression) || expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}

			// Identification variable
			if (stateObject.hasIdentificationVariable()) {
				writer.append(stateObject.getIdentificationVariable());
			}

			// ')'
			if (shouldOutput(expression) || expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	protected void toStringFromClause(AbstractFromClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			AbstractFromClause expression = stateObject.getExpression();

			// 'FROM'
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : FROM, FROM);

			if (shouldOutput(expression) || expression.hasSpaceAfterFrom()) {
				writer.append(SPACE);
			}

			// declaration
			if (stateObject.hasItems()) {
				toStringChildren(stateObject, true);
			}
		}
	}

	protected void toStringIdentificationVariableDeclaration(AbstractIdentificationVariableDeclarationStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			IdentificationVariableDeclaration expression = stateObject.getExpression();

			// Range variable declaration
			stateObject.getRangeVariableDeclaration().accept(this);

			// Join | Join Fetch
			if (stateObject.hasItems()) {

				if (shouldOutput(expression) || expression.hasSpace()) {
					writer.append(SPACE);
				}

				toStringChildren(stateObject, false);
			}
		}
	}

	protected void toStringModifyStatement(AbstractModifyStatementStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			stateObject.getModifyClause().accept(this);

			if (stateObject.hasWhereClause()) {
				// TODO: HANDLE SPACE
				writer.append(SPACE);
				stateObject.getWhereClause().accept(this);
			}
		}
	}

	protected void toStringPathExpression(AbstractPathExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			stateObject.toText(writer);
		}
	}

	protected void toStringRangeVariableDeclaration(AbstractRangeVariableDeclarationStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			RangeVariableDeclaration expression = stateObject.getExpression();

			// "Root" object (abstract schema name or derived declaration)
			stateObject.getRootStateObject().accept(this);

			if (exactMatch && (expression != null) && expression.hasSpaceAfterAbstractSchemaName()) {
				writer.append(SPACE);
			}

			// 'AS'
			if (stateObject.hasAs()) {

				if (!exactMatch || (expression == null)) {
					writer.append(SPACE);
				}

				appendIdentifier((expression != null) ? expression.getActualAsIdentifier() : AS, AS);
			}

			if (exactMatch && (expression != null) && expression.hasSpaceAfterAs()) {
				writer.append(SPACE);
			}

			// Identification variable
			if (stateObject.hasIdentificationVariable() &&
			   !stateObject.isIdentificationVariableVirtual()) {

				if (!exactMatch || (expression == null)) {
					writer.append(SPACE);
				}

				writer.append(stateObject.getIdentificationVariable());
			}
		}
	}

	protected boolean toStringSelectStatement(AbstractSelectStatementStateObject stateObject) {

		AbstractSelectStatement expression = stateObject.getExpression();
		boolean spaceAdded = false;

		// SELECT clause
		stateObject.getSelectClause().accept(this);

		// If no select items were parsed by they got added later, make sure a space is added
		if (shouldOutput(expression) ||
		    expression.hasSpaceAfterSelect() ||
		    (!expression.getSelectClause().hasSelectExpression() &&
		     stateObject.getSelectClause().hasSelectItem())) {

			writer.append(SPACE);
		}

		// FROM clause
		stateObject.getFromClause().accept(this);

		// If no WHERE clause was parsed but was added later, make sure a space is added
		if (exactMatch && (expression != null) && expression.hasSpaceAfterFrom() ||
		    stateObject.hasWhereClause()) {

			writer.append(SPACE);
			spaceAdded = true;
		}

		// WHERE clause
		if (stateObject.hasWhereClause()) {
			stateObject.getWhereClause().accept(this);
			spaceAdded = false;
		}

		// If no GROUP BY clause was parsed but was added later, make sure a space is added
		if (exactMatch && (expression != null) && expression.hasSpaceAfterWhere() ||
		    stateObject.hasGroupByClause()) {

			if (!spaceAdded) {
				writer.append(SPACE);
				spaceAdded = true;
			}
		}

		// GROUP BY clause
		if (stateObject.hasGroupByClause()) {
			stateObject.getGroupByClause().accept(this);
			spaceAdded = false;
		}

		// If no HAVING clause was parsed but was added later, make sure a space is added
		if (exactMatch && (expression != null) && expression.hasSpaceAfterGroupBy() ||
		    stateObject.hasHavingClause()) {

			if (!spaceAdded) {
				writer.append(SPACE);
				spaceAdded = true;
			}
		}

		// HAVING clause
		if (stateObject.hasHavingClause()) {
			stateObject.getHavingClause().accept(this);
			spaceAdded = true;
		}

		return spaceAdded;
	}

	protected void toStringSimpleStateObject(SimpleStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else if (stateObject.hasText()) {
			writer.append(stateObject.getText());
		}
	}

	protected void toStringSingleEncapsulated(AbstractSingleEncapsulatedExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			AbstractSingleEncapsulatedExpression expression = stateObject.getExpression();

			// Identifier
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : stateObject.getIdentifier(), stateObject.getIdentifier());

			// '('
			if (shouldOutput(expression) || expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}

			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}

			// ')'
			if (shouldOutput(expression) || expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	protected void toStringTripleEncapsulated(AbstractTripleEncapsulatedExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			AbstractTripleEncapsulatedExpression expression = stateObject.getExpression();

			// Identifier
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : stateObject.getIdentifier(), stateObject.getIdentifier());

			// '('
			if (shouldOutput(expression) || expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}

			// First expression
			if (stateObject.hasFirst()) {
				stateObject.getFirst().accept(this);
			}

			if (shouldOutput(expression) || expression.hasFirstComma()) {
				writer.append(COMMA);
			}

			if (shouldOutput(expression) || expression.hasSpaceAfterFirstComma()) {
				writer.append(SPACE);
			}

			// Second expression
			if (stateObject.hasSecond()) {
				stateObject.getSecond().accept(this);
			}

			// Third expression
			if (stateObject.hasThird()) {

				if (shouldOutput(expression) || expression.hasSecondComma()) {
					writer.append(COMMA);
				}

				if (shouldOutput(expression) || expression.hasSpaceAfterSecondComma()) {
					writer.append(SPACE);
				}

				stateObject.getThird().accept(this);
			}

			// ')'
			if (shouldOutput(expression) || expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AbsExpressionStateObject stateObject) {
		toStringSingleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AbstractSchemaNameStateObject stateObject) {
		toStringSimpleStateObject(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AdditionExpressionStateObject stateObject) {
		toStringCompound(stateObject, PLUS);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AllOrAnyExpressionStateObject stateObject) {
		toStringSingleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AndExpressionStateObject stateObject) {
		toStringCompound(stateObject, AND);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ArithmeticFactorStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			writer.append(stateObject.getArithmeticSign());

			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AvgFunctionStateObject stateObject) {
		toStringAggregateFunction(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BadExpressionStateObject stateObject) {
		toStringSimpleStateObject(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BetweenExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			BetweenExpression expression = stateObject.getExpression();

			// Expression
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
				writer.append(SPACE);
			}

			// 'NOT
			if (stateObject.hasNot()) {
				appendIdentifier((expression != null) ? expression.getActualNotIdentifier() : NOT, NOT);
				writer.append(SPACE);
			}

			// 'BETWEEN'
			appendIdentifier((expression != null) ? expression.getActualBetweenIdentifier() : BETWEEN, BETWEEN);

			if (shouldOutput(expression) || expression.hasSpaceAfterBetween()) {
				writer.append(SPACE);
			}

			// Lower bound expression
			if (stateObject.hasLowerBound()) {
				stateObject.getLowerBound().accept(this);
			}

			if (shouldOutput(expression) || expression.hasSpaceAfterLowerBound()) {
				writer.append(SPACE);
			}

			// 'AND'
			if (shouldOutput(expression) || expression.hasAnd()) {
				appendIdentifier((expression != null) ? expression.getActualAndIdentifier() : AND, AND);
			}

			if (shouldOutput(expression) || expression.hasSpaceAfterAnd()) {
				writer.append(SPACE);
			}

			// Upper bound expression
			if (stateObject.hasUpperBound()) {
				stateObject.getUpperBound().accept(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CaseExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			CaseExpression expression = stateObject.getExpression();

			// 'CASE'
			appendIdentifier((expression != null) ? expression.getActualCaseIdentifier() : CASE, CASE);

			if (shouldOutput(expression) || expression.hasSpaceAfterCase()) {
				writer.append(SPACE);
			}

			// Case operand
			if (stateObject.hasCaseOperand()) {
				stateObject.getCaseOperand().accept(this);

				if (shouldOutput(expression) || expression.hasSpaceAfterCaseOperand()) {
					writer.append(SPACE);
				}
			}

			// WHEN clauses
			if (stateObject.hasItems()) {
				toStringChildren(stateObject, false);
			}

			if (shouldOutput(expression) || expression.hasSpaceAfterWhenClauses()) {
				writer.append(SPACE);
			}

			// 'ELSE'
			if (shouldOutput(expression) || expression.hasElse()) {
				appendIdentifier((expression != null) ? expression.getActualElseIdentifier() : ELSE, ELSE);
			}

			if (shouldOutput(expression) || expression.hasSpaceAfterElse()) {
				writer.append(SPACE);
			}

			// Else expression
			if (stateObject.hasElse()) {
				stateObject.getElse().accept(this);
			}

			if (shouldOutput(expression) || expression.hasSpaceAfterElseExpression()) {
				writer.append(SPACE);
			}

			// END
			if (shouldOutput(expression) || expression.hasEnd()) {
				appendIdentifier((expression != null) ? expression.getActualEndIdentifier() : END, END);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CoalesceExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			CoalesceExpression expression = stateObject.getExpression();

			// 'COALESCE'
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : COALESCE, COALESCE);

			// '('
			if (shouldOutput(expression) || expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}
			else if (expression.hasSpaceAfterIdentifier()) {
				writer.append(SPACE);
			}

			toStringChildren(stateObject, true);

			// ')'
			if (shouldOutput(expression) || expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberDeclarationStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			CollectionMemberDeclaration expression = stateObject.getExpression();

			// 'IN'
			appendIdentifier((expression != null) ? expression.getActualInIdentifier() : IN, IN);

			// '('
			if (!stateObject.isDerived() && (shouldOutput(expression) || expression.hasLeftParenthesis())) {
				writer.append(LEFT_PARENTHESIS);
			}
			else if (stateObject.isDerived() && (shouldOutput(expression) || expression.hasSpaceAfterIn())) {
				writer.append(SPACE);
			}

			// Collection-valued path expression
			stateObject.getCollectionValuedPath().accept(this);

			// ')'
			if (!stateObject.isDerived() && (shouldOutput(expression) || expression.hasRightParenthesis())) {
				writer.append(RIGHT_PARENTHESIS);

				if (shouldOutput(expression) || expression.hasSpaceAfterRightParenthesis()) {
					writer.append(SPACE);
				}
			}
			else if (stateObject.isDerived() &&
			         stateObject.hasAs() &&
			         (shouldOutput(expression) || expression.hasSpaceAfterRightParenthesis())) {

				writer.append(SPACE);
			}

			// 'AS'
			if (stateObject.hasAs()) {
				appendIdentifier((expression != null) ? expression.getActualAsIdentifier() : AS, AS);

				if (shouldOutput(expression) || expression.hasSpaceAfterAs()) {
					writer.append(SPACE);
				}
			}

			// Identification variable
			if (stateObject.hasIdentificationVariable()) {
				stateObject.getIdentificationVariable().accept(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			CollectionMemberExpression expression = stateObject.getExpression();

			// Entity or value expression
			if (stateObject.hasEntityStateObject()) {
				stateObject.getEntityStateObject().accept(this);
				writer.append(SPACE);
			}

			// 'NOT'
			if (stateObject.hasNot()) {
				appendIdentifier((expression != null) ? expression.getActualNotIdentifier() : NOT, NOT);
				writer.append(SPACE);
			}

			// 'MEMBER'
			appendIdentifier((expression != null) ? expression.getActualMemberIdentifier() : MEMBER, MEMBER);

			if (shouldOutput(expression) || expression.hasSpaceAfterMember()) {
				writer.append(SPACE);
			}

			// 'OF'
			if (stateObject.hasOf()) {
				appendIdentifier((expression != null) ? expression.getActualOfIdentifier() : OF, OF);

				if (shouldOutput(expression) || expression.hasSpaceAfterOf()) {
					writer.append(SPACE);
				}
			}

			// Collection-valued path expression
			stateObject.getCollectionValuedPath().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionValuedPathExpressionStateObject stateObject) {
		toStringPathExpression(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ComparisonExpressionStateObject stateObject) {
		toStringCompound(stateObject, stateObject.getIdentifier());
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConcatExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			ConcatExpression expression = stateObject.getExpression();

			// 'CONCAT'
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : CONCAT, CONCAT);

			// '('
			if (shouldOutput(expression) || expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}

			toStringChildren(stateObject, true);

			// ')'
			if (shouldOutput(expression) || expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConstructorExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			ConstructorExpression expression = stateObject.getExpression();

			// 'NEW'
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : NEW, NEW);

			if (shouldOutput(expression) || expression.hasSpaceAfterNew()) {
				writer.append(SPACE);
			}

			// Class name
			writer.append(stateObject.getClassName());

			// '('
			if (shouldOutput(expression) || expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}

			toStringChildren(stateObject, true);

			// ')'
			if (shouldOutput(expression) || expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CountFunctionStateObject stateObject) {
		toStringAggregateFunction(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DateTimeStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			DateTime expression = stateObject.getExpression();
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : stateObject.getText(), stateObject.getText());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			DeleteClause expression = stateObject.getExpression();

			// 'DELETE'
			appendIdentifier((expression != null) ? expression.getActualDeleteIdentifier() : DELETE, DELETE);

			if (shouldOutput(expression) || expression.hasSpaceAfterDelete()) {
				writer.append(SPACE);
			}

			// 'FROM'
			if (shouldOutput(expression) || expression.hasFrom()) {
				appendIdentifier((expression != null) ? expression.getActualFromIdentifier() : FROM, FROM);
			}

			if (shouldOutput(expression) || expression.hasSpaceAfterFrom()) {
				writer.append(SPACE);
			}

			// Range variable declaration
			stateObject.getRangeVariableDeclaration().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteStatementStateObject stateObject) {
		toStringModifyStatement(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DerivedPathIdentificationVariableDeclarationStateObject stateObject) {
		toStringIdentificationVariableDeclaration(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DerivedPathVariableDeclarationStateObject stateObject) {
		toStringRangeVariableDeclaration(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DivisionExpressionStateObject stateObject) {
		toStringCompound(stateObject, DIVISION);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EmptyCollectionComparisonExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			EmptyCollectionComparisonExpression expression = stateObject.getExpression();

			stateObject.getStateObject().accept(this);
			writer.append(SPACE);

			// 'IS'
			appendIdentifier((expression != null) ? expression.getActualIsIdentifier() : IS, IS);
			writer.append(SPACE);

			// 'NOT'
			if (stateObject.hasNot()) {
				appendIdentifier((expression != null) ? expression.getActualNotIdentifier() : NOT, NOT);
				writer.append(SPACE);
			}

			// 'EMPTY'
			appendIdentifier((expression != null) ? expression.getActualEmptyIdentifier() : EMPTY, EMPTY);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntityTypeLiteralStateObject stateObject) {
		toStringSimpleStateObject(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntryExpressionStateObject stateObject) {
		toStringEncapsulatedIdentificationVariable(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EnumTypeStateObject stateObject) {
		toStringSimpleStateObject(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ExistsExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			ExistsExpression expression = stateObject.getExpression();

			// 'NOT'
			if (stateObject.hasNot()) {
				appendIdentifier((expression != null) ? expression.getActualNotIdentifier() : NOT, NOT);
				writer.append(SPACE);
			}

			// 'EXISTS'
			String actualIdentifier = (expression != null) ? expression.getActualIdentifier() : null;

			if ((actualIdentifier != null) && actualIdentifier.startsWith(NOT)) {
				actualIdentifier = actualIdentifier.substring(4);
			}

			appendIdentifier(actualIdentifier, EXISTS);

			// '('
			if (shouldOutput(expression) || expression.hasLeftParenthesis()) {
				writer.append(formatIdentifier(LEFT_PARENTHESIS));
			}

			// Subquery
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}

			// ')'
			if (shouldOutput(expression) || expression.hasRightParenthesis()) {
				writer.append(formatIdentifier(RIGHT_PARENTHESIS));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FromClauseStateObject stateObject) {
		toStringFromClause(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FunctionExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			FunctionExpression expression = stateObject.getExpression();

			// FUNC
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : FUNC, FUNC);

			// (
			if (shouldOutput(expression) || expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}
			else if (exactMatch && expression.hasSpaceAfterIdentifier()) {
				writer.append(SPACE);
			}

			// Function name
			if (stateObject.hasFunctionName()) {

				writer.append(stateObject.getQuotedFunctionName());

				if (shouldOutput(expression) || expression.hasComma()) {
					writer.append(COMMA);
				}

				if (shouldOutput(expression) || expression.hasSpaceAfterComma()) {
					writer.append(SPACE);
				}
			}

			// Arguments
			toStringChildren(stateObject, true);

			// )
			if (shouldOutput(expression) || expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(GroupByClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			GroupByClause expression = stateObject.getExpression();

			// 'GROUP BY'
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : GROUP_BY, GROUP_BY);

			if (shouldOutput(expression) || expression.hasSpaceAfterGroupBy()) {
				writer.append(SPACE);
			}

			// Group by items
			if (stateObject.hasItems()) {
				toStringChildren(stateObject, true);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(HavingClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			HavingClause expression = stateObject.getExpression();

			// 'HAVING'
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : HAVING, HAVING);

			if (exactMatch && (expression != null) && expression.hasSpaceAfterIdentifier() ||
			    stateObject.hasConditional()) {

				writer.append(SPACE);
			}

			// Conditional expression
			if (stateObject.hasConditional()) {
				stateObject.getConditional().accept(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IdentificationVariableDeclarationStateObject stateObject) {
		toStringIdentificationVariableDeclaration(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IdentificationVariableStateObject stateObject) {
		toStringSimpleStateObject(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IndexExpressionStateObject stateObject) {
		toStringEncapsulatedIdentificationVariable(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			InExpression expression = stateObject.getExpression();

			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
				writer.append(SPACE);
			}

			// 'NOT'
			if (stateObject.hasNot()) {
				appendIdentifier((expression != null) ? expression.getActualNotIdentifier() : NOT, NOT);
				writer.append(SPACE);
			}

			// 'IN'
			appendIdentifier((expression != null) ? expression.getActualInIdentifier() : IN, IN);

			if (!stateObject.isSingleInputParameter()) {
				writer.append(LEFT_PARENTHESIS);
			}
			else if (shouldOutput(expression) || expression.hasSpaceAfterIn()) {
				writer.append(SPACE);
			}

			if (stateObject.hasItems()) {
				toStringChildren(stateObject, true);
			}

			if (!stateObject.isSingleInputParameter()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InputParameterStateObject stateObject) {
		toStringSimpleStateObject(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JoinStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			Join expression = stateObject.getExpression();

			// JOIN
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : stateObject.getJoinType(), stateObject.getJoinType());

			if (shouldOutput(expression) || expression.hasSpaceAfterJoin()) {
				writer.append(SPACE);
			}

			// Join association path
			stateObject.getJoinAssociationPathStateObject().accept(this);

			// Check first if the JOIN FETCH is allowed to have an identification variable
			if (stateObject.hasFetch()) {
				if (expression.hasAs()) {
					writer.append(SPACE);
				}
			}
			// JOIN always needs a whitespace
			else {
				if (shouldOutput(expression) || expression.hasSpaceAfterJoinAssociation()) {
					writer.append(SPACE);
				}
			}

			// AS
			if (stateObject.hasAs()) {

				appendIdentifier((expression != null) ? expression.getActualAsIdentifier() : AS, AS);

				if (shouldOutput(expression) || expression.hasSpaceAfterAs()) {
					writer.append(SPACE);
				}
			}

			// Identification variable
			stateObject.getIdentificationVariableStateObject().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JPQLQueryStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else if (stateObject.hasQueryStatement()) {
			stateObject.getQueryStatement().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeyExpressionStateObject stateObject) {
		toStringEncapsulatedIdentificationVariable(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeywordExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			KeywordExpression expression = stateObject.getExpression();
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : stateObject.getText(), stateObject.getText());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LengthExpressionStateObject stateObject) {
		toStringSingleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LikeExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			LikeExpression expression = stateObject.getExpression();

			// String expression
			if (stateObject.hasStringStateObject()) {
				stateObject.getStringStateObject().accept(this);
			}

			if (shouldOutput(expression) || expression.hasSpaceAfterStringExpression()) {
				writer.append(SPACE);
			}

			// 'NOT'
			if (stateObject.hasNot()) {
				appendIdentifier((expression != null) ? expression.getActualNotIdentifier() : NOT, NOT);
				writer.append(SPACE);
			}

			// 'LIKE'
			appendIdentifier((expression != null) ? expression.getActualLikeIdentifier() : LIKE, LIKE);

			if (shouldOutput(expression) || expression.hasSpaceAfterLike()) {
				writer.append(SPACE);
			}

			// Pattern value
			if (stateObject.hasPatternValue()) {
				stateObject.getPatternValue().accept(this);
			}

			if (exactMatch && (expression != null) && expression.hasSpaceAfterPatternValue()) {
				writer.append(SPACE);
			}

			// Escape character
			if (stateObject.hasEscapeCharacter()) {

				if (!exactMatch) {
					writer.append(SPACE);
				}

				appendIdentifier((expression != null) ? expression.getActualEscapeIdentifier() : ESCAPE, ESCAPE);

				if (shouldOutput(expression) || expression.hasSpaceAfterEscape()) {
					writer.append(SPACE);
				}

				writer.append(stateObject.getEscapeCharacter());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LocateExpressionStateObject stateObject) {
		toStringTripleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LowerExpressionStateObject stateObject) {
		toStringSingleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MaxFunctionStateObject stateObject) {
		toStringAggregateFunction(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MinFunctionStateObject stateObject) {
		toStringAggregateFunction(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ModExpressionStateObject stateObject) {
		toStringDoubleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MultiplicationExpressionStateObject stateObject) {
		toStringCompound(stateObject, MULTIPLICATION);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NotExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			NotExpression expression = stateObject.getExpression();

			// 'NOT'
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : NOT, NOT);

			if (shouldOutput(expression) || expression.hasSpaceAfterNot()) {
				writer.append(SPACE);
			}

			// Expression
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullComparisonExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			NullComparisonExpression expression = stateObject.getExpression();

			// Expression
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
				writer.append(SPACE);
			}

			// 'IS'
			appendIdentifier((expression != null) ? expression.getActualIsIdentifier() : IS, IS);
			writer.append(SPACE);

			// 'NOT'
			if (stateObject.hasNot()) {
				appendIdentifier((expression != null) ? expression.getActualNotIdentifier() : NOT, NOT);
				writer.append(SPACE);
			}

			// 'NULL'
			appendIdentifier((expression != null) ? expression.getActualNullIdentifier() : NULL, NULL);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullIfExpressionStateObject stateObject) {
		toStringDoubleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NumericLiteralStateObject stateObject) {
		toStringSimpleStateObject(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ObjectExpressionStateObject stateObject) {
		toStringEncapsulatedIdentificationVariable(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrderByClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			OrderByClause expression = stateObject.getExpression();

			// 'ORDER BY'
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : ORDER_BY, ORDER_BY);

			if (shouldOutput(expression) || expression.hasSpaceAfterOrderBy()) {
				writer.append(SPACE);
			}

			// Order by items
			if (stateObject.hasItems()) {
				toStringChildren(stateObject, true);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrderByItemStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			OrderByItem expression = stateObject.getExpression();

			// Order by item
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}

			// ASC/DESC
			if (!stateObject.isDefault()) {

				if (shouldOutput(expression) || expression.hasSpaceAfterExpression()) {
					writer.append(SPACE);
				}

				String ordering = stateObject.getOrdering().name();
				String actualOrdering = (expression != null) ? expression.getActualOrdering() : null;

				if (!ordering.equalsIgnoreCase(actualOrdering)) {
					actualOrdering = ordering;
				}

				appendIdentifier(actualOrdering, ordering);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrExpressionStateObject stateObject) {
		toStringCompound(stateObject, OR);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(RangeVariableDeclarationStateObject stateObject) {
		toStringRangeVariableDeclaration(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ResultVariableStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			ResultVariable expression = stateObject.getExpression();

			// Select expression
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}

			if (exactMatch && (expression != null) && expression.hasSelectExpression()) {
				writer.append(SPACE);
			}

			// 'AS'
			if (stateObject.hasAs()) {
				if (!exactMatch || (expression == null)) {
					writer.append(SPACE);
				}
				appendIdentifier((expression != null) ? expression.getActualAsIdentifier() : AS, AS);
			}

			if (exactMatch && (expression != null) && expression.hasSpaceAfterAs()) {
				writer.append(SPACE);
			}

			// Result variable
			if (stateObject.hasResultVariable()) {
				if (!exactMatch || (expression == null)) {
					writer.append(SPACE);
				}
				writer.append(stateObject.getResultVariable());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			SelectClause expression = stateObject.getExpression();

			// SELECT
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : SELECT, SELECT);

			if (shouldOutput(expression) || expression.hasSpaceAfterSelect()) {
				writer.append(SPACE);
			}

			// DISTINCT
			if (stateObject.hasDistinct()) {
				appendIdentifier((expression != null) ? expression.getActualDistinctIdentifier() : DISTINCT, DISTINCT);

				if (shouldOutput(expression) || expression.hasSpaceAfterDistinct()) {
					writer.append(SPACE);
				}
			}

			// Select expressions
			if (stateObject.hasItems()) {
				toStringChildren(stateObject, true);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectStatementStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			boolean endWithSpace = toStringSelectStatement(stateObject);
			SelectStatement expression = stateObject.getExpression();

			// If no ORDER BY clause was parsed but was added later, make sure a space is added
			if (exactMatch && (expression != null) && expression.hasSpaceBeforeOrderBy() ||
			    stateObject.hasOrderByClause()) {

				if (!endWithSpace) {
					writer.append(SPACE);
				}
			}

			// ORDER BY clause
			if (stateObject.hasOrderByClause()) {
				stateObject.getOrderByClause().accept(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleFromClauseStateObject stateObject) {
		toStringFromClause(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			SimpleSelectClause expression = stateObject.getExpression();

			// SELECT
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : SELECT, SELECT);

			if (shouldOutput(expression) || expression.hasSpaceAfterSelect()) {
				writer.append(SPACE);
			}

			// DISTINCT
			if (stateObject.hasDistinct()) {
				appendIdentifier((expression != null) ? expression.getActualDistinctIdentifier() : DISTINCT, DISTINCT);

				if (shouldOutput(expression) || expression.hasSpaceAfterDistinct()) {
					writer.append(SPACE);
				}
			}

			// Select expression
			if (stateObject.hasSelectItem()) {
				stateObject.getSelectItem().accept(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectStatementStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			toStringSelectStatement(stateObject);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SizeExpressionStateObject stateObject) {
		toStringSingleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SqrtExpressionStateObject stateObject) {
		toStringSingleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StateFieldPathExpressionStateObject stateObject) {
		toStringPathExpression(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StringLiteralStateObject stateObject) {
		toStringSimpleStateObject(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubExpressionStateObject stateObject) {
		toStringSingleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubstringExpressionStateObject stateObject) {
		toStringTripleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubtractionExpressionStateObject stateObject) {
		toStringCompound(stateObject, MINUS);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SumFunctionStateObject stateObject) {
		toStringAggregateFunction(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			TreatExpression expression = stateObject.getExpression();

			// TREAT
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : TREAT, TREAT);

			// (
			if (shouldOutput(expression) || expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}

			// Join association path expression
			stateObject.getJoinAssociationPathStateObject().accept(this);

			if (shouldOutput(expression) || expression.hasSpaceAfterCollectionValuedPathExpression()) {
				writer.append(SPACE);
			}

			// AS
			if (stateObject.hasAs()) {

				appendIdentifier((expression != null) ? expression.getActualAsIdentifier() : AS, AS);

				if (shouldOutput(expression) || expression.hasSpaceAfterAs()) {
					writer.append(SPACE);
				}
			}

			// Entity type name
			writer.append(stateObject.getEntityTypeName());

			// )
			if (shouldOutput(expression) || expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TrimExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			TrimExpression expression = stateObject.getExpression();

			// 'TRIM'
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : stateObject.getIdentifier(), stateObject.getIdentifier());

			// '('
			if (shouldOutput(expression) || expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}
			else if ((expression != null) && expression.hasSpaceAfterIdentifier()) {
				writer.append(SPACE);
			}

			// Trim specification
			if (stateObject.hasSpecification()) {
				String specification = stateObject.getSpecification().name();
				String actualSpecification = (expression != null) ? expression.getActualSpecificationIdentifier() : null;

				if (!specification.equalsIgnoreCase(actualSpecification)) {
					actualSpecification = specification;
				}

				appendIdentifier(actualSpecification, specification);
			}

			if (shouldOutput(expression) || expression.hasSpaceAfterSpecification()) {
				writer.append(SPACE);
			}

			// Trim character
			if (stateObject.hasTrimCharacter()) {
				stateObject.getTrimCharacter().accept(this);

				if (shouldOutput(expression) || expression.hasSpaceAfterTrimCharacter()) {
					writer.append(SPACE);
				}
			}

			// 'FROM'
			if (stateObject.hasSpecification() ||
			    stateObject.hasTrimCharacter()) {

				appendIdentifier((expression != null) ? expression.getActualFromIdentifier() : FROM, FROM);

				if (shouldOutput(expression) || expression.hasSpaceAfterFrom()) {
					writer.append(SPACE);
				}
			}

			// String primary
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}

			// ')'
			if (shouldOutput(expression) || expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TypeExpressionStateObject stateObject) {
		toStringSingleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UnknownExpressionStateObject stateObject) {
		toStringSimpleStateObject(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			UpdateClause expression = stateObject.getExpression();

			// 'UPDATE'
			appendIdentifier((expression != null) ? expression.getActualUpdateIdentifier() : UPDATE, UPDATE);

			if (shouldOutput(expression) || expression.hasSpaceAfterUpdate()) {
				writer.append(SPACE);
			}

			// Range variable declaration
			stateObject.getRangeVariableDeclaration().accept(this);

			if (shouldOutput(expression) || expression.hasSpaceAfterRangeVariableDeclaration()) {
				writer.append(SPACE);
			}

			// 'SET'
			if (shouldOutput(expression) || expression.hasSet()) {
				appendIdentifier((expression != null) ? expression.getActualSetIdentifier() : SET, SET);

				if (shouldOutput(expression) || expression.hasSpaceAfterSet()) {
					writer.append(SPACE);
				}
			}

			// Update items
			if (stateObject.hasItems()) {
				toStringChildren(stateObject, true);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateItemStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			UpdateItem expression = stateObject.getExpression();

			// Update item
			stateObject.getStateFieldPath().accept(this);

			if (shouldOutput(expression) || expression.hasSpaceAfterStateFieldPathExpression()) {
				writer.append(SPACE);
			}

			// '='
			if (shouldOutput(expression) || expression.hasEqualSign()) {
				writer.append(EQUAL);
			}

			if (shouldOutput(expression) || expression.hasSpaceAfterEqualSign()) {
				writer.append(SPACE);
			}

			// New value
			if (stateObject.hasNewValue()) {
				stateObject.getNewValue().accept(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateStatementStateObject stateObject) {
		toStringModifyStatement(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpperExpressionStateObject stateObject) {
		toStringSingleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ValueExpressionStateObject stateObject) {
		toStringEncapsulatedIdentificationVariable(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhenClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			WhenClause expression = stateObject.getExpression();

			// 'WHEN'
			appendIdentifier((expression != null) ? expression.getActualWhenIdentifier() : WHEN, WHEN);

			if (shouldOutput(expression) || expression.hasSpaceAfterWhen()) {
				writer.append(SPACE);
			}

			// WHEN expression
			if (stateObject.hasConditional()) {
				stateObject.getConditional().accept(this);
			}

			if (shouldOutput(expression) || expression.hasSpaceAfterWhenExpression()) {
				writer.append(SPACE);
			}

			// 'THEN'
			if (shouldOutput(expression) || expression.hasThen()) {
				appendIdentifier((expression != null) ? expression.getActualThenIdentifier() : THEN, THEN);
			}

			if (shouldOutput(expression) || expression.hasSpaceAfterThen()) {
				writer.append(SPACE);
			}

			// THEN expression
			if (stateObject.hasThen()) {
				stateObject.getThen().accept(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhereClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			WhereClause expression = stateObject.getExpression();

			// 'WHERE
			appendIdentifier((expression != null) ? expression.getActualIdentifier() : WHERE, WHERE);

			if (exactMatch && (expression != null) && expression.hasSpaceAfterIdentifier() ||
			    stateObject.hasConditional()) {

				writer.append(SPACE);
			}

			// Conditional expression
			if (stateObject.hasConditional()) {
				stateObject.getConditional().accept(this);
			}
		}
	}
}