/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlvalue.attribute;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

public class Middle2 {

    @XmlInverseReference(mappedBy="middle2")
    public Middle1 middle1;

    @XmlValue
    public Bottom bottom;

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        Middle2 test = (Middle2) obj;
        if(!equals(middle1 == null, test.middle1 == null)) {
            return false;
        }
        if(!equals(bottom, test.bottom)) {
            return false;
        }
        return true;
    }

    private boolean equals(Object control, Object test) {
        if(null == control) {
            return null == test;
        }
        return control.equals(test);
    }

}
