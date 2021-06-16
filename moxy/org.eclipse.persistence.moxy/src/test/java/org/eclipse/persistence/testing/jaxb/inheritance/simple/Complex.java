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
//    Denise Smith - June 2012
package org.eclipse.persistence.testing.jaxb.inheritance.simple;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Complex extends Simple {

     @XmlAttribute
     String bar;

     public boolean equals(Object compareObject){
        if(compareObject instanceof Complex){
            if(!super.equals(compareObject)){
                return false;
            }
            if(bar == null){
                return ((Complex)compareObject).bar == null;
            }
            return bar.equals(((Complex)compareObject).bar);
        }
        return false;
     }

}
