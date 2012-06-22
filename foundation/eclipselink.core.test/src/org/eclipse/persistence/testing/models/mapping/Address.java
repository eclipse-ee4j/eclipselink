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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.mapping;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Address implements Serializable {
    public Number id;
    public String location;
    public Employee employee;
    public Vector employeeRows;
    public String province;

    public Address() {
    }

    public Object copy() {
        return new Address();
    }

    public static Address example1() {
        Address example = new Address();

        //please keep the province in capitals	
        example.setLocation("OTTAWA");
        example.setProvince("ONTARIO");
        return example;
    }

    public static Address example2() {
        Address example = new Address();

        //Please keep the province in capitals 	
        example.setLocation("Montreal");
        example.setProvince("QUEBEC");
        return example;
    }

    public Employee getEmployee() {
        return employee;
    }

    public String getProvince() {
        return province;
    }

    public String getProvinceFromObject() {
        String province = "";
        if (getProvince() == null) {
            return null;
        }

        if (getProvince().equals("ONTARIO")) {
            province = "ON";
        }
        if (getProvince().equals("QUEBEC")) {
            province = "QUE";
        }
        return province;
    }

    public String getProvinceFromRow(Record row, Session session) {
        String code = (String)row.get("PROVINCE");
        String provinceString = null;

        // This method was causing this model to fail randomly because
        // the read for employee cause the newly commited objects to be read
        // into the cache before the merge, so the merge did not merge into the
        // originals used for testing compares.
        //this.employee = ((Employee) session.readObject(Employee.class)));
        // The test is basically verifying that the session can be used in transforms
        // so just do an SQL read.
        this.employeeRows = session.executeSelectingCall(new SQLCall("Select * from MAP_EMP"));

        if (code == "ON") {
            provinceString = new String("ONTARIO");
        }
        if (code == "QUE") {
            provinceString = new String("QUEBEC");
        }
        return provinceString;
    }

    public void setEmployee(Employee anEmployee) {
        this.employee = anEmployee;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("MAP_ADD");

        definition.addIdentityField("A_ID", java.math.BigDecimal.class, 15);
        definition.addField("LOCATION", String.class, 15);
        definition.addField("PROVINCE", String.class, 3);
        return definition;
    }

    public String toString() {
        return "Address(" + location + "--" + province + ")";
    }
}
