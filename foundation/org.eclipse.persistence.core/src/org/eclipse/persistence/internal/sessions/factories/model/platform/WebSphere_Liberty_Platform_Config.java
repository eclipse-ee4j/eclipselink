/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Rick Curtis - Add support for WebSphere Liberty.
 ******************************************************************************/
package org.eclipse.persistence.internal.sessions.factories.model.platform;

public class WebSphere_Liberty_Platform_Config extends ServerPlatformConfig {
    public WebSphere_Liberty_Platform_Config() {
        super("org.eclipse.persistence.platform.server.was.WebSphere_Liberty_Platform");
    }
}
