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

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * A persistence provider supplies an instance of this interface to the
 * PersistenceUnitInfo.addTransformer method. The supplied transformer instance
 * will get called to transform entity class files when they are loaded or
 * redefined. The transformation occurs before the class is defined by the JVM.
 */
public interface ClassTransformer {
    /**
     * Invoked when a class is being loaded or redefined. The implementation of
     * this method may transform the supplied class file and return a new
     * replacement class file.
     * 
     * @param loader
     *            The defining loader of the class to be transformed, may be
     *            null if the bootstrap loader
     * @param className
     *            The name of the class in the internal form of fully qualified
     *            class and interface names
     * @param classBeingRedefined
     *            If this is a redefine, the class being redefined, otherwise
     *            null
     * @param protectionDomain
     *            The protection domain of the class being defined or redefined
     * @param classfileBuffer
     *            The input byte buffer in class file format - must not be
     *            modified
     * @return A well-formed class file buffer (the result of the transform), or
     *         null if no transform is performed
     * @throws IllegalClassFormatException
     *             If the input does not represent a well-formed class file
     */
    byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException;
}