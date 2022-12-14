/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.platform.server.wildfly;

import jakarta.persistence.spi.PersistenceUnitInfo;
import org.eclipse.persistence.internal.helper.JPAClassLoaderHolder;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.platform.server.JMXEnabledPlatform;
import org.eclipse.persistence.platform.server.JMXServerPlatformBase;
import org.eclipse.persistence.services.wildfly.MBeanWildFlyRuntimeServices;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.ExternalTransactionController;
import org.eclipse.persistence.transaction.wildfly.WildFlyTransactionController;
import org.eclipse.persistence.transaction.wildfly.WildFlyTransactionController11;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing WildFly-specific server behavior.
 *
 * This platform overrides:
 *
 * getExternalTransactionControllerClass(): to use an WildFly-specific controller class
 *
 */
public class WildFlyPlatform extends JMXServerPlatformBase implements JMXEnabledPlatform {

    /**
     * The following constants and attributes are used to determine the module and application name
     * to satisfy the requirements for 248746 where we provide an identifier pair for JMX sessions.
     * Each application can have several modules.
     * 1) Application name - the persistence unit associated with the session (a 1-1 relationship)
     * 2) Module name - the ejb or war jar name (there is a 1-many relationship for module:session(s))
     */
    static {
        /** Override by subclass: Search String in application server ClassLoader for the application:persistence_unit name */
        APP_SERVER_CLASSLOADER_APPLICATION_PU_SEARCH_STRING_PREFIX = "/deploy/";
        /** Override by subclass: Search String in application server session for ejb modules */
        APP_SERVER_CLASSLOADER_MODULE_EJB_SEARCH_STRING_PREFIX = ".jar/";
        /** Override by subclass: Search String in application server session for war modules */
        APP_SERVER_CLASSLOADER_MODULE_WAR_SEARCH_STRING_PREFIX = ".war/";
        APP_SERVER_CLASSLOADER_APPLICATION_PU_SEARCH_STRING_POSTFIX = "/}";
        APP_SERVER_CLASSLOADER_MODULE_EJB_WAR_SEARCH_STRING_POSTFIX = "postfix,match~not;required^";
    }

    /**
     * INTERNAL:
     * Default Constructor: All behavior for the default constructor is inherited
     */
    public WildFlyPlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
        this.enableRuntimeServices();
        // Create the JMX MBean specific to this platform for later registration
        this.prepareServerSpecificServicesMBean();
    }

    @Override
    public boolean isRuntimeServicesEnabledDefault() {
        return true;
    }

    /**
     * INTERNAL: getExternalTransactionControllerClass(): Answer the class of external transaction controller to use
     * for WildFly. This is read-only.
     *
     * @return Class externalTransactionControllerClass
     *
     * @see org.eclipse.persistence.transaction.JTATransactionController
     * @see org.eclipse.persistence.platform.server.ServerPlatformBase#isJTAEnabled()
     * @see org.eclipse.persistence.platform.server.ServerPlatformBase#disableJTA()
     * @see org.eclipse.persistence.platform.server.ServerPlatformBase#initializeExternalTransactionController()
     */
    @Override
    public Class<? extends ExternalTransactionController> getExternalTransactionControllerClass() {
        if (externalTransactionControllerClass == null){
            externalTransactionControllerClass = isJTA11()
                    ? WildFlyTransactionController11.class : WildFlyTransactionController.class;
        }
        return externalTransactionControllerClass;
    }

    /**
     * INTERNAL:
     * JIRA EJBTHREE-572 requires that we use the real classLoader in place of the getNewTempClassLoader().
     * The override code should stay in place until the UCL3 loader does not throw a NPE on loadClass()
     *
     * @param puInfo - the persistence unit info
     * @return ClassLoaderHolder - a composite object containing the classLoader and the flag
     *     that is true if the classLoader returned is temporary
     *
     *  @see JPAClassLoaderHolder
     */
    @Override
    public JPAClassLoaderHolder getNewTempClassLoader(PersistenceUnitInfo puInfo) {
        // Bug 6460732: Use real classLoader instead of getNewTempClassLoader for now to avoid a WildFly NPE on loadClass()
        ClassLoader realClassLoader = puInfo.getClassLoader();
        AbstractSessionLog.getLog().log(AbstractSessionLog.WARNING, "persistence_unit_processor_wildfly_temp_classloader_bypassed",//
                puInfo.getPersistenceUnitName(), realClassLoader);
        return new JPAClassLoaderHolder(realClassLoader, false);
    }

    /**
     * INTERNAL:
     * prepareServerSpecificServicesMBean(): Server specific implementation of the
     * creation and deployment of the JMX MBean to provide runtime services for the
     * databaseSession.
     *
     * Default is to do nothing.
     * Implementing platform classes must override this function and supply
     * the server specific MBean instance for later registration by calling it in the constructor.
     *
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #registerMBean()
     */
    @Override
    public void prepareServerSpecificServicesMBean() {
        // No check for an existing cached MBean - we will replace it if it exists
        if(getDatabaseSession() != null && shouldRegisterRuntimeBean) {
            this.setRuntimeServicesMBean(new MBeanWildFlyRuntimeServices(getDatabaseSession()));
        }
    }

    /**
     * INTERNAL:
     * serverSpecificRegisterMBean(): Server specific implementation of the
     * creation and deployment of the JMX MBean to provide runtime services for my
     * databaseSession.
     *
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #registerMBean()
     */
    @Override
    public void serverSpecificRegisterMBean() {
       super.serverSpecificRegisterMBean();
        // get and cache module and application name during registration
        initializeApplicationNameAndModuleName();
    }

}
