/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.sequencing.*;

/**
 * Attempt to override connected sequence should fail - the old sequence should remain in place.
 */
public class CannotOverrideConnectedSequenceTest extends AutoVerifyTestCase {

    Sequence originalSequence;
    Sequence newSequence;
    public CannotOverrideConnectedSequenceTest() {
        setDescription("Attempt to override connected sequence should fail - the old sequence should remain in place.");
    }

    public void setup() {
        if (!getSession().isConnected()) {
            throw new TestProblemException("This test requires the session to be connected.");
        }
        String seqName = getSession().getDescriptor(Employee.class).getSequenceNumberName();
        originalSequence = getSession().getPlatform().getSequence(seqName);
    }
    public void test() {
        if (originalSequence.isTable()) {
            newSequence = new NativeSequence(originalSequence.getName());
        } else {
            newSequence = new TableSequence(originalSequence.getName());
        }
        getDatabaseSession().addSequence(newSequence);
    }

    public void verify() {
        Sequence sequence = getSession().getPlatform().getSequence(originalSequence.getName());
        if (sequence != originalSequence) {
            throw new TestErrorException("Connected sequence was overridden.");
        }
    }
}
