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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.directtofield.schematype;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class SchemaTypeProject extends Project {

  public SchemaTypeProject() {
    addDescriptor(getByteHolderDescriptor());
  }

  private XMLDescriptor getByteHolderDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(ByteHolder.class);
    descriptor.setDefaultRootElement("byteholder");
           
    XMLDirectMapping bytesMapping = new XMLDirectMapping();
    bytesMapping.setAttributeName("bytes");
    bytesMapping.setXPath("bytes/text()");
    descriptor.addMapping(bytesMapping);     

    return descriptor;
  }
  
}
