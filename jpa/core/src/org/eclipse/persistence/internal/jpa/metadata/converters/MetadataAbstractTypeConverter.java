/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.converters;

/**
 * Abstract type converter class.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public abstract class MetadataAbstractTypeConverter extends MetadataAbstractConverter  {
    /**
     * INTERNAL:
     */
    public abstract String getName();
    
    /**
     * INTERNAL:
     */
    protected abstract Class getDataType();
    
    /**
     * INTERNAL:
     */
    protected abstract Class getObjectType();
}
