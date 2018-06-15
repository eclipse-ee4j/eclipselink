/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      tware - initial implementation
package org.eclipse.persistence.jpa.rs.eventlistener;

import java.util.Map;

/**
 * Provides a mechanism for plugging in database event listener creation.
 * @author tware
 *
 */
public interface DatabaseEventListenerFactory {

    public DescriptorBasedDatabaseEventListener createDatabaseEventListener(Map<String, Object> properties);

}
