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
 *     Matt MacIvor - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.objectgraph;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class PhoneNumber {
    
    @XmlAttribute
    public String areaCode;
    
    @XmlValue
    public String number;
    
    public boolean equals(Object obj) {
        PhoneNumber pn = (PhoneNumber)obj;
        
        return (areaCode == pn.areaCode || areaCode.equals(pn.areaCode))
                && (number == pn.number || number.equals(pn.number));
    }
}
