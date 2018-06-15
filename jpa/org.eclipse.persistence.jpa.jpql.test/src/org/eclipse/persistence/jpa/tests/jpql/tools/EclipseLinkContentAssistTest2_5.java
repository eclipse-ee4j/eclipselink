/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
import java.util.Collections;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.utility.CollectionTools;
import org.eclipse.persistence.jpa.tests.jpql.EclipseLinkVersionTools;
import org.junit.Test;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;

/**
 * This unit-test tests the JPQL content assist at various position within the JPQL query and with
 * complete and incomplete queries and make sure the EclipseLink additional support works correctly.
 *
 * @version 2.5.1
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkContentAssistTest2_5 extends AbstractContentAssistTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> classNames() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> clauses(String afterIdentifier, String beforeIdentifier, boolean subquery) {

        List<String> proposals = super.clauses(afterIdentifier, beforeIdentifier, subquery);

        if (subquery) {
            return proposals;
        }

        if (afterIdentifier == SELECT) {

            if (beforeIdentifier != FROM     &&
                beforeIdentifier != WHERE    &&
                beforeIdentifier != GROUP_BY &&
                beforeIdentifier != HAVING   &&
                beforeIdentifier != ORDER_BY &&
                beforeIdentifier != UNION    &&
                beforeIdentifier != EXCEPT   &&
                beforeIdentifier != INTERSECT) {

                proposals.add(UNION);
                proposals.add(EXCEPT);
                proposals.add(INTERSECT);
            }
        }
        else if (afterIdentifier == FROM) {

            if (beforeIdentifier != WHERE    &&
                beforeIdentifier != GROUP_BY &&
                beforeIdentifier != HAVING   &&
                beforeIdentifier != ORDER_BY &&
                beforeIdentifier != UNION    &&
                beforeIdentifier != EXCEPT   &&
                beforeIdentifier != INTERSECT) {

                proposals.add(UNION);
                proposals.add(EXCEPT);
                proposals.add(INTERSECT);
            }
        }
        else if (afterIdentifier == WHERE) {

            if (beforeIdentifier != GROUP_BY &&
                beforeIdentifier != HAVING   &&
                beforeIdentifier != ORDER_BY &&
                beforeIdentifier != UNION    &&
                beforeIdentifier != EXCEPT   &&
                beforeIdentifier != INTERSECT) {

                proposals.add(UNION);
                proposals.add(EXCEPT);
                proposals.add(INTERSECT);
            }
        }
        else if (afterIdentifier == GROUP_BY) {

            if (beforeIdentifier != HAVING   &&
                beforeIdentifier != ORDER_BY &&
                beforeIdentifier != UNION    &&
                beforeIdentifier != EXCEPT   &&
                beforeIdentifier != INTERSECT) {

                proposals.add(UNION);
                proposals.add(EXCEPT);
                proposals.add(INTERSECT);
            }
        }
        else if (afterIdentifier == HAVING) {

            if (beforeIdentifier != ORDER_BY &&
                beforeIdentifier != UNION    &&
                beforeIdentifier != EXCEPT   &&
                beforeIdentifier != INTERSECT) {

                proposals.add(UNION);
                proposals.add(EXCEPT);
                proposals.add(INTERSECT);
            }
        }
        else if (afterIdentifier == ORDER_BY) {

            if (beforeIdentifier != UNION  &&
                beforeIdentifier != EXCEPT &&
                beforeIdentifier != INTERSECT) {

                proposals.add(UNION);
                proposals.add(EXCEPT);
                proposals.add(INTERSECT);
            }
        }

        return proposals;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> columnNames(String tableName) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> enumConstants() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> enumTypes() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> fromClauseInternalClauses(String afterIdentifier) {

        if (EclipseLinkVersionTools.isNewerThan2_4(getGrammar())) {
            List<String> proposals = new ArrayList<String>();

            if (afterIdentifier == FROM) {
                proposals.addAll(super.fromClauseInternalClauses(FROM));
                proposals.add(START_WITH);
                proposals.add(CONNECT_BY);
                proposals.add(ORDER_SIBLINGS_BY);
                proposals.add(AS_OF);
            }
            else if (afterIdentifier == JOIN) {
                proposals.add(START_WITH);
                proposals.add(CONNECT_BY);
                proposals.add(ORDER_SIBLINGS_BY);
                proposals.add(AS_OF);
            }
            else if (afterIdentifier == START_WITH) {
                proposals.add(CONNECT_BY);
                proposals.add(ORDER_SIBLINGS_BY);
                proposals.add(AS_OF);
            }
            else if (afterIdentifier == CONNECT_BY) {
                proposals.add(ORDER_SIBLINGS_BY);
                proposals.add(AS_OF);
            }
            else if (afterIdentifier == ORDER_SIBLINGS_BY) {
                proposals.add(AS_OF);
            }

            return proposals;
        }

        return super.fromClauseInternalClauses(afterIdentifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isJoinFetchIdentifiable() {
        EclipseLinkVersion currentVersion = EclipseLinkVersion.value(getGrammar().getProviderVersion());
        return currentVersion.isNewerThanOrEqual(EclipseLinkJPQLGrammar2_4.VERSION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> tableNames() {
        return Collections.emptyList();
    }

    @Test
    public void test_AsOfClause_01() {

        String jpqlQuery = "select e from Employee e";
        int position = jpqlQuery.length();
        testDoesNotHaveTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_02() {

        String jpqlQuery = "select e from Employee e ";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_03() {

        String jpqlQuery = "select e from Employee e A";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_04() {

        String jpqlQuery = "select e from Employee e AS";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_05() {

        String jpqlQuery = "select e from Employee e AS ";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_06() {

        String jpqlQuery = "select e from Employee e AS O";
        int position = jpqlQuery.length();
        testHasOnlyTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_07() {

        String jpqlQuery = "select e from Employee e AS OF";
        int position = jpqlQuery.length();
        testHasOnlyTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_08() {

        String jpqlQuery = "select e from Employee e AS OF ";
        int position = jpqlQuery.length();
        testDoesNotHaveTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_10() {

        String jpqlQuery = "select e from Employee e AS OF ";
        int position = "select e from Employee e A".length();
        testHasTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_11() {

        String jpqlQuery = "select e from Employee e AS OF ";
        int position = "select e from Employee e AS".length();
        testHasTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_12() {

        String jpqlQuery = "select e from Employee e AS OF ";
        int position = "select e from Employee e AS ".length();
        testHasTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_13() {

        String jpqlQuery = "select e from Employee e AS OF ";
        int position = "select e from Employee e A".length();
        testHasTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_14() {

        String jpqlQuery = "select e from Employee e AS OF ";
        int position = "select e from Employee e AS".length();
        testHasTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_15() {

        String jpqlQuery = "select e from Employee e AS OF ";
        int position = "select e from Employee e AS ".length();
        testHasTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_16() {

        String jpqlQuery = "select e from Employee e AS OF ";
        int position = "select e from Employee e AS O".length();
        testHasTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_17() {

        String jpqlQuery = "select e from Employee e AS OF ";
        int position = "select e from Employee e AS OF".length();
        testHasTheseProposals(jpqlQuery, position, AS_OF);
    }

    @Test
    public void test_AsOfClause_18() {

        String jpqlQuery = "select e from Employee e AS OF ";
        int position = jpqlQuery.length();

        List<String> proposals = new ArrayList<String>();
        proposals.add(SCN);
        proposals.add(TIMESTAMP);
        proposals.add("e");
        CollectionTools.addAll(proposals, bnfAccessor.scalarExpressionFunctions());

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public void test_AsOfClause_19() {

        String jpqlQuery = "select e from Employee e AS OF SCN ";
        int position = jpqlQuery.length();

        List<String> proposals = new ArrayList<String>();
        proposals.add("e");
        CollectionTools.addAll(proposals, bnfAccessor.scalarExpressionFunctions());

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public void test_AsOfClause_20() {

        String jpqlQuery = "select e from Employee e AS OF SCN";
        int position = jpqlQuery.length();
        testHasOnlyTheseProposals(jpqlQuery, position, SCN, TIMESTAMP);
    }

    @Test
    public void test_AsOfClause_21() {

        String jpqlQuery = "select e from Employee e AS OF TIMESTAMP";
        int position = jpqlQuery.length();
        testHasOnlyTheseProposals(jpqlQuery, position, SCN, TIMESTAMP);
    }

    @Test
    public void test_AsOfClause_22() {

        String jpqlQuery = "select e from Employee e AS OF TIMESTAMP ";
        int position = jpqlQuery.length();

        List<String> proposals = new ArrayList<String>();
        proposals.add("e");
        CollectionTools.addAll(proposals, bnfAccessor.scalarExpressionFunctions());

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public void test_AsOfClause_23() {

        String jpqlQuery = "select e from Employee e AS OF TIMESTAMP e.name";
        int position = "select e from Employee e AS OF ".length();
        testHasOnlyTheseProposals(jpqlQuery, position, SCN, TIMESTAMP);
    }

    @Test
    public void test_AsOfClause_24() {

        String jpqlQuery = "select e from Employee e AS OF TIMESTAMP e.name";
        int position = "select e from Employee e AS OF T".length();
        testHasOnlyTheseProposals(jpqlQuery, position, SCN, TIMESTAMP);
    }

    @Test
    public void test_AsOfClause_25() {

        String jpqlQuery = "select e from Employee e AS OF SCN e.name";
        int position = "select e from Employee e AS OF S".length();
        testHasOnlyTheseProposals(jpqlQuery, position, SCN, TIMESTAMP);
    }

    @Test
    public void test_AsOfClause_26() {

        String jpqlQuery = "select e from Employee e AS OF SCN e.name + WHERE e.name = 'JPQL'";
        int position = "select e from Employee e AS OF SCN e.name + ".length();

        List<String> proposals = new ArrayList<String>();
        proposals.add("e");
        CollectionTools.addAll(proposals, bnfAccessor.scalarExpressionFunctions());

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public void test_ConnectByClause_01() {

        String jpqlQuery = "select e from Employee e ";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_02() {

        String jpqlQuery = "select e from Employee e C";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_03() {

        String jpqlQuery = "select e from Employee e CO";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_04() {

        String jpqlQuery = "select e from Employee e CON";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_05() {

        String jpqlQuery = "select e from Employee e CONN";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_06() {

        String jpqlQuery = "select e from Employee e CONNEC";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_07() {

        String jpqlQuery = "select e from Employee e CONNECT";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_08() {

        String jpqlQuery = "select e from Employee e CONNECT ";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_09() {

        String jpqlQuery = "select e from Employee e CONNECT B";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_10() {

        String jpqlQuery = "select e from Employee e CONNECT BY";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_11() {

        String jpqlQuery = "select e from Employee e CONNECT BY ";
        int position = jpqlQuery.length();

        List<String> proposals = new ArrayList<String>();
        proposals.add("e");
        CollectionTools.addAll(proposals, bnfAccessor.collectionValuedPathExpressionFunctions());

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public void test_ConnectByClause_12() {

        String jpqlQuery = "select e from Employee e CONNECT BY ";
        int position = "select e from Employee e C".length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_13() {

        String jpqlQuery = "select e from Employee e CONNECT BY ";
        int position = "select e from Employee e CO".length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_14() {

        String jpqlQuery = "select e from Employee e CONNECT BY ";
        int position = "select e from Employee e CON".length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_15() {

        String jpqlQuery = "select e from Employee e CONNECT BY ";
        int position = "select e from Employee e CONN".length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_16() {

        String jpqlQuery = "select e from Employee e CONNECT BY ";
        int position = "select e from Employee e CONNE".length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_17() {

        String jpqlQuery = "select e from Employee e CONNECT BY ";
        int position = "select e from Employee e CONNEC".length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_18() {

        String jpqlQuery = "select e from Employee e CONNECT BY ";
        int position = "select e from Employee e CONNECT".length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_19() {

        String jpqlQuery = "select e from Employee e CONNECT BY ";
        int position = "select e from Employee e CONNECT ".length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_20() {

        String jpqlQuery = "select e from Employee e CONNECT BY ";
        int position = "select e from Employee e CONNECT B".length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_21() {

        String jpqlQuery = "select e from Employee e CONNECT BY ";
        int position = "select e from Employee e CONNECT BY".length();
        testHasTheseProposals(jpqlQuery, position, CONNECT_BY);
    }

    @Test
    public void test_ConnectByClause_22() {

        String jpqlQuery = "select e from Employee e CONNECT BY ";
        int position = jpqlQuery.length();

        List<String> proposals = new ArrayList<String>();
        proposals.add("e");
        CollectionTools.addAll(proposals, bnfAccessor.collectionValuedPathExpressionFunctions());

        testHasTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public void test_ConnectByClause_23() {

        String jpqlQuery = "select e from Employee e JOIN e.manager k CONNECT BY k";
        int position = jpqlQuery.length();

        List<String> proposals = new ArrayList<String>();
        proposals.add("k");
        CollectionTools.addAll(proposals, filter(bnfAccessor.collectionValuedPathExpressionFunctions(), "K"));

        testHasTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public void test_HierarchicalQueryClause_01() {

        String jpqlQuery = "select e from Employee e START WITH ";
        int position = jpqlQuery.length();

        List<String> proposals = new ArrayList<String>();
        proposals.add("e");
        CollectionTools.addAll(proposals, bnfAccessor.conditionalExpressionsFunctions());

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public void test_HierarchicalQueryClause_02() {

        String jpqlQuery = "select e from Employee e START WITH C";
        int position = jpqlQuery.length();

        List<String> proposals = new ArrayList<String>();
        CollectionTools.addAll(proposals, filter(bnfAccessor.conditionalExpressionsFunctions(), "C"));

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public void test_HierarchicalQueryClause_03() {

        String jpqlQuery = "select e from Employee e START WITH O";
        int position = jpqlQuery.length();

        List<String> proposals = new ArrayList<String>();
        CollectionTools.addAll(proposals, filter(bnfAccessor.conditionalExpressionsFunctions(), "O"));

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public void test_HierarchicalQueryClause_04() {

        String jpqlQuery = "select e from Employee e CONNECT BY e.name ";
        int position = jpqlQuery.length();

        List<String> proposals = new ArrayList<String>();
        CollectionTools.addAll(proposals, fromClauseInternalClauses(CONNECT_BY));
        CollectionTools.addAll(proposals, clauses(FROM, null, false));

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public void test_HierarchicalQueryClause_05() {

        String jpqlQuery = "select e from Employee e START WITH  CONNECT BY";
        int position = "select e from Employee e START WITH ".length();

        List<String> proposals = new ArrayList<String>();
        proposals.add("e");
        CollectionTools.addAll(proposals, bnfAccessor.conditionalExpressionsFunctions());

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public void test_HierarchicalQueryClause_06() {

        String jpqlQuery = "select e from Employee e CONNECT BY e.name O";
        int position = jpqlQuery.length();

        List<String> proposals = new ArrayList<String>();
        CollectionTools.addAll(proposals, filter(fromClauseInternalClauses(CONNECT_BY), "O"));
        CollectionTools.addAll(proposals, filter(clauses(FROM, null, false), "O"));

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public void test_HierarchicalQueryClause_07() {

        String jpqlQuery = "select e from Employee e CONNECT BY e.name O WHERE e.name = 'JPQL'";
        int position = "select e from Employee e CONNECT BY e.name O".length();
        testHasOnlyTheseProposals(jpqlQuery, position, ORDER_SIBLINGS_BY);
    }

    @Test
    public final void test_Invalid_001() {
        String jpqlQuery = "SELECT e FROM Employee CONNECT BY e.name O";
        int position = "SELECT e FROM Employee CONNECT BY e.name O".length();
        testHasOnlyTheseProposals(jpqlQuery, position, ORDER_SIBLINGS_BY, ORDER_BY);
    }

    @Test
    public final void test_Invalid_002() {
        String jpqlQuery = "SELECT e FROM Employee CONNECT BY e.name O WHERE e.name IS e.name";
        int position = "SELECT e FROM Employee CONNECT BY e.name O WHERE".length();
        testHasOnlyTheseProposals(jpqlQuery, position, WHERE);
    }

    @Test
    public final void test_Invalid_003() {
        String jpqlQuery = "SELECT e FROM Employee CONNECT BY e.name O WHERE e.name IS e.name";
        int position = "SELECT e FROM Employee CONNECT BY e.name O WHERE e.name IS e".length();
        testHasNoProposals(jpqlQuery, position);
    }

    @Test
    public final void test_Invalid_004() {
        String jpqlQuery = "SELECT e FROM Employee CONNECT BY e.name O WHERE e.name IS e.name";
        int position = jpqlQuery.length();
        testHasNoProposals(jpqlQuery, position);
    }

    @Test
    public final void test_Invalid_005() {
        String jpqlQuery = "SELECT e FROM Employee CONNECT BY e.name O WHERE e.name IS e.name IS ";
        int position = jpqlQuery.length();
        testHasNoProposals(jpqlQuery, position);
    }

    @Test
    public final void test_Invalid_006() {
        String jpqlQuery = "SELECT e FROM Employee CONNECT BY e.name O WHERE e.name IS e.name WHERE";
        int position = jpqlQuery.length();
        testHasNoProposals(jpqlQuery, position);
    }

    @Test
    public void test_StartWithClause_01() {

        String jpqlQuery = "select e from Employee e ";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_02() {

        String jpqlQuery = "select e from Employee e S";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_03() {

        String jpqlQuery = "select e from Employee e ST";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_04() {

        String jpqlQuery = "select e from Employee e STA";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_05() {

        String jpqlQuery = "select e from Employee e START";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_06() {

        String jpqlQuery = "select e from Employee e START ";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_07() {

        String jpqlQuery = "select e from Employee e START W";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_08() {

        String jpqlQuery = "select e from Employee e START WI";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_09() {

        String jpqlQuery = "select e from Employee e START WIT";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_10() {

        String jpqlQuery = "select e from Employee e START WITH";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_11() {

        String jpqlQuery = "select e from Employee e START WITH ";
        int position = jpqlQuery.length();

        List<String> proposals = new ArrayList<String>();
        proposals.add("e");
        CollectionTools.addAll(proposals, bnfAccessor.conditionalExpressionsFunctions());

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public void test_StartWithClause_12() {

        String jpqlQuery = "select e from Employee e START WITH ";
        int position = "select e from Employee e S".length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_13() {

        String jpqlQuery = "select e from Employee e START WITH ";
        int position = "select e from Employee e ST".length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_14() {

        String jpqlQuery = "select e from Employee e START WITH ";
        int position = "select e from Employee e STA".length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_15() {

        String jpqlQuery = "select e from Employee e START WITH ";
        int position = "select e from Employee e STAR".length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_16() {

        String jpqlQuery = "select e from Employee e START WITH ";
        int position = "select e from Employee e START".length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_17() {

        String jpqlQuery = "select e from Employee e START WITH ";
        int position = "select e from Employee e START W".length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_18() {

        String jpqlQuery = "select e from Employee e START WITH ";
        int position = "select e from Employee e START WI".length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_19() {

        String jpqlQuery = "select e from Employee e START WITH ";
        int position = "select e from Employee e START WIT".length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_20() {

        String jpqlQuery = "select e from Employee e START WITH ";
        int position = "select e from Employee e START WITH".length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_21() {

        String jpqlQuery = "select e from Employee e START WITH ";
        int position = "select e from Employee e START WITH".length();
        testHasTheseProposals(jpqlQuery, position, START_WITH);
    }

    @Test
    public void test_StartWithClause_22() {

        String jpqlQuery = "select e from Employee e start with e.name + WHERE e.name = 'JPQL'";
        int position = "select e from Employee e start with e.name + ".length();

        List<String> proposals = new ArrayList<String>();
        proposals.add("e");
        CollectionTools.addAll(proposals, bnfAccessor.arithmeticTermFunctions());

        testHasOnlyTheseProposals(jpqlQuery, position, proposals);
    }
}
