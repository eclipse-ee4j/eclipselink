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
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.core.helper;

public interface CoreField {

    /**
     * Return the unqualified name of the field.
     */
    public String getName();

    public Class getType();

    /**
     * Set the unqualified name of the field.
     */
    public void setName(String name);
    
    /**
     * Set the Java class type that corresponds to the field.
     * The JDBC type is determined from the class type,
     * this is used to optimize performance, and for binding.
     */
    public void setType(Class type);

}