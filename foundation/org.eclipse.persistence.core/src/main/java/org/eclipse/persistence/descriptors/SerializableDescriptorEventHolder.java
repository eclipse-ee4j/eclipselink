/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//     08/01/2012-2.5 Chris Delahunt
//       - 371950: Metadata caching
package org.eclipse.persistence.descriptors;

import java.io.Serializable;

import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * INTERNAL:
 * Interface to hold onto a DescriptorEvents within a project so they can be rebuilt after serialization.
 *
 * @author Chris Delahunt
 * @since EclipseLink 2.5
 */
public interface SerializableDescriptorEventHolder extends Serializable {
    void addListenerToEventManager(ClassDescriptor descriptor, AbstractSession session, ClassLoader loader);

}
