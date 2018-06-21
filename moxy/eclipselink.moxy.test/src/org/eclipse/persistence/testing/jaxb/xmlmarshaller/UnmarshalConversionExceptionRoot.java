/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class UnmarshalConversionExceptionRoot {

    public int a;
    public int b;
    public int c;
    public int d;

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        UnmarshalConversionExceptionRoot test = (UnmarshalConversionExceptionRoot) obj;
        return a == test.a && b == test.b && c == test.c && d == test.d;
    }

}
