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
package org.eclipse.persistence.jpa.tests.jpql.model;

import org.eclipse.persistence.jpa.jpql.model.query.FuncExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.model.query.TreatExpressionStateObject;

import static org.eclipse.persistence.jpa.jpql.parser.AbstractExpression.*;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.junit.Assert.*;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings({"nls", "unused"}) // For the import statement: see bug 330740
public abstract class EclipseLinkJPQLStateObjectTest extends AbstractStateObjectTest {

	public static StateObjectTester stateObject_224() throws Exception {

		// SELECT FUNC('NVL', e.firstName, 'NoFirstName'),
		//        func('NVL', e.lastName,  'NoLastName')
		// FROM Employee e

		return selectStatement(
			select(
				func("NVL", path("e.firstName"), string("'NoFirstName'")),
				func("NVL", path("e.lastName"),  string("'NoLastName'"))
			),
			from("Employee", "e")
		);
	}

	protected static FuncExpressionStateObjectTester func(String functionName,
	                                                      StateObjectTester... funcItems) {

		return new FuncExpressionStateObjectTester(functionName, collection(funcItems));
	}

	protected static FuncExpressionStateObjectTester func(String functionName,
	                                                      StateObjectTester funcItem) {

		return new FuncExpressionStateObjectTester(functionName, funcItem);
	}

	protected static TreatExpressionStateObjectTester treat(StateObjectTester collectionValuedPathExpression,
	                                                        String entityTypeName) {

		return new TreatExpressionStateObjectTester(collectionValuedPathExpression, false, entityTypeName);
	}

	protected static TreatExpressionStateObjectTester treat(String collectionValuedPathExpression,
	                                                        String entityTypeName) {

		return treat(collectionPath(collectionValuedPathExpression), entityTypeName);
	}

	protected static TreatExpressionStateObjectTester treatAs(StateObjectTester collectionValuedPathExpression,
	                                                          String entityTypeName) {

		return new TreatExpressionStateObjectTester(collectionValuedPathExpression, true, entityTypeName);
	}

	protected static TreatExpressionStateObjectTester treatAs(String collectionValuedPathExpression,
	                                                          String entityTypeName) {

		return treatAs(collectionPath(collectionValuedPathExpression), entityTypeName);
	}

	protected static final class FuncExpressionStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester funcItems;
		private String functionName;

		FuncExpressionStateObjectTester(String functionName, StateObjectTester funcItems) {
			super();
			this.functionName = functionName;
			this.funcItems    = funcItems;
		}

		public void test(StateObject stateObject) {
			assertInstance(stateObject, FuncExpressionStateObject.class);

			FuncExpressionStateObject funcExpression = (FuncExpressionStateObject) stateObject;
			assertEquals(functionName, funcExpression.getFunctionName());
			funcItems.test(funcExpression.items());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(functionName);
			if (!funcItems.isNull()) {
				sb.append(COMMA);
				sb.append(SPACE);
				sb.append(funcItems);
			}
			return sb.toString();
		}
	}

	protected static final class TreatExpressionStateObjectTester extends AbstractStateObjectTester {

		private StateObjectTester collectionValuedPathExpression;
		private String entityTypeName;
		private boolean hasAs;

		protected TreatExpressionStateObjectTester(StateObjectTester collectionValuedPathExpression,
		                                           boolean hasAs,
		                                           String entityTypeName) {
			super();
			this.hasAs                          = hasAs;
			this.entityTypeName                 = entityTypeName;
			this.collectionValuedPathExpression = collectionValuedPathExpression;
		}

		public void test(StateObject stateObject) {

			assertTrue(stateObject.isDecorated());
			stateObject = stateObject.getDecorator();

			assertInstance(stateObject, TreatExpressionStateObject.class);

			TreatExpressionStateObject treatExpressionStateObject = (TreatExpressionStateObject) stateObject;
			assertEquals(hasAs,          treatExpressionStateObject.hasAs());
			assertEquals(entityTypeName, treatExpressionStateObject.getEntityTypeName());

			collectionValuedPathExpression.test(treatExpressionStateObject.getJoinAssociationPathStateObject());
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(TREAT);
			sb.append(LEFT_PARENTHESIS);
			sb.append(collectionValuedPathExpression);
			sb.append(SPACE);
			if (hasAs) {
				sb.append(AS);
				sb.append(SPACE);
			}
			sb.append(entityTypeName);
			sb.append(RIGHT_PARENTHESIS);
			return sb.toString();
		}
	}
}