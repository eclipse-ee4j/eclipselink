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
package org.eclipse.persistence.testing.models.jpa.customfeatures;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.xdb.DirectToXMLTypeMapping;

public class XmlDataCustomizer implements DescriptorCustomizer {

    public void customize(final ClassDescriptor descriptor) throws Exception{
         descriptor.removeMappingForAttributeName("resume_xml");
         DirectToXMLTypeMapping mapping1 = new DirectToXMLTypeMapping();
         mapping1.setAttributeName("resume_xml");
         mapping1.setFieldName("XMLDATA");
         descriptor.addMapping(mapping1);

         descriptor.removeMappingForAttributeName("resume_dom");
         DirectToXMLTypeMapping mapping2 = new DirectToXMLTypeMapping();
         mapping2.setAttributeName("resume_dom");
         mapping2.setFieldName("XMLDOM");
         descriptor.addMapping(mapping2);
     }
}

