/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 30 April 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.xmlpath;

public class CanadianAddress extends Address {

    public String province;
    public String postalCode;

    public boolean equals(Object obj) {
        return province.equals(((CanadianAddress) obj).province) && postalCode.equals(((CanadianAddress) obj).postalCode) && super.equals(obj);
    }

}
