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
//     Denise Smith  February, 2013
package org.eclipse.persistence.testing.jaxb.map;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Root {


    @XmlElement(name="map1")
    public Map<String, String> stringStringMap;
    @XmlElement(name="map2")
    public Map<Integer,ComplexValue > integerComplexValueMap;
    @XmlElement(name="map3")
    public Map<String,String[] > stringArrayMap;
    @XmlElement(name="map4")
    public Map<String,List<String>> stringListMap;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Root root = (Root) o;

        if (stringArrayMap != null ? root.stringArrayMap == null || !Arrays.deepEquals(stringArrayMap.keySet().toArray(), root.stringArrayMap.keySet().toArray()) || !Arrays.deepEquals(stringArrayMap.values().toArray(), root.stringArrayMap.values().toArray()) : root.stringArrayMap != null)
            return false;
        if (stringListMap != null ? root.stringListMap == null || !Arrays.deepEquals(stringListMap.keySet().toArray(), root.stringListMap.keySet().toArray()) || !Arrays.deepEquals(stringListMap.values().toArray(), root.stringListMap.values().toArray()) : root.stringListMap != null)
            return false;
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
        result = 62 * result + (stringArrayMap != null ? stringArrayMap.hashCode() : 0);
        result = 93 * result + (stringListMap != null ? stringListMap.hashCode() : 0);
        return result;
    }
}
