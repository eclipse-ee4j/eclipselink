/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Attempt to override connected sequence should fail - the old sequence should remain in place.
 */
public class CannotOverrideConnectedSequenceTest extends AutoVerifyTestCase {

    Sequence originalSequence;
    Sequence newSequence;
    public CannotOverrideConnectedSequenceTest() {
        setDescription("Attempt to override connected sequence should fail - the old sequence should remain in place.");
    }

    @Override
    public void setup() {
        if (!getSession().isConnected()) {
            throw new TestProblemException("This test requires the session to be connected.");
        }
        String seqName = getSession().getDescriptor(Employee.class).getSequenceNumberName();
        originalSequence = getSession().getPlatform().getSequence(seqName);
    }
    @Override
    public void test() {
        if (originalSequence.isTable()) {
            newSequence = new NativeSequence(originalSequence.getName());
        } else {
            newSequence = new TableSequence(originalSequence.getName());
        }
        getDatabaseSession().addSequence(newSequence);
    }

    @Override
    public void verify() {
        Sequence sequence = getSession().getPlatform().getSequence(originalSequence.getName());
        if (sequence != originalSequence) {
            throw new TestErrorException("Connected sequence was overridden.");
        }
    }
}
