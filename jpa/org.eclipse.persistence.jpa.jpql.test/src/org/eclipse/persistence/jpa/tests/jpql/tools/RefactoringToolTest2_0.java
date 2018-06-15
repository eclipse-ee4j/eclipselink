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

import org.eclipse.persistence.jpa.jpql.tools.DefaultRefactoringTool;
import org.eclipse.persistence.jpa.jpql.tools.RefactoringTool;
import org.eclipse.persistence.jpa.tests.jpql.UniqueSignature;
import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries2_0.*;
import static org.junit.Assert.*;

/**
 * The abstract definition of a unit-test that tests {@link org.eclipse.persistence.jpa.jpql.
 * RefactoringTool RefactoringTool} when the JPA version is 2.0.
 *
 * @version 2.6
 * @since 2.4
 * @author Pascal Filion
 */
@UniqueSignature
@SuppressWarnings("nls")
public final class RefactoringToolTest2_0 extends AbstractRefactoringToolTest {

    private RefactoringTool buildRefactoringTool(String jpqlQuery) throws Exception {
        return new DefaultRefactoringTool(getPersistenceUnit(), getJPQLQueryBuilder(), jpqlQuery);
    }

    @Test
    public final void test_RenameEntityName_1() throws Exception {

        // SELECT TYPE(employee) FROM Employee employee WHERE TYPE(employee) <> Exempt
        String jpqlQuery = query_007();
        RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
        refactoringTool.renameEntityName("Exempt", "Exemption");

        String expected = "SELECT TYPE(employee) FROM Employee employee WHERE TYPE(employee) <> Exemption";
        assertEquals(expected, refactoringTool.toActualText());
    }

    @Test
    public final void test_RenameEntityName_2() throws Exception {

        // SELECT e.name,
        //        CASE TYPE(e) WHEN Exempt THEN 'Exempt'
        //                     WHEN Contractor THEN 'Contractor'
        //                     WHEN Intern THEN 'Intern'
        //                     ELSE 'NonExempt'
        //        END
        // FROM Employee e, Contractor c
        // WHERE e.dept.name = 'Engineering'

        String jpqlQuery = query_002();
        RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);

        refactoringTool.renameEntityName("Exempt", "Exemption");
        String expected = "SELECT e.name, CASE TYPE(e) WHEN Exemption THEN 'Exempt' WHEN Contractor THEN 'Contractor' WHEN Intern THEN 'Intern' ELSE 'NonExempt' END FROM Employee e, Contractor c WHERE e.dept.name = 'Engineering'";
        assertEquals(expected, refactoringTool.toActualText());

        refactoringTool.renameEntityName("Contractor", "Manager");
        expected = "SELECT e.name, CASE TYPE(e) WHEN Exemption THEN 'Exempt' WHEN Manager THEN 'Contractor' WHEN Intern THEN 'Intern' ELSE 'NonExempt' END FROM Employee e, Manager c WHERE e.dept.name = 'Engineering'";
        assertEquals(expected, refactoringTool.toActualText());
    }

    @Test
    public final void test_RenameEntityName_3() throws Exception {

        // SELECT e.dept, e.empId, e.roomNumber, e.salary, UPPER(e.name) AS name_order FROM employee:Employee e WHERE e.name LIKE 'myArtifactWith%' ORDER BY name_order ASC
        String jpqlQuery = query_017();
        RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
        refactoringTool.renameEntityName("employee:Employee", "employee_Employee_v1");

        String expected = "SELECT e.dept, e.empId, e.roomNumber, e.salary, UPPER(e.name) AS name_order FROM employee_Employee_v1 e WHERE e.name LIKE 'myArtifactWith%' ORDER BY name_order ASC";
        assertEquals(expected, refactoringTool.toActualText());
    }

    @Test
    public final void test_RenameEntityName_4() throws Exception {

        // SELECT a.name, a.UUID, a.typeUUID AS assetTypeUUID, p.name AS projectName, ap.usageType FROM Asset a, UsedAssetUsingProject ap, Project p WHERE a.UUID = ap.usedAsset AND ap.usingProject = p.UUID
        String jpqlQuery = query_018();
        RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
        refactoringTool.renameEntityName("Asset",                 "governance_Asset_v1");
        refactoringTool.renameEntityName("UsedAssetUsingProject", "governance_UsedAssetUsingProject_v1");
        refactoringTool.renameEntityName("Project",               "governance_Project_v1");

        String expected = "SELECT a.name, a.UUID, a.typeUUID AS assetTypeUUID, p.name AS projectName, ap.usageType FROM governance_Asset_v1 a, governance_UsedAssetUsingProject_v1 ap, governance_Project_v1 p WHERE a.UUID = ap.usedAsset AND ap.usingProject = p.UUID";
        assertEquals(expected, refactoringTool.toActualText());
    }

    @Test
    public final void test_RenameResultVariable_1() throws Exception {

        String jpqlQuery = "SELECT NEW java.util.Vector(a.employees) AS u FROM Address A";
        RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
        refactoringTool.renameResultVariable("u", "v");

        String expected = "SELECT NEW java.util.Vector(a.employees) AS v FROM Address A";
        assertEquals(expected, refactoringTool.toActualText());
    }

    @Test
    public final void test_RenameResultVariable_2() throws Exception {

        String jpqlQuery = "SELECT e.name AS n FROM Employee e";
        RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
        refactoringTool.renameResultVariable("u", "v");

        assertEquals(jpqlQuery, refactoringTool.toActualText());
    }

    @Test
    public final void test_RenameResultVariable_3() throws Exception {

        String jpqlQuery = "SELECT e.name AS n, 2 + 2 o FROM Address A";
        RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
        refactoringTool.renameResultVariable("o", "value");

        String expected = "SELECT e.name AS n, 2 + 2 value FROM Address A";
        assertEquals(expected, refactoringTool.toActualText());
    }

    @Test
    public final void test_RenameResultVariable_4() throws Exception {

        String jpqlQuery = "SELECT e.name AS n, e.age a FROM Address A ORDER BY n";
        RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
        refactoringTool.renameResultVariable("n", "name");

        String expected = "SELECT e.name AS name, e.age a FROM Address A ORDER BY name";
        assertEquals(expected, refactoringTool.toActualText());
    }

    @Test
    public final void test_RenameResultVariable_5() throws Exception {

        String jpqlQuery = "SELECT e.name AS n, e.age a FROM Address A ORDER BY n, a";
        RefactoringTool refactoringTool = buildRefactoringTool(jpqlQuery);
        refactoringTool.renameResultVariable("n", "name");
        refactoringTool.renameResultVariable("a", "age");

        String expected = "SELECT e.name AS name, e.age age FROM Address A ORDER BY name, age";
        assertEquals(expected, refactoringTool.toActualText());
    }
}
