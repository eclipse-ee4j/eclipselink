/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.eis.cobol;


/**
* <b>Purpose</b>: to allow on demand conversions of fields which are redefined.  This is
* necessary because different data formats may be used by different fields that redefine the
* same area of a byte array.
*/
public class CobolRedefinedFieldValue {

    /** byte array the represents the data for the record */
    private byte[] myRecordData;

    /** field meta data associated with the redefine */
    FieldMetaData myFieldMetaData;

    public CobolRedefinedFieldValue() {
        initialize();
    }

    public CobolRedefinedFieldValue(FieldMetaData metaData, byte[] recordData) {
        initialize(metaData, recordData);
    }

    protected void initialize(FieldMetaData metaData, byte[] recordData) {
        myRecordData = recordData;
        myFieldMetaData = metaData;
    }

    /**
    * getter for field meta data
    */
    public FieldMetaData getFieldMetaData() {
        return myFieldMetaData;
    }

    /**
    * setter for field meta data
    */
    public void setFieldMetaData(FieldMetaData newFieldMetaData) {
        myFieldMetaData = newFieldMetaData;
    }

    /**
    * getter for record data
    */
    public byte[] getRecordData() {
        return myRecordData;
    }

    /**
    * setter for record data
    */
    public void setRecordData(byte[] newRecordData) {
        myRecordData = newRecordData;
    }

    protected void initialize() {
    }

    /**
    * calculates and extracts the value from the byte array myRecordData
    */
    public Object getValue() {
        return getFieldMetaData().extractValueFromArray(getRecordData());
    }
}
