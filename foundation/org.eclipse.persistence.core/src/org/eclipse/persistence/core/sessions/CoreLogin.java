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
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.core.sessions;

import org.eclipse.persistence.internal.core.databaseaccess.CorePlatform;

/**
 * INTERNAL
 * A abstraction of login capturing behavior common to all persistence types.
 */
public interface CoreLogin<PLATFORM extends CorePlatform> {

    public PLATFORM getDatasourcePlatform();

}
