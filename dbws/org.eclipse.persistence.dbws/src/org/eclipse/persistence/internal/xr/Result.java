/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.internal.xr;

// Javase imports

// Java extension imports
import javax.xml.namespace.QName;

// EclipseLink imports
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat;

/**
 * <p><b>INTERNAL</b>: Sub-component of an {@link Operation}, indicates the type
 * of return value from the database, as well as if there is more than one value
 * and if those value(s) will be handled using binary attachements.
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 *
 */
public class Result {

    protected QName type;
    protected Attachment attachment;
    protected SimpleXMLFormat simpleXMLFormat;
    protected Boolean isCollection = null;
    protected Integer jdbcType = null;

    public Result() {
    }
    protected Result(Boolean isCollection) {
        this.isCollection = isCollection;
    }

    public QName getType() {
        return type;
    }
    public void setType(QName type) {
        this.type = type;
    }

    public Attachment getAttachment() {
        return attachment;
    }
    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public SimpleXMLFormat getSimpleXMLFormat() {
        return simpleXMLFormat;
    }
    public void setSimpleXMLFormat(SimpleXMLFormat simpleXMLFormat) {
        this.simpleXMLFormat = simpleXMLFormat;
    }
    public boolean isSimpleXMLFormat () {
        return simpleXMLFormat != null;
    }

    public boolean isCollection () {
        return isCollection == null ? false : isCollection.booleanValue();
    }

    /**
     * Indicates if the JDBC type should be set on the call.
     */
    public boolean isJdbcTypeSet() {
        return jdbcType != null;
    }

    /**
     * Indicates the JDBC type code to be set on the call.
     */
    public int getJdbcType() {
        return jdbcType;
    }

    /**
     * Set the JDBC type code to be set on the call.
     */
    public void setJdbcType(int jdbcType) {
        this.jdbcType = jdbcType;
    }
}
