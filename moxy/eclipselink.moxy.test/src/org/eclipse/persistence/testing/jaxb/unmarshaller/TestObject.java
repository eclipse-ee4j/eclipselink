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
//     Denise Smith - 2.3.1
package org.eclipse.persistence.testing.jaxb.unmarshaller;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement
public class TestObject {
    @XmlValue
    public byte[] bytes;

    public boolean equals(Object compareObject){
        if(compareObject instanceof TestObject){
            return Arrays.equals(bytes, ((TestObject)compareObject).bytes);
        }
        return false;
    }
}
