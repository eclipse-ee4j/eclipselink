/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.superclassoverride;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SubClass extends SpecialMap<String, String> {

    @XmlElement(name="myProperty")
    public String getMyProperty() {
        return super.get("myProperty");
    }

    public void setMyProperty(String value) {
        super.put("myProperty", value);
    }

    public boolean equals(Object subClass) {
        return getMyProperty().equals(((SubClass)subClass).getMyProperty());
    }
}
