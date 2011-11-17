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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.parser.AbstractEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.AbstractSingleEncapsulatedExpression;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.FuncExpression;
import org.eclipse.persistence.jpa.jpql.parser.TreatExpression;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.junit.Assert.*;

/**
 * The abstract definition of a unit-test used to test the parser itself and the test methods for
 * EclipseLink specific extension.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("unused" /* For the extra import statement, see bug 330740 */)
public abstract class EclipseLinkJPQLParserTest extends JPQLParserTest {

	protected static FuncExpressionTester func(String functionName, ExpressionTester... funcItems) {
		return new FuncExpressionTester(functionName, collection(funcItems));
	}

	protected static FuncExpressionTester func(String functionName, ExpressionTester funcItem) {
		return new FuncExpressionTester(functionName, funcItem);
	}

	protected static TreatExpressionTester treat(ExpressionTester collectionValuedPathExpression,
	                                             ExpressionTester entityTypeName) {

		return new TreatExpressionTester(collectionValuedPathExpression, false, entityTypeName);
	}

	protected static TreatExpressionTester treat(String collectionValuedPathExpression,
	                                             String entityTypeName) {

		return treat(collectionPath(collectionValuedPathExpression), entity(entityTypeName));
	}

	protected static TreatExpressionTester treatAs(ExpressionTester collectionValuedPathExpression,
	                                               ExpressionTester entityTypeName) {

		return new TreatExpressionTester(collectionValuedPathExpression, true, entityTypeName);
	}

	protected static TreatExpressionTester treatAs(String collectionValuedPathExpression,
	                                               String entityTypeName) {

		return treatAs(collectionPath(collectionValuedPathExpression), entity(entityTypeName));
	}

	protected static final class FuncExpressionTester extends AbstractSingleEncapsulatedExpressionTester {

		private String functionName;
		boolean hasComma;
		boolean hasSpaceAfterComma;

		FuncExpressionTester(String functionName, ExpressionTester funcItems) {
			super(funcItems);
			this.functionName = functionName;
			this.hasSpaceAfterComma = true;
			this.hasComma = true;
		}

		@Override
		protected Class<? extends AbstractSingleEncapsulatedExpression> expressionType() {
			return FuncExpression.class;
		}

		@Override
		protected String identifier() {
			return FUNC;
		}

		@Override
		public void test(Expression expression) {
			super.test(expression);

			FuncExpression funcExpression = (FuncExpression) expression;
			assertEquals(functionName,       funcExpression.getFunctionName());
			assertEquals(hasComma,           funcExpression.hasComma());
			assertEquals(hasSpaceAfterComma, funcExpression.hasSpaceAFterComma());
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			if (functionName != null) {
				sb.append(functionName);
			}
			if (hasComma) {
				sb.append(COMMA);
			}
			if (hasSpaceAfterComma) {
				sb.append(SPACE);
			}
			super.toStringEncapsulatedExpression(sb);
		}
	}

	protected static final class TreatExpressionTester extends AbstractEncapsulatedExpressionTester {

		private ExpressionTester collectionValuedPathExpression;
		private ExpressionTester entityTypeName;
		private boolean hasAs;
		public boolean hasSpaceAfterAs;
		public boolean hasSpaceAfterCollectionValuedPathExpression;

		protected TreatExpressionTester(ExpressionTester collectionValuedPathExpression,
		                                boolean hasAs,
		                                ExpressionTester entityTypeName) {
			super();
			this.hasAs           = hasAs;
			this.entityTypeName  = entityTypeName;
			this.hasSpaceAfterAs = hasAs;
			this.hasSpaceAfterCollectionValuedPathExpression = true;
			this.collectionValuedPathExpression              = collectionValuedPathExpression;
		}

		@Override
		protected Class<? extends AbstractEncapsulatedExpression> expressionType() {
			return TreatExpression.class;
		}

		@Override
		protected boolean hasEncapsulatedExpression() {
			return !collectionValuedPathExpression.isNull() || !entityTypeName.isNull() || hasAs;
		}

		@Override
		protected String identifier() {
			return TREAT;
		}

		@Override
		protected void toStringEncapsulatedExpression(StringBuilder sb) {
			sb.append(collectionValuedPathExpression);
			if (hasSpaceAfterCollectionValuedPathExpression) {
				sb.append(SPACE);
			}
			if (hasAs) {
				sb.append(AS);
			}
			if (hasSpaceAfterAs) {
				sb.append(SPACE);
			}
			sb.append(entityTypeName);
		}
	}
}