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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.nested;

import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.Employee;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.MailingAddress;

public class CompositeObjectNestedProject extends Project {

  public CompositeObjectNestedProject() {
    addDescriptor(getEmployeeDescriptor());
    addDescriptor(getProjectDescriptor());
    addDescriptor(getMailingAddressDescriptor());
  }


	private XMLDescriptor getProjectDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(org.eclipse.persistence.testing.oxm.mappings.compositeobject.nested.Project.class);
    descriptor.setDefaultRootElement("project");

		XMLDirectMapping nameMapping = new XMLDirectMapping();
    nameMapping.setXPath("name/text()");
    nameMapping.setAttributeName("name");
    descriptor.addMapping(nameMapping); 

		XMLCompositeObjectMapping leaderMapping = new XMLCompositeObjectMapping();
    leaderMapping.setAttributeName("leader");
    leaderMapping.setXPath("project-leader");
    leaderMapping.setGetMethodName("getLeader");
    leaderMapping.setSetMethodName("setLeader");
    leaderMapping.setReferenceClass(Employee.class);
    descriptor.addMapping(leaderMapping);

		return descriptor;
	}
	
  private XMLDescriptor getEmployeeDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(Employee.class);

    XMLDirectMapping idMapping = new XMLDirectMapping();
    idMapping.setXPath("id/text()");
    idMapping.setAttributeName("id");
    descriptor.addMapping(idMapping);   

   	XMLCompositeObjectMapping addressMapping = new XMLCompositeObjectMapping();
    addressMapping.setAttributeName("mailingAddress");
    addressMapping.setXPath("mailing-address");
    addressMapping.setReferenceClass(MailingAddress.class);
    descriptor.addMapping(addressMapping);   
    
    return descriptor;
  }

  private XMLDescriptor getMailingAddressDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(MailingAddress.class);
   
    XMLDirectMapping streetMapping = new XMLDirectMapping();
    streetMapping.setAttributeName("street");
    streetMapping.setXPath("street/text()");
    descriptor.addMapping(streetMapping);   

    XMLDirectMapping cityMapping = new XMLDirectMapping();
    cityMapping.setAttributeName("city");
    cityMapping.setXPath("city/text()");
    descriptor.addMapping(cityMapping);   

    XMLDirectMapping provinceMapping = new XMLDirectMapping();
    provinceMapping.setAttributeName("province");
    provinceMapping.setXPath("province/text()");
    descriptor.addMapping(provinceMapping);   

    XMLDirectMapping postalCodeMapping = new XMLDirectMapping();
    postalCodeMapping.setAttributeName("postalCode");
    postalCodeMapping.setXPath("postal-code/text()");
    descriptor.addMapping(postalCodeMapping);   

    return descriptor;
  }  
  
}
