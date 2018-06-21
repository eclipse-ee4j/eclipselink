/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     dmccann - August 11/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlcustomizer;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

/**
 * DescriptorCustomizer implementation for testing external metadata functionality.
 *
 * @see DescriptorCustomizer
 */
public class MyXmlEmployeeCustomizer implements DescriptorCustomizer {

    public void customize(ClassDescriptor descriptor) throws Exception {
        XMLDirectMapping firstNameMapping = (XMLDirectMapping) descriptor.getMappingForAttributeName("firstName");
        XMLField fnxField = (XMLField) firstNameMapping.getField();
        fnxField.setXPath("my-first-name/text()");

        XMLDirectMapping lastNameMapping  = (XMLDirectMapping) descriptor.getMappingForAttributeName("lastName" );
        XMLField lnxField = (XMLField) lastNameMapping.getField();
        lnxField.setXPath("my-last-name/text()");
    }

}
