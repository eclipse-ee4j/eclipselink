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
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmltype;

import javax.xml.bind.annotation.*;


public class USAddressFactory {
    
    public static USAddress getUSAddress(){
        return new USAddress("Mark Baker", "23 Elm St", 
           "Dayton", "OH", 90952);

    
    
 /*   public boolean equals(Object obj) {
        USAddress addr = (USAddress)obj;
        return name.equals(addr.name) && city.equals(addr.city) && street.equals(addr.street) && state.equals(addr.state);
    }
    */
}
}

