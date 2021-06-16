/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
