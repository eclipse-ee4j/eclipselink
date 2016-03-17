/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/15/2016 Jody Grassel
 *       - 489794: Add support for WebSphere EJBEmbeddable platform.
 ******************************************************************************/

package org.eclipse.persistence.internal.sessions.factories.model.platform;

public class WebSphere_EJBEmbeddable_PlatformConfig extends ServerPlatformConfig {
    public WebSphere_EJBEmbeddable_PlatformConfig() {
        super("org.eclipse.persistence.platform.server.was.WebSphere_EJBEmbeddable_Platform");
    }
}
