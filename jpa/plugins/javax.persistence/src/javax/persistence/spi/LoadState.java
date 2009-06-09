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
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *     		     Specification available from http://jcp.org/en/jsr/detail?id=317
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
package javax.persistence.spi;

/**
 * The LoadState is returned from the PersistenceProvider's isLoaded methods to
 * indicate the load status of the given entity, attribute, or relationship with
 * respect to the provider being used.
 * 
 * @see PersistenceProvider#isLoaded(Object)
 * @see PersistenceProvider#isLoadedWithoutReference(Object, String)
 * @see PersistenceProvider#isLoadedWithReference(Object, String)
 * 
 * @since Java Persistence 2.0
 */
public enum LoadState {
    /**
     * The state of the element is known to have been loaded
     */
    LOADED,

    /**
     * The state of the element is known not to have been loaded
     */
    NOT_LOADED,

    /**
     * The load state of the element cannot be determined
     */
    UNKNOWN
}