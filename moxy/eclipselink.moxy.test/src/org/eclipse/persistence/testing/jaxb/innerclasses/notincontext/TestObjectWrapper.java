/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - February 2012
package org.eclipse.persistence.testing.jaxb.innerclasses.notincontext;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestObjectWrapper {

    public TestObject testObject;

    public boolean equals (Object compareObject){
        if(compareObject instanceof TestObjectWrapper){
            if(testObject == null){
                return ((TestObjectWrapper)compareObject) == null;
            }
            return testObject.equals(((TestObjectWrapper)compareObject).testObject);
        }
        return false;
    }
}
