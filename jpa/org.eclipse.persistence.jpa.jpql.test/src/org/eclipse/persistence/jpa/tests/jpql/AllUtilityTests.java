/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
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
