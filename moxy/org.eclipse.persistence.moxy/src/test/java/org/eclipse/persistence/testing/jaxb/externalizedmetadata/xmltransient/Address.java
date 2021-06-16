/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - June 17/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "address")
public class Address {
    public String state;
    public String street;
    public String city;
    public String zip;

    public boolean equals(Object obj) {
        if (obj == null) { return false; }

        Address addObj;
        try {
            addObj = (Address) obj;
        } catch (ClassCastException e) {
            return false;
        }

        if(city == null){
            if(addObj.city != null){
                return false;
            }
        }else if(!city.equals(addObj.city)){
            return false;
        }

        if(street == null){
            if(addObj.street != null){
                return false;
            }
        }else if(!street.equals(addObj.street)){
            return false;
        }

        if(state == null){
            if(addObj.state != null){
                return false;
            }
        }else if(!state.equals(addObj.state)){
            return false;
        }

        if(zip == null){
            if(addObj.zip != null){
                return false;
            }
        }else if(!zip.equals(addObj.zip)){
            return false;
        }

        return true;
    }
}
