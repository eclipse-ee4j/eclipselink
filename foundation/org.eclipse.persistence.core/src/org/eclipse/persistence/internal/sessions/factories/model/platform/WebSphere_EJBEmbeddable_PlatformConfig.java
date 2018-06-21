/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
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
//     03/15/2016 Jody Grassel
//       - 489794: Add support for WebSphere EJBEmbeddable platform.

package org.eclipse.persistence.internal.sessions.factories.model.platform;

public class WebSphere_EJBEmbeddable_PlatformConfig extends ServerPlatformConfig {
    public WebSphere_EJBEmbeddable_PlatformConfig() {
        super("org.eclipse.persistence.platform.server.was.WebSphere_EJBEmbeddable_Platform");
    }
}
