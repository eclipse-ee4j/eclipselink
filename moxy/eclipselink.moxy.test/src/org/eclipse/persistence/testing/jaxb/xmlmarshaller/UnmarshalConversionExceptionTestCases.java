/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

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