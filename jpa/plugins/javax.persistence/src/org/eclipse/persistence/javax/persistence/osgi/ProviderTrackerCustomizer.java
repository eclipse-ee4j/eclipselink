/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     tware,mkeith - Initial OSGi support for JPA 
 *     ssmith - Refactored functionality into Activator
 *
 ******************************************************************************/
package org.eclipse.persistence.javax.persistence.osgi;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class ProviderTrackerCustomizer implements ServiceTrackerCustomizer {

    private Activator activator;

    public ProviderTrackerCustomizer(Activator activator) {
        this.activator = activator;
    }

    protected Activator getActivator() {
        return this.activator;
    }

    // -------------------------------------------------------------
    // Service was added
    // -------------------------------------------------------------
    public Object addingService(ServiceReference reference) {
        return getActivator().addProvider(reference);
    }

    // -------------------------------------------------------------
    // Service was modified
    // -------------------------------------------------------------
    public void modifiedService(ServiceReference reference, Object serviceObject) {
        // Rogue provider -- we don't support modifying provider services
        removedService(reference, serviceObject);
    }

    // -------------------------------------------------------------
    // Service was removed
    // -------------------------------------------------------------
    public void removedService(ServiceReference reference, Object serviceObject) {
        getActivator().removeProvider(reference);
    }

}
