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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.sequencing.SequencingControl;
import org.eclipse.persistence.testing.framework.*;

public class SequencingRollbackTest extends AutoVerifyTestCase {
    protected boolean shouldUseSeparateConnection;
    protected boolean shouldUseSeparateConnectionOriginal;
    protected int sequencePreallocationSizeOriginal;
    protected boolean failed;

    public SequencingRollbackTest(boolean shouldUseSeparateConnection) {
        super();
        String name = "Sequencing rollback test - ";
        this.shouldUseSeparateConnection = shouldUseSeparateConnection;
        if (shouldUseSeparateConnection) {
            setName(name + "separate sequencing accessor is allowed");
        } else {
            setName(name + "separate sequencing accessor is NOT allowed");
        }
    }

    protected SequencingControl getSequencingControl() {
        return getDatabaseSession().getSequencingControl();
    }

    public void setup() {
        shouldUseSeparateConnectionOriginal = getSequencingControl().shouldUseSeparateConnection();
//    sequencePreallocationSizeOriginal = getSequencingControl().getPreallocationSize();
        if (shouldUseSeparateConnectionOriginal != shouldUseSeparateConnection) {
            getSequencingControl().setShouldUseSeparateConnection(shouldUseSeparateConnection);
            getSequencingControl().resetSequencing();
        }
        getSequencingControl().initializePreallocated();
//    getSequencingControl().setPreallocationSize(2);
    }

    public void test() {
        beginTransaction();

        // There are no sequence numbers preallocated, therefore new 2 sequence numbers
        // should be preallocated during getNextValue call
        int seqNum1 = getSession().getNextSequenceNumberValue(Employee.class).intValue();

        rollbackTransaction();

        int seqNum2 = getSession().getNextSequenceNumberValue(Employee.class).intValue();
        int seqNum3 = getSession().getNextSequenceNumberValue(Employee.class).intValue();
        int seqNum4 = getSession().getNextSequenceNumberValue(Employee.class).intValue();

        // this would cause PK unique constrain vialotion
        failed = seqNum2 == seqNum4;
    }

    public void verify() {
        if (failed) {
            throw new TestErrorException("Duplication of sequence number has occurred");
        }
    }

    public void reset() {
        if (shouldUseSeparateConnectionOriginal != shouldUseSeparateConnection) {
            getSequencingControl().setShouldUseSeparateConnection(shouldUseSeparateConnectionOriginal);
            getSequencingControl().resetSequencing();
        }
        getSequencingControl().initializePreallocated();
//    getSequencingControl().setPreallocationSize(sequencePreallocationSizeOriginal);
    }
}
