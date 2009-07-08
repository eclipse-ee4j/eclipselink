/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files  
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * INTERNAL:
 * Parent object that is used to hold onto a valid JPA decorated file.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class MetadataFile extends MetadataAccessibleObject {
    /**
     * INTERNAL:
     */
    public MetadataFile(XMLEntityMappings entityMappings) {
        super(entityMappings.getMappingFile(), entityMappings);
    }
    
    /**
     * INTERNAL:
     * Return the element of this accessible object. Right now it's just the
     * top most entity mappings tag. At least it's something. Could probably
     * get more descriptive, right now it's only used in merging and for those
     * elements that do not tie to an annotated element (class, method or field)
     * That is, unnamed <entity-mappings> level elements and
     * <persistence-unit-metadata> and <persistence-unit-defaults> elements. 
     * So this will have to do for now ... plus no one looks at the logging
     * messages anyway ... ;-)
     */
    public Object getElement() {
        return "<entity-mappings>";
    }
}
