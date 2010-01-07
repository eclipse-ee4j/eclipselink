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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries.optimization;

import java.util.*;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.*;

/**
 * This class tests the batch reading feature.
 */
public class ReadAllBatchReadingTest extends ReadAllTest {
    public ReadAllBatchReadingTest(int size) {
        super(Employee.class, size);
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        super.setup();
    }

    protected void verify() throws Exception {
        super.verify();

        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.dontMaintainCache();
        query.cascadeAllParts();
        query.setSelectionCriteria(getQuery().getSelectionCriteria());
        Vector normalEmps = (Vector)getSession().executeQuery(query);
        //
        org.eclipse.persistence.internal.queries.ContainerPolicy containerPolicy = getQuery().getContainerPolicy();

        if (getQuery().getContainerPolicy().isCursorPolicy()) {
            if ((normalEmps.size() != ((Vector)this.objectsFromDatabase).size()) || 
                (normalEmps.size() != getOriginalObjectsSize())) {
                throw new TestErrorException("Size of batch result does not match.");
            }

        } else {
            if ((normalEmps.size() != containerPolicy.sizeFor(objectsFromDatabase)) || 
                (normalEmps.size() != getOriginalObjectsSize())) {
                throw new TestErrorException("Size of batch result does not match.");
            }
        }

        Enumeration enumtr = null;
        Object iter = null;
        if (containerPolicy.isCursorPolicy()) {
            enumtr = ((Vector)objectsFromDatabase).elements();
        } else {
            iter = containerPolicy.iteratorFor(objectsFromDatabase);
        }
        int subset = Math.min(3, normalEmps.size());
        for (int index = 0; index < subset; index++) {
            Employee nemployee = (Employee)normalEmps.elementAt(index);
            Employee bemployee = null;
            if (containerPolicy.isCursorPolicy()) {
                bemployee = (Employee)enumtr.nextElement();
            } else {
                bemployee = (Employee)containerPolicy.next(iter, (AbstractSession)getSession());
            }


            bemployee.getAddress();
            bemployee.getManager();
            bemployee.getPhoneNumbers();
            bemployee.getProjects();
            bemployee.getResponsibilitiesList();
            ((AbstractSession)getSession()).compareObjects(bemployee.getAddress(), nemployee.getAddress());

            if (bemployee.getPhoneNumbers().size() != nemployee.getPhoneNumbers().size()) {
                throw new TestErrorException("Phones not match.");
            }
            if (bemployee.getProjects().size() != nemployee.getProjects().size()) {
                throw new TestErrorException("Projects not match.");
            }
            if (bemployee.getResponsibilitiesList().size() != nemployee.getResponsibilitiesList().size()) {
                throw new TestErrorException("Responsibilties list not match.");
            }

            if (!((bemployee.getManager() == null) && (nemployee.getManager() == null))) {
                if ((bemployee.getManager() == null) || (nemployee.getManager() == null)) {
                    throw new TestErrorException("Mgr null not match.");
                } else if (!bemployee.getManager().getFirstName().equals(nemployee.getManager().getFirstName())) {
                    throw new TestErrorException("Mgr name of batch result does not match.");
                }
            }
        }
    }
}
