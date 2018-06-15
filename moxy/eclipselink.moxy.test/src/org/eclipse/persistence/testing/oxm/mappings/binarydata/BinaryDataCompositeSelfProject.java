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
//     Matt MacIvor - January 18/2010 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.binarydata;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class BinaryDataCompositeSelfProject extends Project {

    public BinaryDataCompositeSelfProject() {
        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getMyImageDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        descriptor.addMapping(idMapping);

        XMLCompositeObjectMapping imageMapping = new XMLCompositeObjectMapping();
        imageMapping.setAttributeName("myImage");
        imageMapping.setXPath("my-image");
        imageMapping.setReferenceClass(MyImage.class);
        descriptor.addMapping(imageMapping);

        return descriptor;
    }

    private XMLDescriptor getMyImageDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MyImage.class);

        XMLBinaryDataMapping dataMapping = new XMLBinaryDataMapping();
        dataMapping.setAttributeName("myBytes");
        dataMapping.setXPath(".");
        dataMapping.setShouldInlineBinaryData(false);
        descriptor.addMapping(dataMapping);

        return descriptor;
    }
}
