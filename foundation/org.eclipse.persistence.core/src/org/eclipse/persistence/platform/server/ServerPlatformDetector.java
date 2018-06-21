/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
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
//     01/05/2015 Rick Curtis
//       - 455683: Automatically detect target server
package org.eclipse.persistence.platform.server;

/**
 * A generic interface that allows implementors to try to detect which platform is currently being executed on.
 */
public interface ServerPlatformDetector {
    /**
     * @return {@link org.eclipse.persistence.config.TargetServer} constant if this detector determines the platform
     * this is currently being run on. Return null if unknown.
     */
    public String checkPlatform();

}
