/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.tools;

import org.eclipse.persistence.jpa.jpql.tools.BasicRefactoringTool;
import org.eclipse.persistence.jpa.jpql.tools.DefaultBasicRefactoringTool;
import org.eclipse.persistence.jpa.tests.jpql.UniqueSignature;
import org.junit.Test;

/**
 * The abstract definition of a unit-test that tests {@link org.eclipse.persistence.jpa.jpql.
 * BasicRefactoringTool BasicRefactoringTool} when the JPA version is 2.1.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@UniqueSignature
@SuppressWarnings("nls")
public final class BasicRefactoringToolTest2_1 extends AbstractBasicRefactoringToolTest {

    private BasicRefactoringTool buildRefactoringTool(String jpqlQuery) throws Exception {
        return new DefaultBasicRefactoringTool(jpqlQuery, getGrammar(), getPersistenceUnit());
    }

    @Test
    public void test_RenameClassName_1() throws Exception {

        String jpqlQuery = "SELECT e FROM test.oracle.employee.Employee e";
        BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

        int offset = "SELECT e FROM ".length();
        String oldValue = "test.oracle.employee.Employee";
        String newValue = "oracle.project.Employee";
        refactoringTool.renameClassName(oldValue, newValue);

        String expected = "SELECT e FROM oracle.project.Employee e";
        testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
    }

    @Test
    public void test_RenameEntity_1() throws Exception {

        String jpqlQuery = "Select e fRoM Employee e JoiN TrEaT(e.projects aS LargeProject) lp";
        BasicRefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

        int offset = "Select e fRoM Employee e JoiN TrEaT(e.projects aS ".length();
        String oldValue = "LargeProject";
        String newValue = "SmallProject";
        refactoringTool.renameEntityName(oldValue, newValue);

        String expected = "Select e fRoM Employee e JoiN TrEaT(e.projects aS SmallProject) lp";
        testChange(refactoringTool, jpqlQuery, expected, offset, oldValue, newValue);
    }
}
