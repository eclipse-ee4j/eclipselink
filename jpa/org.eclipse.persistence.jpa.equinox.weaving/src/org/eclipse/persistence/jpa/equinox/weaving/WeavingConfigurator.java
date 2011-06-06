/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware, ssmith - Initial Equinox weaving code
 ******************************************************************************/  
package org.eclipse.persistence.jpa.equinox.weaving;

import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.osgi.framework.debug.FrameworkDebugOptions;

public class WeavingConfigurator implements HookConfigurator {

	private static final String OPTION_DEBUG = "org.eclipse.persistence.jpa.equinox.weaving/debug";

	public WeavingConfigurator() {
        super();
    }

    public void addHooks(HookRegistry hookRegistry) {
		FrameworkDebugOptions dbgOptions = FrameworkDebugOptions.getDefault();
		boolean debug = dbgOptions.getBooleanOption(OPTION_DEBUG, false);
		if (debug) {
            Debug.println("EclipseLink: Adding WeaverRegistry Class Loading Hook"); //$NON-NLS-1$
        }
        hookRegistry.addClassLoadingHook(WeaverRegistry.getInstance());
        hookRegistry.addAdaptorHook(new WeavingAdaptor());
    }
}
