/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     gyorke - Java Persistence 2.0 - Post Proposed Final Draft (March 13, 2009) Updates
 *               Specification available from http://jcp.org/en/jsr/detail?id=317
 *
 * Java(TM) Persistence API, Version 2.0 - EARLY ACCESS
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP).  The code is untested and presumed not to be a  
 * compatible implementation of JSR 317: Java(TM) Persistence API, Version 2.0.   
 * We encourage you to migrate to an implementation of the Java(TM) Persistence 
 * API, Version 2.0 Specification that has been tested and verified to be compatible 
 * as soon as such an implementation is available, and we encourage you to retain 
 * this notice in any implementation of Java(TM) Persistence API, Version 2.0 
 * Specification that you distribute.
 ******************************************************************************/
package javax.persistence;

/**
 * Utility interface between the application and the persistence
 * provider(s). 
 */

public interface PersistenceUtil {
    /**
     * Determine the load state of a given persistent attribute
     * regardless of the persistence provider that created the
     * containing entity.
     * @param attributeName name of attribute whose load state is
     *    to be determined
     * @return false if entity's state has not been loaded or
     *  if the attribute state has not been loaded, otherwise true
     */
    public boolean isLoaded(Object entity, String attributeName);

    /**
     * Determine the load state of an entity regardless 
     * of the persistence provider that created it.
     * This method can be used to determine the load state 
     * of an entity passed as a reference.  An entity is
     * considered loaded if all attributes for which FetchType
     * EAGER has been specified have been loaded.
     * The isLoaded(Object, String) method should be used to 
     * determine the load state of an attribute.
     * Not doing so might lead to unintended loading of state.
     *
     * @return false if the entity has not be loaded, otherwise 
     * true.
     */
    public boolean isLoaded(Object entity);
}
