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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public class AttributeListOnTargetTestProject extends AttributeOnTargetProject {
    public AttributeListOnTargetTestProject() {
        super();
    }

    public XMLDescriptor getAddressDescriptor() {
        XMLDescriptor xmlDescriptor = super.getAddressDescriptor();
        XMLCompositeDirectCollectionMapping collectionMapping = new XMLCompositeDirectCollectionMapping();
        collectionMapping.setAttributeName("provinces");
        collectionMapping.setXPath("@provinces");
        collectionMapping.setUsesSingleNode(true);
        xmlDescriptor.addMapping(collectionMapping);

        return xmlDescriptor;
    }
}
