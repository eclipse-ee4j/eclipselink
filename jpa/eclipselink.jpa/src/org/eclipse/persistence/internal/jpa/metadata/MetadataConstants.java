/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata;

/**
 * INTERNAL:
 * 
 * Static values for metadata processing.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataConstants {
    /* Relationship mappings. */
    public static final String LAZY = "LAZY";
    public static final String EAGER = "EAGER";
    
    /* Inheritance constants. */
    public static final String SINGLE_TABLE = "SINGLE_TABLE";
    public static final String TABLE_PER_CLASS = "TABLE_PER_CLASS";
    
    /* Discriminator column type constants. */
    public static final String CHAR = "CHAR";
    public static final String STRING = "STRING";
    public static final String INTEGER = "INTEGER";
    
    /* Order by constants. */
    public static final String ASCENDING = "ASC";
    public static final String DESCENDING = "DESC";
    
    /* Temporal field classification constants. */
    public static final String DATE = "DATE";
    public static final String TIME = "TIME";
    public static final String TIMESTAMP = "TIMESTAMP";
    
    /* XML cascade type constants. */
	public static final String CASCADE_ALL = "cascade-all";
    public static final String CASCADE_MERGE = "cascade-merge";
    public static final String CASCADE_REMOVE = "cascade-remove";
    public static final String CASCADE_PERSIST = "cascade-persist";
    public static final String CASCADE_REFRESH = "cascade-refresh";
    
    /* Sequencing and Change tracking constant. */
    public static final String AUTO = "AUTO";
    
    /* Change tracking constants. */
    public static final String ATTRIBUTE = "ATTRIBUTE";
    public static final String OBJECT = "OBJECT";
    public static final String DEFERRED = "DEFERRED";
    
    /* Sequencing constants. */
    public static final String TABLE = "TABLE";
    public static final String IDENTITY = "IDENTITY";
    public static final String SEQUENCE = "SEQUENCE";
    public static final String DEFAULT_AUTO_GENERATOR = "SEQ_GEN";
    public static final String DEFAULT_TABLE_GENERATOR = "SEQ_GEN_TABLE";
    public static final String DEFAULT_SEQUENCE_GENERATOR = "SEQ_GEN_SEQUENCE";
    
    /* Reserved converter names. */
    public static final String CONVERT_NONE = "none";
    public static final String CONVERT_SERIALIZED = "serialized";
    
    /* Optimistic locking constants. */
    public static final String ALL_COLUMNS = "ALL_COLUMNS";
    public static final String CHANGED_COLUMNS = "CHANGED_COLUMNS";
    public static final String SELECTED_COLUMNS = "SELECTED_COLUMNS";
    public static final String VERSION_COLUMN = "VERSION_COLUMN";
    
    /* Cache constants. */
    public static final String FULL = "FULL";
    public static final String WEAK = "WEAK";
    public static final String SOFT = "SOFT";
    public static final String SOFT_WEAK = "SOFT_WEAK";
    public static final String HARD_WEAK = "HARD_WEAK";
    public static final String CACHE = "CACHE";
    public static final String NONE = "NONE";
    public static final String SEND_OBJECT_CHANGES = "SEND_OBJECT_CHANGES";
    public static final String INVALIDATE_CHANGED_OBJECTS = "INVALIDATE_CHANGED_OBJECTS";
    public static final String SEND_NEW_OBJECTS_WITH_CHANGES = "SEND_NEW_OBJECTS_WITH_CHANGES";
    
    /* Stored procedure parameter constants. */
    public static final String IN = "IN";
    public static final String OUT = "OUT";
    public static final String IN_OUT = "IN_OUT";
    public static final String OUT_CURSOR = "OUT_CURSOR";
}
