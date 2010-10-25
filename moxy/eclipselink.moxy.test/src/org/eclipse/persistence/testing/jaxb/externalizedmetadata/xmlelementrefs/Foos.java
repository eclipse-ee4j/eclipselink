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
 * dmccann - December 04/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs;

import java.util.List;

import javax.xml.bind.JAXBElement;

//@javax.xml.bind.annotation.XmlRootElement(name="my-foos")
public class Foos {
    //@javax.xml.bind.annotation.XmlElementWrapper(name="items")
    //@javax.xml.bind.annotation.XmlElementRefs({
    //    @javax.xml.bind.annotation.XmlElementRef(name="integer-root", namespace="myns"), 
    //    @javax.xml.bind.annotation.XmlElementRef(name="root")
    //})
    public List<JAXBElement> items;
}
