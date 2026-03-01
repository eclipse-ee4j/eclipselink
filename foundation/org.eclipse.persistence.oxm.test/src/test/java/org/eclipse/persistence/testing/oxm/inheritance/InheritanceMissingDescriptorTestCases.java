/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.inheritance;

import org.eclipse.persistence.oxm.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.testing.oxm.OXTestCase;

import java.io.InputStream;
import java.io.StringWriter;

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
            assertEquals("An incorrect XMLValidation was thrown.", XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT, exception.getErrorCode());
            return;
        } catch (Exception exception) {
            fail("An unexpected exception was thrown.");
            return;
        }
        fail("An XMLValidation should have been caught but wasn't.");
    }

    public void testMissingDescriptorRead() {
        try {
            InputStream carStream = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/inheritance/car.xml");
            Object car = unmarshaller.unmarshal(carStream);
        } catch (XMLMarshalException exception) {
            assertEquals("An incorrect XMLValidation was thrown.", XMLMarshalException.NO_DESCRIPTOR_WITH_MATCHING_ROOT_ELEMENT, exception.getErrorCode());
            return;
        } catch (Exception exception) {
            fail("An unexpected exception was caught.");
            return;
        }
        fail("An XMLValidation should have been caught but wasn't.");
    }
}
