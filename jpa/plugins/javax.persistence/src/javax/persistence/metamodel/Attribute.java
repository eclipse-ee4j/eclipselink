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
 * An attribute of a Java type
 *
 * @param <X> The represented type that contains the attribute
 * @param <Y> The type of the represented attribute
 */
public interface Attribute<X, Y> {

	public static enum PersistentAttributeType {
	    MANY_TO_ONE, ONE_TO_ONE, BASIC, EMBEDDED,
	    MANY_TO_MANY, ONE_TO_MANY, ELEMENT_COLLECTION
	}

    /**
     * Return the name of the attribute.
     * @return name
     */
    String getName();

    /**
     *  Return the persistent attribute type for the attribute.
     *  @return persistent attribute type
     */
    PersistentAttributeType getPersistentAttributeType();

    /**
     *  Return the managed type representing the type in which 
     *  the attribute was declared.
     *  @return declaring type
     */
    ManagedType<X> getDeclaringType();

    /**
     *  Return the Java type of the represented attribute.
     *  @return Java type
     */
    Class<Y> getJavaType();

    /**
     *  Return the java.lang.reflect.Member for the represented 
     *  attribute.
     *  @return corresponding java.lang.reflect.Member
     */
    java.lang.reflect.Member getJavaMember();

    /**
     *  Is the attribute an association.
     *  @return whether boolean indicating whether attribute 
     *          corresponds to an association
     */
    boolean isAssociation();

    /**
     *  Is the attribute collection-valued.
     *  @return boolean indicating whether attribute is 
     *          collection-valued
     */
    boolean isCollection();
	
}
