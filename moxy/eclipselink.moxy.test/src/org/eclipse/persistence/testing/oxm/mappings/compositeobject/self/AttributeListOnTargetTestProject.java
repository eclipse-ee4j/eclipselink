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
//     Oracle - initial API and implementation from Oracle TopLink
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
