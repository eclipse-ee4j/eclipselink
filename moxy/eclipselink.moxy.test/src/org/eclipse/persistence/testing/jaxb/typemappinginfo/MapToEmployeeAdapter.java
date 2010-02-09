/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * mmacivor - December 15/2009 - 2.0.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MapToEmployeeAdapter extends XmlAdapter<Employee, Map>{

    public Employee marshal(Map v) throws Exception {
        Employee emp = new Employee();
        emp.firstName = "John";
        emp.lastName = "Doe";
        return emp;
    }

    public Map unmarshal(Employee v) throws Exception {
        Map map = new HashMap();
        map.put("firstName", "John");
        map.put("lastName", "Doe");
        return map;
    }

}
