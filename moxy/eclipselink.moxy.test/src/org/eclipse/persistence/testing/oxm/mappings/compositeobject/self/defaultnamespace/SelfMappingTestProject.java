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
* dmccann - Nov.19/2008 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.defaultnamespace;

import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class SelfMappingTestProject extends Project {
    private NamespaceResolver namespaceResolver;

    public SelfMappingTestProject() {
        namespaceResolver = new NamespaceResolver();
        namespaceResolver.setDefaultNamespaceURI("http://www.foo.com/bar/baz");
        addDescriptor(getAddressDescriptor());
        addDescriptor(getAddressLinesDescriptor());
    }
    
    public XMLDescriptor getAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);
        descriptor.setDefaultRootElement("Address");
        descriptor.setNamespaceResolver(namespaceResolver);
        
        XMLDirectMapping attentionOfNameMapping = new XMLDirectMapping();
        attentionOfNameMapping.setAttributeName("attentionOfName");
        attentionOfNameMapping.setXPath("AttentionOfName/text()");
        descriptor.addMapping(attentionOfNameMapping);

        XMLDirectMapping careOfNameMapping = new XMLDirectMapping();
        careOfNameMapping.setAttributeName("careOfName");
        careOfNameMapping.setXPath("CareOfName/text()");
        descriptor.addMapping(careOfNameMapping);
        
        XMLCompositeObjectMapping addressLinesMapping = new XMLCompositeObjectMapping();
        addressLinesMapping.setAttributeName("addressLines");
        addressLinesMapping.setReferenceClass(AddressLines.class);
        addressLinesMapping.setXPath(".");
        descriptor.addMapping(addressLinesMapping);
        
        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setXPath("CityName/text()");
        descriptor.addMapping(cityMapping);
        
        XMLDirectMapping stateMapping = new XMLDirectMapping();
        stateMapping.setAttributeName("state");
        stateMapping.setXPath("StateCode/text()");
        descriptor.addMapping(stateMapping);
        
        XMLDirectMapping postalCodeMapping = new XMLDirectMapping();
        postalCodeMapping.setAttributeName("postalCode");
        postalCodeMapping.setXPath("PostalCode/text()");
        descriptor.addMapping(postalCodeMapping);
        
        XMLDirectMapping countryCodeMapping = new XMLDirectMapping();
        countryCodeMapping.setAttributeName("countryCode");
        countryCodeMapping.setXPath("CountryCode/text()");
        descriptor.addMapping(countryCodeMapping);
        
        return descriptor;
    }
    
    public XMLDescriptor getAddressLinesDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AddressLines.class);
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping addressLine1Mapping = new XMLDirectMapping();
        addressLine1Mapping.setAttributeName("addressLine1");
        addressLine1Mapping.setXPath("LineOne/text()");
        descriptor.addMapping(addressLine1Mapping);
        
        XMLDirectMapping addressLine2Mapping = new XMLDirectMapping();
        addressLine2Mapping.setAttributeName("addressLine2");
        addressLine2Mapping.setXPath("LineTwo/text()");
        descriptor.addMapping(addressLine2Mapping);
        
        XMLDirectMapping addressLine3Mapping = new XMLDirectMapping();
        addressLine3Mapping.setAttributeName("addressLine3");
        addressLine3Mapping.setXPath("LineThree/text()");
        descriptor.addMapping(addressLine3Mapping);
        
        XMLDirectMapping addressLine4Mapping = new XMLDirectMapping();
        addressLine4Mapping.setAttributeName("addressLine4");
        addressLine4Mapping.setXPath("LineFour/text()");
        descriptor.addMapping(addressLine4Mapping);
        
        return descriptor;
    }
}
