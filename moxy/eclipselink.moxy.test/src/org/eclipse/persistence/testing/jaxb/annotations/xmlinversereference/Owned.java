/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - Matt MacIvor - 9/20/2012 - 2.4.2 - Initial implementation
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
