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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.converter.typesafeenum;

import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.jaxb.JAXBTypesafeEnumConverter;
import org.eclipse.persistence.oxm.mappings.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.sessions.Project;

public class TypeSafeEnumConverterProject extends Project {
    public TypeSafeEnumConverterProject() {
        super();
        addDescriptor(getEmployeeDescriptor());
    }

    public ClassDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setDefaultRootElement("employee");
        descriptor.setJavaClass(Employee.class);

        XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setXPath("first-name/text()");
        descriptor.addMapping(firstNameMapping);

        XMLDirectMapping lastNameMapping = new XMLDirectMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setXPath("last-name/text()");
        descriptor.addMapping(lastNameMapping);

        XMLDirectMapping shirtSizeMapping = new XMLDirectMapping();
        shirtSizeMapping.setAttributeName("shirtSize");
        shirtSizeMapping.setXPath("shirt-size/text()");
        descriptor.addMapping(shirtSizeMapping);

        XMLCompositeDirectCollectionMapping hatSizesMapping = new XMLCompositeDirectCollectionMapping();
        hatSizesMapping.setAttributeName("hatSizes");
                hatSizesMapping.useCollectionClass(java.util.ArrayList.class);
        hatSizesMapping.setXPath("hat-size/text()");
        descriptor.addMapping(hatSizesMapping);

        descriptor.setAmendmentClass(TypeSafeEnumConverterProject.class);
        return descriptor;
    }

    public static void amendDescriptor(ClassDescriptor desc) {
        XMLDirectMapping shirtmapping = (XMLDirectMapping)desc.getMappingForAttributeName("shirtSize");
        JAXBTypesafeEnumConverter shirtConverter = new JAXBTypesafeEnumConverter();
        shirtConverter.setEnumClass(MyTypeSafeEnumClass.class);
        ((XMLDirectMapping)shirtmapping).setConverter(shirtConverter);

        XMLCompositeDirectCollectionMapping hatmapping = (XMLCompositeDirectCollectionMapping)desc.getMappingForAttributeName("hatSizes");
        JAXBTypesafeEnumConverter hatConverter = new JAXBTypesafeEnumConverter();
        hatConverter.setEnumClass(MyTypeSafeEnumClass.class);
        ((XMLCompositeDirectCollectionMapping)hatmapping).setValueConverter(hatConverter);
    }

    public static void amendDescriptorNoEnumClass(ClassDescriptor desc) {
        XMLDirectMapping shirtmapping = (XMLDirectMapping)desc.getMappingForAttributeName("shirtSize");
        JAXBTypesafeEnumConverter shirtConverter = new JAXBTypesafeEnumConverter();
        ((XMLDirectMapping)shirtmapping).setConverter(shirtConverter);
    }

    public static void amendDescriptorNoSuchMethod(ClassDescriptor desc) {
        XMLDirectMapping shirtmapping = (XMLDirectMapping)desc.getMappingForAttributeName("shirtSize");
        JAXBTypesafeEnumConverter shirtConverter = new JAXBTypesafeEnumConverter();
        shirtConverter.setEnumClass(TypeSafeEnumConverterTestCases.class);
        ((XMLDirectMapping)shirtmapping).setConverter(shirtConverter);
    }

    public static void amendDescriptorInvalidEnumClass(ClassDescriptor desc) {
        XMLDirectMapping shirtmapping = (XMLDirectMapping)desc.getMappingForAttributeName("shirtSize");
        JAXBTypesafeEnumConverter shirtConverter = new JAXBTypesafeEnumConverter();
        shirtConverter.setEnumClassName("a.b.c");
        ((XMLDirectMapping)shirtmapping).setConverter(shirtConverter);
    }
}
