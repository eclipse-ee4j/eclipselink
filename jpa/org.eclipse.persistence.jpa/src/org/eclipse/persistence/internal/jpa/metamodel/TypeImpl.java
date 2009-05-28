/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.metamodel;

import javax.persistence.metamodel.Type;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the Type interface 
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>: 
 * 
 * @see javax.persistence.metamodel.Type 
 * @since EclipseLink 2.0 - JPA 2.0
 *  
 * Contributors: 
 *     03/19/2009-2.0  dclarke  - initial API start    
 *     04/30/2009-2.0  mobrien - finish implementation for EclipseLink 2.0 release
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 */ 
public abstract class TypeImpl<X> implements Type<X> {
    
    /** The Java Class used that this Type represents */
    private Class javaClass;
    
    protected TypeImpl(Class javaClass) {
        this.javaClass = javaClass;
    }

    public Class<X> getJavaType() {
        return this.javaClass;
    }

    public abstract PersistenceType getPersistenceType();

}
