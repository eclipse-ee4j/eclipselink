/*******************************************************************************
 * Copyright (c) 2008, 2009 Sun Microsystems. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     Linda DeMichiel -Java Persistence 2.0 - Proposed Final Draft, Version 2.0 (August 31, 2009)
 *     Specification available from http://jcp.org/en/jsr/detail?id=317
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
package javax.persistence.metamodel;

import java.util.Set;

/**
 * Provides access to the metamodel of persistent
 * entities in the persistence unit. 
 *
 * @since Java Persistence 2.0
 */
public interface Metamodel {

    /**
     *  Return the metamodel entity type representing the entity.
     *  @param cls  the type of the represented entity
     *  @return the metamodel entity type
     *  @throws IllegalArgumentException if not an entity
     */
    <X> EntityType<X> entity(Class<X> cls);

    /**
     *  Return the metamodel managed type representing the 
     *  entity, mapped superclass, or embeddable class.
     *  @param cls  the type of the represented managed class
     *  @return the metamodel managed type
     *  @throws IllegalArgumentException if not a managed class
     */
    <X> ManagedType<X> type(Class<X> cls);

    /**
     *  Return the metamodel embeddable type representing the
     *  embeddable class.
     *  @param cls  the type of the represented embeddable class
     *  @return the metamodel embeddable type
     *  @throws IllegalArgumentException if not an embeddable class
     */
    <X> EmbeddableType<X> embeddable(Class<X> cls);

    /**
     *  Return the metamodel managed types.
     *  @return the metamodel managed types
     */
    Set<ManagedType<?>> getManagedTypes();

    /**
     * Return the metamodel entity types.
     * @return the metamodel entity types
     */
    Set<EntityType<?>> getEntities();

    /**
     * Return the metamodel embeddable types.  Returns empty set
     * if there are no embeddable types.
     * @return the metamodel embeddable types
     */
    Set<EmbeddableType<?>> getEmbeddables();
}
