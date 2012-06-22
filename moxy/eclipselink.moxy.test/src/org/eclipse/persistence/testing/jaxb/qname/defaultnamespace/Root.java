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
 *     Matt MacIvor - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.qname.defaultnamespace;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

@XmlRootElement(name="root", namespace="myns")
public class Root {
    public QName qname;
    
    public List<QName> listOfNames;
    
    public boolean equals(Object obj) {
        if(!(obj instanceof Root)) {
            return false;
        }
        
        Root root = (Root)obj;
        return this.qname.equals(root.qname) && listOfNames.equals(root.listOfNames);
    }

}
