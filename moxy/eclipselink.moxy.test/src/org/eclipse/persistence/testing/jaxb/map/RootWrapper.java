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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="root")
@XmlType(name="root")
public class RootWrapper {


    @XmlElementWrapper(name="map1")
    public Map<String, String> stringStringMap;
    @XmlElementWrapper(name="map2")
    public Map<Integer,ComplexValue > integerComplexValueMap;
    @XmlElement(name="map3")
    public Map<String,String[] > stringArrayMap;
    @XmlElement(name="map4")
    public Map<String,List<String>> stringListMap;

     public boolean equals(Object obj){
            if(!(obj instanceof RootWrapper)) {
                return false;
            }
            RootWrapper compare = (RootWrapper)obj;
            if (stringArrayMap != null ? compare.stringArrayMap == null || !Arrays.deepEquals(stringArrayMap.keySet().toArray(), compare.stringArrayMap.keySet().toArray()) || !Arrays.deepEquals(stringArrayMap.values().toArray(), compare.stringArrayMap.values().toArray()) : compare.stringArrayMap != null)
                return false;
            if (stringListMap != null ? compare.stringListMap == null || !Arrays.deepEquals(stringListMap.keySet().toArray(), compare.stringListMap.keySet().toArray()) || !Arrays.deepEquals(stringListMap.values().toArray(), compare.stringListMap.values().toArray()) : compare.stringListMap != null)
                return false;
            if (integerComplexValueMap != null ? !integerComplexValueMap.equals(compare.integerComplexValueMap) : compare.integerComplexValueMap != null)
                return false;
            return stringStringMap.equals(compare.stringStringMap) && integerComplexValueMap.equals(compare.integerComplexValueMap);
      }
}
