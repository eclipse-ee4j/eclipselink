/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Iaroslav Savytskyi - 2.6 - initial implementation
 ******************************************************************************/
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
