/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - Feb 23/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.transformation.element;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.sessions.Project;

public class ElementProject extends Project {

    public ElementProject() {
        this.addDescriptor(getRootDescriptor());
    }

    private XMLDescriptor getRootDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Root.class);
        xmlDescriptor.setDefaultRootElement("root");

        XMLTransformationMapping typeMapping = new XMLTransformationMapping();
        typeMapping.setAttributeName("element");
        ElementTransformer elementTransformer = new ElementTransformer();
        typeMapping.setAttributeTransformer(elementTransformer);
        typeMapping.addFieldTransformer("START", elementTransformer);
        typeMapping.addFieldTransformer("INTERMEDIATE", elementTransformer);
        typeMapping.addFieldTransformer("END", elementTransformer);
        xmlDescriptor.addMapping(typeMapping);

        return xmlDescriptor;
    }

}
