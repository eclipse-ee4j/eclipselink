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
//     Denise Smith -  January 2014
package org.eclipse.persistence.testing.jaxb.typemappinginfo.pkg2;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Thing {

    public int something;

    public boolean equals(Object obj){
        if(obj instanceof Thing){
            return something == ((Thing)obj).something;
        }
        return false;
    }
}
