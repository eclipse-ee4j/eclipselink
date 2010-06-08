/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     kchen - Feb 22 2008, bug 217745
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.queries.*;

/**
 * For Bug 217745 - Verify DataSourceCall.translateCustomQuery() and translatePureCustomQuery() methods
 * are able to process '#', '?' and single quota properly and translate SQL into correct format.
 * 
 *  @author Kyle Chen
 */
public class CustomQueryStringTranlateValidationTest extends JPQLTestCase {

    public void test() throws Exception {
        //the elements for the EJBQLTestString array are, SqlId,SqlStringWillBeTranslated,expectedResult}
        String[][] EJBQLTestString = 
        {
            {"SQL1",
                "SELECT type 'Category',CHAR_LENGTH('#231 O''connor Street')",
                "SELECT type 'Category',CHAR_LENGTH('#231 O''connor Street')"},
             
               {"SQL2",
                "BEGIN SProc_Insert_PHolders(#ssn, '#occupation', #sex); END;",
                "BEGIN SProc_Insert_PHolders(?, '#occupation', ?); END;"},
               
                {"SQL3",
                 "SELECT employee e WHERE e.id=?1 and e.address='#231 O''connor Street' and e.phone=?3",
                 "SELECT employee e WHERE e.id=? and e.address='#231 O''connor Street' and e.phone=?"},

                {"SQL4",
                  "BEGIN SProc_Insert_PHolders(#ssn, ?1,'#occupation', ?2); END;",
                  "BEGIN SProc_Insert_PHolders(?, ?1,'#occupation', ?2); END;"},

                {"SQL5",
                  "SELECT employee e WHERE e.id='?1' and e.address='#231 O''connor Street' and e.phone='?3'",
                  "SELECT employee e WHERE e.id='?1' and e.address='#231 O''connor Street' and e.phone='?3'"},
                  
                {"SQL6",
                  "SELECT employee e WHERE e.id='#1' and e.address='#2'",
                      "SELECT employee e WHERE e.id='#1' and e.address='#2'"},

                 {"SQL7",
                   "SELECT employee e WHERE e.id='#1 #2'",
                   "SELECT employee e WHERE e.id='#1 #2'"},
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
