/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     2008-10-29 - James Sutherland - initial implementation
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
