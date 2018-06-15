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
//     Mike Norman - May 2008, created DBWS test package
package dbws.testing.relationships;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;

public class RelationshipsEmployee {

    public int empId;
    public String firstName;
    public String lastName;
    public int version;
    public Date startDate;
    public Time startTime;
    public Date endDate;
    public Time endTime;
    public String gender;
    public BigDecimal salary;
    public RelationshipsAddress address;
    public Collection<RelationshipsPhone> phones = new ArrayList<RelationshipsPhone>();
    public Collection<String> responsibilities = new ArrayList<String>();

    public RelationshipsEmployee() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RelationshipsEmployee");
        sb.append("[");
        sb.append(empId);
        sb.append("]");

        return sb.toString();
    }
}
