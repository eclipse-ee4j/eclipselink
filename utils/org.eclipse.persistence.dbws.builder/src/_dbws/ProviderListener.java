/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package _dbws;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ProviderListener implements ServletContextListener {

    public static ServletContext SC = null;

    public  ProviderListener() {
        super();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        SC = sce.getServletContext();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // no-op
    }
}
