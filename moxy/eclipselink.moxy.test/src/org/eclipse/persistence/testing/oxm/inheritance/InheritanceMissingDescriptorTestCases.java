/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.inheritance;

import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import javax.xml.transform.dom.DOMResult;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.exceptions.XMLMarshalException;

public class InheritanceMissingDescriptorTestCases extends OXTestCase {
    public XMLMarshaller marshaller;
    public XMLContext context;
    public XMLUnmarshaller unmarshaller;

    public InheritanceMissingDescriptorTestCases(String name) {
        super(name);
        context = getXMLContext(new InheritanceMissingDescriptorProject());
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
    }

    public void testMissingDescriptorWrite() {
        Car car = new Car();
        car.numberOfDoors = 2;
        car.milesPerGallon = 30;
        car.model = "Grand Am";
        car.manufacturer = "Pontiac";
        car.topSpeed = 220;

        try {
            StringWriter writer = new StringWriter();
            marshaller.marshal(car, writer);
        } catch (XMLMarshalException exception) {
            assertTrue("An incorrect XMLValidation was thrown.", exception.getErrorCode() == XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT);
            return;
        } catch (Exception exception) {
            assertTrue("An unexpected exception was thrown.", false);
            return;
        }
        assertTrue("An XMLValidation should have been caught but wasn't.", false);
    }

    public void testMissingDescriptorRead() {
        try {
            InputStream carStream = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/inheritance/car.xml");
            Object car = unmarshaller.unmarshal(carStream);
        } catch (XMLMarshalException exception) {
            assertTrue("An incorrect XMLValidation was thrown.", exception.getErrorCode() == XMLMarshalException.NO_DESCRIPTOR_WITH_MATCHING_ROOT_ELEMENT);
            return;
        } catch (Exception exception) {
            assertTrue("An unexpected exception was caught.", false);
            return;
        }
        assertTrue("An XMLValidation should have been caught but wasn't.", false);
    }
}
