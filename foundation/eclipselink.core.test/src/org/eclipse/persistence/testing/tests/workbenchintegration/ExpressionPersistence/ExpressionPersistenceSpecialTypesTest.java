/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.workbenchintegration.ExpressionPersistence;

import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.testing.models.conversion.ConversionDataObject;


//Test some special types in expression comparison
public class ExpressionPersistenceSpecialTypesTest extends ExpressionPersistenceTest {

    public ExpressionPersistenceSpecialTypesTest(String queryName, DatabaseQuery query) {
        super(queryName, query);
        setDescription("Test that expressions persisted by the WorkBench in the deployent XML works correctly for some special java types");
    }

    public void test() {
        getSession().executeQuery(basicQuery);
        systemQuery = 
                (getSession().getDescriptor(ConversionDataObject.class)).getQueryManager().getQuery(queryName);
        getSession().executeQuery(systemQuery);
    }
}
