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
package org.eclipse.persistence.testing.oxm.descriptor.rootelement.identifiedbynamespace;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;

import org.eclipse.persistence.testing.oxm.descriptor.rootelement.EmailAddress;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.MailingAddress;

public class RootElementIdentifiedByNamespaceProject extends Project {

  private final static String EMAIL_PREFIX = "email";
  private final static String EMAIL_NAMESPACE = "www.example.com/some-dir/email.xsd";
  private final static String MAILING_PREFIX = "mailing";
  private final static String MAILING_NAMESPACE = "www.example.com/some-dir/mailing.xsd";

  public RootElementIdentifiedByNamespaceProject() {
    super();
    addDescriptor(buildEmailAddressDescriptor());
    addDescriptor(buildMailingAddressDescriptor());
  }

  private XMLDescriptor buildEmailAddressDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(EmailAddress.class);
    descriptor.setDefaultRootElement(EMAIL_PREFIX + ":address");

    NamespaceResolver namespaceResolver = new NamespaceResolver();
    namespaceResolver.put(EMAIL_PREFIX, EMAIL_NAMESPACE);
    descriptor.setNamespaceResolver(namespaceResolver);

    return descriptor;
  }

  private XMLDescriptor buildMailingAddressDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(MailingAddress.class);
    descriptor.setDefaultRootElement(MAILING_PREFIX + ":address");

    NamespaceResolver namespaceResolver = new NamespaceResolver();
    namespaceResolver.put(MAILING_PREFIX, MAILING_NAMESPACE);
    descriptor.setNamespaceResolver(namespaceResolver);

    return descriptor;
  }

}
