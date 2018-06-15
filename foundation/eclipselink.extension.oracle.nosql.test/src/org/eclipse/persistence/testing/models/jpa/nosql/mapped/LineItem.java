/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.nosql.mapped;

import java.io.Serializable;
import java.math.*;

//import javax.persistence.Embeddable;

//import org.eclipse.persistence.annotations.DataFormatType;
//import org.eclipse.persistence.annotations.NoSql;

/**
 * Model line item class, maps to LINE record.
 */
// The mapped format does not currently support collections.
//@Embeddable
//@NoSql(dataFormat=DataFormatType.MAPPED)
public class LineItem implements Serializable {
    public long lineNumber;
    public String itemName;
    public long quantity;
    public BigDecimal itemPrice;

    public String toString() {
        return "LineItem(" + lineNumber + ", " + itemName + ", " + quantity + ", " + itemPrice + ")";
    }
}
