/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Chris Delahunt (Oracle) May 13, 2008-1.0M8  
 *       - New file introduced for bug 217164.
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.mappings;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

/**
 * INTERNAL:
 * AccessMethodsMetadata. Metadata for user specified property access methods
 * 
 * @author Chris Delahunt
 * @since EclipseLink 1.0M8
 */
public class AccessMethodsMetadata extends ORMetadata {
    String getMethodName;
    String setMethodName;

    /**
     * INTERNAL:
     */
    public AccessMethodsMetadata() {
        super("<access-methods>");
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getGetMethodName(){
        return getMethodName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getSetMethodName(){
        return setMethodName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setGetMethodName(String getMethodName){
        this.getMethodName = getMethodName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSetMethodName(String setMethodName){
        this.setMethodName = setMethodName;
    }
}
