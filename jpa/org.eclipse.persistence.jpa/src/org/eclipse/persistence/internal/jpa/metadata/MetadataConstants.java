/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/01/2008-1.1 Guy Pelletier 
 *       - 249329: To remain JPA 1.0 compliant, any new JPA 2.0 annotations should be referenced by name
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata;

/**
 * INTERNAL:
 * Common metatata processing constants. Of particular interest are JPA 2.0
 * annotations and enums. To ensure EclipseLink remains 1.0 compliant we must
 * reference new annotations and enums by string names. 
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.1
 */
public class MetadataConstants {
    /** Access annotation */
    public static final String ACCESS_ANNOTATION = "javax.persistence.Access";
    
    /** AccessType enum values */
    public static final String FIELD = "FIELD";
    public static final String PROPERTY = "PROPERTY";

    /** 
     * Metamodel processing for MappedSuperclasses non-functional names.<p>
     * @See MetadataProject.addMetamodelMappedSuperclass() 
     **/
    public static final String MAPPED_SUPERCLASS_RESERVED_PK_NAME = "__PK_METAMODEL_RESERVED_IN_MEM_ONLY_FIELD_NAME";
    public static final String MAPPED_SUPERCLASS_RESERVED_TABLE_NAME = "__METAMODEL_RESERVED_IN_MEM_ONLY_TABLE_NAME";    

}
