/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.tools;

import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * The root test suite containing the unit-tests testing the refactoring functionality.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
    AllRefactoringToolTest1_0.class,
    AllRefactoringToolTest2_0.class,
    AllRefactoringToolTest2_1.class,

    AllBasicRefactoringToolTest1_0.class,
    AllBasicRefactoringToolTest2_0.class,
    AllBasicRefactoringToolTest2_1.class,
})
@RunWith(JPQLTestRunner.class)
public final class AllRefactoringToolTests {

    private AllRefactoringToolTests() {
        super();
    }
}
