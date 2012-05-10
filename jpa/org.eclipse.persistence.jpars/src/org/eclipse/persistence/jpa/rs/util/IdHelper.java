/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 		dclarke/tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.internal.jpa.CMP3Policy;
import org.eclipse.persistence.internal.queries.EntityFetchGroup;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.sessions.server.Server;


/**
 * EclipseLink helper class used for converting composite key values passed into
 * JAX-RS calls as query or matrix parameters into a value that can be used in a
 * find.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.0
 */
public class IdHelper {

    private static final String SEPARATOR_STRING = "+";
    
    public static Object buildId(PersistenceContext app, String entityName, String idString) {
        Server session = app.getJpaSession();
        ClassDescriptor descriptor = app.getDescriptor(entityName);
        List<DatabaseMapping> pkMappings = descriptor.getObjectBuilder().getPrimaryKeyMappings();
        List<SortableKey> pkIndices = new ArrayList<SortableKey>();
        int index = 0;
        for (DatabaseMapping mapping: pkMappings){
            pkIndices.add(new SortableKey(mapping, index));
            index++;
        }
        Collections.sort(pkIndices);
        
        // Handle composite key in map
        int[] elementIndex = new int[pkMappings.size()];
        Object[] keyElements = new Object[pkMappings.size()];
        StringTokenizer tokenizer = new StringTokenizer(idString, SEPARATOR_STRING);
        int tokens = tokenizer.countTokens();
        if (tokens != pkMappings.size()){
            throw new RuntimeException("Failed, incorrect number of keys values");
        }
        index = 0;
        Iterator<SortableKey> iterator = pkIndices.iterator();
        while (tokenizer.hasMoreTokens()){
            SortableKey key = iterator.next();
            Object idValue = session.getPlatform().getConversionManager().convertObject(tokenizer.nextToken(), key.getMapping().getAttributeClassification());
            keyElements[key.getIndex()] = idValue;
            index++;
        }

        if (descriptor.hasCMPPolicy()) {
            CMP3Policy policy = (CMP3Policy) descriptor.getCMPPolicy();
            return policy.createPrimaryKeyInstanceFromPrimaryKeyValues((AbstractSession) session, elementIndex, keyElements);
        }

        if (keyElements.length == 1) {
            return keyElements[0];
        }
        return keyElements;
    }
    
    public static String stringifyId(Object entity, String typeName, PersistenceContext app){
        ClassDescriptor descriptor = app.getDescriptor(typeName);
        List<DatabaseMapping> pkMappings = descriptor.getObjectBuilder().getPrimaryKeyMappings();
        if (pkMappings.isEmpty()){
            return "";
        }
        List<SortableKey> pkIndices = new ArrayList<SortableKey>();
        int index = 0;
        for (DatabaseMapping mapping: pkMappings){
            pkIndices.add(new SortableKey(mapping, index));
            index++;
        }
        Collections.sort(pkIndices);
        StringBuffer key = new StringBuffer();
        Iterator<SortableKey> sortableKeys = pkIndices.iterator();
        while (sortableKeys.hasNext()){
            DatabaseMapping mapping = sortableKeys.next().getMapping();
            key.append(mapping.getAttributeValueFromObject(entity).toString());
            if (sortableKeys.hasNext()){
                key.append(SEPARATOR_STRING);
            }
        }
        return key.toString();
    }
     
    
    /**
     * build a shell of an object based on a primary key.  The object shell will be an instance of the object with
     * only primary key populated
     * @param context
     * @param entityType
     * @param id
     * @return
     */
    public static Object buildObjectShell(PersistenceContext context, String entityType, Object id){
        ClassDescriptor descriptor = context.getDescriptor(entityType);
        List<DatabaseMapping> pkMappings = descriptor.getObjectBuilder().getPrimaryKeyMappings();
        DynamicEntityImpl entity = null;
        if (descriptor.hasCMPPolicy()) {
            CMP3Policy policy = (CMP3Policy) descriptor.getCMPPolicy();
            entity = (DynamicEntityImpl)policy.createBeanUsingKey(id, context.getJpaSession());
        } else { 
            entity = (DynamicEntityImpl)context.newEntity(entityType);
            // if there is only one PK mapping, we assume the id object represents the value of that mapping
            if (pkMappings.size() == 1){
                entity.set(pkMappings.get(0).getAttributeName(), id);
            } else {
                // If there are more that one PK, we assume an array as produced by buildId() above with the keys
                // based on a sorted order of PK fields
                List<SortableKey> pkIndices = new ArrayList<SortableKey>();
                int index = 0;
                for (DatabaseMapping mapping: pkMappings){
                    pkIndices.add(new SortableKey(mapping, index));
                    index++;
                }
                Collections.sort(pkIndices);
                Object[] keyElements = (Object[])id;
                for (SortableKey key: pkIndices){
                    entity.set(key.getMapping().getAttributeName(), keyElements[key.getIndex()]);
                }
            }
        }

        entity._persistence_setId(id);
        entity._persistence_setSession(JpaHelper.getDatabaseSession(context.getEmf()));

        return entity;
    }
    
    private static class SortableKey implements Comparable<SortableKey>{
        
        private DatabaseMapping mapping;
        private int index;
        
        public SortableKey(DatabaseMapping mapping, int index){
            this.mapping = mapping;
            this.index = index;
        }
        
        public int compareTo(SortableKey o){
            return mapping.getAttributeName().compareTo(o.getMapping().getAttributeName());
        }
        
        public DatabaseMapping getMapping(){
            return mapping;
        }
        
        public int getIndex(){
            return index;
        }

    }
}
