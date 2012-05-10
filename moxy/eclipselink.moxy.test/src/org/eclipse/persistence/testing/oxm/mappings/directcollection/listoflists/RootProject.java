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
 *     rbarkhouse - 2009-05-05 14:32:00 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.directcollection.listoflists;

import java.util.ArrayList;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLListConverter;
import org.eclipse.persistence.sessions.Project;

public class RootProject extends Project {

    public RootProject() {
        addDescriptor(getRootDescriptor());
    }

    private XMLDescriptor getRootDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Root.class);
        descriptor.setDefaultRootElement("root");

        XMLCompositeDirectCollectionMapping itemsMapping = new XMLCompositeDirectCollectionMapping();
        itemsMapping.setAttributeName("items");
        itemsMapping.setXPath("item/text()");
        itemsMapping.setFieldElementClass(ArrayList.class);
        itemsMapping.useCollectionClass(ArrayList.class);

        XMLListConverter listConverter = new XMLListConverter();
        listConverter.setObjectClass(Double.class);
        itemsMapping.setValueConverter(listConverter);

        descriptor.addMapping(itemsMapping);
        return descriptor;
    }

}
