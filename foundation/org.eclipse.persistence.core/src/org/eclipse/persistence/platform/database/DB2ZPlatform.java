/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/19/2015 - Rick Curtis
//       - 462586 : Add national character support for z/OS.
package org.eclipse.persistence.platform.database;

import java.util.Hashtable;

import org.eclipse.persistence.internal.databaseaccess.FieldTypeDefinition;

/**
 * <b>Purpose</b>: Provides DB2 z/OS specific behavior.
 * <p>
 * This provides for some additional compatibility in certain DB2 versions.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li>Support creating tables that handle multibyte characters
 * </ul>
 */
public class DB2ZPlatform extends DB2Platform {
    @Override
    protected Hashtable buildFieldTypes() {
        Hashtable<Class<?>, Object> res = super.buildFieldTypes();
        if (getUseNationalCharacterVaryingTypeForString()) {
            res.put(String.class, new FieldTypeDefinition("VARCHAR", DEFAULT_VARCHAR_SIZE));
        }
        return res;
    }

    @Override
    public String getTableCreationSuffix() {
        // If we're on Z and using unicode support we need to append CCSID
        // UNICODE on the table rather than FOR MIXED DATA on each column
        if (getUseNationalCharacterVaryingTypeForString()) {
            return " CCSID UNICODE";
        }
        return super.getTableCreationSuffix();
    }

}
