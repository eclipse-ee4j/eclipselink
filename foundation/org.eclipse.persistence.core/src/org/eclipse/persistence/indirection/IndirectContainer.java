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
package org.eclipse.persistence.indirection;


/**
 * <b>Purpose</b>:
 * Define an interface for a Container that can also act as an EclipseLink
 * "indirection" object; i.e. the Container will only read its contents from
 * the database when necessary (typically, on receipt of the first
 * Container-related message).
 * <p>
 *
 * @see org.eclipse.persistence.internal.indirection.TransparentIndirectionPolicy
 * @author Big Country
 *    @since TOPLink/Java 2.5
 */
public interface IndirectContainer {

    /**
     * PUBLIC:
     * This is used by the indirection policy to build the
     * UOW clone of the container.
     * @return org.eclipse.persistence.indirection.ValueHolderInterface A representation of the valueholder  * which this container uses
     */
    public ValueHolderInterface getValueHolder();

    /**
     * PUBLIC:
     * Return whether the contents have been read from the database.
     * This is used periodically by the indirection policy to determine whether
     * to trigger the database read.
     */
    public boolean isInstantiated();

    /**
     * PUBLIC:
     * Set the valueHolder.
     * This is used by the indirection policy to build the
     * UOW clone of the container.
     */
    public void setValueHolder(ValueHolderInterface valueHolder);
}
