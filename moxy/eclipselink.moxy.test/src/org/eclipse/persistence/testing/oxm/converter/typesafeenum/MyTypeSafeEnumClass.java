/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.converter.typesafeenum;

import java.util.HashMap;
import java.util.Map;

public class MyTypeSafeEnumClass {
    private final static Map valueMap = new HashMap();
    private final String value;
    public final static String femaleString = "female";
    public final static MyTypeSafeEnumClass femaleEnum = new MyTypeSafeEnumClass(femaleString);
    public final static String maleString = "male";
    public final static MyTypeSafeEnumClass maleEnum = new MyTypeSafeEnumClass(maleString);
    public final static String smallString = "small";
    public final static MyTypeSafeEnumClass smallEnum = new MyTypeSafeEnumClass(smallString);
    public final static String mediumString = "medium";
    public final static MyTypeSafeEnumClass mediumEnum = new MyTypeSafeEnumClass(mediumString);
    public final static String largeString = "large";
    public final static MyTypeSafeEnumClass largeEnum = new MyTypeSafeEnumClass(largeString);

    protected MyTypeSafeEnumClass(String v) {
        value = v;
        valueMap.put(v, this);
    }

    public java.lang.String toString() {
        return value;
    }

    public java.lang.String getValue() {
        return value;
    }

    public static MyTypeSafeEnumClass fromValue(java.lang.String value) {
        MyTypeSafeEnumClass obj = ((MyTypeSafeEnumClass)valueMap.get(value));
        if (obj == null) {
            throw new java.lang.IllegalArgumentException();
        } else {
            return obj;
        }
    }

    public static MyTypeSafeEnumClass fromString(java.lang.String str) {
        return fromValue(str);
    }
}
