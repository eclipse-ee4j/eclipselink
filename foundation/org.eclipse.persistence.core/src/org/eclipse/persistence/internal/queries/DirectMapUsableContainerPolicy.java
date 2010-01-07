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
 *     tware - initial implementation
 ******************************************************************************/ 
package org.eclipse.persistence.internal.queries;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.queries.ObjectBuildingQuery;

/**
 * INTERNAL:
 * This interface is implemented by any ContainerPolicy that can be used with a DirectMapMapping
 *
 * @author tware
 *
 */
public interface DirectMapUsableContainerPolicy {

    /**
     * Extract the key for the map from the provided row
     * @param row
     * @param query
     * @param session
     * @return
     */
    public Object buildKey(AbstractRecord row, ObjectBuildingQuery query, AbstractSession session);

    /**
     * INTERNAL:
     * Return true if keys are the same.  False otherwise
     */
    public boolean compareContainers(Object firstObjectMap, Object secondObjectMap);
    
    /**
     * INTERNAL:
     * Return the DatabaseField that represents the key in a DirectMapMapping
     * @return
     */
    public DatabaseField getDirectKeyField(CollectionMapping baseMapping);
    
    /**
     * INTERNAL:
     * Set the DatabaseField that will represent the key in a DirectMapMapping
     * @param keyField
     * @param descriptor
     */
    public void setKeyField(DatabaseField keyField, ClassDescriptor descriptor);
    
    /**
     * INTERNAL:
     * Get the Converter for the key of this mapping if one exists
     * @return
     */
    public Converter getKeyConverter();
    
    /**
     * INTERNAL:
     * Used during initialization of DirectMapMapping.  Sets the descriptor associated with
     * the key
     * @param descriptor
     */
    public void setDescriptorForKeyMapping(ClassDescriptor descriptor);
    
    /**
     * INTERNAL:
     * Set a converter on the KeyField of a DirectCollectionMapping
     * @param keyConverter
     * @param mapping
     */
    public void setKeyConverter(Converter keyConverter, DirectMapMapping mapping);
    
    /**
     * INTERNAL:
     * Set the name of the class to be used as a converter for the key of a DirectMapMaping
     * @param keyConverterClassName
     * @param mapping
     */
    public void setKeyConverterClassName(String keyConverterClassName, DirectMapMapping mapping);
    
    /**
     * INTERNAL:
     * Used during initialization of DirectMapMapping to make the ContainerPolicy aware of the 
     * DatabaseField used for the value portion of the mapping
     * @param directField
     * @param valueConverter
     */
    public void setValueField(DatabaseField directField, Converter valueConverter);
}
