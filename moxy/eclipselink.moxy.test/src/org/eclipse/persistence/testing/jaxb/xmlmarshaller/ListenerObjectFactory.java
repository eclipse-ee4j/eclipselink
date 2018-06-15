/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ListenerObjectFactory {

    @XmlElementDecl(name="ledo")
    public JAXBElement<ListenerElementDeclObject> createElementDecl(ListenerElementDeclObject ledo) {
        return new JAXBElement<ListenerElementDeclObject>(new QName("ledo"), ListenerElementDeclObject.class, ledo);
    }

}
