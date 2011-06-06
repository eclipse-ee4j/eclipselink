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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.sequenced;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.sessions.Project;

public class EmployeeProject extends Project {

    private XMLDescriptor employeeDescriptor;
    private XMLDescriptor addressDescriptor;
    private XMLDescriptor dependentDescriptor;
    private XMLDescriptor phoneNumberDescriptor;
    
	public EmployeeProject() {
		super();
		this.addDescriptor(getEmployeeDescriptor());
		this.addDescriptor(getAddressDescriptor());
		this.addDescriptor(getDependentDescriptor());
		this.addDescriptor(getPhoneNumberDescriptor());
	}
	
	public XMLDescriptor getEmployeeDescriptor() {
	    if(null == employeeDescriptor) {	    
	        employeeDescriptor = new XMLDescriptor();
	        employeeDescriptor.setJavaClass(Employee.class);
	        employeeDescriptor.setDefaultRootElement("ns:employee");
	        employeeDescriptor.setSequencedObject(true);
		
	        NamespaceResolver nsResolver = new NamespaceResolver();
	        nsResolver.put("ns", "urn:example");
	        employeeDescriptor.setNamespaceResolver(nsResolver);
		
	        XMLDirectMapping idMapping = new XMLDirectMapping();
	        idMapping.setAttributeName("id");
	        idMapping.setXPath("@id");
	        employeeDescriptor.addMapping(idMapping);

	        XMLDirectMapping fnMapping = new XMLDirectMapping();
	        fnMapping.setAttributeName("firstName");
	        fnMapping.setXPath("personal-info/ns:first-name/text()");
	        employeeDescriptor.addMapping(fnMapping);		

	        XMLDirectMapping lnMapping = new XMLDirectMapping();
	        lnMapping.setAttributeName("lastName");
	        lnMapping.setXPath("personal-info/last-name/text()");
	        employeeDescriptor.addMapping(lnMapping);     
		
	        XMLCompositeObjectMapping addressMapping = new XMLCompositeObjectMapping();
	        addressMapping.setAttributeName("address");
	        addressMapping.setXPath("address");
	        addressMapping.setReferenceClass(Address.class);
	        employeeDescriptor.addMapping(addressMapping);

	        XMLCompositeObjectMapping dependentMapping = new XMLCompositeObjectMapping();
	        dependentMapping.setAttributeName("dependent");
	        dependentMapping.setXPath("dependent");
	        dependentMapping.setReferenceClass(Dependent.class);
	        employeeDescriptor.addMapping(dependentMapping);
		
	        XMLAnyObjectMapping anyMapping = new XMLAnyObjectMapping();
	        anyMapping.setAttributeName("any");
	        anyMapping.setUseXMLRoot(true);
	        employeeDescriptor.addMapping(anyMapping);
		
	        XMLFragmentMapping nodeMapping = new XMLFragmentMapping();
	        nodeMapping.setAttributeName("node");
	        nodeMapping.setXPath("fragment");
	        employeeDescriptor.addMapping(nodeMapping);
		
	        /*
		    XMLChoiceMapping choiceMapping = new XMLChoiceMapping();
		    choiceMapping.setAttributeName("choice");
		    choiceMapping.addChoiceElement("choice-address", Address.class);
		    choiceMapping.addChoiceElement("choice-dependent", Dependent.class);
		    choiceMapping.addChoiceElement("choice-string/text()", String.class);
		    employeeDescriptor.addMapping(choiceMapping);
	         */
	    }
	    return employeeDescriptor;
	}

	public XMLDescriptor getAddressDescriptor() {
	    if(null == addressDescriptor) {
	        addressDescriptor = new XMLDescriptor();
	        addressDescriptor.setJavaClass(Address.class);
	        addressDescriptor.setDefaultRootElement("ADDRESS-ROOT");
	        addressDescriptor.addPrimaryKeyFieldName("@aid");

	        XMLDirectMapping idMapping = new XMLDirectMapping();
	        idMapping.setAttributeName("id");
	        idMapping.setXPath("@aid");
	        addressDescriptor.addMapping(idMapping);

	        XMLDirectMapping streetMapping = new XMLDirectMapping();
	        streetMapping.setAttributeName("street");
	        streetMapping.setXPath("street/text()");
	        addressDescriptor.addMapping(streetMapping);

	        XMLDirectMapping cityMapping = new XMLDirectMapping();
	        cityMapping.setAttributeName("city");
	        cityMapping.setXPath("city/text()");
	        addressDescriptor.addMapping(cityMapping);
	    }
		return addressDescriptor;
	}
	
	public XMLDescriptor getDependentDescriptor() {
	    if(null == dependentDescriptor) {
	        dependentDescriptor = new XMLDescriptor();
	        dependentDescriptor.setJavaClass(Dependent.class);
	        dependentDescriptor.setSequencedObject(true);
	        dependentDescriptor.setDefaultRootElement("BAR");

	        XMLDirectMapping fnMapping = new XMLDirectMapping();
	        fnMapping.setAttributeName("firstName");
	        fnMapping.setXPath("first-name/text()");
	        dependentDescriptor.addMapping(fnMapping);

	        XMLDirectMapping lnMapping = new XMLDirectMapping();
	        lnMapping.setAttributeName("lastName");
	        lnMapping.setXPath("last-name/text()");
	        dependentDescriptor.addMapping(lnMapping);		
		
	        XMLObjectReferenceMapping addressMapping = new XMLObjectReferenceMapping();
	        addressMapping.setAttributeName("address");
	        addressMapping.setReferenceClass(Address.class);
	        addressMapping.addSourceToTargetKeyFieldAssociation("address-id/text()" , "@aid");
	        dependentDescriptor.addMapping(addressMapping);
	    }
		return dependentDescriptor;
	}

    public XMLDescriptor getPhoneNumberDescriptor() {
        if(null == phoneNumberDescriptor) {
            phoneNumberDescriptor = new XMLDescriptor();
            phoneNumberDescriptor.setJavaClass(PhoneNumber.class);
            phoneNumberDescriptor.setDefaultRootElement("phone-number");
            phoneNumberDescriptor.setSequencedObject(true);

            XMLDirectMapping areaCodeMapping = new XMLDirectMapping();
            areaCodeMapping.setAttributeName("areaCode");
            areaCodeMapping.setXPath("area-code/text()");
            phoneNumberDescriptor.addMapping(areaCodeMapping);

            XMLDirectMapping numberMapping = new XMLDirectMapping();
            numberMapping.setAttributeName("number");
            numberMapping.setXPath("text()");
            phoneNumberDescriptor.addMapping(numberMapping);

            XMLDirectMapping extensionMapping = new XMLDirectMapping();
            extensionMapping.setAttributeName("extension");
            extensionMapping.setXPath("extension/text()");
            phoneNumberDescriptor.addMapping(extensionMapping);
        }
        return phoneNumberDescriptor;
    }
	
}
