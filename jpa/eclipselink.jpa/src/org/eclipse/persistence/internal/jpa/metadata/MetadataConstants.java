/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata;

/**
 * Static values for metadata processing.
 * Rule of thumb: Don't put a constant in here if it is available in an enum.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataConstants {
	/* JPA Schema specs */
	public static final String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	public static final String PERSISTENCE_SCHEMA_NAME = "xsd/persistence_1_0.xsd";
	public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	
	/* Access types */
	public static final String FIELD = "FIELD";
	public static final String PROPERTY = "PROPERTY";
    
    /* Order by constants. */
    public static final String ASCENDING = "ASC";
    public static final String DESCENDING = "DESC";
    
    /* Sequencing constants */
    public static final String DEFAULT_AUTO_GENERATOR = "SEQ_GEN";
    public static final String DEFAULT_TABLE_GENERATOR = "SEQ_GEN_TABLE";
    public static final String DEFAULT_SEQUENCE_GENERATOR = "SEQ_GEN_SEQUENCE";
    public static final String DEFAULT_IDENTITY_GENERATOR = "SEQ_GEN_IDENTITY";
    
    /* Reserved converter names. */
    public static final String CONVERT_NONE = "none";
    public static final String CONVERT_SERIALIZED = "serialized";
}
