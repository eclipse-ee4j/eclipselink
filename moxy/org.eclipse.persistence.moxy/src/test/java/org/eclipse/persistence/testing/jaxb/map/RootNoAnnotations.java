/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Denise Smith  February, 2013
package org.eclipse.persistence.testing.jaxb.map;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name="root")
@XmlType(name="root")
public class RootNoAnnotations {

    public Map<String, String> stringStringMap;
    public Map<Integer,ComplexValue > integerComplexValueMap;
    public Map<String,String[] > stringArrayMap;
    public Map<String,List<String>> stringListMap;

     public boolean equals(Object obj){
            if(!(obj instanceof RootNoAnnotations compare)) {
                return false;
            }
         if (stringArrayMap != null ? compare.stringArrayMap == null || !Arrays.deepEquals(stringArrayMap.keySet().toArray(), compare.stringArrayMap.keySet().toArray()) || !Arrays.deepEquals(stringArrayMap.values().toArray(), compare.stringArrayMap.values().toArray()) : compare.stringArrayMap != null)
                return false;
            if (stringListMap != null ? compare.stringListMap == null || !Arrays.deepEquals(stringListMap.keySet().toArray(), compare.stringListMap.keySet().toArray()) || !Arrays.deepEquals(stringListMap.values().toArray(), compare.stringListMap.values().toArray()) : compare.stringListMap != null)
                return false;
            if (integerComplexValueMap != null ? !integerComplexValueMap.equals(compare.integerComplexValueMap) : compare.integerComplexValueMap != null)
                return false;
            return stringStringMap.equals(compare.stringStringMap) && integerComplexValueMap.equals(compare.integerComplexValueMap);
      }
}
