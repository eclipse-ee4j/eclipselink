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
// dmccann - November 27/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelements;

import java.util.List;

//@javax.xml.bind.annotation.XmlRootElement
public class Bar {
    //@javax.xml.bind.annotation.XmlIDREF
    //@javax.xml.bind.annotation.XmlElementWrapper(name="contact")
    //@javax.xml.bind.annotation.XmlElements({
    //    @javax.xml.bind.annotation.XmlElement(name="address", type=Address.class),
    //    @javax.xml.bind.annotation.XmlElement(name="phone", type=Phone.class)
    //})
    public List items;
}
