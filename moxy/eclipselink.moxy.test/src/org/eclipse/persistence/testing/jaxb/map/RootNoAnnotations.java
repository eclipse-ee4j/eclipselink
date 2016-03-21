/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="root")
@XmlType(name="root")
public class RootNoAnnotations {

	public Map<String, String> stringStringMap;
	public Map<Integer,ComplexValue > integerComplexValueMap;
        public Map<String,String[] > stringArrayMap;
        public Map<String,List<String>> stringListMap;
	
	 public boolean equals(Object obj){
	    	if(!(obj instanceof RootNoAnnotations)) {
	    		return false;
	    	}
                RootNoAnnotations root = (RootNoAnnotations)obj;
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
}
