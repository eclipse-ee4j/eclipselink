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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.schematype;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class SchemaTypeQNameProject extends Project {

      public SchemaTypeQNameProject() {
        addDescriptor(getQNameHolderDescriptor());
      }

      private XMLDescriptor getQNameHolderDescriptor() {
            XMLDescriptor descriptor = new XMLDescriptor();
            descriptor.setJavaClass(QNameHolder.class);
            descriptor.setDefaultRootElement("qnameholder");

            XMLDirectMapping qnameMapping = new XMLDirectMapping();
            qnameMapping.setAttributeName("theQName");
            qnameMapping.setXPath("the-qname/text()");
            ((XMLField)qnameMapping.getField()).setSchemaType(XMLConstants.QNAME_QNAME);
            descriptor.addMapping(qnameMapping);

            XMLCompositeDirectCollectionMapping qnamesMapping = new XMLCompositeDirectCollectionMapping();
            qnamesMapping.setAttributeName("theQNames");
            qnamesMapping.setXPath("the-qnames/item/text()");
            ((XMLField)qnamesMapping.getField()).setSchemaType(XMLConstants.QNAME_QNAME);
            descriptor.addMapping(qnamesMapping);

            XMLCompositeDirectCollectionMapping qnames2Mapping = new XMLCompositeDirectCollectionMapping();
            qnames2Mapping.setAttributeName("theQNames2");
            qnames2Mapping.setXPath("the-qnames/item2/text()");
            ((XMLField)qnames2Mapping.getField()).setSchemaType(XMLConstants.QNAME_QNAME);
            ((XMLField)qnames2Mapping.getField()).setUsesSingleNode(true);
            descriptor.addMapping(qnames2Mapping);

            NamespaceResolver nr = new NamespaceResolver();
            nr.put("somePrefix", "someURI");
            nr.put("xsd", "http://www.w3.org/2001/XMLSchema");
            nr.setDefaultNamespaceURI("mydefaultnamespace");
            descriptor.setNamespaceResolver(nr);

            return descriptor;
          }
}
