/*******************************************************************************
 * Copyright (c) 1998, 2014 Oracle and/or its affiliates. All rights reserved.
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

        return mapEquals(stringArrayMap, root.stringArrayMap)
                && mapEquals(stringListMap, root.stringListMap)
                && mapEquals(integerComplexValueMap, root.integerComplexValueMap)
                && mapEquals(stringStringMap, root.stringStringMap);
    }

    private boolean mapEquals(Map my, Map other) {
        return my == null ? other == null : other != null
                && Arrays.deepEquals(my.keySet().toArray(), other.keySet().toArray())
                && Arrays.deepEquals(my.values().toArray(), other.values().toArray());
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
