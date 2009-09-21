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

/**
 * Instances of the type <code>Type</code> represent persistent object 
 * or attribute types.
 *
 * @param <X>  The type of the represented object or attribute
 *
 * @since Java Persistence 2.0
 */
public interface Type<X> {

       public static enum PersistenceType {

	   /** Entity */
           ENTITY, 

	   /** Embeddable class */
	   EMBEDDABLE, 

	   /** Mapped superclass */
	   MAPPED_SUPERCLASS, 

	   /** Basic type */
	   BASIC
       }

    /**
     *  Return the persistence type.
     *  @return persistence type
     */	
    PersistenceType getPersistenceType();

    /**
     *  Return the represented Java type.
     *  @return Java type
     */
    Class<X> getJavaType();
}
