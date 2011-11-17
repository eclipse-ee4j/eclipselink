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

import java.util.ListIterator;
import org.eclipse.persistence.jpa.jpql.model.query.AbsExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.AbstractConditionalClauseStateObject;
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
import org.eclipse.persistence.jpa.jpql.model.query.GroupByClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.HavingClauseStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IdentificationVariableDeclarationStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IdentificationVariableStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.InExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.IndexExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.InputParameterStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.JoinFetchStateObject;
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
import org.eclipse.persistence.jpa.jpql.model.query.StateObjectVisitor;
import org.eclipse.persistence.jpa.jpql.model.query.StringLiteralStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubstringExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SubtractionExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.SumFunctionStateObject;
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
import org.eclipse.persistence.jpa.jpql.parser.GroupByClause;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.InExpression;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.JoinFetch;
import org.eclipse.persistence.jpa.jpql.parser.KeywordExpression;
import org.eclipse.persistence.jpa.jpql.parser.LikeExpression;
import org.eclipse.persistence.jpa.jpql.parser.NotExpression;
import org.eclipse.persistence.jpa.jpql.parser.NullComparisonExpression;
import org.eclipse.persistence.jpa.jpql.parser.OrderByClause;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem;
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem.Ordering;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.ResultVariable;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectClause;
import org.eclipse.persistence.jpa.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.jpql.parser.WhenClause;

