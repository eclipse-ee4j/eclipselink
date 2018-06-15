/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
    public void addListenerToEventManager(ClassDescriptor descriptor, AbstractSession session, ClassLoader loader);

}
