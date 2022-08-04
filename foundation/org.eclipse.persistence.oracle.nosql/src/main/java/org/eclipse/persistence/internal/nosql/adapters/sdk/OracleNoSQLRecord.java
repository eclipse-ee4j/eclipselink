/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.nosql.adapters.sdk;

import java.util.HashMap;

import jakarta.resource.cci.MappedRecord;

/**
 * Simple mapped record.
 *
 * @author Radek Felcman
 * @since EclipseLink 4.0
 */
public class OracleNoSQLRecord extends HashMap implements MappedRecord {

    public static final String SORT = "$sort";

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

    public OracleNoSQLRecord(String name) {
        super();
        this.name = name;
        this.description = "Oracle NoSQL key/value data";
    }

    @Override
    public String getRecordShortDescription() {
        return description;
    }

    @Override
    public void setRecordShortDescription(String description) {
        this.description = description;
    }

    @Override
    public String getRecordName() {
        return name;
    }

    @Override
    public void setRecordName(String name) {
        this.name = name;
    }

    @Override
    public Object get(Object key) {
        Object result = super.get(key);
        if (result == null) {
            result = super.get(((String)key).toUpperCase());
        }
        if (result == null) {
            result = super.get(((String)key).toLowerCase());
        }
        return result;
    }

}
