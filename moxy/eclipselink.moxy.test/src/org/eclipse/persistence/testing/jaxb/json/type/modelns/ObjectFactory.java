/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - 2.7.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.type.modelns;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
  
  @XmlElementDecl(namespace = Namespace.NAME, name = "koo-Root")
  public JAXBElement<KooSuper> createKoo(KooSuper value) {
    return new JAXBElement<KooSuper>(new QName(Namespace.NAME, "koo-Root"), KooSuper.class, null, value);
  }
  
  @XmlElementDecl(namespace = Namespace.NAME, name = "tel-Root")
  public JAXBElement<Tel> createTel(Tel value) {
    return new JAXBElement<Tel>(new QName(Namespace.NAME, "tel-Root"), Tel.class, null, value);
  }
  
}
