/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.io.StringReader;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class UnmarshalConversionExceptionTestCases extends TestCase {

    private static String XML = "<root><a>ONE</a><b>2</b><c>THREE</c><d>4</d></root>";

    private Unmarshaller unmarshaller;

    @Override
    protected void setUp() throws Exception {
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {UnmarshalConversionExceptionRoot.class}, null);
        unmarshaller = jc.createUnmarshaller();
    }

    public void testIgnoreConversionExceptions() throws Exception {
        UnmarshalConversionExceptionRoot control = new UnmarshalConversionExceptionRoot();
        control.a = 0;
        control.b = 2;
        control.c = 0;
        control.d = 4;
        UnmarshalConversionExceptionRoot test = (UnmarshalConversionExceptionRoot) unmarshaller.unmarshal(new StringReader(XML));
        assertEquals(control, test);
    }

    public void testCatchConversionExceptions() {
        try {
            unmarshaller.setEventHandler(new ValidationEventHandler() {
                public boolean handleEvent(ValidationEvent arg0) {
                    return false;
                }
            });
            unmarshaller.unmarshal(new StringReader(XML));
        } catch(JAXBException e) {
            return;
        }
        fail();
    }

    public void testCountConversionExceptions() throws Exception {
        CountingValidationEventHandler cveh = new CountingValidationEventHandler();
        unmarshaller.setEventHandler(cveh);
        unmarshaller.unmarshal(new StringReader(XML));
        assertEquals(2, cveh.getCount());
    }

    private static class CountingValidationEventHandler implements ValidationEventHandler {

        private int count = 0;

        public boolean handleEvent(ValidationEvent arg0) {
            count++;
            return true;
        }

        public int getCount() {
            return count;
        }

    }

}
