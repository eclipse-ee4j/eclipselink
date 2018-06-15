/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.events;
/**
 *  @version $Header: Address.java 17-may-2006.14:00:56 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class Address {
    public String street;

    public boolean equals(Object obj) {
        if(!(obj instanceof Address)) {
            return false;
        }
        String objStreet = ((Address)obj).street;
        return objStreet == street || (objStreet != null && street != null && objStreet.equals(street));
    }
}
