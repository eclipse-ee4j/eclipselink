/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     bdoughan - Feb 23/2009 - 2.0 - Initial implementation
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
