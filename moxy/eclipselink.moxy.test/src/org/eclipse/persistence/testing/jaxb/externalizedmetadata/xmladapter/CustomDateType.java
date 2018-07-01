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
// dmccann - July 21/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter;

public class CustomDateType {
    public int year;
    public int month;
    public int day;

    public CustomDateType() {}

    public boolean equals(Object obj) {
        if (!(obj instanceof CustomDateType)) {
            return false;
        }
        CustomDateType mcType = (CustomDateType) obj;
        if (mcType.day != day || mcType.month != month || mcType.year != year) {
            return false;
        }
        return true;
    }
}
