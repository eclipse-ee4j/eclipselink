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

/**
 * The abstract definition of {@link StateObjectVisitor}, which implements all the methods but does
 * nothing. It can be subclassed so that only the required methods are overridden.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractStateObjectVisitor implements StateObjectVisitor {

	/**
	 * {@inheritDoc}
	 */
	public void visit(AbsExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AbstractSchemaNameStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AdditionExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AllOrAnyExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AndExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ArithmeticFactorStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AvgFunctionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BadExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BetweenExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CaseExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CoalesceExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberDeclarationStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionValuedPathExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ComparisonExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConcatExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConstructorExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CountFunctionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DateTimeStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteClauseStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteStatementStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DerivedPathVariableDeclarationStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DerivedPathIdentificationVariableDeclarationStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DivisionExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EmptyCollectionComparisonExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntityTypeLiteralStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntryExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EnumTypeStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ExistsExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FromClauseStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(GroupByClauseStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(HavingClauseStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IdentificationVariableDeclarationStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IdentificationVariableStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IndexExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InputParameterStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JoinFetchStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JoinStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JPQLQueryStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeyExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeywordExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LengthExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LikeExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LocateExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LowerExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MaxFunctionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MinFunctionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ModExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MultiplicationExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NotExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullComparisonExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullIfExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NumericLiteralStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ObjectExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrderByClauseStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrderByItemStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(RangeVariableDeclarationStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ResultVariableStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectClauseStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectStatementStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleFromClauseStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectClauseStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectStatementStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SizeExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SqrtExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StateFieldPathExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StringLiteralStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubstringExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubtractionExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SumFunctionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TrimExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TypeExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UnknownExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateClauseStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateItemStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateStatementStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpperExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ValueExpressionStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhenClauseStateObject stateObject) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhereClauseStateObject stateObject) {
	}
}