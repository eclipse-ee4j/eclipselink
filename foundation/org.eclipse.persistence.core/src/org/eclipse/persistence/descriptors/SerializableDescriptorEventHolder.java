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
 *     Oracle - initial API and implementation
 *     08/01/2012-2.5 Chris Delahunt
 *       - 371950: Metadata caching 
 ******************************************************************************/ 
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
