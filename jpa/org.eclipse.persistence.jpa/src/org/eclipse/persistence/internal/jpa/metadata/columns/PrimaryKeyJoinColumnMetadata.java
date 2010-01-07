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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * INTERNAL:
 * Object to process JPA primary key join columns EclipseLink database fields.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class PrimaryKeyJoinColumnMetadata extends RelationalColumnMetadata {
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public PrimaryKeyJoinColumnMetadata() {
        super("<primary-key-join-column>");
    }
    
    /**
     * INTERNAL:
     */
    public PrimaryKeyJoinColumnMetadata(MetadataAnnotation primaryKeyJoinColumn, MetadataAccessibleObject accessibleObject) {
        super(primaryKeyJoinColumn, accessibleObject);
    }
}
