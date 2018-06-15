/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - March 2013
package org.eclipse.persistence.testing.jaxb.substitution;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementRef;

public class Order {

    @XmlElementRef(name = "person", namespace = "myNamespace", type = JAXBElement.class)
    protected JAXBElement<Person> client;

}
