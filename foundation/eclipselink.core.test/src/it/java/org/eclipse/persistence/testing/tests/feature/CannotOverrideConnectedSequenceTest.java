/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
