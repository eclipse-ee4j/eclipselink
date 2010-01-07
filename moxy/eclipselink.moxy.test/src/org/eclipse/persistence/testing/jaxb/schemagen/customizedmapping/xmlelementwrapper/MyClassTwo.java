/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* dmccann - May 21/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlelementwrapper;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "name-wrapper", namespace = "http://www.example.com/Two")
@XmlType(namespace = "http://www.example.com/Two")
public class MyClassTwo {
    public javax.xml.namespace.QName qName;
}
