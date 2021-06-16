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
