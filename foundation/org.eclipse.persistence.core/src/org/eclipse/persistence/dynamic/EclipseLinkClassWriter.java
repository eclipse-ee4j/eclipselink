/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.dynamic;

public interface EclipseLinkClassWriter {
    
    public byte[] writeClass(DynamicClassLoader loader, String className) throws ClassNotFoundException;
    
    public boolean isCompatible(EclipseLinkClassWriter writer);
    
    public Class<?> getParentClass();
    
    public String getParentClassName();
}