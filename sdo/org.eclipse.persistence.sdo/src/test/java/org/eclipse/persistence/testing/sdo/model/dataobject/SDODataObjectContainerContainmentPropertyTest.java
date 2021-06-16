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
package org.eclipse.persistence.testing.sdo.model.dataobject;

public class SDODataObjectContainerContainmentPropertyTest extends SDODataObjectContainerContainmentPropertyTestCases {
    public SDODataObjectContainerContainmentPropertyTest(String name) {
        super(name);
    }

    // purpose: test if a dataobject's cotainer is changed if other dataobject
    // reset its containment property's value to this dataobject, e.g.
    // Before, b contains c, then
    public void testContainerContainmentPropertyAfterReset() {
        this.assertEquals(dataObject_b, dataObject_c.getContainer());
        dataObject_a.set(property_a, dataObject_c);
        this.assertFalse(dataObject_c.getContainer().equals(dataObject_b));
    }

    // purpose: test if a dataobject's cotainer is changed if other dataobject
    // reset its containment property's value to this dataobject, e.g.
    // Before, b contains c, then
    public void testContainerContainmentPropertyAfterUnset() {
        this.assertEquals(dataObject_a, dataObject_b.getContainer());
        dataObject_a.unset(property_a);
        this.assertNull(dataObject_b.getContainer());
    }
}
