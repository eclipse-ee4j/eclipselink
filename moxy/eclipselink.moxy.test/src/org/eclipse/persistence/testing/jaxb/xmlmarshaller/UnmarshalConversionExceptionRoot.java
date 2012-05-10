/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
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