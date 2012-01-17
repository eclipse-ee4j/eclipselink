/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
