/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
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

    public void contextInitialized(ServletContextEvent sce) {
        SC = sce.getServletContext();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        // no-op
    }
}
