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
//    Denise Smith - February 20, 2013
package org.eclipse.persistence.testing.jaxb.xmlschematype;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="holder")
public class XSDTypesHolder {

    public List<Object> things;

    public boolean equals(Object obj){
        if(obj instanceof XSDTypesHolder){
            for(int i=0;i<things.size(); i++){
                Object next = things.get(i);
                Object nextCompare = ((XSDTypesHolder)obj).things.get(i);
                if(next instanceof byte[] && nextCompare instanceof byte[]){
                    if(!(Arrays.equals((byte[] )next, (byte[] )nextCompare))){
                        return false;
                    }
                }else if(!next.equals(nextCompare)){
                    return false;
                }
            }
            return true;

        }
        return false;
    }
}
