/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - August 26/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmljoinnode;

import org.eclipse.persistence.oxm.annotations.XmlJoinNode;
import org.eclipse.persistence.oxm.annotations.XmlJoinNodes;

public class Employee {
    public String id;
    
    // the following is invalid and should be overridden by XML metadata
    @XmlJoinNodes({
        @XmlJoinNode(xmlPath="waddress/@id", referencedXmlPath="@id"),
        @XmlJoinNode(xmlPath="waddress/city/text()", referencedXmlPath="city/text()")
    })
    public Address workAddress;
    
    public Employee() {}
    
    public Employee(String id, Address workAddress) {
        this.id = id;
        this.workAddress = workAddress;
    }
}
