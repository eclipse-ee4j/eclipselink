/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//    Denise Smith - February 20, 2013
package org.eclipse.persistence.testing.jaxb.xmlschematype;

import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;

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
