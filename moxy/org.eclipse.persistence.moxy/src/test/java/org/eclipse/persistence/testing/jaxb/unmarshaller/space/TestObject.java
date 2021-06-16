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
//     Denise Smith - 2.4 - January 2012
package org.eclipse.persistence.testing.jaxb.unmarshaller.space;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestObject {

    public String theString;
    @XmlAnyElement
    public List<Object> theAny;

    public boolean equals(Object theObject){
        if(theObject instanceof TestObject){
            if(theString == null){
                return ((TestObject)theObject).theString == null;
            }
            return theString.equals(((TestObject)theObject).theString);
        }
        return false;
    }
}
