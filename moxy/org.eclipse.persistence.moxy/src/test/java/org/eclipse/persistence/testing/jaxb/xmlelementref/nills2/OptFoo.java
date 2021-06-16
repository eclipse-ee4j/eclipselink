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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlelementref.nills2;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OptFoo {
  
  @XmlElementRef(name = "bar", namespace = "NS", type = JAXBElement.class, required = false)
  protected JAXBElement<Bar> bar;
  
  public OptFoo() {}
  
  public OptFoo(final JAXBElement<Bar> bar) {
    this.bar = bar;
  }
  
  public JAXBElement<Bar> getBar() {
    return bar;
  }
  
  public void setBar(final JAXBElement<Bar> bar) {
    this.bar = bar;
  }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OptFoo)) {
            return false;
        }
        OptFoo e = (OptFoo) obj;
        if (!isEqual(e.getBar(), this.getBar())) {
            return false;
        }
        return true;
    }

    private boolean isEqual(JAXBElement<?> e1, JAXBElement<?> e2) {
        return e1.getName().equals(e2.getName()) &&
                e1.getDeclaredType().equals(e2.getDeclaredType()) &&
                (e1.isNil() == e2.isNil()) &&
                (e1.getValue()).equals(e2.getValue());
    }

}
