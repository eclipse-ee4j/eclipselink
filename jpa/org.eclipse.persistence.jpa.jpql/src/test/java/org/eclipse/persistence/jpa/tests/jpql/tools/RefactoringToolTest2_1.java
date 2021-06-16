/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.tools;

import org.eclipse.persistence.jpa.jpql.tools.DefaultRefactoringTool;
import org.eclipse.persistence.jpa.jpql.tools.RefactoringTool;
import org.eclipse.persistence.jpa.tests.jpql.UniqueSignature;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * The abstract definition of a unit-test that tests {@link org.eclipse.persistence.jpa.jpql.
 * RefactoringTool RefactoringTool} when the JPA version is 2.1.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@UniqueSignature
@SuppressWarnings("nls")
public final class RefactoringToolTest2_1 extends AbstractRefactoringToolTest {

    private RefactoringTool buildRefactoringTool(String jpqlQuery) throws Exception {
        return new DefaultRefactoringTool(getPersistenceUnit(), getJPQLQueryBuilder(), jpqlQuery);
    }

    @Test
    public void test_RenameEntity_1() throws Exception {

        String jpqlQuery = "Select e fRoM Employee e JoiN TrEaT(e.projects aS LargeProject) lp";
        RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
        refactoringTool.renameEntityName("LargeProject", "SmallProject");

        String expected = "Select e fRoM Employee e JoiN TrEaT(e.projects aS SmallProject) lp";
        assertEquals(expected, refactoringTool.toActualText());
    }
}
