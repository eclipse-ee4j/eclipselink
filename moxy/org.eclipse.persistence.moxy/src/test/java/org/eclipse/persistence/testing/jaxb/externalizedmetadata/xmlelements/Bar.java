/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - November 27/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelements;

import java.util.List;

//@jakarta.xml.bind.annotation.XmlRootElement
public class Bar {
    //@jakarta.xml.bind.annotation.XmlIDREF
    //@jakarta.xml.bind.annotation.XmlElementWrapper(name="contact")
    //@jakarta.xml.bind.annotation.XmlElements({
    //    @jakarta.xml.bind.annotation.XmlElement(name="address", type=Address.class),
    //    @jakarta.xml.bind.annotation.XmlElement(name="phone", type=Phone.class)
    //})
    public List items;
}
