/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.EclipseLinkJPQLQueries2_4.*;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

/**
 * This test suite contains a series of unit-tests that test parsing JPQL queries that follows the
 * JPQL grammar defined in JPA 2.0 and the additional support provider by EclipseLink version 2.4.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkJPQLParserTests2_4 extends JPQLParserTest {

    @Test
    public void test_Query_009() {

        // Select e, e2 from Employee e left join Employee e2 on e.address = e2.address

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e"), variable("e2")),
            from("Employee", "e",
                leftJoin(
                    abstractSchemaName("Employee"),
                    variable("e2"),
                    on(path("e.address").equal(path("e2.address")))
                )
            )
        );

        testQuery(query_009(), selectStatement);
    }

    @Test
    public void test_Query_010() {

        // Select avg(sal.salary)
      // from ( Select max(e.salary) salary, e.address.city city
      //        from Employee e
      //        group by e.address.city )
      //      sal

        SelectStatementTester selectStatement = selectStatement(
            select(avg("sal.salary")),
            from(
                identificationVariableDeclaration(
                    rangeVariableDeclaration(
                        sub(
                            subSelectStatement(
                                subSelect(
                                    resultVariable(max("e.salary"), "salary"),
                                    resultVariable(path("e.address.city"), "city")
                                ),
                                subFrom("Employee", "e"),
                                groupBy(path("e.address.city"))
                            )
                        ),
                        variable("sal")
                    )
                )
            )
        );

        testQuery(query_010(), selectStatement);
    }

    @Test
    public void test_Query_011() {

        // Select addr
      // from (Select e from Employee e) a JOIN a.address addr

        SelectStatementTester selectStatement = selectStatement(
            select(variable("addr")),
            from(
                identificationVariableDeclaration(
                    rangeVariableDeclaration(
                        sub(
                            subSelectStatement(
                                subSelect(variable("e")),
                                subFrom("Employee", "e")
                            )
                        ),
                        variable("a")
                    ),
                    join("a.address", "addr")
                )
            )
        );

        testQuery(query_011(), selectStatement);
    }

    @Test
    public void test_Query_012() {

        // Select addr
      // from (Select e from Employee e) as a JOIN a.address addr

        SelectStatementTester selectStatement = selectStatement(
            select(variable("addr")),
            from(
                identificationVariableDeclaration(
                    rangeVariableDeclarationAs(
                        sub(
                            subSelectStatement(
                                subSelect(variable("e")),
                                subFrom("Employee", "e")
                            )
                        ),
                        variable("a")
                    ),
                    join("a.address", "addr")
                )
            )
        );

        testQuery(query_012(), selectStatement);
    }

    @Test
    public void test_Query_013() {

        // Select a from Address a where a.city = 'Ottawa'
        // union Select a2 from Address a2
        // union all Select a2 from Address a2
        // intersect Select a from Address a where a.city = 'Ottawa'
        // except Select a from Address a where a.city = 'Ottawa'

        SelectStatementTester selectStatement = selectStatement(
            select(variable("a")),
            from("Address", "a"),
            where(path("a.city").equal(string("'Ottawa'"))),
            union(
                subSelect(variable("a2")),
                subFrom("Address", "a2")
            ),
            unionAll(
                subSelect(variable("a2")),
                subFrom("Address", "a2")
            ),
            intersect(
                subSelect(variable("a")),
                subFrom("Address", "a"),
                where(path("a.city").equal(string("'Ottawa'")))
            ),
            except(
                subSelect(variable("a")),
                subFrom("Address", "a"),
                where(path("a.city").equal(string("'Ottawa'")))
            )
        );

        testQuery(query_013(), selectStatement);
    }

    @Test
    public void test_Query_015() {

        // Select e from Employee e, TABLE('TENANTS') t where e.tenant = t.TENANT and t.ACTIVE = true

        SelectStatementTester selectStatement = selectStatement(
            select(variable("e")),
            from(
                identificationVariableDeclaration("Employee", "e"),
                tableVariableDeclaration("'TENANTS'", "t")
            ),
            where(
                    path("e.tenant").equal(path("t.TENANT"))
                .and(
                    path("t.ACTIVE").equal(TRUE())
                )
            )
        );

        testQuery(query_015(), selectStatement);
    }

    @Test
    public void test_Query_016() {

        // Update Employee set name='JPQL' WHERE column!='value'

        ExpressionTester updateStatement = updateStatement(
            update(
                "Employee", "this",
                set("{this}.name", string("'JPQL'")), false
            ),
            where(
                virtualVariable("this", "column").notEqual(string("'value'"))
            )
        );

        testQuery(query_016(), updateStatement);
    }

    @Test
    public void test_Query_017() {

        // select DISTINCT NEW com.ca.waae.dbaccess.dao.MetaPropDef(p.id.metaId, p.jilName, p.dbTable, p.dbColumn, p.dbType)
        // FROM UjoMetaProperty p
        // WHERE p.dbTable!='tablename'
        // ORDER BY p.id.metaId

        ExpressionTester selectStatement = selectStatement(
            selectDistinct(
                new_(
                    "com.ca.waae.dbaccess.dao.MetaPropDef",
                    path("p.id.metaId"),
                    path("p.jilName"),
                    path("p.dbTable"),
                    path("p.dbColumn"),
                    path("p.dbType")
                )
            ),
            from("UjoMetaProperty", "p"),
            where(path("p.dbTable").notEqual(string("'tablename'"))),
            orderBy(orderByItem("p.id.metaId"))
        );

        testQuery(query_017(), selectStatement);
    }
}
