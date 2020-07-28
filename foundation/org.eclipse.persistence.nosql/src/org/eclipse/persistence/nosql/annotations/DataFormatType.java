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
//     James Sutherland - initial design and implementation
package org.eclipse.persistence.nosql.annotations;


/**
 * Used to configure the data format type for an EIS descriptor.
 *
 * @see NoSql
 * @see org.eclipse.persistence.eis.EISDescriptor#setDataFormat(String)
 * @author James Sutherland
 * @since EclipseLink 2.4
 */
public enum DataFormatType {
    /**
     * XML data is used.  Objects are converted to XML.
     * This is the default data format.
     */
    XML,

    /**
     * JCA IndexedRecords are used, objects data is decomposed into an array of field values.
     */
    INDEXED,

    /**
     * JCA MappedRecords are used, objects data is decomposed into a Map of key/value pairs.
     */
    MAPPED
}
