/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith  February, 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.map;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Root {


    @XmlElement(name="map1")
    public Map<String, String> stringStringMap;
    @XmlElement(name="map2")
    public Map<Integer,ComplexValue > integerComplexValueMap;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Root root = (Root) o;

        if (integerComplexValueMap != null ? !integerComplexValueMap.equals(root.integerComplexValueMap) : root.integerComplexValueMap != null)
            return false;
        if (stringStringMap != null ? !stringStringMap.equals(root.stringStringMap) : root.stringStringMap != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stringStringMap != null ? stringStringMap.hashCode() : 0;
        result = 31 * result + (integerComplexValueMap != null ? integerComplexValueMap.hashCode() : 0);
        return result;
    }
}
