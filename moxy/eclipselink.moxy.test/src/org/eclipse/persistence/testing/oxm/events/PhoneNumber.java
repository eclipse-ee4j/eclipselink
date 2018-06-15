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
 *  @version $Header: PhoneNumber.java 17-may-2006.14:01:33 mmacivor Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class PhoneNumber {
    public String number;

    public boolean equals(Object obj) {
        if(!(obj instanceof PhoneNumber)) {
            return false;
        }
        String objNum = ((PhoneNumber)obj).number;
        return objNum == number || (objNum != null && number != null && objNum.equals(number));
    }
}
