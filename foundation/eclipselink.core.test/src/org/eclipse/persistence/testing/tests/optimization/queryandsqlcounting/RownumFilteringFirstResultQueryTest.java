/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * This tests Oracle's Rownum filtering feature, using a query with FirstResults set
 */
public class RownumFilteringFirstResultQueryTest extends RownumFilteringQueryTest{
    public RownumFilteringFirstResultQueryTest() {
        super(0,11,1);
    }

    public RownumFilteringFirstResultQueryTest(Class classToQueryOn){
        super(0,11,1);
        this.queryToUse = new ReadAllQuery(classToQueryOn);
    }

    public void test(){
        int totalresults = ((java.util.Vector)getSession().executeQuery(getQuery())).size();
        expectedResultSize = totalresults - firstResult;
        expectedResultSize = expectedResultSize<0 ? 0:expectedResultSize;
        super.test();
    }

    public void verify() {
        if ( resultSize != expectedResultSize){
            throw new TestErrorException("A ReadAllQuery with MaxRows="+maxRows+",FirstResult="+firstResult+" returned "+
                    resultSize+" result(s) when "+expectedResultSize+" result(s) were expected.");
        }
        if ( queryString==null){
            throw new TestErrorException("A ReadAllQuery with MaxRows="+maxRows+",FirstResult="+firstResult+" did not generate an SQL string.");
        }
        int firstSelectIndex = queryString.indexOf("SELECT");
        int lastSelectIndex = queryString.lastIndexOf("SELECT");
        int firstRowNumIndex = queryString.indexOf("ROWNUM");

        if ( (firstSelectIndex == lastSelectIndex) || (firstRowNumIndex ==-1) ){
            throw new TestErrorException("A ReadAllQuery with MaxRows="+maxRows+",FirstResult="+firstResult
                    +" did not generate proper SQL string Using Oracle pagination feature.");
        }
    }
}
