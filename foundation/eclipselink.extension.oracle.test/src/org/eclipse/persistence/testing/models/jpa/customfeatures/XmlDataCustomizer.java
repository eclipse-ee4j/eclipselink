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

