/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     kchen - Feb 22 2008, bug 217745
//     Dmitry Kornilov - Nov 10, 2014, bug 450818
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.queries.ReadAllQuery;

/**
 * For Bug 217745 - Verify DataSourceCall.translateCustomQuery() and translatePureCustomQuery() methods
 * are able to process '#', '?' and single quota properly and translate SQL into correct format.
 *
 * @author Kyle Chen
 * @author Dmitry Kornilov (test extended to cover Bug 450818)
 */
public class CustomQueryStringTranlateValidationTest extends JPQLTestCase {

    public void test() throws Exception {
        // the elements for the EJBQLTestString array: {SqlId, SqlStringWillBeTranslated, expectedResult}
        String[][] EJBQLTestString = {
            { "SQL1",
              "SELECT type 'Category',CHAR_LENGTH('#231 O''connor Street')",
              "SELECT type 'Category',CHAR_LENGTH('#231 O''connor Street')" },

            { "SQL2",
              "BEGIN SProc_Insert_PHolders(#ssn, '#occupation', #sex); END;",
              "BEGIN SProc_Insert_PHolders(?, '#occupation', ?); END;" },

            { "SQL3",
              "SELECT employee e WHERE e.id=?1 and e.address='#231 O''connor Street' and e.phone=?3",
              "SELECT employee e WHERE e.id=? and e.address='#231 O''connor Street' and e.phone=?" },

            { "SQL4",
              "BEGIN SProc_Insert_PHolders(#ssn, ?1,'#occupation', ?2); END;",
              "BEGIN SProc_Insert_PHolders(?, ?1,'#occupation', ?2); END;" },

            { "SQL5",
              "SELECT employee e WHERE e.id='?1' and e.address='#231 O''connor Street' and e.phone='?3'",
              "SELECT employee e WHERE e.id='?1' and e.address='#231 O''connor Street' and e.phone='?3'" },

            { "SQL6",
              "SELECT employee e WHERE e.id='#1' and e.address='#2'",
              "SELECT employee e WHERE e.id='#1' and e.address='#2'" },

            { "SQL7",
              "SELECT employee e WHERE e.id='#1 #2'",
              "SELECT employee e WHERE e.id='#1 #2'" },

            { "SQL8",
              "SELECT 'Single quotes #' from employee",
              "SELECT 'Single quotes #' from employee" },

            { "SQL9",
              "SELECT \"Double quotes #\" from employee",
              "SELECT \"Double quotes #\" from employee" },

            { "SQL10",
              "SELECT `MySQL quotes #` from employee",
              "SELECT `MySQL quotes #` from employee" },
        };
        verifyCustomQuery(EJBQLTestString);
    }

    /**
     * The method verify if custom query string translate correctly.
     */
    private void verifyCustomQuery(String[][] testDatas) throws Exception{
        ReadAllQuery query = new ReadAllQuery();
        query.setShouldBindAllParameters(false);
        for(String[] testData : testDatas ){
            String sqlID = testData[0];
            String sqlString = testData[1];
            String expectedResult = testData[2];
            query.setSQLString(sqlString);
            query.getCall().translateCustomQuery();
            String translatedString = query.getCall().getQueryString();
            if(!translatedString.equals(expectedResult)){
                throw new Exception("DataSourceCall.translateCustomQuery() translated SQL["+sqlID+"] into incorrect result ["+translatedString+"].");
            }
        }
    }
}
