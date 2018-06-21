/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     mobrien - initial API and implementation
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
