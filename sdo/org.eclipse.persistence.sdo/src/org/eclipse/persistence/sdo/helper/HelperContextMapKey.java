/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * David McCann = 2.1 - Initial contribution
 ******************************************************************************/
package org.eclipse.persistence.sdo.helper;

/**
 * Class to facilitate caching of global helper contexts. Instances of this class
 * are intended to be used as the key in the global helper context map in the 
 * SDOHelperContext class. Equality will be based on the applicationName attribute 
 * if set, or the class loader if applicationName is null. When caching on application
 * name, the loader attribute will be used to determine if a redeployment has occurred; 
 * this is necessary, as in the case of a redeploy the applicationName would match, but
 * the loader would be different.
 * 
 * Note that all instances of this class are expected to have a loader set.
 *  
 */
public class HelperContextMapKey {
    private String applicationName;
    private ClassLoader classLoader;

    /**
     * This constructor should be used when caching on class loader.
     * 
     * @param loader
     */
    public HelperContextMapKey(ClassLoader classLoader) {
        this.applicationName = null;
        this.classLoader = classLoader;
    }

    /**
     * This constructor should be used when caching on application name.  The loader
     * will be used to determine if a redeploy has occurred, i.e. same application
     * name but different class loaders.
     * 
     * @param applicationName
     * @param loader
     */
    public HelperContextMapKey(String applicationName, ClassLoader classLoader) {
        this.applicationName = applicationName;
        this.classLoader = classLoader;
    }

    /**
     * Return the applicationName value.
     * 
     * @return
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Return the loader value.
     * 
     * @return
     */
    public ClassLoader getLoader() {
        return classLoader;
    }

    /**
     * Indicates if a given class loader is equal to this HelperContextMapKey instance's
     * class loader.  This method will typically be used to determine if a redeploy has 
     * occurred.
     *  
     * @param loader
     * @return
     */
    boolean areLoadersEqual(ClassLoader loader) {
        return this.classLoader == loader;
    }

    /**
     * Return a hashCode (as an int) for this instance.
     * 
     */
    public int hashCode() {
        return 7;
    }
    
    /**
     * Equality will be based on the applicationName attribute if set, or the class 
     * loader if applicationName is null.
     * 
     * @param Object to be compared to this HelperContextMapKey instance
     * @return true if the given object is equal to this HelperContextMapKey 
     *         instance; false otherwise
     */
    public boolean equals(Object obj) {
        HelperContextMapKey ckey;
        try {
            ckey = (HelperContextMapKey) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        
        // if the applicationName is non-null we will base equality on it
        if (applicationName != null) {
            return this.applicationName.equals(ckey.getApplicationName()) || areLoadersEqual(ckey.getLoader());
        }
        // at this point we have to assume that the class loader is the map key, so base equality on it
        return areLoadersEqual(ckey.getLoader());
    }
}
