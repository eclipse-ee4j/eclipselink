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
import org.eclipse.persistence.jpa.jpql.parser.OrderByItem.Ordering;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * The abstract definition of a {@link IJPQLQueryFormatter}, which converts an {@link StateObject}
 * into its string representation that can be used as a real JPQL query.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractJPQLQueryFormatter extends BaseJPQLQueryFormatter {

	/**
	 * Creates a new <code>AbstractJPQLQueryFormatter</code>.
	 *
	 * @param style Determines how the JPQL identifiers are written out
	 * @exception NullPointerException The IdentifierStyle cannot be <code>null</code>
	 */
	protected AbstractJPQLQueryFormatter(IdentifierStyle style) {
		super(style);
	}

	protected String newLine() {
		return SPACE;
	}

	protected void toStringAggregateFunction(AggregateFunctionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {

			// <identifier>
			writer.append(formatIdentifier(stateObject.getIdentifier()));

			// '('
			writer.append(formatIdentifier(LEFT_PARENTHESIS));

			// 'DISTINCT'
			if (stateObject.hasDistinct()) {
				writer.append(formatIdentifier(DISTINCT));
				writer.append(SPACE);
			}

			// Encapsulated expression
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}

			// ')'
			writer.append(formatIdentifier(RIGHT_PARENTHESIS));
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

	protected void toStringCompound(CompoundExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			if (stateObject.hasLeft()) {
				stateObject.getLeft().accept(this);
				writer.append(SPACE);
			}

			writer.append(formatIdentifier(stateObject.getIdentifier()));

			if (stateObject.hasRight()) {
				writer.append(SPACE);
				stateObject.getRight().accept(this);
			}
		}
	}

	protected void toStringConditional(AbstractConditionalClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			writer.append(formatIdentifier(stateObject.getIdentifier()));

			if (stateObject.hasConditional()) {
				writer.append(SPACE);
				stateObject.getConditional().accept(this);
			}
		}
	}

	protected void toStringDoubleEncapsulated(AbstractDoubleEncapsulatedExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			writer.append(formatIdentifier(stateObject.getIdentifier()));
			writer.append(formatIdentifier(LEFT_PARENTHESIS));

			if (stateObject.hasFirst()) {
				stateObject.getFirst().accept(this);
			}

			writer.append(COMMA);

			if (stateObject.hasSecond()) {
				writer.append(SPACE);
				stateObject.getSecond().accept(this);
			}

			writer.append(formatIdentifier(RIGHT_PARENTHESIS));
		}
	}

	protected void toStringEncapsulatedIdentificationVariable(EncapsulatedIdentificationVariableExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			writer.append(formatIdentifier(stateObject.getIdentifier()));
			writer.append(formatIdentifier(LEFT_PARENTHESIS));

			if (stateObject.hasIdentificationVariable()) {
				writer.append(stateObject.getIdentificationVariable());
			}

			writer.append(formatIdentifier(RIGHT_PARENTHESIS));
		}
	}

	protected void toStringFromClause(AbstractFromClauseStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			writer.append(formatIdentifier(FROM));

			if (stateObject.hasItems()) {
				writer.append(SPACE);
				toStringChildren(stateObject, true);
			}
		}
	}

	protected void toStringIdentificationVariableDeclaration(AbstractIdentificationVariableDeclarationStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			stateObject.getRangeVariableDeclaration().accept(this);

			if (stateObject.hasItems()) {
				writer.append(SPACE);
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
			stateObject.toString(writer);
		}
	}

	protected void toStringRangeVariableDeclaration(AbstractRangeVariableDeclarationStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {

			// Root object
			stateObject.getRootStateObject().accept(this);

			// 'AS'
			if (stateObject.hasAs()) {
				writer.append(SPACE);
				writer.append(formatIdentifier(AS));
			}

			// Identification variable
			if (stateObject.hasIdentificationVariable() &&
			   !stateObject.isIdentificationVariableVirtual()) {

				writer.append(SPACE);
				writer.append(stateObject.getIdentificationVariable());
			}
		}
	}

	protected void toStringSelectStatement(AbstractSelectStatementStateObject stateObject,
	                                       boolean useNewLine) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			stateObject.getSelectClause().accept(this);
			writer.append(useNewLine ? newLine() : SPACE);
			stateObject.getFromClause().accept(this);

			if (stateObject.hasWhereClause()) {
				writer.append(useNewLine ? newLine() : SPACE);
				stateObject.getWhereClause().accept(this);
			}

			if (stateObject.hasGroupByClause()) {
				writer.append(useNewLine ? newLine() : SPACE);
				stateObject.getGroupByClause().accept(this);
			}

			if (stateObject.hasHavingClause()) {
				writer.append(useNewLine ? newLine() : SPACE);
				stateObject.getHavingClause().accept(this);
			}
		}
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
			writer.append(formatIdentifier(stateObject.getIdentifier()));
			writer.append(formatIdentifier(LEFT_PARENTHESIS));

			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}

			writer.append(formatIdentifier(RIGHT_PARENTHESIS));
		}
	}

	protected void toStringTripleEncapsulated(AbstractTripleEncapsulatedExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			writer.append(formatIdentifier(stateObject.getIdentifier()));
			writer.append(formatIdentifier(LEFT_PARENTHESIS));

			if (stateObject.hasFirst()) {
				stateObject.getFirst().accept(this);
			}

			if (stateObject.hasSecond()) {
				writer.append(COMMA);
				writer.append(SPACE);
				stateObject.getSecond().accept(this);
			}

			if (stateObject.hasThird()) {
				writer.append(COMMA);
				writer.append(SPACE);
				stateObject.getThird().accept(this);
			}

			writer.append(formatIdentifier(RIGHT_PARENTHESIS));
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
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
				writer.append(SPACE);
			}

			if (stateObject.hasNot()) {
				writer.append(formatIdentifier(NOT_BETWEEN));
			}
			else {
				writer.append(formatIdentifier(BETWEEN));
			}

			if (stateObject.hasLowerBound()) {
				writer.append(SPACE);
				stateObject.getLowerBound().accept(this);
			}

			writer.append(SPACE);
			writer.append(formatIdentifier(AND));

			if (stateObject.hasUpperBound()) {
				writer.append(SPACE);
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
			writer.append(formatIdentifier(CASE));
			writer.append(SPACE);

			if (stateObject.hasCaseOperand()) {
				stateObject.getCaseOperand().accept(this);
				writer.append(SPACE);
			}

			if (stateObject.hasItems()) {
				toStringChildren(stateObject, false);
				writer.append(SPACE);
			}

			writer.append(formatIdentifier(ELSE));
			writer.append(SPACE);

			if (stateObject.hasElse()) {
				stateObject.getElse().accept(this);
				writer.append(SPACE);
			}

			writer.append(formatIdentifier(END));
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

			writer.append(formatIdentifier(COALESCE));
			writer.append(formatIdentifier(LEFT_PARENTHESIS));

			for (ListIterator<? extends StateObject> iter = stateObject.items(); iter.hasNext(); )
			{
				iter.next().accept(this);

				if (iter.hasNext()) {
					writer.append(COMMA_SPACE);
				}
			}

			writer.append(formatIdentifier(RIGHT_PARENTHESIS));
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

			writer.append(formatIdentifier(IN));

			if (!stateObject.isDerived()) {
				writer.append(formatIdentifier(LEFT_PARENTHESIS));
			}
			else {
				writer.append(SPACE);
			}

			stateObject.getCollectionValuedPath().accept(this);

			if (!stateObject.isDerived()) {
				writer.append(formatIdentifier(RIGHT_PARENTHESIS));
			}

			if (stateObject.hasAs()) {
				writer.append(SPACE);
				writer.append(formatIdentifier(AS));
			}

			if (stateObject.hasIdentificationVariable()) {
				writer.append(SPACE);
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
			if (stateObject.hasEntityStateObject()) {
				stateObject.getEntityStateObject().accept(this);
				writer.append(SPACE);
			}

			if (stateObject.hasNot() && stateObject.hasOf()) {
				writer.append(formatIdentifier(NOT_MEMBER_OF));
			}
			else if (stateObject.hasNot()) {
				writer.append(formatIdentifier(NOT_MEMBER));
			}
			else if (stateObject.hasOf()) {
				writer.append(formatIdentifier(MEMBER_OF));
			}
			else {
				writer.append(formatIdentifier(MEMBER));
			}

			writer.append(SPACE);
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
			toText(stateObject);
		}
		else {

			writer.append(formatIdentifier(CONCAT));
			writer.append(formatIdentifier(LEFT_PARENTHESIS));
			toStringChildren(stateObject, true);
			writer.append(formatIdentifier(RIGHT_PARENTHESIS));
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

			// 'NEW'
			writer.append(formatIdentifier(NEW));
			writer.append(SPACE);

			// Class name
			writer.append(stateObject.getClassName());

			// '('
			writer.append(formatIdentifier(LEFT_PARENTHESIS));

			// Constructor parameters
			toStringChildren(stateObject, true);

			// ')'
			writer.append(formatIdentifier(RIGHT_PARENTHESIS));
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
		else if (stateObject.hasText()) {
			writer.append(formatIdentifier(stateObject.getText()));
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
			writer.append(formatIdentifier(DELETE_FROM));
			writer.append(SPACE);
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
		toStringCompound(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EmptyCollectionComparisonExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			stateObject.getStateObject().accept(this);
			writer.append(SPACE);
			writer.append(formatIdentifier(stateObject.hasNot() ? IS_NOT_EMPTY : IS_EMPTY));
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

			// 'NOT'
			if (stateObject.hasNot()) {
				writer.append(formatIdentifier(NOT));
				writer.append(SPACE);
			}

			// 'EXISTS'
			writer.append(formatIdentifier(stateObject.getIdentifier()));

			// '('
			writer.append(formatIdentifier(LEFT_PARENTHESIS));

			// Subquery
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}

			// ')'
			writer.append(formatIdentifier(RIGHT_PARENTHESIS));
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
			stateObject.getDecorator().accept(this);
		}
		else {
			writer.append(formatIdentifier(stateObject.getIdentifier()));
			writer.append(formatIdentifier(LEFT_PARENTHESIS));

			if (stateObject.hasFunctionName()) {
				writer.append(stateObject.getQuotedFunctionName());

				if (stateObject.hasItems()) {
					writer.append(COMMA_SPACE);
				}
			}

			toStringChildren(stateObject, true);
			writer.append(formatIdentifier(RIGHT_PARENTHESIS));
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

			writer.append(formatIdentifier(GROUP_BY));

			if (stateObject.hasItems()) {
				writer.append(SPACE);
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

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {
			writer.append(formatIdentifier(INDEX));
			writer.append(formatIdentifier(LEFT_PARENTHESIS));
			if (stateObject.hasIdentificationVariable()) {
				writer.append(stateObject.getIdentificationVariable());
			}
			writer.append(formatIdentifier(RIGHT_PARENTHESIS));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {

			// State object
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
				writer.append(SPACE);
			}

			// 'IN'
			writer.append(formatIdentifier(stateObject.hasNot() ? NOT_IN : IN));

			// Input parameter
			if (stateObject.isSingleInputParameter()) {
				writer.append(SPACE);
				toStringChildren(stateObject, false);
			}
			// Encapsulated state object
			else {
				// '('
				writer.append(formatIdentifier(LEFT_PARENTHESIS));

				// Encapsulated state objects
				if (stateObject.hasItems()) {
					toStringChildren(stateObject, true);
				}

				// ')'
				writer.append(formatIdentifier(RIGHT_PARENTHESIS));
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
			writer.append(formatIdentifier(stateObject.getJoinType()));
			writer.append(SPACE);
			stateObject.getJoinAssociationPathStateObject().accept(this);

			if (stateObject.hasAs()) {
				writer.append(formatIdentifier(AS));
			}

			if (stateObject.hasIdentificationVariable()) {
				writer.append(SPACE);
				stateObject.getIdentificationVariableStateObject().accept(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JPQLQueryStateObject stateObject) {
		toText(stateObject.getQueryStatement());
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
		else if (stateObject.hasText()) {
			writer.append(formatIdentifier(stateObject.getText()));
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

			if (stateObject.hasStringStateObject()) {
				stateObject.getStringStateObject().accept(this);
				writer.append(SPACE);
			}

			writer.append(formatIdentifier(stateObject.hasNot() ? NOT_LIKE : LIKE));

			if (stateObject.hasPatternValue()) {
				writer.append(SPACE);
				stateObject.getPatternValue().accept(this);
			}

			if (stateObject.hasEscapeCharacter()) {
				writer.append(SPACE);
				writer.append(formatIdentifier(ESCAPE));
				writer.append(SPACE);
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
		toStringCompound(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NotExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			toText(stateObject);
		}
		else {

			writer.append(formatIdentifier(NOT));

			if (stateObject.hasStateObject()) {
				writer.append(SPACE);
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

			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
				writer.append(SPACE);
			}

			writer.append(formatIdentifier(stateObject.hasNot() ? IS_NOT_NULL : IS_NULL));
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

			writer.append(formatIdentifier(ORDER_BY));

			if (stateObject.hasItems()) {
				writer.append(SPACE);
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

			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}

			if (stateObject.getOrdering() != Ordering.DEFAULT) {
				writer.append(SPACE);
				writer.append(formatIdentifier(stateObject.getOrdering().name()));
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
			toText(stateObject);
		}
		else {

			// Select item
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}

			// 'AS'
			if (stateObject.hasAs()) {
				writer.append(SPACE);
				writer.append(formatIdentifier(AS));
			}

			// Result variable
			if (stateObject.hasResultVariable()) {
				writer.append(SPACE);
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

			writer.append(formatIdentifier(SELECT));

			if (stateObject.hasDistinct()) {
				writer.append(SPACE);
				writer.append(formatIdentifier(DISTINCT));
			}

			if (stateObject.hasItems()) {
				writer.append(SPACE);
				toStringChildren(stateObject, true);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectStatementStateObject stateObject) {

		toStringSelectStatement(stateObject, true);

		if (stateObject.hasOrderByClause()) {
			writer.append(newLine());
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
			toText(stateObject);
		}
		else {

			writer.append(formatIdentifier(SELECT));

			if (stateObject.hasDistinct()) {
				writer.append(SPACE);
				writer.append(formatIdentifier(DISTINCT));
			}

			if (stateObject.hasSelectItem()) {
				writer.append(SPACE);
				stateObject.getSelectItem().accept(this);
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
		toStringAggregateFunction(stateObject);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpressionStateObject stateObject) {

		if (stateObject.isDecorated()) {
			stateObject.getDecorator().accept(this);
		}
		else {
			// TREAT
			writer.append(formatIdentifier(TREAT));

			// (
			writer.append(formatIdentifier(LEFT_PARENTHESIS));

			// Collection-valued path expression
			stateObject.getJoinAssociationPathStateObject().toText(writer);

			writer.append(SPACE);

			// AS
			if (stateObject.hasAs()) {
				writer.append(formatIdentifier(AS));
				writer.append(SPACE);
			}

			// Entity type name
			writer.append(stateObject.getEntityTypeName());

			// )
			writer.append(formatIdentifier(RIGHT_PARENTHESIS));
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

			// 'TRIM'
			writer.append(formatIdentifier(stateObject.getIdentifier()));

			// '('
			writer.append(formatIdentifier(LEFT_PARENTHESIS));

			// Trim specification
			if (stateObject.hasSpecification()) {
				writer.append(formatIdentifier(stateObject.getSpecification().name()));
				writer.append(SPACE);
			}

			// Trim character
			if (stateObject.hasTrimCharacter()) {
				stateObject.getTrimCharacter().accept(this);
				writer.append(SPACE);
			}

			// 'FROM'
			if (stateObject.hasSpecification() ||
			    stateObject.hasTrimCharacter()) {

				writer.append(formatIdentifier(FROM));
				writer.append(SPACE);
			}

			// Encapsulated expression
			if (stateObject.hasStateObject()) {
				stateObject.getStateObject().accept(this);
			}

			// ')'
			writer.append(formatIdentifier(RIGHT_PARENTHESIS));
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

			writer.append(formatIdentifier(UPDATE));
			writer.append(SPACE);
			stateObject.getRangeVariableDeclaration().accept(this);
			writer.append(SPACE);
			writer.append(formatIdentifier(SET));

			if (stateObject.hasItems()) {
				writer.append(SPACE);
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

			stateObject.getStateFieldPath().accept(this);

			writer.append(SPACE);
			writer.append(formatIdentifier(EQUAL));

			if (stateObject.hasNewValue()) {
				writer.append(SPACE);
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

			writer.append(formatIdentifier(WHEN));

			if (stateObject.hasConditional()) {
				writer.append(SPACE);
				stateObject.getConditional().accept(this);
			}

			writer.append(SPACE);
			writer.append(formatIdentifier(THEN));

			if (stateObject.hasThen()) {
				writer.append(SPACE);
				stateObject.getThen().accept(this);
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