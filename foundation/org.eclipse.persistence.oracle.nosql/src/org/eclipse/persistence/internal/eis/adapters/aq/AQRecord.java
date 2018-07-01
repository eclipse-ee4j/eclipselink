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
//     Oracle - initial API and implementation from Oracle TopLink
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
