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
 *     2008-10-29 - James Sutherland - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.descriptors;

/**
 * Define an interface for utility methods weaved into the persistence classes.
 * This defines any persistence object including aggregates/embeddables.
 * 
 * @author  James Sutherland
 * @since   EclipseLink 1.1
 */
public interface PersistenceObject {
    Object _persistence_shallow_clone();
    Object _persistence_new(PersistenceObject object);
    Object _persistence_get(String attribute);
    void _persistence_set(String attribute, Object value);
}