import static org.eclipse.persistence.jpa.jpql.model.AbstractJPQLQueryFormatter.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This {@link IJPQLQueryFormatter} is used to output a string representation of a {@link StateObject}
 * based on how it was parsed, which means this formatter can only be used when the {@link StateObject}
 * was created from parsing a JPQL query because it needs to retrieve parsing information from the
 * corresponding {@link Expression}.
 * <p>
 * It is possible to partially match the JPQL query that was parsed, the value of the <em>exactMatch</em>
 * will determine whether the string representation of any given {@link StateObject} should reflect
 * the exact string that was parsed. <code>true</code> will use every bit of information contained
 * in the corresponding {@link Expression} to perfectly match what was parsed; <code>false</code>
 * will only match the case sensitivity of the JPQL identifiers.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractActualJPQLQueryFormatter implements StateObjectVisitor,
                                                                  IJPQLQueryFormatter {

	/**
	 * Determines whether the string representation of any given {@link StateObject} should reflect
	 * the exact string that was parsed: <code>true</code> will use every bit of information
	 * contained in the corresponding {@link Expression} to perfectly match what was parsed;
	 * <code>false</code> will only match the case sensitivity of the JPQL identifiers.
	 */
	protected final boolean exactMatch;

	/**
	 * The holder of the string representation of the JPQL query.
	 */
	protected final StringBuilder writer;

	/**
	 * Creates a new <code>ActualJPQLQueryFormatter</code>.
	 *
	 * @param exactMatch Determines whether the string representation of any given {@link StateObject}
	 * should reflect the exact string that was parsed: <code>true</code> will use every bit of
	 * information contained in the corresponding {@link Expression} to perfectly match what was
	 * parsed; <code>false</code> will only match the case sensitivity of the JPQL identifiers
	 */
	protected AbstractActualJPQLQueryFormatter(boolean exactMatch) {
		super();
		this.exactMatch = exactMatch;
		this.writer     = new StringBuilder();
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

		if (actualIdentifier.length() == 0) {
			actualIdentifier = identifier;
		}

		writer.append(actualIdentifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return writer.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString(StateObject stateObject) {

		if (writer.length() > 0) {
			writer.delete(0, writer.length());
		}

		stateObject.accept(this);

		return toString();
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

	protected void toStringCompound(CompoundExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			CompoundExpression expression = stateObject.getExpression();

			// Left expression
			if (stateObject.hasLeft()) {
				stateObject.getLeft().accept(this);
				writer.append(SPACE);
			}
			else if (expression.hasLeftExpression()) {
				writer.append(expression.getLeftExpression().toActualText());
				writer.append(SPACE);
			}

			// Identifier
			writer.append(expression.getActualIdentifier());

			if (!exactMatch | expression.hasSpaceAfterIdentifier()) {
				writer.append(SPACE);
			}

			// Right expression
			if (stateObject.hasRight()) {
				stateObject.getRight().accept(this);
			}
			else {
				writer.append(expression.getRightExpression().toActualText());
			}
		}
	}

	protected void toStringConditional(AbstractConditionalClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			writer.append(stateObject.getIdentifier());

			if (stateObject.hasConditional()) {
				// TODO: HANDLE SPACE
				writer.append(SPACE);
				stateObject.getConditional().accept(this);
			}
		}
	}

	protected void toStringDoubleEncapsulated(AbstractDoubleEncapsulatedExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			AbstractDoubleEncapsulatedExpression expression = stateObject.getExpression();

			// Identifier
			writer.append(stateObject.getIdentifier());

			// '('
			if (!exactMatch | expression.hasLeftParenthesis()) {
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
			if (!exactMatch | expression.hasComma()) {
				writer.append(COMMA);
			}

			if (!exactMatch | expression.hasSpaceAfterComma()) {
				writer.append(SPACE);
			}

			// Second expression
			if (stateObject.hasSecond()) {
				stateObject.getSecond().accept(this);
			}

			// ')'
			if (!exactMatch | expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	protected void toStringEncapsulatedIdentificationVariable(EncapsulatedIdentificationVariableExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			EncapsulatedIdentificationVariableExpression expression = stateObject.getExpression();

			// Identifier
			writer.append(stateObject.getIdentifier());

			// '('
			if (!exactMatch | expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}

			// Identification variable
			if (stateObject.hasIdentificationVariable()) {
				writer.append(stateObject.getIdentificationVariable());
			}

			// ')'
			if (!exactMatch | expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	protected void toStringFromClause(AbstractFromClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			AbstractFromClause expression = stateObject.getExpression();

			// 'FROM'
			writer.append(expression.getActualIdentifier());

			if (!exactMatch | expression.hasSpaceAfterFrom()) {
				writer.append(SPACE);
			}

			// declaration
			if (stateObject.hasItems()) {
				toStringChildren(stateObject, true);
			}
		}
	}

	private void toStringIdentificationVariableDeclaration(AbstractIdentificationVariableDeclarationStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			IdentificationVariableDeclaration expression = stateObject.getExpression();

			// Range variable declaration
			stateObject.getRangeVariableDeclaration().accept(this);

			// Join | Join Fetch
			if (stateObject.hasItems()) {

				if (!exactMatch | expression.hasSpace()) {
					writer.append(SPACE);
				}

				toStringChildren(stateObject, false);
			}
		}
	}

	protected void toStringModifyStatement(AbstractModifyStatementStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
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
			stateObject.getDecorator().accept(this);
		}
		else {
			stateObject.toText(writer);
		}
	}

	private void toStringRangeVariableDeclaration(AbstractRangeVariableDeclarationStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			RangeVariableDeclaration expression = stateObject.getExpression();

			// "Root" object (abstract schema name or derived declaration)
			stateObject.getRootStateObject().accept(this);

			if (!exactMatch | expression.hasSpaceAfterAbstractSchemaName()) {
				writer.append(SPACE);
			}

			// 'AS'
			if (stateObject.hasAs()) {
				appendIdentifier(expression.getActualAsIdentifier(), AS);

				if (!exactMatch | expression.hasSpaceAfterAs()) {
					writer.append(SPACE);
				}
			}

			// Identification variable
			if (stateObject.hasIdentificationVariable() &&
			   !stateObject.isIdentificationVariableVirtual()) {

				writer.append(stateObject.getIdentificationVariable());
			}
		}
	}

	protected void toStringSelectStatement(AbstractSelectStatementStateObject stateObject,
	                                       boolean useNewLine) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			AbstractSelectStatement expression = stateObject.getExpression();

			// SELECT clause
			stateObject.getSelectClause().accept(this);

			if (!exactMatch | expression.hasSpaceAfterSelect()) {
				writer.append(SPACE);
			}

			// FROM clause
			stateObject.getFromClause().accept(this);

			if (!exactMatch && expression.hasSpaceAfterFrom()) {
				writer.append(SPACE);
			}

			// WHERE clause
			if (stateObject.hasWhereClause()) {
				if (exactMatch) {
					writer.append(SPACE);
				}
				stateObject.getWhereClause().accept(this);
			}

			if (!exactMatch && expression.hasSpaceAfterWhere()) {
				writer.append(SPACE);
			}

			// GROUP BY clause
			if (stateObject.hasGroupByClause()) {
				if (exactMatch) {
					writer.append(SPACE);
				}
				stateObject.getGroupByClause().accept(this);
			}

			if (!exactMatch && expression.hasSpaceAfterGroupBy()) {
				writer.append(SPACE);
			}

			// HAVING clause
			if (stateObject.hasHavingClause()) {
				if (exactMatch) {
					writer.append(SPACE);
				}
				stateObject.getHavingClause().accept(this);
			}
		}
	}

	protected void toStringSimpleStateObject(SimpleStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else if (stateObject.hasText()) {
			writer.append(stateObject.getText());
		}
	}

	protected void toStringSingleEncapsulated(AbstractSingleEncapsulatedExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			AbstractSingleEncapsulatedExpression expression = stateObject.getExpression();

			// Identifier
			writer.append(expression.getActualIdentifier());

			// '('
			if (!exactMatch | expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}

			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}
			else if (expression.hasEncapsulatedExpression()) {
				writer.append(expression.toActualText());
			}

			// ')'
			if (!exactMatch | expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	protected void toStringTripleEncapsulated(AbstractTripleEncapsulatedExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			AbstractTripleEncapsulatedExpression expression = stateObject.getExpression();

			// Identifier
			writer.append(stateObject.getIdentifier());

			// '('
			if (!exactMatch | expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}

			// First expression
			if (stateObject.hasFirst()) {
				stateObject.getFirst().accept(this);
			}

			if (!exactMatch | expression.hasFirstComma()) {
				writer.append(COMMA);
			}

			if (!exactMatch | expression.hasSpaceAfterFirstComma()) {
				writer.append(SPACE);
			}

			// Second expression
			if (stateObject.hasSecond()) {
				stateObject.getSecond().accept(this);
			}

			if (!exactMatch | expression.hasSecondComma()) {
				writer.append(COMMA);
			}

			if (!exactMatch | expression.hasSpaceAfterSecondComma()) {
				writer.append(SPACE);
			}

			// Third expression
			if (stateObject.hasThird()) {
				stateObject.getThird().accept(this);
			}

			// ')'
			if (!exactMatch | expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	/**
	 * Visits the given {@link StateObject} and prevents its decorator to be called, which will
	 * prevent any possible recursion when the decorator is outputting the information.
	 *
	 * @param stateObject The decorated {@link stateObject} to traverse without going through the
	 * decorator
	 */
	protected void toText(StateObject stateObject) {

		if (stateObject.isDecorated()) {

			StateObject decorator = stateObject.getDecorator();
			stateObject.decorate(null);

			try {
				stateObject.accept(this);
			}
			finally {
				stateObject.decorate(decorator);
			}
		}
		else {
			stateObject.accept(this);
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
		toStringCompound(stateObject);
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
		toStringCompound(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ArithmeticFactorStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			writer.append(stateObject.getArithmeticSign());

			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}
			else {
				writer.append(stateObject.getExpression().getExpression().toActualText());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AvgFunctionStateObject stateObject) {
		toStringSingleEncapsulated(stateObject);
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
			stateObject.getDecorator().accept(this);
		}
		else {
			BetweenExpression expression = stateObject.getExpression();

			// Expression
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
				writer.append(SPACE);
			}
			else if (expression.hasExpression()) {
				writer.append(expression.getExpression().toActualText());
				writer.append(SPACE);
			}

			// 'NOT
			if (stateObject.hasNot()) {
				appendIdentifier(expression.getActualNotIdentifier(), NOT);
				writer.append(SPACE);
			}

			// 'BETWEEN'
			writer.append(expression.getActualBetweenIdentifier());

			if (!exactMatch | expression.hasSpaceAfterBetween()) {
				writer.append(SPACE);
			}

			// Lower bound expression
			if (stateObject.hasLowerBound()) {
				stateObject.getLowerBound().accept(this);
			}
			else {
				writer.append(expression.getLowerBoundExpression().toActualText());
			}

			if (!exactMatch | expression.hasSpaceAfterLowerBound()) {
				writer.append(SPACE);
			}

			// 'AND'
			if (!exactMatch | expression.hasAnd()) {
				appendIdentifier(expression.getActualAndIdentifier(), AND);
			}

			if (!exactMatch | expression.hasSpaceAfterAnd()) {
				writer.append(SPACE);
			}

			// Upper bound expression
			if (stateObject.hasUpperBound()) {
				stateObject.getUpperBound().accept(this);
			}
			else {
				writer.append(expression.getUpperBoundExpression().toActualText());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CaseExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			CaseExpression expression = stateObject.getExpression();

			// 'CASE'
			writer.append(expression.getActualCaseIdentifier());

			if (!exactMatch | expression.hasSpaceAfterCase()) {
				writer.append(SPACE);
			}

			// Case operand
			if (stateObject.hasCaseOperand()) {
				stateObject.getCaseOperand().accept(this);
			}
			else if (expression.hasCaseOperand()) {
				writer.append(expression.getCaseOperand().toActualText());
			}

			if (!exactMatch | expression.hasSpaceAfterCaseOperand()) {
				writer.append(SPACE);
			}

			// WHEN clauses
			if (stateObject.hasItems()) {
				toStringChildren(stateObject, false);
			}
			else {
				writer.append(expression.getWhenClauses().toActualText());
			}

			if (!exactMatch | expression.hasSpaceAfterWhenClauses()) {
				writer.append(SPACE);
			}

			// 'ELSE'
			if (!exactMatch | expression.hasElse()) {
				appendIdentifier(expression.getActualElseIdentifier(), ELSE);
			}

			if (!exactMatch | expression.hasSpaceAfterElse()) {
				writer.append(SPACE);
			}

			// Else expression
			if (stateObject.hasElse()) {
				stateObject.getElse().accept(this);
			}
			else {
				writer.append(expression.getElseExpression().toActualText());
			}

			if (!exactMatch | expression.hasSpaceAfterElseExpression()) {
				writer.append(SPACE);
			}

			// END
			if (!exactMatch | expression.hasEnd()) {
				appendIdentifier(expression.getActualEndIdentifier(), END);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CoalesceExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			CoalesceExpression expression = stateObject.getExpression();

			// 'COALESCE'
			writer.append(expression.getActualIdentifier());

			// '('
			if (!exactMatch | expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}
			else if (expression.hasSpaceAfterIdentifier()) {
				writer.append(SPACE);
			}

			toStringChildren(stateObject, true);

			// ')'
			if (!exactMatch | expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberDeclarationStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			CollectionMemberDeclaration expression = stateObject.getExpression();

			// 'IN'
			writer.append(expression.getActualInIdentifier());

			// '('
			if (!stateObject.isDerived()) {
				writer.append(LEFT_PARENTHESIS);
			}
			else if (expression.hasSpaceAfterIn()) {
				writer.append(SPACE);
			}

			// Collection-valued path expression
			stateObject.getCollectionValuedPath().accept(this);

			// ')'
			if (!stateObject.isDerived()) {
				writer.append(RIGHT_PARENTHESIS);
			}
			else if (expression.hasSpaceAfterRightParenthesis()) {
				writer.append(SPACE);
			}

			// 'AS'
			if (stateObject.hasAs()) {
				appendIdentifier(expression.getActualAsIdentifier(), AS);
			}

			if (!exactMatch | expression.hasSpaceAfterAs()) {
				writer.append(SPACE);
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
			stateObject.getDecorator().accept(this);
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
				appendIdentifier(expression.getActualNotIdentifier(), NOT);
				writer.append(SPACE);
			}

			// 'MEMBER'
			writer.append(expression.getActualMemberIdentifier());

			if (!exactMatch | expression.hasSpaceAfterMember()) {
				writer.append(SPACE);
			}

			// 'OF'
			if (stateObject.hasOf()) {
				appendIdentifier(expression.getActualOfIdentifier(), OF);
			}

			if (!exactMatch | expression.hasSpaceAfterOf()) {
				writer.append(SPACE);
			}

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
		toStringCompound(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConcatExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			ConcatExpression expression = stateObject.getExpression();

			// 'CONCAT'
			writer.append(expression.getActualIdentifier());

			// '('
			if (!exactMatch | expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}

			toStringChildren(stateObject, true);

			// ')'
			if (!exactMatch | expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConstructorExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			ConstructorExpression expression = stateObject.getExpression();

			// 'NEW'
			writer.append(expression.getActualIdentifier());

			if (!exactMatch | expression.hasSpaceAfterNew()) {
				writer.append(SPACE);
			}

			// Class name
			writer.append(stateObject.getClassName());

			// '('
			if (!exactMatch | expression.hasLeftParenthesis()) {
				writer.append(LEFT_PARENTHESIS);
			}

			toStringChildren(stateObject, true);

			// ')'
			if (!exactMatch | expression.hasRightParenthesis()) {
				writer.append(RIGHT_PARENTHESIS);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CountFunctionStateObject stateObject) {
		toStringSingleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DateTimeStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			DateTime expression = stateObject.getExpression();
			writer.append(expression.getActualIdentifier());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			DeleteClause expression = stateObject.getExpression();

			// 'DELETE'
			writer.append(expression.getActualDeleteIdentifier());

			if (!exactMatch | expression.hasSpaceAfterDelete()) {
				writer.append(SPACE);
			}

			// 'FROM'
			if (!exactMatch | expression.hasFrom()) {
				writer.append(expression.getActualFromIdentifier());
			}

			if (!exactMatch | expression.hasSpaceAfterFrom()) {
				writer.append(SPACE);
			}

			stateObject.getDeclaration().accept(this);
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
	public void visit(DerivedPathVariableDeclarationStateObject stateObject) {
		toStringRangeVariableDeclaration(stateObject);
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
	public void visit(DivisionExpressionStateObject stateObject) {
		toStringCompound(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EmptyCollectionComparisonExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			EmptyCollectionComparisonExpression expression = stateObject.getExpression();

			stateObject.getStateObject().accept(this);
			writer.append(SPACE);

			// 'IS'
			writer.append(expression.getActualIsIdentifier());
			writer.append(SPACE);

			// 'NOT'
			if (stateObject.hasNot()) {
				appendIdentifier(expression.getActualNotIdentifier(), NOT);
				writer.append(SPACE);
			}

			// 'EMPTY'
			writer.append(expression.getActualEmptyIdentifier());
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
		toStringSingleEncapsulated(stateObject);
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
	public void visit(GroupByClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			GroupByClause expression = stateObject.getExpression();

			// 'GROUP BY'
			writer.append(expression.getActualIdentifier());

			if (!exactMatch | expression.hasSpaceAfterGroupBy()) {
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
		toStringConditional(stateObject);
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
			stateObject.getDecorator().accept(this);
		}
		else {
			InExpression expression = stateObject.getExpression();

			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
				writer.append(SPACE);
			}
			else if (expression.hasExpression()) {
				writer.append(expression.getExpression().toActualText());
				writer.append(SPACE);
			}

			// 'NOT'
			if (stateObject.hasNot()) {
				appendIdentifier(expression.getActualNotIdentifier(), NOT);
				writer.append(SPACE);
			}

			// 'IN'
			writer.append(expression.getActualInIdentifier());

			if (!stateObject.isSingleInputParameter()) {
				writer.append(LEFT_PARENTHESIS);
			}
			else if (!exactMatch | expression.hasSpaceAfterIn()) {
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
	public void visit(JoinFetchStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			JoinFetch expression = stateObject.getExpression();

			// JOIN FETCH
			writer.append(expression.getActualIdentifier());

			if (!exactMatch | expression.hasSpaceAfterFetch()) {
				writer.append(SPACE);
			}

			// Join association path
			stateObject.getJoinAssociationPathStateObject().accept(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JoinStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			Join expression = stateObject.getExpression();

			// JOIN
			writer.append(expression.getActualIdentifier());

			if (!exactMatch | expression.hasSpaceAfterJoin()) {
				writer.append(SPACE);
			}

			// Join association path
			stateObject.getJoinAssociationPathStateObject().accept(this);

			if (!exactMatch | expression.hasSpaceAfterJoinAssociation()) {
				writer.append(SPACE);
			}

			// AS
			if (stateObject.hasAs()) {

				appendIdentifier(expression.getActualAsIdentifier(), AS);

				if (!exactMatch | expression.hasSpaceAfterAs()) {
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
			stateObject.getDecorator().accept(this);
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
			stateObject.getDecorator().accept(this);
		}
		else {
			KeywordExpression expression = stateObject.getExpression();
			writer.append(expression.getActualIdentifier());
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
			stateObject.getDecorator().accept(this);
		}
		else {
			LikeExpression expression = stateObject.getExpression();

			// String expression
			if (stateObject.hasStringStateObject()) {
				stateObject.getStringStateObject().accept(this);
			}
			else {
				writer.append(expression.getStringExpression().toActualText());
			}

			if (!exactMatch | expression.hasSpaceAfterStringExpression()) {
				writer.append(SPACE);
			}

			// 'NOT'
			if (stateObject.hasNot()) {
				appendIdentifier(expression.getActualNotIdentifier(), NOT);
				writer.append(SPACE);
			}

			// 'LIKE'
			writer.append(expression.getActualLikeIdentifier());

			if (!exactMatch | expression.hasSpaceAfterLike()) {
				writer.append(SPACE);
			}

			// Pattern value
			if (stateObject.hasPatternValue()) {
				stateObject.getPatternValue().accept(this);
			}
			else {
				writer.append(expression.getPatternValue().toActualText());
			}

			if (!exactMatch | expression.hasSpaceAfterPatternValue()) {
				writer.append(SPACE);
			}

			// Escape character
			if (stateObject.hasEscapeCharacter()) {
				appendIdentifier(expression.getActualEscapeIdentifier(), ESCAPE);

				if (!exactMatch | expression.hasSpaceAfterEscape()) {
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
		toStringSingleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MinFunctionStateObject stateObject) {
		toStringSingleEncapsulated(stateObject);
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
		toStringCompound(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NotExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			NotExpression expression = stateObject.getExpression();

			// 'NOT'
			writer.append(expression.getActualIdentifier());

			if (!exactMatch | expression.hasSpaceAfterNot()) {
				writer.append(SPACE);
			}

			// Expression
			if (stateObject.hasStateObject()) {
				stateObject.accept(this);
			}
			else {
				writer.append(expression.getExpression().toActualText());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullComparisonExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			NullComparisonExpression expression = stateObject.getExpression();

			// Expression
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
				writer.append(SPACE);
			}
			else if (expression.hasExpression()) {
				writer.append(expression.getExpression().toActualText());
				writer.append(SPACE);
			}

			// 'IS'
			writer.append(expression.getActualIsIdentifier());
			writer.append(SPACE);

			// 'NOT'
			if (stateObject.hasNot()) {
				writer.append(expression.getActualNotIdentifier());
				writer.append(SPACE);
			}

			// 'NULL'
			writer.append(expression.getActualNullIdentifier());
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
			stateObject.getDecorator().accept(this);
		}
		else {
			OrderByClause expression = stateObject.getExpression();

			// 'ORDER BY'
			writer.append(expression.getActualIdentifier());

			if (!exactMatch | expression.hasSpaceAfterOrderBy()) {
				writer.append(SPACE);
			}

			// Order by items
			if (stateObject.hasItems()) {
				toStringChildren(stateObject, true);
			}
			else {
				writer.append(expression.getOrderByItems().toActualText());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrderByItemStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			OrderByItem expression = stateObject.getExpression();

			// Order by item
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}
			else {
				writer.append(expression.getExpression().toActualText());
			}

			if (!exactMatch | expression.hasSpaceAfterExpression()) {
				writer.append(SPACE);
			}

			// ASC/DESC
			if (stateObject.getOrdering() != Ordering.DEFAULT) {
				writer.append(expression.getActualOrdering());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrExpressionStateObject stateObject) {
		toStringCompound(stateObject);
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
			stateObject.getDecorator().accept(this);
		}
		else {
			ResultVariable expression = stateObject.getExpression();

			// Select expression
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}
			else {
				writer.append(expression.getResultVariable().toActualText());
			}

			// 'AS'
			if (stateObject.hasAs()) {
				writer.append(expression.getActualAsIdentifier());
			}

			if (!exactMatch | expression.hasSpaceAfterAs()) {
				writer.append(SPACE);
			}

			// Result variable
			if (stateObject.hasResultVariable()) {
				writer.append(stateObject.getResultVariable());
			}
			else {
				writer.append(expression.getResultVariable().toActualText());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			SelectClause expression = stateObject.getExpression();

			// SELECT
			writer.append(expression.getActualIdentifier());

			if (!exactMatch | expression.hasSpaceAfterSelect()) {
				writer.append(SPACE);
			}

			// DISTINCT
			if (stateObject.hasDistinct()) {
				appendIdentifier(expression.getActualDistinctIdentifier(), DISTINCT);

				if (!exactMatch | expression.hasSpaceAfterDistinct()) {
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

		toStringSelectStatement(stateObject, true);

		SelectStatement expression = stateObject.getExpression();

		if (!exactMatch && expression.hasSpaceBeforeOrderBy()) {
			writer.append(SPACE);
		}

		// ORDER BY clause
		if (stateObject.hasOrderByClause()) {
			if (exactMatch) {
				writer.append(SPACE);
			}
			stateObject.getOrderByClause().accept(this);
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
			stateObject.getDecorator().accept(this);
		}
		else {
			SimpleSelectClause expression = stateObject.getExpression();

			// SELECT
			writer.append(expression.getActualIdentifier());

			if (!exactMatch | expression.hasSpaceAfterSelect()) {
				writer.append(SPACE);
			}

			// DISTINCT
			if (stateObject.hasDistinct()) {
				appendIdentifier(expression.getActualDistinctIdentifier(), DISTINCT);
			}

			if (!exactMatch | expression.hasSpaceAfterDistinct()) {
				writer.append(SPACE);
			}

			// Select expression
			if (stateObject.hasSelectItem()) {
				stateObject.getSelectItem().accept(this);
			}
			else if (exactMatch) {
				writer.append(expression.getSelectExpression().toActualText());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectStatementStateObject stateObject) {
		toStringSelectStatement(stateObject, false);
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
		toStringCompound(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SumFunctionStateObject stateObject) {
		toStringSingleEncapsulated(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TrimExpressionStateObject stateObject) {
		toStringSingleEncapsulated(stateObject);
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
			stateObject.getDecorator().accept(this);
		}
		else {
			UpdateClause expression = stateObject.getExpression();

			// 'UPDATE'
			writer.append(expression.getActualUpdateIdentifier());

			if (!exactMatch | expression.hasSpaceAfterUpdate()) {
				writer.append(SPACE);
			}

			// Range variable declaration
			stateObject.getRangeVariableDeclaration().accept(this);

			if (!exactMatch | expression.hasSpaceAfterRangeVariableDeclaration()) {
				writer.append(SPACE);
			}

			// 'SET'
			if (!exactMatch | expression.hasSet()) {
				appendIdentifier(expression.getActualSetIdentifier(), SET);
			}

			if (!exactMatch | expression.hasSpaceAfterSet()) {
				writer.append(SPACE);
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
			stateObject.getDecorator().accept(this);
		}
		else {
			UpdateItem expression = stateObject.getExpression();

			// Update item
			stateObject.getStateFieldPath().accept(this);

			if (!exactMatch | expression.hasSpaceAfterStateFieldPathExpression()) {
				writer.append(SPACE);
			}

			// '='
			if (!exactMatch | expression.hasEqualSign()) {
				writer.append(EQUAL);
			}

			if (!exactMatch | expression.hasSpaceAfterEqualSign()) {
				writer.append(SPACE);
			}

			// New value
			if (stateObject.hasNewValue()) {
				stateObject.getNewValue().accept(this);
			}
			else {
				writer.append(expression.getNewValue().toActualText());
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
			stateObject.getDecorator().accept(this);
		}
		else {
			WhenClause expression = stateObject.getExpression();

			// 'WHEN'
			writer.append(expression.getActualWhenIdentifier());

			if (!exactMatch | expression.hasSpaceAfterWhen()) {
				writer.append(SPACE);
			}

			// WHEN expression
			if (stateObject.hasConditional()) {
				stateObject.getConditional().accept(this);
			}
			else {
				writer.append(expression.getWhenExpression().toActualText());
			}

			if (!exactMatch | expression.hasSpaceAfterWhenExpression()) {
				writer.append(SPACE);
			}

			// 'THEN'
			if (!exactMatch | expression.hasThen()) {
				appendIdentifier(expression.getActualThenIdentifier(), THEN);
			}

			if (!exactMatch | expression.hasSpaceAfterThen()) {
				writer.append(SPACE);
			}

			// THEN expression
			if (stateObject.hasThen()) {
				stateObject.getThen().accept(this);
			}
			else {
				writer.append(expression.getThenExpression().toActualText());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhereClauseStateObject stateObject) {
		toStringConditional(stateObject);
	}
}