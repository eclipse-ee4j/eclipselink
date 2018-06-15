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
package org.eclipse.persistence.testing.models.jpa.mongo;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.NoSql;


/**
 * Model Buyer class, maps to BUYER record.
 */
@Entity
@NoSql(dataFormat=DataFormatType.MAPPED)
public class Buyer {
    @Id
    public int id1;
    @Id
    public int id2;

    public String name;

    public String toString() {
        return "Customer(" + name + ")";
    }
}
