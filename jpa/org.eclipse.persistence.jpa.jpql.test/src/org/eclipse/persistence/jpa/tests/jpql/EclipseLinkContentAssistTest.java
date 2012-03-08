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
package org.eclipse.persistence.jpa.tests.jpql;

import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.junit.Test;

import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public class EclipseLinkContentAssistTest extends AbstractContentAssistTest {

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isJoinFetchIdentifiable() {
		return getGrammar().getProviderVersion().equals(EclipseLinkJPQLGrammar2_4.VERSION);
	}

	@Test
	public void test_Func_01() {
		test_AbstractSingleEncapsulatedExpression_01(FUNC);
	}

	@Test
	public void test_Func_02() {
		test_AbstractSingleEncapsulatedExpression_02(FUNC);
	}

	@Test
	public void test_Func_03() {
		test_AbstractSingleEncapsulatedExpression_03(FUNC);
	}

	@Test
	public void test_Func_04() {
		test_AbstractSingleEncapsulatedExpression_04(FUNC);
	}

	@Test
	public void test_Func_05() {
		test_AbstractSingleEncapsulatedExpression_05(FUNC);
	}

	@Test
	public void test_Func_06() {
		test_AbstractSingleEncapsulatedExpression_06(FUNC);
	}

	@Test
	public void test_Func_07() {
		test_AbstractSingleEncapsulatedExpression_07(FUNC);
	}

	@Test
	public void test_Func_08() {
		test_AbstractSingleEncapsulatedExpression_08(FUNC);
	}

	@Test
	public void test_Func_09() {
		test_AbstractSingleEncapsulatedExpression_09(FUNC);
	}

	@Test
	public void test_Func_10() {
		test_AbstractSingleEncapsulatedExpression_10(FUNC);
	}
}