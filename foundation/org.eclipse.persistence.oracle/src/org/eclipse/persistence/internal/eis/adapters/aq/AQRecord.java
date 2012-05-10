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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.eis.adapters.aq;

import java.util.ArrayList;
import javax.resource.cci.*;

/**
 * Simple indexed record.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class AQRecord extends ArrayList implements IndexedRecord {
    protected String description;
    protected String name;

    /**
     * Default constructor.
     */
    public AQRecord() {
        super();
        this.name = "AQ record";
        this.description = "AQ message data";
    }

    public String getRecordShortDescription() {
        return description;
    }

    public void setRecordShortDescription(String description) {
        this.description = description;
    }

    public String getRecordName() {
        return name;
    }

    public void setRecordName(String name) {
        this.name = name;
    }
}
