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
package org.eclipse.persistence.testing.oxm.descriptor.rootelement.multipleroots;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.EmailAddress;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.MailingAddress;

public class MultipleRootsProject  extends Project {

  public MultipleRootsProject() {
    super();
    addDescriptor(buildMailingAddressDescriptor());
  }

  private XMLDescriptor buildMailingAddressDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(MailingAddress.class);
    descriptor.setDefaultRootElement("mailing-address");
    descriptor.addRootElement("theAddress");
    descriptor.addRootElement("someAddress");
    descriptor.setDefaultRootElement("theAddress");

    XMLDirectMapping mapping = new XMLDirectMapping();
    mapping.setAttributeName("street");
    mapping.setXPath("street");
    descriptor.addMapping(mapping);
    return descriptor;
  }
}
