/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.oxm.*;

/**
 *  @version $Header: InheritanceTestCases.java 30-mar-2005.15:47:58 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class InheritanceTestCases extends OXTestCase {
    public XMLContext context;
    public XMLMarshaller marshaller;
    public XMLUnmarshaller unmarshaller;

    public InheritanceTestCases(String name) throws Exception {
        super(name);
        context = getXMLContext("InheritanceSession");
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
        //marshaller = getXMLMarshaller(new InheritanceProject());
    }

    public void testWrite() throws Exception {
        Car car = new Car();
        car.numberOfDoors = 2;
        car.milesPerGallon = 30;
        car.model = "Grand Am";
        car.manufacturer = "Pontiac";
        car.topSpeed = 220;

        Document carDocument = marshaller.objectToXML(car);
        Element root = (Element)carDocument.getElementsByTagNameNS("mynamespaceuri", "vehicle").item(0);
        Attr elem = root.getAttributeNodeNS("http://www.w3.org/2001/XMLSchema-instance", "type");
        String carType = elem.getNodeValue();
        assertTrue("The type field was written incorrectly for the subclass", carType.equals("prefix:car-type"));

        Vehicle vehicle = new Vehicle();
        vehicle.model = "Blah Blah";
        vehicle.manufacturer = "Some Place";
        vehicle.topSpeed = 10000;

        Document vehicleDocument = marshaller.objectToXML(vehicle);
        root = (Element)vehicleDocument.getElementsByTagNameNS("mynamespaceuri", "vehicle").item(0);
        elem = root.getAttributeNodeNS("http://www.w3.org/2001/XMLSchema-instance", "type");
        assertNull("A type attribute was written but should not have been for the superclass", elem);
    }

    public void testRead() throws Exception {
        InputStream carStream = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/inheritance/car.xml");
        Object car = unmarshaller.unmarshal(carStream);
        assertTrue("Wrong object returned for subclass", car instanceof Car);

        InputStream vehicleStream = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/inheritance/vehicle.xml");
        Object vehicle = unmarshaller.unmarshal(vehicleStream);
        assertTrue("Wrong object returned for superclass", vehicle.getClass().equals(Vehicle.class));

    }

    public void testReadWithDifferentPrefix() throws Exception {
        InputStream carStream = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/inheritance/car_different_prefix.xml");
        Object car = unmarshaller.unmarshal(carStream);
        assertTrue("Wrong object returned for subclass", car instanceof Car);

        InputStream vehicleStream = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/inheritance/vehicle_different_prefix.xml");
        Object vehicle = unmarshaller.unmarshal(vehicleStream);
        assertTrue("Wrong object returned for superclass", vehicle.getClass().equals(Vehicle.class));

    }
    
    public void testReadWithSubClassSpecified(){
    	InputStream carStream = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/inheritance/car.xml");
    	Object unmarshalledObject = unmarshaller.unmarshal(carStream, Car.class);

    	assertTrue("Wrong object returned for subclass", unmarshalledObject instanceof Car);    	  
    }
    
    public void testReadWithSuperClassSpecified(){
    	InputStream carStream = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/inheritance/car.xml");
    	Object unmarshalledObject = unmarshaller.unmarshal(carStream, Vehicle.class);

    	assertTrue("Wrong object returned for subclass", unmarshalledObject instanceof Car);    	  
    }
    
}
