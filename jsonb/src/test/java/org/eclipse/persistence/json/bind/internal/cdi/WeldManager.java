/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.cdi;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Starts and Shutdowns Weld container, for CDI testing purposes.
 *
 * @author Roman Grigoriadi
 */
public class WeldManager {

    private Weld weld;

    private InitialContext initialContext;


    public void startWeld(Class... scannedClasses) throws NamingException {
        weld = new Weld().beanClasses(scannedClasses).disableDiscovery();
        WeldContainer container = weld.initialize();
        initialContext = new InitialContext();
        initialContext.bind(JsonbComponentInstanceCreatorFactory.BEAN_MANAGER_NAME, container.getBeanManager());
    }

    public void shutdownWeld() throws NamingException {
        weld.shutdown();
        initialContext.unbind(JsonbComponentInstanceCreatorFactory.BEAN_MANAGER_NAME);
    }

}
