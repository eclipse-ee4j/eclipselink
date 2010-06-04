/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - November 27/2009 - 2.0 - Initial implementation
 ******************************************************************************/
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
