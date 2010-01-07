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
*     bdoughan - December 17/2009 - 2.0.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.advancedxpath;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.sessions.Project;

public class CustomerProject extends Project {

    public CustomerProject() {
        XMLLogin xmlLogin = new XMLLogin();
        xmlLogin.setDatasourcePlatform(new DOMPlatform());
        this.setDatasourceLogin(xmlLogin);

        this.addDescriptor(getCustomerDescriptor());
        this.addDescriptor(getAddressDescriptor());
    }

    private XMLDescriptor getCustomerDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Customer.class);
        xmlDescriptor.setDefaultRootElement("ns:customer");

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns", "urn:example");
        xmlDescriptor.setNamespaceResolver(nsResolver);

        XMLCompositeObjectMapping addressMapping = new XMLCompositeObjectMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setXPath("//ns:customer/ns:phone-number/preceding-sibling::*[1]");
        addressMapping.setReferenceClass(Address.class);
        xmlDescriptor.addMapping(addressMapping);

        return xmlDescriptor;
    }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Address.class);

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns", "urn:example");
        xmlDescriptor.setNamespaceResolver(nsResolver);

        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setXPath("text()");
        xmlDescriptor.addMapping(streetMapping);

        return xmlDescriptor;
    }

}