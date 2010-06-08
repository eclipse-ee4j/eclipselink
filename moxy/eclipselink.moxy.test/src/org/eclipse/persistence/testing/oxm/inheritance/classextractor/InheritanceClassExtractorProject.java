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
package org.eclipse.persistence.testing.oxm.inheritance.classextractor;

import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.inheritance.Vehicle;

/**
 *  @version $Header: InheritanceProject.java 02-nov-2006.10:57:11 gyorke Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class InheritanceClassExtractorProject extends Project {
    private NamespaceResolver namespaceResolver;

    public InheritanceClassExtractorProject() {
        super();

        namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        namespaceResolver.put("prefix", "mynamespaceuri");

        addDescriptor(getParkingLotDescriptor());
        addDescriptor(getVehicleDescriptor());
        addDescriptor(getCarDescriptor());
    }

    public XMLDescriptor getVehicleDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.oxm.inheritance.Vehicle.class);
        descriptor.setDefaultRootElement("prefix:vehicle");
        descriptor.setNamespaceResolver(namespaceResolver);
       
        descriptor.getInheritancePolicy().setShouldReadSubclasses(true);
        descriptor.getInheritancePolicy().setClassExtractionMethodName("getClassForRow");       

        XMLDirectMapping modelMapping = new XMLDirectMapping();
        modelMapping.setAttributeName("model");
        modelMapping.setXPath("prefix:model/text()");
        descriptor.addMapping(modelMapping);

        XMLDirectMapping manufacturerMapping = new XMLDirectMapping();
        manufacturerMapping.setAttributeName("manufacturer");
        manufacturerMapping.setXPath("prefix:manufacturer/text()");
        descriptor.addMapping(manufacturerMapping);

        XMLDirectMapping speedMapping = new XMLDirectMapping();
        speedMapping.setAttributeName("topSpeed");
        speedMapping.setXPath("prefix:top-speed/text()");
        descriptor.addMapping(speedMapping);

        return descriptor;
    }

    public XMLDescriptor getCarDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.oxm.inheritance.Car.class);
        descriptor.setNamespaceResolver(namespaceResolver);
        descriptor.setDefaultRootElement("prefix:car");
        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.testing.oxm.inheritance.Vehicle.class);

        XMLDirectMapping doorsMapping = new XMLDirectMapping();
        doorsMapping.setAttributeName("numberOfDoors");
        doorsMapping.setXPath("prefix:number-of-doors/text()");
        descriptor.addMapping(doorsMapping);

        XMLDirectMapping fuelMapping = new XMLDirectMapping();
        fuelMapping.setAttributeName("milesPerGallon");
        fuelMapping.setXPath("prefix:miles-per-gallon/text()");
        descriptor.addMapping(fuelMapping);

        return descriptor;
    }

    private XMLDescriptor getParkingLotDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.oxm.inheritance.ParkingLot.class);
        descriptor.setNamespaceResolver(namespaceResolver);
        descriptor.setDefaultRootElement("prefix:parking-lot");

        XMLCompositeCollectionMapping vehiclesMapping = new XMLCompositeCollectionMapping();
        vehiclesMapping.setAttributeName("vehicles");
        vehiclesMapping.setReferenceClass(Vehicle.class);
        vehiclesMapping.setXPath("prefix:vehicles/prefix:vehicle");
        descriptor.addMapping(vehiclesMapping);

        return descriptor;
    }
}
