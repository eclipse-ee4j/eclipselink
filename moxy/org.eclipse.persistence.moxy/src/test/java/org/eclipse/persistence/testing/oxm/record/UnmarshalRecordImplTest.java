/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Iaroslav Savytskyi - 2.6 - initial implementation
package org.eclipse.persistence.testing.oxm.record;

import org.eclipse.persistence.internal.oxm.record.UnmarshalRecordImpl;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertFalse;

public class UnmarshalRecordImplTest {

    /**
     * Testing the case when we are processing unmapped element with null value.
     */
    @Test
    public void testEndElement() throws SAXException {
        UnmarshalRecordImpl ur = new UnmarshalRecordImpl(null);
        ur.setNil(true);
        ur.endUnmappedElement("", "foo", "foo");
        assertFalse("Expected nil flag in unmarshalRecord to be FALSE", ur.isNil());
    }
}
