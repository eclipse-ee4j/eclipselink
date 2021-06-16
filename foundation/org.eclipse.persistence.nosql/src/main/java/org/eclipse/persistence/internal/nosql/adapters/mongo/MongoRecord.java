/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.nosql.adapters.mongo;

import java.util.HashMap;

import jakarta.resource.cci.*;

/**
 * Simple mapped record.
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoRecord extends HashMap implements MappedRecord {
    public static final String SORT = "$sort";

    protected String description;
    protected String name;

    /**
     * Default constructor.
     */
    public MongoRecord() {
        super();
        this.name = "Mongo record";
        this.description = "Mongo BSon data";
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
}
