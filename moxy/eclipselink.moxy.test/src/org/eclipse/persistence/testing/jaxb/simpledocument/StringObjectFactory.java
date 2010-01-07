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
* mmacivor - April 25/2008 - 1.0M8 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.simpledocument;

import javax.xml.bind.annotation.*;
import javax.xml.bind.*;
import javax.xml.namespace.*;
import java.util.Date;

@XmlRegistry
public class StringObjectFactory {

    @XmlElementDecl(namespace = "myns", name = "stringroot")
    public JAXBElement<String> createStringRoot() {
        return new JAXBElement(new QName("myns", "stringroot"), String.class, null);
    }
}
