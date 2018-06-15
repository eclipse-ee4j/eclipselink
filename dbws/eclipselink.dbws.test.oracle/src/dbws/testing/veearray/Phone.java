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

package dbws.testing.veearray;

public class Phone {

    public String areaCode;
    public String phonenumber;
    public String type;

    public Phone() {
        super();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
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
