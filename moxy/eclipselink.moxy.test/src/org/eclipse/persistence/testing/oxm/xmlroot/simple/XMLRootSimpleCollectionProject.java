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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.xmlroot.simple;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.xmlroot.complex.Employee;

public class XMLRootSimpleCollectionProject extends Project {
    public XMLRootSimpleCollectionProject() {
        super();
        this.addDescriptor(getRootDescriptor());
    }

    private XMLDescriptor getRootDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(RootObjectWithSimpleCollection.class);
        xmlDescriptor.setDefaultRootElement("ns0:myRoot");

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns0", "mynamespace");
        xmlDescriptor.setNamespaceResolver(nsResolver);

        XMLCompositeDirectCollectionMapping theMapping = new XMLCompositeDirectCollectionMapping();
        theMapping.setAttributeName("theList");
        theMapping.setXPath("text()");
        theMapping.setFieldElementClass(Integer.class);
        theMapping.setUsesSingleNode(true);
        xmlDescriptor.addMapping(theMapping);
        return xmlDescriptor;
    }

}
