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

import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite tests the JPQL queries written for JPA 2.1 and tests them with the JPQL grammar
 * defined for JPA 2.1 as well as with the additional support EclipseLink 2.4 or later offers.
 *
 * @version 2.4
 * @since 2.4
 */
@SuiteClasses
({
	// Test the parser with hundreds of JPQL queries
//	JPQLQueriesTest2_1.class,

	// Individual unit-tests
	OnClauseTest.class,
})
public final class JPQLParserTests2_1 {

	private JPQLParserTests2_1() {
		super();
	}
}