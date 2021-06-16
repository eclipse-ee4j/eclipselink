/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - 2.7.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.type.modelns;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
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
