/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.4.2
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.norootelement;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement
@XmlSeeAlso(Customer.class)
public class Person {

    public String name;
    
    public boolean equals(Object obj){
        if(obj instanceof Person){
            String otherName = ((Person)obj).name;
            return name == null && otherName == null || name.equals(otherName); 
        }else{
            return false;
        }
    }
}
