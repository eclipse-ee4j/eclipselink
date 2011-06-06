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
 * dmccann - December 01/2010 - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.choice.ref;

public class Address {

    public String id;
    
    public String street;
    
    public String zip;
    
    public boolean equals(Object obj) {
        Address addr = (Address)obj;
        
        return id.equals(addr.id)&& street.equals(addr.street) && zip.equals(addr.zip); 
    }
}
