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
 *     dminsky - initial API and implementation
 ******************************************************************************/ 
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Vector;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.mappings.foundation.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class MutableAttributeObject {

    protected int id;
    
    // supported byte[] type
    protected byte[] byteArray;
    
    // java.util types
    protected java.util.Calendar utilCalendar;
    protected java.util.Date utilDate;
    
    // java.sql types
    protected java.sql.Date sqlDate;
    protected java.sql.Time sqlTime;
    protected java.sql.Timestamp sqlTimestamp;
    
    // custom java.util.Date subclass
    protected DateSubclass dateSubclass;

    public MutableAttributeObject() {
        super();
    }
    
    public static MutableAttributeObject example1() {
        MutableAttributeObject container = new MutableAttributeObject();
        
        long timeNow = System.currentTimeMillis();

        // byte[] with all values set to 1
        byte[] someBytes = new byte[128];
        java.util.Arrays.fill(someBytes, (byte)1);
        container.setByteArray(someBytes);
        
        // java.util types
        container.setUtilDate(new java.util.Date(timeNow));
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(timeNow);
        container.setUtilCalendar(cal);
        
        // java.sql types
        container.setSqlTime(new java.sql.Time(timeNow));
        container.setSqlDate(new java.sql.Date(timeNow));
        container.setSqlTimestamp(new java.sql.Timestamp(timeNow));
        
        // custom date subclass
        container.setDateSubclass(new DateSubclass());
        return container;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public java.util.Calendar getUtilCalendar() {
        return utilCalendar;
    }

    public void setUtilCalendar(java.util.Calendar utilCalendar) {
        this.utilCalendar = utilCalendar;
    }

    public java.util.Date getUtilDate() {
        return utilDate;
    }

    public void setUtilDate(java.util.Date utilDate) {
        this.utilDate = utilDate;
    }

    public java.sql.Time getSqlTime() {
        return sqlTime;
    }

    public void setSqlTime(java.sql.Time sqlTime) {
        this.sqlTime = sqlTime;
    }

    public java.sql.Timestamp getSqlTimestamp() {
        return sqlTimestamp;
    }

    public void setSqlTimestamp(java.sql.Timestamp sqlTimestamp) {
        this.sqlTimestamp = sqlTimestamp;
    }

    public java.sql.Date getSqlDate() {
        return sqlDate;
    }

    public void setSqlDate(java.sql.Date sqlDate) {
        this.sqlDate = sqlDate;
    }

    public DateSubclass getDateSubclass() {
        return dateSubclass;
    }

    public void setDateSubclass(DateSubclass dateSubclass) {
        this.dateSubclass = dateSubclass;
    }
    
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* define the class, table and descriptor properties. */
        descriptor.setJavaClass(MutableAttributeObject.class);
        descriptor.setTableName("MUTABLE_TYPE");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");

        /* Next define the direct mappings. */
        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("byteArray", "BYTE_ARRAY");
        descriptor.addDirectMapping("dateSubclass", "DATE_SUBCLASS");
        descriptor.addDirectMapping("sqlDate", "SQL_DATE");
        descriptor.addDirectMapping("sqlTime", "SQL_TIME");
        descriptor.addDirectMapping("sqlTimestamp", "SQL_TIMESTAMP");
        descriptor.addDirectMapping("utilCalendar", "UTIL_CALENDAR");
        descriptor.addDirectMapping("utilDate", "UTIL_DATE");

        /* Configure all of the mappings as mutable, except the id mapping */
        Vector mappings = descriptor.getMappings();
        for (int i = 0; i < mappings.size(); i++) {
            AbstractDirectMapping mapping = (AbstractDirectMapping)mappings.get(i);
            if (!mapping.getAttributeName().equalsIgnoreCase("id")) {
                mapping.setIsMutable(true);
            }
        }

        return descriptor;
    }
    
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("MUTABLE_TYPE");

        // identity field
        definition.addIdentityField("ID", java.lang.Integer.class);
        
        // regular direct mapping fields
        definition.addField("BYTE_ARRAY", java.sql.Blob.class); // byte[] mapping to BLOB
        definition.addField("DATE_SUBCLASS", java.sql.Date.class);
        definition.addField("SQL_DATE", java.sql.Date.class);
        definition.addField("SQL_TIME", java.sql.Time.class);
        definition.addField("SQL_TIMESTAMP", java.sql.Timestamp.class);
        definition.addField("UTIL_CALENDAR", java.sql.Timestamp.class);
        definition.addField("UTIL_DATE", java.sql.Date.class);

        return definition;
    }
    
}
