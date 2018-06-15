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
// October 30, 2012
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.oxm.annotations.XmlReadTransformer;
import org.eclipse.persistence.oxm.annotations.XmlTransformation;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformers;

@XmlRootElement
@XmlType(propOrder={"id","anotherID"})
public class SimpleRoot {
    public int id;

    @XmlTransformation
    public int getAnotherID(){
        return 5;
    }

    public boolean equals(Object obj){
        if(obj instanceof SimpleRoot){
            SimpleRoot compareObj = (SimpleRoot)obj;
            return id == compareObj.id;
        }
        return false;
    }
}
