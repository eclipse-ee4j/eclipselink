/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * rbarkhouse - 2011 March 21 - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlvirtualaccessmethods;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ext-object-root")
public class ExtObjectRoot {

    @XmlElement(name = "objA")
    @XmlElementWrapper(name = "flexObjectAs")
    public ArrayList<ExtObjectA> flexObjectAs = new ArrayList<ExtObjectA>();

    @XmlElement(name = "objB")
    @XmlElementWrapper(name = "flexObjectBs")
    public ArrayList<ExtObjectB> flexObjectBs = new ArrayList<ExtObjectB>();

}