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
 *     tware - added in fix for bug 277550
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.util.ArrayList;
import java.util.List;

/**
 * PUBLIC:
 * 
 * This class is used by our JPA annotation processing to discover which annotations may coexist with a 
 * javax.persistence.Transient annotation.  If javax.persistence.Transient appears on a field or property with an
 * annotation in the javax.persistence or org.eclipse.persistence package that is not in the list returned by getTransientCompatibleAnnotations()
 * an exception will be thrown.
 * @author tware
 *
 */
public class TransientCompatibleAnnotations {

    private static List<String> transientCompatibleAnnotations = null;
    
    /**
     * PUBLIC:
     * Return a list of classnames of annotations that are compatible with the javax.persistence.Transient
     * annotation.
     * @return
     */
    public static List<String> getTransientCompatibleAnnotations(){
        if (transientCompatibleAnnotations == null){
            transientCompatibleAnnotations = new ArrayList();
            transientCompatibleAnnotations.add("javax.persistence.PersistenceUnits");
            transientCompatibleAnnotations.add("javax.persistence.PersistenceUnit");
            transientCompatibleAnnotations.add("javax.persistence.PersistenceContext");
            transientCompatibleAnnotations.add("javax.persistence.PersistenceContexts");
            transientCompatibleAnnotations.add("javax.persistence.Access");
            transientCompatibleAnnotations.add("javax.persistence.Transient");
        }
        return transientCompatibleAnnotations;
    }
    
}
