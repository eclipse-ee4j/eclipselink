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
//     bdoughan - July 21/2010 - Initial implementation
package org.eclipse.persistence.testing.jaxb.interfaces;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public interface PhoneNumber {

    @XmlAttribute()
    String getType();
    void setType(String type);

    @XmlValue()
    String getValue();
    void setValue(String value);

}
