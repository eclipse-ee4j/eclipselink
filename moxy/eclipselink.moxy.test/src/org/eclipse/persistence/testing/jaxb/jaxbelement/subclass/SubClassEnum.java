/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - January 2012 - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbelement.subclass;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.jaxbelement.enumeration.Coin;

@XmlRootElement(name = "root")
public class SubClassEnum extends JAXBElement<Coin> {

    public SubClassEnum(QName name, Class<Coin> declaredType, Coin value) {
        super(name, declaredType, value);
    }
    /*
    public SubClassEnum(QName name, Class<Coin> declaredType, Coin value) {
        super(name, declaredType, value);
    }
    */
    public SubClassEnum() {    	
        this(new QName("root"), Coin.class, Coin.QUARTER);
    }

}
