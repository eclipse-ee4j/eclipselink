/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith
package org.eclipse.persistence.testing.jaxb.jaxbcontext.duplicateelem;

import java.math.BigDecimal;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    @XmlElementDecl(namespace= "aa", name="root")
    public JAXBElement<String> createRoot() {
        return new JAXBElement<String>(new QName("root"), String.class, "");
    }

    @XmlElementDecl(namespace= "aa",name="root")
    public JAXBElement<BigDecimal> createRootBD() {
        return new JAXBElement<BigDecimal>(new QName("root"), BigDecimal.class, new BigDecimal(""));
    }

}
