package org.eclipse.persistence.testing.jaxb.xmlvalue.none;

/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
import javax.xml.bind.annotation.XmlElement;

public class InvalidParent {

    private String parentProperty;

    @XmlElement
    public String getParentProperty() {
        return parentProperty;
    }

    public void setParentProperty(String parentProperty) {
        this.parentProperty = parentProperty;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        ValidParent test = (ValidParent) obj;
        if(null == parentProperty) {
            return null == test.getParentProperty();
        } else {
            return parentProperty.equals(test.getParentProperty());
        }
    }

}