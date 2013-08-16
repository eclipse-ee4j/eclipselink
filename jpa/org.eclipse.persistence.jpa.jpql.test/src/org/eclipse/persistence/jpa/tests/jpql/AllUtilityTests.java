/*******************************************************************************
 * Copyright (c) 2006, 2013 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.tests.jpql.parser.WordParserTest;
import org.eclipse.persistence.jpa.tests.jpql.tools.DefaultContentAssistProposalsTest;
import org.eclipse.persistence.jpa.tests.jpql.tools.utility.XmlEscapeCharacterConverterTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@SuiteClasses({
	WordParserTest.class,
	ExpressionToolsTest.class,
	DefaultContentAssistProposalsTest.class,
	XmlEscapeCharacterConverterTest.class
})
@RunWith(JPQLTestRunner.class)
public final class AllUtilityTests {

	private AllUtilityTests() {
		super();
	}
}