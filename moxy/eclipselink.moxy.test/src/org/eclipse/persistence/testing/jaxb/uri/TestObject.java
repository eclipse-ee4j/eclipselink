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
//     Denise Smith - 2.4 - February 2012
package org.eclipse.persistence.testing.jaxb.uri;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestObject {

    public URI theURI;
    public List<URI> theURIs;

    public boolean equals(Object compareObject){
        if(compareObject instanceof TestObject){
            if(theURI == null){
                if(((TestObject)compareObject).theURI != null){
                    return false;
                }
            }else if(!theURI.equals(((TestObject)compareObject).theURI)){
                return false;
            }
            if(theURIs == null){
                if(((TestObject)compareObject).theURIs != null){
                    return false;
                }
            }else{
                if(!theURIs.containsAll(((TestObject)compareObject).theURIs) || !((TestObject)compareObject).theURIs.containsAll(theURIs)){
                    return false;
                }
            }

            return true;
        }
        return false;
    }
}
