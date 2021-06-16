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
package org.eclipse.persistence.testing.tests.proxyindirection;

import java.util.Vector;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

//Bug#4251902 Make Proxy Indirection writable and readable to deployment xml.  useProxyIndirection() now creates
//a ProxyIndirectionPolicy that includes not only the interfaces of the referenceClass, but also all the super
//interfaces of the referenceClass.  To test this new functionality, Computer, ComputerImpl, DesktopComputer and
//DesktopComputerImpl have been added.
public class InheritanceReadAllTest extends AutoVerifyTestCase  {
    Vector cubicles;

    public InheritanceReadAllTest() {
        setDescription("Tests ReadAllObjects using Proxy Indirection with target interfaces including super interfaces.");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
    }

    public void test() {
        ReadAllQuery q = new ReadAllQuery();
        q.setReferenceClass(Cubicle.class);
        cubicles = (Vector)getSession().executeQuery(q);
    }

    public void verify() {
        if (cubicles.size() != 3) {
            throw new TestErrorException("All Cubicles were not read in.  Expected 3, got " + cubicles.size() + ".");
        }

        // Test the indirection
        for (int i = 0; i < cubicles.size(); i++) {
            Cubicle c = (Cubicle)cubicles.elementAt(i);
            if (((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(DesktopComputerImpl.class).getSize() != i) {
                throw new TestErrorException("ProxyIndirection did not work - DesktopComputer was read in along with Cubicle.");
            }
            c.getComputer().getDescription();
            if (((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(DesktopComputerImpl.class).getSize() == i) {
                throw new TestErrorException("ProxyIndirection did not work - DesktopComputer was not read in when triggered from Cubicle.");

            }
        }
    }
}
