/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     mobrien - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.helper;

/**
 * INTERNAL:
 * This class is a composite object containing the classLoader and 
 * a flag that is true if the classLoader returned is temporary.<br>
 * JIRA EJBTHREE-572 requires that we use the real classLoader in place of the getNewTempClassLoader().
 * The override code should stay in place until the UCL3 loader does not throw a NPE on loadClass()
 * @return ClassLoaderHolder - may be a temporary classLoader
 */
public class JPAClassLoaderHolder {

        private ClassLoader classLoader;
        private boolean isTempClassLoader;
        
        /**
         * INTERNAL:
         * Create an instance of JPAClassLoaderHolder that wraps aClassLoader that is an original call to get*ClassLoader().
         * @param aClassLoader
         */
        public JPAClassLoaderHolder(ClassLoader aClassLoader) {
            this(aClassLoader, true);
        }
        
        /**
         * INTERNAL:
         * Create an instance of JPAClassLoaderHolder that wraps aClassLoader and the inUse boolean.
         * @param aClassLoader
         * @param isThisTempClassLoader
         */
        public JPAClassLoaderHolder(ClassLoader aClassLoader, boolean isThisTempClassLoader) {
            classLoader = aClassLoader;
            isTempClassLoader = isThisTempClassLoader;
        }

        /**
         * INTERNAL:
         * @return the classLoader
         */
        public ClassLoader getClassLoader() {
            return classLoader;
        }

        /**
         * INTERNAL:
         * @return boolean is true if this classLoader is temporary<br>
         */
        public boolean isTempClassLoader() {
            return isTempClassLoader;
        }
}
