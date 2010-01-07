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
package org.eclipse.persistence.testing.oxm.descriptor.rootelement.identifiedbyname;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;

import org.eclipse.persistence.testing.oxm.descriptor.rootelement.EmailAddress;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.MailingAddress;

public class RootElementIdentifiedByNameProject extends Project {

  public RootElementIdentifiedByNameProject() {
    super();
    addDescriptor(buildEmailAddressDescriptor());
    addDescriptor(buildMailingAddressDescriptor());
  }

  private XMLDescriptor buildEmailAddressDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(EmailAddress.class);
    descriptor.setDefaultRootElement("email-address");
    
    XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
    schemaReference.setSchemaContext("/emailType");
    descriptor.setSchemaReference(schemaReference);   
   
    return descriptor;
  }

  private XMLDescriptor buildMailingAddressDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(MailingAddress.class);
    descriptor.setDefaultRootElement("mailing-address");
    
    XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
    schemaReference.setSchemaContext("/mailingType");
    descriptor.setSchemaReference(schemaReference);   
    return descriptor;
  }

}
