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
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.nosql.adapters.nosql;

import java.util.HashMap;

import javax.resource.cci.*;

/**
 * Simple mapped record.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class OracleNoSQLRecord extends HashMap implements MappedRecord {
    protected String description;
    protected String name;

    /**
     * Default constructor.
     */
    public OracleNoSQLRecord() {
        super();
        this.name = "Oracle NoSQL record";
        this.description = "Oracle NoSQL key/value data";
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
