/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlschematype;

import javax.xml.bind.annotation.*;

import java.util.Calendar;

@XmlRootElement
public class USPrice {

    @XmlElement(name="start-date")
    @XmlSchemaType(name = "date")
    public Calendar date;

    public boolean equals(Object o) {
        if(!(o instanceof USPrice) || o == null) {
            return false;
        } else {
            return ((USPrice) o).date.getTimeInMillis() == this.date.getTimeInMillis();
        }
    }

    public String toString() {
        return "USPRICE(" + date + ")";
    }
}
