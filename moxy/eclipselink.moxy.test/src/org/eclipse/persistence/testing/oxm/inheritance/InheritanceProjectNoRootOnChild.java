/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:     
 *     Denise Smith - January 4th, 2010 - 2.0.1
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.inheritance;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public class InheritanceProjectNoRootOnChild extends InheritanceProject{

	public XMLDescriptor getCarDescriptor() {
	        XMLDescriptor descriptor = new XMLDescriptor();
	        descriptor.setJavaClass(org.eclipse.persistence.testing.oxm.inheritance.Car.class);
	        descriptor.setNamespaceResolver(namespaceResolver);
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
}
