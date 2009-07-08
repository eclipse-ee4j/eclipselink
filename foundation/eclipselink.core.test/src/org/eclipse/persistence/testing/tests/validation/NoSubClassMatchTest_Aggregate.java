/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.AggregateChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.sessions.DatabaseSession;


//AggregateMapping is an abstract class, therefore a subclass is implemented to assist the testing.
public class NoSubClassMatchTest_Aggregate extends ExceptionTest {
    IntegrityChecker orgIntegrityChecker;
    AggregateChangeRecord changeRecord;
    TestAggregateMapping mapping;

    public NoSubClassMatchTest_Aggregate() {
        setDescription("This tests no subclass match for AggregateMapping (TL-ERROR 126)");
    }

    protected void setup() {
        expectedException = DescriptorException.noSubClassMatch(null, new TestAggregateMapping());

        mapping = new TestAggregateMapping();
        ClassDescriptor descriptor = ((DatabaseSession)getSession()).getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        mapping.setReferenceDescriptor(descriptor);

        ObjectChangeSet changeSet = new ObjectChangeSet(new TestAggregateMapping(), new UnitOfWorkChangeSet(), true);
        changeRecord = new AggregateChangeRecord(changeSet);
        changeRecord.setChangedObject(changeSet);

        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    public void reset() {
        getSession().setIntegrityChecker(orgIntegrityChecker);
    }

    public void test() {
        try {
            mapping.mergeChangesIntoObject(new Object(), changeRecord, new Object(), new MergeManager((AbstractSession)getSession()));
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}
