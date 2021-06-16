/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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
    String checkPlatform();

}
