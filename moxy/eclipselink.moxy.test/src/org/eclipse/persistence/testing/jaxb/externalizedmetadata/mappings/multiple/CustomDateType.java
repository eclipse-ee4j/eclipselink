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
 * dmccann - November 23/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.multiple;

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
