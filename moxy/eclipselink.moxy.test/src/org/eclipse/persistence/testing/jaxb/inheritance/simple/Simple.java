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
//    Denise Smith - June 2012
package org.eclipse.persistence.testing.jaxb.inheritance.simple;

import javax.xml.bind.annotation.XmlValue;

public class Simple {
    @XmlValue
    String foo;

    public boolean equals(Object compareObject){
        if(compareObject instanceof Simple){
            if(foo == null){
                return ((Simple)compareObject).foo == null;
            }
            return foo.equals(((Simple)compareObject).foo);
        }
        return false;
    }
}
