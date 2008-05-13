/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors;

/**
 * AccessMethodsMetadata. Metadata for user specified property access methods
 * 
 * @author Chris Delahunt
 * @since EclipseLink 1.0M8
 */
public class AccessMethodsMetadata {
    String getMethodName;
    String setMethodName;

    public String getGetMethodName(){
        return getMethodName;
    }
    
    public void setGetMethodName(String getMethodName){
        this.getMethodName = getMethodName;
    }

    public String getSetMethodName(){
        return setMethodName;
    }

    public void setSetMethodName(String setMethodName){
        this.setMethodName = setMethodName;
    }
}
