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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.proxyindirection;


/*
 * Address interface.
 *
 * Define behavior for Address objects.
 *
 * @author        Rick Barkhouse
 * @since        08/15/2000 15:51:05
 */
public interface Address {
    public String getCity();

    public String getCountry();

    public int getID();

    public String getPostalCode();

    public String getState();

    public String getStreet();

    public void setCity(String value);

    public void setCountry(String value);

    public void setID(int value);

    public void setPostalCode(String value);

    public void setState(String value);

    public void setStreet(String value);
}
