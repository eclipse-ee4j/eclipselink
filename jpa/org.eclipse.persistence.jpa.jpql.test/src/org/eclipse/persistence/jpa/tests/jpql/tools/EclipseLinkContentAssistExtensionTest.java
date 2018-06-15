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
import org.eclipse.persistence.jpa.jpql.tools.ContentAssistExtension;
import org.eclipse.persistence.jpa.jpql.tools.ContentAssistProposals;
import org.eclipse.persistence.jpa.jpql.tools.ContentAssistProposals.ClassType;
import org.eclipse.persistence.jpa.jpql.tools.ResultQuery;
import org.eclipse.persistence.jpa.jpql.utility.CollectionTools;
import org.junit.Test;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.junit.Assert.*;

/**
 * Unit-test for {@link ContentAssistExtension} when the JPQL grammar is extended with EclipseLink.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkContentAssistExtensionTest extends AbstractContentAssistExtensionTest {


    /**
     * {@inheritDoc}
     */
    @Override
    protected ContentAssistExtension buildContentAssistExtension() {
        return new ContentAssistExtension() {
            public Iterable<String> classNames(String prefix, ClassType type) {
                if (type == ClassType.INSTANTIABLE) {
                    return filter(EclipseLinkContentAssistExtensionTest.this.classNames(), prefix);
                }
                return filter(EclipseLinkContentAssistExtensionTest.this.enumTypes(), prefix);
            }
            public Iterable<String> columnNames(String tableName, String prefix) {
                return filter(EclipseLinkContentAssistExtensionTest.this.columnNames(tableName), prefix);
            }
            public Iterable<String> tableNames(String prefix) {
                return filter(EclipseLinkContentAssistExtensionTest.this.tableNames(), prefix);
            }
        };
    }

    private void test_buildQuery(String jpqlQuery,
                                 int position,
                                 String proposal,
                                 String expectedJpqlQuery,
                                 int expectedPosition,
                                 boolean insert) throws Exception {

        ContentAssistProposals proposals = buildContentAssistProposals(jpqlQuery, position);
        ResultQuery result = proposals.buildQuery(jpqlQuery, proposal, position, insert);
        assertEquals(expectedJpqlQuery, result.getQuery());
        assertEquals(expectedPosition,  result.getPosition());
    }

    @Test
    public void test_buildQuery_001_Insert() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('') EMP";
        int position = "SELECT e FROM Employee e, TABLE(".length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE''') EMP";
        int expectedPosition = "SELECT e FROM Employee e, TABLE('EMPLOYEE'".length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, true);
    }

    @Test
    public void test_buildQuery_001_Override() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('') EMP";
        int position = "SELECT e FROM Employee e, TABLE(".length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP";
        int expectedPosition = "SELECT e FROM Employee e, TABLE('EMPLOYEE'".length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, false);
    }

    @Test
    public void test_buildQuery_002_Insert() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('') EMP";
        int position = "SELECT e FROM Employee e, TABLE('".length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE'') EMP";
        int expectedPosition = "SELECT e FROM Employee e, TABLE('EMPLOYEE'".length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, true);
    }

    @Test
    public void test_buildQuery_002_Override() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('') EMP";
        int position = "SELECT e FROM Employee e, TABLE('".length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP";
        int expectedPosition = "SELECT e FROM Employee e, TABLE('EMPLOYEE'".length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, false);
    }

    @Test
    public void test_buildQuery_003_Insert() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('E";
        int position = jpqlQuery.length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE'";
        int expectedPosition = expectedJpqlQuery.length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, true);
    }

    @Test
    public void test_buildQuery_003_Override() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('E";
        int position = jpqlQuery.length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE'";
        int expectedPosition = expectedJpqlQuery.length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, false);
    }

    @Test
    public void test_buildQuery_004_Insert() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('E";
        int position = "SELECT e FROM Employee e, TABLE('".length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE'E";
        int expectedPosition = "SELECT e FROM Employee e, TABLE('EMPLOYEE'".length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, true);
    }

    @Test
    public void test_buildQuery_004_Override() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('E";
        int position = "SELECT e FROM Employee e, TABLE('".length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE'";
        int expectedPosition = expectedJpqlQuery.length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, false);
    }

    @Test
    public void test_buildQuery_005_Insert() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE() EMP";
        int position = "SELECT e FROM Employee e, TABLE(".length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP";
        int expectedPosition = "SELECT e FROM Employee e, TABLE('EMPLOYEE'".length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, true);
    }

    @Test
    public void test_buildQuery_005_Override() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE() EMP";
        int position = "SELECT e FROM Employee e, TABLE(".length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP";
        int expectedPosition = "SELECT e FROM Employee e, TABLE('EMPLOYEE'".length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, false);
    }

    @Test
    public void test_buildQuery_006_Insert() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP";
        int position = "SELECT e FROM Employee e, TABLE('EMPLOYEE".length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE'') EMP";
        int expectedPosition = "SELECT e FROM Employee e, TABLE('EMPLOYEE'".length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, true);
    }

    @Test
    public void test_buildQuery_006_Override() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP";
        int position = "SELECT e FROM Employee e, TABLE('EMPLOYEE".length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP";
        int expectedPosition = "SELECT e FROM Employee e, TABLE('EMPLOYEE'".length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, false);
    }

    @Test
    public void test_buildQuery_007_Insert() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE(";
        int position = jpqlQuery.length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE'";
        int expectedPosition = expectedJpqlQuery.length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, true);
    }

    @Test
    public void test_buildQuery_007_Override() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE(";
        int position = jpqlQuery.length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE'";
        int expectedPosition = expectedJpqlQuery.length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, false);
    }

    @Test
    public void test_buildQuery_008_Insert() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE(E";
        int position = jpqlQuery.length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE'";
        int expectedPosition = expectedJpqlQuery.length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, true);
    }

    @Test
    public void test_buildQuery_008_Override() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE(E";
        int position = jpqlQuery.length();
        String proposal = "'EMPLOYEE'";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE'";
        int expectedPosition = expectedJpqlQuery.length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, false);
    }

    @Test
    public void test_buildQuery_009_Insert() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP, TABLE('ADDRESS') a WHERE e.name = EMP.LAST_NAME and a.A = 100";
        int position = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP, TABLE('ADDRESS') a WHERE e.name = EMP.LAST_NAME and a.A".length();
        String proposal = "ADDRESS_ID";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP, TABLE('ADDRESS') a WHERE e.name = EMP.LAST_NAME and a.ADDRESS_ID = 100";
        int expectedPosition = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP, TABLE('ADDRESS') a WHERE e.name = EMP.LAST_NAME and a.ADDRESS_ID".length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, true);
    }

    @Test
    public void test_buildQuery_009_Override() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP, TABLE('ADDRESS') a WHERE e.name = EMP.LAST_NAME and a.A = 100";
        int position = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP, TABLE('ADDRESS') a WHERE e.name = EMP.LAST_NAME and a.A".length();
        String proposal = "ADDRESS_ID";

        String expectedJpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP, TABLE('ADDRESS') a WHERE e.name = EMP.LAST_NAME and a.ADDRESS_ID = 100";
        int expectedPosition = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP, TABLE('ADDRESS') a WHERE e.name = EMP.LAST_NAME and a.ADDRESS_ID".length();

        test_buildQuery(jpqlQuery, position, proposal, expectedJpqlQuery, expectedPosition, false);
    }

    @Test
    public void test_classNames_001() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, ";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, classNames());
    }

    @Test
    public void test_classNames_002() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, jpql.query.Employee emp";
        int position = "SELECT e FROM Employee e, jpql.query.Employee".length();
        testHasTheseProposals(jpqlQuery, position, filter(classNames(), "jpql.query.Employee"));
    }

    @Test
    public void test_classNames_003() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, jpql.query.";
        int position = jpqlQuery.length();
        testHasTheseProposals(jpqlQuery, position, filter(classNames(), "jpql.query."));
    }

    @Test
    public void test_classNames_004() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, jpql.query.Employee emp";
        int position = "SELECT e FROM Employee e, jpql.query.".length();
        testHasTheseProposals(jpqlQuery, position, filter(classNames(), "jpql.query."));
    }

    @Test
    public void test_classNames_005() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, jpql.query.Employee emp";
        int position = "SELECT e FROM Employee e, ".length();

        List<String> proposals = new ArrayList<String>();
        proposals.add(IN);
        CollectionTools.addAll(proposals, entityNames());
        CollectionTools.addAll(proposals, classNames());

        testHasTheseProposals(jpqlQuery, position, proposals);
    }

    @Test
    public void test_columnNames_01() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP WHERE EMP.";
        int position = jpqlQuery.length();
        testHasOnlyTheseProposals(jpqlQuery, position, columnNames("EMPLOYEE"));
    }

    @Test
    public void test_columnNames_02() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP WHERE EMP.F";
        int position = jpqlQuery.length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(columnNames("EMPLOYEE"), "F"));
    }

    @Test
    public void test_columnNames_03() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP WHERE EMP.FIRST_NAME";
        int position = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP WHERE EMP.".length();
        testHasOnlyTheseProposals(jpqlQuery, position, columnNames("EMPLOYEE"));
    }

    @Test
    public void test_columnNames_04() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP WHERE EMP.FIRST_NAME";
        int position = jpqlQuery.length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(columnNames("EMPLOYEE"), "FIRST_NAME"));
    }

    @Test
    public void test_columnNames_05() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP WHERE EMP.A";
        int position = jpqlQuery.length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(columnNames("EMPLOYEE"), "A"));
    }

    @Test
    public void test_columnNames_06() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP WHERE e.";
        int position = jpqlQuery.length();
        testDoesNotHaveTheseProposals(jpqlQuery, position, columnNames("EMPLOYEE"));
    }

    @Test
    public void test_columnNames_07() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP, TABLE('ADDRESS') a WHERE e.name = EMP.LAST_NAME and a.A = 100";
        int position = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP, TABLE('ADDRESS') a WHERE e.name = EMP.LAST_NAME and a.".length();
        testDoesNotHaveTheseProposals(jpqlQuery, position, columnNames("EMPLOYEE"));
    }

    @Test
    public void test_columnNames_08() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP, TABLE('ADDRESS') a WHERE e.name = EMP.LAST_NAME and a.A = 100";
        int position = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP, TABLE('ADDRESS') a WHERE e.name = EMP.LAST_NAME and a.A".length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(columnNames("ADDRESS"), "A"));
    }

    @Test
    public void test_tableNames_01() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, ";
        int position = jpqlQuery.length();
        testDoesNotHaveTheseProposals(jpqlQuery, position, tableNames());
    }

    @Test
    public void test_tableNames_02() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE(";
        int position = jpqlQuery.length();
        testHasOnlyTheseProposals(jpqlQuery, position, tableNames());
    }

    @Test
    public void test_tableNames_03() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('";
        int position = jpqlQuery.length();
        testHasOnlyTheseProposals(jpqlQuery, position, tableNames());
    }

    @Test
    public void test_tableNames_04() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('')";
        int position = "SELECT e FROM Employee e, TABLE(".length();
        testHasOnlyTheseProposals(jpqlQuery, position, tableNames());
    }

    @Test
    public void test_tableNames_05() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('')";
        int position = "SELECT e FROM Employee e, TABLE('".length();
        testHasOnlyTheseProposals(jpqlQuery, position, tableNames());
    }

    @Test
    public void test_tableNames_06() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE')";
        int position = "SELECT e FROM Employee e, TABLE(".length();
        testHasOnlyTheseProposals(jpqlQuery, position, tableNames());
    }

    @Test
    public void test_tableNames_07() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE')";
        int position = "SELECT e FROM Employee e, TABLE('".length();
        testHasOnlyTheseProposals(jpqlQuery, position, tableNames());
    }

    @Test
    public void test_tableNames_08() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('E')";
        int position = "SELECT e FROM Employee e, TABLE('E".length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(tableNames(), "E"));
    }

    @Test
    public void test_tableNames_09() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE')";
        int position = "SELECT e FROM Employee e, TABLE('EMPLOYEE".length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(tableNames(), "EMPLOYEE"));
    }

    @Test
    public void test_tableNames_10() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE')";
        int position = "SELECT e FROM Employee e, TABLE('EMPLOYEE'".length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(tableNames(), "EMPLOYEE"));
    }

    @Test
    public void test_tableNames_11() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('E";
        int position = "SELECT e FROM Employee e, TABLE('E".length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(tableNames(), "E"));
    }

    @Test
    public void test_tableNames_12() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('E";
        int position = "SELECT e FROM Employee e, TABLE('".length();
        testHasOnlyTheseProposals(jpqlQuery, position, tableNames());
    }

    @Test
    public void test_tableNames_13() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('E";
        int position = "SELECT e FROM Employee e, TABLE(".length();
        testHasOnlyTheseProposals(jpqlQuery, position, tableNames());
    }

    @Test
    public void test_tableNames_14() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE('EMPLOYEE') EMP";
        int position = "SELECT e FROM Employee e, TABLE('EMPLOYEE'".length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(tableNames(), "EMPLOYEE"));
    }

    @Test
    public void test_tableNames_15() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE() EMP";
        int position = "SELECT e FROM Employee e, TABLE(".length();
        testHasOnlyTheseProposals(jpqlQuery, position, tableNames());
    }

    @Test
    public void test_tableNames_16() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE(EMPLOYEE) EMP";
        int position = "SELECT e FROM Employee e, TABLE(".length();
        testHasOnlyTheseProposals(jpqlQuery, position, tableNames());
    }

    @Test
    public void test_tableNames_17() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, TABLE(EMPLOYEE) EMP";
        int position = "SELECT e FROM Employee e, TABLE(EMPLOYEE".length();
        testHasOnlyTheseProposals(jpqlQuery, position, filter(tableNames(), "EMPLOYEE"));
    }
}
