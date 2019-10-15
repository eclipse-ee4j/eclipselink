/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Mike Norman - May 2008, created DBWS test package
package dbws.testing.relationships;

public class RelationshipsPhone {

    public String areaCode;
    public String phonenumber;
    public String type;
    public int empId;

    public RelationshipsPhone() {
        super();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RelationshipsPhone");
        sb.append("(");
        sb.append(areaCode);
        sb.append(") ");
        sb.append(phonenumber);
        sb.append(" {");
        sb.append(type);
        sb.append("}");
        return sb.toString();
    }

}
