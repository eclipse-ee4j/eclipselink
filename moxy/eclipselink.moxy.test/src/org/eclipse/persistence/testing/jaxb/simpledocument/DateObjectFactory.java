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
// mmacivor - April 25/2008 - 1.0M8 - Initial implementation
package org.eclipse.persistence.testing.jaxb.simpledocument;

import javax.xml.bind.annotation.*;
import javax.xml.bind.*;
import javax.xml.namespace.*;
import java.util.Date;

@XmlRegistry
public class DateObjectFactory {

    @XmlElementDecl(namespace = "myns", name = "dateroot")
    public JAXBElement<Date> createDateRoot() {
        return new JAXBElement(new QName("myns", "dateroot"), Date.class, null);
    }

}
