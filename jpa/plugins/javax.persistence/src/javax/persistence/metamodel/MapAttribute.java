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
 *               Specification available from http://jcp.org/en/jsr/detail?id=317
*     gyorke  - Post PFD updates
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

/**
 * Instances of the type MapAttribute represent persistent Map-valued
 * attributes.
 *
 * @param <X> The type the represented Map belongs to
 * @param <K> The type of the key of the represented Map
 * @param <V> The type of the value of the represented Map
 */
public interface MapAttribute<X, K, V> 
	extends PluralAttribute<X, java.util.Map<K, V>, V> {

    /**
     * Return the Java type of the map key.
     * @return Java key type
     */
    Class<K> getKeyJavaType();

    /**
     * Return the type representing the key type of the map.
     * @return type representing key type
     */
    Type<K> getKeyType();
}
