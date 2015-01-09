/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/05/2015 Rick Curtis
 *       - 455683: Automatically detect target server
 ******************************************************************************/
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
