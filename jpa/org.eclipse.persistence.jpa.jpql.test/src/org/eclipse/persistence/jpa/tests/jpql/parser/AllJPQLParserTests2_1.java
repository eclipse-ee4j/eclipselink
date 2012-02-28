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

import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_1;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite tests the JPQL queries written for JPA 2.1 and tests them with the JPQL grammar
 * defined for JPA 2.1 as well as with the additional support EclipseLink 2.4 or later offers.
 *
 * @version 2.4
 * @since 2.4
 */
@SuiteClasses({
	JPQLParserTests2_1.class
})
@RunWith(JPQLTestRunner.class)
public final class AllJPQLParserTests2_1 {

	private AllJPQLParserTests2_1() {
		super();
	}

	@JPQLGrammarTestHelper
	static JPQLGrammar[] buildJPQLGrammars() {
		return new JPQLGrammar[] {
			JPQLGrammar2_1.instance(),
			EclipseLinkJPQLGrammar2_4.instance()
		};
	}
}