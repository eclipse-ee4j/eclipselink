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

import org.eclipse.persistence.internal.jpa.metadata.accessors.DirectAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.annotations.StructConverter;
import org.eclipse.persistence.jpa.config.StructConverterType;
import java.sql.Types;

/**
 * Place holder for a StructConverter
 * 
 * This class will allow a StructConverter to be added to a Session through annotations when
 * defined with the @StructConverter annotation.
 *
 */
public class MetadataStructConverter extends MetadataAbstractConverter {

    private StructConverter converter;
    
    public MetadataStructConverter(StructConverter converter){
        this.converter = converter;
    }
    
    /**
     * INTERNAL: 
     */
    public String getName() {
        return converter.name();
    }
    
    public String getConverterClassName(){
        if (converter.converter().equals(StructConverterType.JGeometry)){
            return "org.eclipse.persistence.platform.database.oracle.converters.JGeometryConverter";
        }
        return converter.converter();
    }
    
    /**
     * INTERNAL: 
     */
    public void process(DatabaseMapping mapping, DirectAccessor accessor) {
        if (mapping.isAbstractDirectMapping()){
            AbstractDirectMapping directMapping = ((AbstractDirectMapping)mapping); 
            directMapping.setFieldType(Types.STRUCT);
            directMapping.setConverter(null);
            directMapping.setConverterClassName(null);
        } else if (!(mapping.isDirectCollectionMapping() || mapping.isDirectMapMapping())){
            accessor.getValidator().throwInvalidMappingForStructConverter(converter.name(), mapping);
        }
    }
    
}
