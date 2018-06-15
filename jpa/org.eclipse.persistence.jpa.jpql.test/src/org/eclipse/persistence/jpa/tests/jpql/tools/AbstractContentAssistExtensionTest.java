/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;
import jpql.query.EnumType;
import org.eclipse.persistence.jpa.jpql.tools.ContentAssistProposals;
import org.eclipse.persistence.jpa.jpql.tools.ResultQuery;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.utility.CollectionTools;
import org.junit.Test;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.junit.Assert.*;

/**
 * The unit-tests for {@link ContentAssistExtension}, which is used by {@link
 * org.eclipse.persistence.jpa.jpql.AbstractJPQLQueryHelper#buildContentAssistProposals(int, org.eclipse.persistence.jpa.jpql.ContentAssistExtension)
 * AbstractJPQLQueryHelper.buildContentAssistProposals(int, ContentAssistExtension)}.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractContentAssistExtensionTest extends ContentAssistTest {

    @Test
    public final void test_buildQuery_07() throws Exception {

        String jpqlQuery = "SELECT e, NEW java.lang.String FROM Employee e";
        int position = "SELECT e, NEW ".length();
        String proposal = "java.lang.StringBuilder";

        ContentAssistProposals proposals = buildContentAssistProposals(jpqlQuery, position);
        ResultQuery result = proposals.buildQuery(jpqlQuery, proposal, position, false);

        String expectedJpqlQuery = "SELECT e, NEW java.lang.StringBuilder FROM Employee e";
        int expectedPosition = "SELECT e, NEW java.lang.StringBuilder".length();

        assertEquals(expectedJpqlQuery, result.getQuery());
        assertEquals(expectedPosition,  result.getPosition());
    }

    @Test
    public final void test_buildQuery_08() throws Exception {

        String jpqlQuery = "SELECT e, NEW java.lang FROM Employee e";
        int position = "SELECT e, NEW ".length();
        String proposal = "java.lang.String";

        ContentAssistProposals proposals = buildContentAssistProposals(jpqlQuery, position);
        ResultQuery result = proposals.buildQuery(jpqlQuery, proposal, position, false);

        String expectedJpqlQuery = "SELECT e, NEW java.lang.String FROM Employee e";
        int expectedPosition = "SELECT e, NEW java.lang.String".length();

        assertEquals(expectedJpqlQuery, result.getQuery());
        assertEquals(expectedPosition,  result.getPosition());
    }

    @Test
    public final void test_classNames_01() throws Exception {

        String jpqlQuery = "SELECT e, NEW  FROM Employee e";
        int position = "SELECT e, NEW ".length();
        testHasOnlyTheseProposals(jpqlQuery, position, classNames());
    }

    @Test
    public final void test_classNames_02() throws Exception {

        String jpqlQuery = "SELECT e, NEW  FROM Employee e";
        int position = "SELECT e,".length();
        testDoesNotHaveTheseProposals(jpqlQuery, position, classNames());
    }

    @Test
    public final void test_classNames_03() throws Exception {

        String jpqlQuery = "SELECT e, NEW  FROM Employee e";
        int position = "SELECT e, ".length();
        testDoesNotHaveTheseProposals(jpqlQuery, position, classNames());
    }

    @Test
    public final void test_classNames_04() throws Exception {

        String jpqlQuery = "SELECT e, NEW java.lang. FROM Employee e";
        int position = "SELECT e, NEW java.lang.".length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(classNames(), "java.lang."));
    }

    @Test
    public final void test_classNames_05() throws Exception {

        String jpqlQuery = "SELECT e, NEW java.lang.String FROM Employee e";
        int position = "SELECT e, NEW java.lang.String".length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(classNames(), "java.lang.String"));
    }

    @Test
    public final void test_classNames_06() throws Exception {

        String jpqlQuery = "SELECT e, NEW java.lang.String FROM Employee e";
        int position = "SELECT e, NEW".length();
        testHasOnlyTheseProposals(jpqlQuery, position, NEW);
    }

    @Test
    public final void test_classNames_07() throws Exception {

        String jpqlQuery = "SELECT e, NEW java.lang.String FROM Employee e";
        int position = "SELECT e, NEW ".length();
        testHasOnlyTheseProposals(jpqlQuery, position, classNames());
    }

    @Test
    public final void test_classNames_08() throws Exception {

        String jpqlQuery = "SELECT e, NEW java.lang FROM Employee e";
        int position = "SELECT e, NEW java.lang".length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(classNames(), "java.lang"));
    }

    @Test
    public final void test_classNames_09() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.status = jpql.query.EnumType.FIRST_NAME";
        int position = "SELECT e FROM Employee e WHERE e.status = jpql.query.".length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(enumTypes(), "jpql.query."));
    }

    @Test
    public final void test_classNames_10() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.status = jpql.query.EnumType.FIRST_NAME";
        int position = "SELECT e FROM Employee e WHERE e.status = jpql.query.E".length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(enumTypes(), "jpql.query.E"));
    }

    @Test
    public final void test_enumConstants_01() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.status = jpql.query.EnumType.";
        int position = jpqlQuery.length();
        testHasOnlyTheseProposals(jpqlQuery, position, enumConstants());
    }

    @Test
    public final void test_enumConstants_02() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.status = jpql.query.EnumType.F";
        int position = jpqlQuery.length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(enumConstants(), "F"));
    }

    @Test
    public final void test_enumConstants_03() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.status = jpql.query.EnumType.FIRST_NAME";
        int position = jpqlQuery.length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(enumConstants(), EnumType.FIRST_NAME.name()));
    }

    @Test
    public final void test_enumConstants_04() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.status = jpql.query.EnumType.FIRST_NAME";
        int position = "SELECT e FROM Employee e WHERE e.status = jpql.query.EnumType.".length();
        testHasOnlyTheseProposals(jpqlQuery, position, enumConstants());
    }

    @Test
    public final void test_enumConstants_05() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.status = jpql.query.EnumType.F";
        int position = "SELECT e FROM Employee e WHERE e.status = jpql.query.EnumType.".length();
        testHasOnlyTheseProposals(jpqlQuery, position, enumConstants());
    }

    @Test
    public final void test_enumConstants_06() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE CASE e.status WHEN ";
        int position = jpqlQuery.length();

        List<String> proposals = new ArrayList<String>();
        proposals.add("e");
        CollectionTools.addAll(proposals, bnfAccessor.conditionalExpressionsFunctions());

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public final void test_enumConstants_07() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE CASE e.status WHEN jpql.query.EnumType.";
        int position = jpqlQuery.length();

        IType type = getPersistenceUnit().getTypeRepository().getType(EnumType.class.getName());
        List<String> proposals = new ArrayList<String>();
        CollectionTools.addAll(proposals, type.getEnumConstants());

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public final void test_enumConstants_08() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE CASE e.status WHEN jpql.query.EnumType.FIRST_NAME THEN ";
        int position = jpqlQuery.length();

        List<String> proposals = new ArrayList<String>();
        proposals.add("e");
        CollectionTools.addAll(proposals, bnfAccessor.scalarExpressionFunctions());

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public final void test_enumConstants_09() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE CASE e.status WHEN jpql.query.EnumType.FIRST_NAME THEN 'JPQL' ELSE ";
        int position = jpqlQuery.length();

        List<String> proposals = new ArrayList<String>();
        proposals.add("e");
        CollectionTools.addAll(proposals, bnfAccessor.scalarExpressionFunctions());

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }
}
