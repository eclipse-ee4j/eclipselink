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
package org.eclipse.persistence.testing.tests.inheritance;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.inheritance.*;

/**
 * For Bug 6001198 - CLASSCAST EXCEPTION WHEN DESCRIPTORS ARE REINITILIZED AT RUNTIME
 *
 * Test to ensure that no exceptions are thrown when reInitializeJoinedAttributes() is
 * invoked on a ClassDescriptor when inheritance is involved.
 * Although this method is internal, it is invoked by OneToOneMapping>>setUsesJoining()
 * which is a deprecated method. This test invokes this API directly for this reason.
 * @author dminsky
 */
public class ReinitializeJoiningOnClassDescriptorWithInheritanceTest extends TestCase {

    protected Exception thrownException;

    public ReinitializeJoiningOnClassDescriptorWithInheritanceTest() {
        super();
        setDescription("Test reinitializing the joined attributes of a ClassDescriptor at the root of an inheritance tree");
    }

    public void test() {
        // descriptor used is the root of an inheritance tree
        ClassDescriptor descriptor = getSession().getDescriptor(Computer.class);
        try {
            // Called by setUsesJoining() (deprecated)
            descriptor.reInitializeJoinedAttributes();
        } catch (Exception e) {
            thrownException = e;
        }
    }

    public void verify() {
        if (thrownException != null) {
            String msg = "Invoking ClassDescriptor>>reInitializeJoinedAttributes() threw an Exception";
            throw new TestErrorException(msg, thrownException);
        }
    }

    public void reset() {
        thrownException = null;
    }

}
