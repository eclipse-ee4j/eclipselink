/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting;

import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * This tests Oracle's Rownum filtering feature, using a query with FirstResults set
 */
public class RownumFilteringFirstResultQueryTest extends RownumFilteringQueryTest{
    public RownumFilteringFirstResultQueryTest() {
        super(0,11,1);
    }
    
    public void verify() {
        if ( resultSize != expectedResultSize){
            throw new TestErrorException("A ReadAllQuery with MaxRows=2,FirstResult=1 returned "+
                resultSize+" result(s) when 1 was expected.");
        }
        if ( queryString==null){
            throw new TestErrorException("A ReadAllQuery with MaxRows=2,FirstResult=1 did not generate an SQL string.");
        }
        int firstSelectIndex = queryString.indexOf("SELECT");
        int lastSelectIndex = queryString.lastIndexOf("SELECT");
        int firstRowNumIndex = queryString.indexOf("ROWNUM");
        
        if ( (firstSelectIndex == lastSelectIndex) || (firstRowNumIndex ==-1) ){
            throw new TestErrorException("A ReadAllQuery with MaxRows=2,FirstResult=1 did not generate proper SQL "+
                "string Using Oracle pagination feature.");
        }
    }
}
