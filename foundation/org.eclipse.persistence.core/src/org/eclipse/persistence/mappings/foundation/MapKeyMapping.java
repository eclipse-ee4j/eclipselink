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
 *     tware - initial API check-in for MappedKeyMapContainerPolicy
 ******************************************************************************/
package org.eclipse.persistence.mappings.foundation;

import java.util.List;
import java.util.Map;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.ReadQuery;

/**
 * MapKeyMapping is implemented by DatabaseMappings that can be used to map the key in a map
 * that uses a MappedKeyMapContainerPolicy.  This interface provides the facilities to retreive data
 * for the key from the database, to get data from the object to put in the database, and to appropriately
 * initialize the mappings.
 * 
 * @see MappedKeyMapContainerPolicy
 * @see AbstractDirectMapping
 * @see AggregateObjectMapping
 * @see OneToOneMapping
 * @author tware
 *
 */
public interface MapKeyMapping extends MapComponentMapping {

    /**
     * INTERNAL:
     * Used when initializing queries for mappings that use a Map
     * Called when the selection query is being initialized to add the fields for the map key to the query
     */
    public void addAdditionalFieldsToQuery(ReadQuery selectionQuery, Expression baseExpression);
    
    /**
     * INTERNAL:
     * Used when initializing queries for mappings that use a Map
     * Called when the insert query is being initialized to ensure the fields for the map key are in the insert query
     */
    public void addFieldsForMapKey(AbstractRecord joinRow);

    /**
     * Build a clone of the given element in a unitOfWork
     * @param element
     * @param unitOfWork
     * @param isExisting
     * @return
     */
    public Object buildElementClone(Object element, UnitOfWorkImpl unitOfWork, boolean isExisting);

    /**
     * INTERNAL:
     * Depending on the MapKeyMapping, a different selection query may be required to retrieve the
     * map when the map is based on a DirectCollectionMapping
     * @return
     */
    public ReadQuery buildSelectionQueryForDirectCollectionKeyMapping(ContainerPolicy containerPolicy);
    
    /**
     * INTERNAL:
     * Return the fields that make up the identity of the mapped object.  For mappings with
     * a primary key, it will be the set of fields in the primary key.  For mappings without
     * a primary key it will likely be all the fields
     * @return
     */
    public List<DatabaseField> getIdentityFieldsForMapKey();
    
    /**
     * INTERNAL:
     * If required, get the targetVersion of the source object from the merge manager
     * @return
     */
    public Object getTargetVersionOfSourceObject(Object object, MergeManager mergeManager);
    
    /**
     * INTERNAL:
     * Extract the fields for the Map key from the object to use in a query
     * @return
     */
    public Map extractIdentityFieldsForQuery(Object key, AbstractSession session);


    /**
     * INTERNAL:
     * Making any mapping changes necessary to use a the mapping as a map key prior to initializing the mapping
     */
    public void preinitializeMapKey(DatabaseTable table) throws DescriptorException;
    
    /**
     * INTERNAL:
     * Allow the key mapping to unwrap the object
     * @param key
     * @param session
     * @return
     */
    
    public Object unwrapKey(Object key, AbstractSession session);
    
    /**
     * INTERNAL:
     * Allow the key mapping to wrap the object
     * @param key
     * @param session
     * @return
     */
    public Object wrapKey(Object key, AbstractSession session);

}
