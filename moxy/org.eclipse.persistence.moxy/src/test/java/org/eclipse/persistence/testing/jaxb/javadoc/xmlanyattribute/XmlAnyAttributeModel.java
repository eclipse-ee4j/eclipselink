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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlanyattribute;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

@XmlRootElement
public class XmlAnyAttributeModel {

    @XmlAnyAttribute
    public Map<QName,Object> any;
    public String title;

        public Map<QName,Object> getAny(){
            if( any == null ){
                any = new HashMap<QName,Object>();
            }
            return any;
        }

        @XmlElement
        public String getTitle(){
            return title;
        }
        public void setTitle( String value ){
            title = value;
        }

         public boolean equals(Object object) {
             XmlAnyAttributeModel x = ((XmlAnyAttributeModel)object);
            //return x.getTitle().equals(this.getTitle());
             return x.getAny().equals(this.getAny()) && x.getTitle().equals(this.getTitle());
        }
}
