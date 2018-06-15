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
//     Denise Smith - 2.4 - January 2013
package org.eclipse.persistence.testing.jaxb.xmlelement.nulls;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Something {

    public boolean equals(Object obj){
        if(obj instanceof Something){
            return true;
        }
        return false;
    }
}
