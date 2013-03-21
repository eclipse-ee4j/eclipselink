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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm;

/**
 * <p>An implementation of XMLUnmarshalListener can be set on an XMLUnmarshaller to provide additional
 * behaviour during unmarshal operations.</p>
 */
public interface XMLUnmarshalListener {
	
    /**
     * Event that will be called before objects are unmarshalled.
     *
     * @param target A newly created instance of the object to be unmarshalled.  
     * @param parent the owning object of the object that will be unmarshalled. This may be null.
     */
    public void beforeUnmarshal(Object target, Object parent);
    
    
    /**
     * Event that will be called after objects are unmarshalled.
     *
     * @param target the object that was unmarshalled.
     * @param parent the owning object of the object that was unmarshalled. This may be null.
     */
    public void afterUnmarshal(Object target, Object parent);
}
