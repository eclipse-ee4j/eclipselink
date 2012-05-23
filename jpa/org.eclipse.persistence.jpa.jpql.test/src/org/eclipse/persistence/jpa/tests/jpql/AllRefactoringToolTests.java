/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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