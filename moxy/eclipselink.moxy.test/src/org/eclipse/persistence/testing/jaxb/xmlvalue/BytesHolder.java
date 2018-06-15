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
//     Denise Smith - initial API and implementation
package org.eclipse.persistence.testing.jaxb.xmlvalue;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="theRoot")
public class BytesHolder {

    @XmlValue
    public byte[] theBytes;

    public boolean equals(Object o) {
        if(!(o instanceof BytesHolder) || o == null) {
            return false;
        } else {
            if (theBytes == null){
                if(((BytesHolder)o).theBytes != null){
                    return false;
                }
            }else {
                if(((BytesHolder)o).theBytes == null){
                    return false;
                }
            }
            if (theBytes.length != ((BytesHolder)o).theBytes.length){
                return false;
            }
            for (int i=0; i<theBytes.length; i++) {
                if (theBytes[i] != ((BytesHolder)o).theBytes[i]) {
                    return false;
                }
            }
        }
        return true;
    }

}
