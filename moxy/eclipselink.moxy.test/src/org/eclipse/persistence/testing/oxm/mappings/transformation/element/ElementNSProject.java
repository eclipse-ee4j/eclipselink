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
*     bdoughan - Feb 23/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.transformation.element;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.sessions.Project;

public class ElementNSProject extends Project {

    public ElementNSProject() {
        this.addDescriptor(getRootDescriptor());
    }

    private XMLDescriptor getRootDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Root.class);
        xmlDescriptor.setDefaultRootElement("ns:root");

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("ns", "urn:element");
        xmlDescriptor.setNamespaceResolver(namespaceResolver);

        XMLTransformationMapping typeMapping = new XMLTransformationMapping();
        typeMapping.setAttributeName("element");
        ElementNSTransformer elementTransformer = new ElementNSTransformer();
        typeMapping.setAttributeTransformer(elementTransformer);
        typeMapping.addFieldTransformer("ns:START", elementTransformer);
        typeMapping.addFieldTransformer("ns:INTERMEDIATE", elementTransformer);
        typeMapping.addFieldTransformer("ns:END", elementTransformer);
        xmlDescriptor.addMapping(typeMapping);

        return xmlDescriptor;
    }

}
