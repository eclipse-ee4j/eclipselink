/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
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

    DescriptorBasedDatabaseEventListener createDatabaseEventListener(Map<String, Object> properties);

}
