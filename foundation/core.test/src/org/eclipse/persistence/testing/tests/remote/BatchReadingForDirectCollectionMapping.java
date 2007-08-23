/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.remote;

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class BatchReadingForDirectCollectionMapping extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {

    public BatchReadingForDirectCollectionMapping() {
        setDescription("BatchReadingForDirectCollectionMapping");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() {
        ReadAllQuery q = new ReadAllQuery(Employee.class);
        q.addBatchReadAttribute("responsibilitiesList");
        try {
            Vector a = (Vector)getSession().executeQuery(q);
            for (Enumeration enumtr = a.elements(); enumtr.hasMoreElements(); ) {
                Employee emp = (Employee)enumtr.nextElement();
                Vector respList = emp.getResponsibilitiesList();
                // System.out.println(respList);
            }
        } catch (Exception e) {
            System.out.println("Allocations NOT read due to exception:" + e);
        }
    }
}
