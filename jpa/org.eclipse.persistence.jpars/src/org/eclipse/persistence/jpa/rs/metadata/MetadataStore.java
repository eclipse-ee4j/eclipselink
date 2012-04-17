/****************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      tware - 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.metadata;

import java.util.List;
import java.util.Map;

/**
 * Implementers are capable of storing information about a PersistenceContext in some kind of a persistent store
 * @author tware
 *
 */
public interface MetadataStore {

    public void persistMetadata(String name, String url);
    
    public List<Application> retreiveMetadata();
    
    public void close();
    
    public void clearMetadata();
    
    public boolean isInitialized();
    
    public Map<String, Object> getProperties();

    public void setProperties(Map<String, Object> properties);
}
