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

import org.eclipse.persistence.jpa.jpql.parser.DefaultJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.tools.ContentAssistExtension;
import org.eclipse.persistence.jpa.jpql.tools.DefaultContentAssistProposals;
import org.eclipse.persistence.jpa.jpql.tools.ResultQuery;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * The unit-tests for {@link DefaultContentAssistProposals}.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DefaultContentAssistProposalsTest {

    @Test
    public void test_BuildXmlQuery_01() {

        String jpqlQuery = "SELECT a FROM Employee e";
        int position = "SELECT a".length();
        String proposal = "ABS";

        String expectedJPQLQuery = "SELECT ABS FROM Employee e";
        int expectedPosition = "SELECT ABS".length();

        DefaultContentAssistProposals proposals = buildProposals();
        ResultQuery result = proposals.buildXmlQuery(jpqlQuery, proposal, position, false);

        assertNotNull("ResultQuery cannot be null", result);
        assertEquals (expectedJPQLQuery, result.getQuery());
        assertEquals (expectedPosition,  result.getPosition());
    }

    @Test
    public void test_BuildXmlQuery_02() {

        String jpqlQuery = "SELECT 2 &lt; e.id FROM Employee e";
        int position = "SELECT 2 ".length();
        String proposal = "<>";

        DefaultContentAssistProposals proposals = buildProposals();
        ResultQuery result = proposals.buildXmlQuery(jpqlQuery, proposal, position, false);

        String expectedJPQLQuery = "SELECT 2 &lt;&gt; e.id FROM Employee e";
        int expectedPosition = "SELECT 2 &lt;&gt;".length();

        assertNotNull("ResultQuery cannot be null", result);
        assertEquals (expectedJPQLQuery, result.getQuery());
        assertEquals (expectedPosition,  result.getPosition());
    }

    @Test
    public void test_BuildXmlQuery_03() {

        String jpqlQuery = "SELECT 2 &lt; e.id FROM Employee e";
        int position = "SELECT 2 &".length();
        String proposal = "<>";

        DefaultContentAssistProposals proposals = buildProposals();
        ResultQuery result = proposals.buildXmlQuery(jpqlQuery, proposal, position, false);

        String expectedJPQLQuery = "SELECT 2 &lt;&gt; e.id FROM Employee e";
        int expectedPosition = "SELECT 2 &lt;&gt;".length();

        assertNotNull("ResultQuery cannot be null", result);
        assertEquals (expectedJPQLQuery, result.getQuery());
        assertEquals (expectedPosition,  result.getPosition());
    }

    @Test
    public void test_BuildXmlQuery_04() {

        String jpqlQuery = "SELECT 2 &lt; e.id FROM Employee e";
        int position = "SELECT 2 &l".length();
        String proposal = "<>";

        DefaultContentAssistProposals proposals = buildProposals();
        ResultQuery result = proposals.buildXmlQuery(jpqlQuery, proposal, position, false);

        String expectedJPQLQuery = "SELECT 2 &lt;&gt; e.id FROM Employee e";
        int expectedPosition = "SELECT 2 &lt;&gt;".length();

        assertNotNull("ResultQuery cannot be null", result);
        assertEquals (expectedJPQLQuery, result.getQuery());
        assertEquals (expectedPosition,  result.getPosition());
    }

    @Test
    public void test_BuildXmlQuery_05() {

        String jpqlQuery = "SELECT 2 &lt; e.id FROM Employee e";
        int position = "SELECT 2 &lt".length();
        String proposal = "<>";

        DefaultContentAssistProposals proposals = buildProposals();
        ResultQuery result = proposals.buildXmlQuery(jpqlQuery, proposal, position, false);

        String expectedJPQLQuery = "SELECT 2 &lt;&gt; e.id FROM Employee e";
        int expectedPosition = "SELECT 2 &lt;&gt;".length();

        assertNotNull("ResultQuery cannot be null", result);
        assertEquals (expectedJPQLQuery, result.getQuery());
        assertEquals (expectedPosition,  result.getPosition());
    }

    @Test
    public void test_BuildXmlQuery_06() {

        String jpqlQuery = "SELECT 2 &lt; e.id FROM Employee e";
        int position = "SELECT 2 &lt;".length();
        String proposal = "<>";

        DefaultContentAssistProposals proposals = buildProposals();
        ResultQuery result = proposals.buildXmlQuery(jpqlQuery, proposal, position, false);

        String expectedJPQLQuery = "SELECT 2 &lt;&gt; e.id FROM Employee e";
        int expectedPosition = "SELECT 2 &lt;&gt;".length();

        assertNotNull("ResultQuery cannot be null", result);
        assertEquals (expectedJPQLQuery, result.getQuery());
        assertEquals (expectedPosition,  result.getPosition());
    }

    @Test
    public void test_BuildXmlQuery_07() {

        String jpqlQuery = "SELECT 2 &lt; e.id FROM Employee e WHERE e.name = '&amp;' ";
        int position = jpqlQuery.length();
        String proposal = "AND";

        DefaultContentAssistProposals proposals = buildProposals();
        ResultQuery result = proposals.buildXmlQuery(jpqlQuery, proposal, position, false);

        String expectedJPQLQuery = "SELECT 2 &lt; e.id FROM Employee e WHERE e.name = '&amp;' AND";
        int expectedPosition = expectedJPQLQuery.length();

        assertNotNull("ResultQuery cannot be null", result);
        assertEquals (expectedJPQLQuery, result.getQuery());
        assertEquals (expectedPosition,  result.getPosition());
    }

    @Test
    public void test_BuildXmlQuery_08() {

        String jpqlQuery = "SELECT 2 &lt; &#101;.id FROM Employee &#101; WHERE e.name ";
        int position = jpqlQuery.length();
        String proposal = ">";

        DefaultContentAssistProposals proposals = buildProposals();
        ResultQuery result = proposals.buildXmlQuery(jpqlQuery, proposal, position, false);

        String expectedJPQLQuery = "SELECT 2 &lt; &#101;.id FROM Employee &#101; WHERE e.name &gt;";
        int expectedPosition = expectedJPQLQuery.length();

        assertNotNull("ResultQuery cannot be null", result);
        assertEquals (expectedJPQLQuery, result.getQuery());
        assertEquals (expectedPosition,  result.getPosition());
    }

    private DefaultContentAssistProposals buildProposals() {
        return new DefaultContentAssistProposals(
            DefaultJPQLGrammar.instance(),
            ContentAssistExtension.NULL_HELPER
        );
    }
}
