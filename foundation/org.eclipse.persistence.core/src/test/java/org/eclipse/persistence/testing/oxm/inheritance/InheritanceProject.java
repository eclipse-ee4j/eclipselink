/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

/**
 *  @version $Header: InheritanceProject.java 02-nov-2006.10:57:11 gyorke Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class InheritanceProject extends Project {
    protected NamespaceResolver namespaceResolver;

    public InheritanceProject() {
        this(false);
    }

    public InheritanceProject(boolean defaultNS) {
        super();

        namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        namespaceResolver.put("prefix", "mynamespaceuri");

        if(defaultNS){
            namespaceResolver.setDefaultNamespaceURI("mynamespaceuri");
        }

        addDescriptor(getParkingLotDescriptor());
        addDescriptor(getVehicleDescriptor());
        addDescriptor(getCarDescriptor());
    }

    public XMLDescriptor getVehicleDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.oxm.inheritance.Vehicle.class);
        descriptor.setDefaultRootElement("prefix:vehicle");
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLField classIndicatorField = new XMLField("@xsi:type");
        descriptor.getInheritancePolicy().setClassIndicatorField(classIndicatorField);
        descriptor.getInheritancePolicy().addClassIndicator(Vehicle.class, "prefix:vehicle-type");
        descriptor.getInheritancePolicy().addClassIndicator(Car.class, "prefix:car-type");

        descriptor.getInheritancePolicy().setShouldReadSubclasses(true);


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
        descriptor.setDefaultRootElement("prefix:vehicle");
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
