/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - Matt MacIvor - 9/20/2012 - 2.4.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

public class Owned {
    public Owner owner;
    
    public boolean equals(Object obj) {
        Owned owned = (Owned)obj;
        if(owner != null && owned.owner != null) {
            return true;
        }
        if(owner == owned.owner) {
            return true;
        }
        return false;
    }
}
