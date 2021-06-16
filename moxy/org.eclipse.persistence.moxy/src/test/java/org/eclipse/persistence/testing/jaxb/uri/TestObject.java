/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.4 - February 2012
package org.eclipse.persistence.testing.jaxb.uri;

import java.net.URI;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;

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
