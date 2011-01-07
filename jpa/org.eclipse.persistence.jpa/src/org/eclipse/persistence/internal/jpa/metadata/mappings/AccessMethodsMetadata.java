/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 *     03/29/2010-2.1 Guy Pelletier 
 *       - 267217: Add Named Access Type to EclipseLink-ORM
 *     04/09/2010-2.1 Guy Pelletier 
 *       - 307050: Add defaults for access methods of a VIRTUAL access type
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.mappings;

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;

/**
 * INTERNAL:
 * Metadata for user specified property access methods and also used with
 * VIRTUAL access types. When specified in XML the set and get method names
 * are required. For VIRTUAL access defaults we use the defaults "get" and 
 * "set" if no access methods are specified.
 * 
 * @author Chris Delahunt
 * @since EclipseLink 1.0M8
 */
public class AccessMethodsMetadata extends ORMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    // Access method names are required in XML, therefore will override these
    // default values used for VIRTUAL access defaults.
    String getMethodName = "get";
    String setMethodName = "set";

    /**
     * INTERNAL:
     */
    public AccessMethodsMetadata() {
        super("<access-methods>");
    }
    
    /**
     * INTERNAL:
     */
    public AccessMethodsMetadata clone() {
        AccessMethodsMetadata accessMethods = new AccessMethodsMetadata();
        accessMethods.setGetMethodName(getGetMethodName());
        accessMethods.setSetMethodName(getSetMethodName());
        return accessMethods;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof AccessMethodsMetadata) {
            AccessMethodsMetadata accessMethods = (AccessMethodsMetadata) objectToCompare;
            
            if (! valuesMatch(getMethodName, accessMethods.getGetMethodName())) {
                return false;
            }
            
            return valuesMatch(setMethodName, accessMethods.getSetMethodName());
        }
        
        return false;
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
    
    /**
     * INTERNAL:
     * Used for validation exception message string and debugging.
     */
    public String toString() {
        return "Get method name: " + getMethodName + ", Set method name: " + setMethodName;
    }
}
