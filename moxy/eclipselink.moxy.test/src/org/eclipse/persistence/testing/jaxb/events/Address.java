/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.events;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

public class Address {
    public String street;
    
    public boolean equals(Object obj) {
        if(!(obj instanceof Address)) {
            return false;
        }
        String objStreet = ((Address)obj).street;
        return objStreet == street || (objStreet != null && street != null && objStreet.equals(street));
    }
    
    public void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) {
        ((Employee)parent).triggeredEvents.add(JAXBUnmarshalListenerImpl.ADDRESS_BEFORE_UNMARSHAL);        
    }
    
    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        ((Employee)parent).triggeredEvents.add(JAXBUnmarshalListenerImpl.ADDRESS_AFTER_UNMARSHAL);                
    }    
    
    
}
