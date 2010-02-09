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
 *     Mike Norman - May 2008, created DBWS test package
 ******************************************************************************/

package dbws.testing.oracleobjecttype;

public class Address {

    public Address() {
    }

    public String street;
    public String city;
    public String province;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(street);
        sb.append(" ");
        sb.append(city);
        sb.append(" ");
        sb.append(province);
        return sb.toString();
    }
}
