/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     06/30/2010-2.1.1 Michael O'Brien 
 *       - 316513: Enable JMX MBean functionality for JBoss, Glassfish and WebSphere in addition to WebLogic
 *       Move JMX MBean generic registration code up from specific platforms
 *       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/316513</link>        
 ******************************************************************************/  
package org.eclipse.persistence.platform.server.jboss;

import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.internal.helper.JPAClassLoaderHolder;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.platform.server.JMXEnabledPlatform;
import org.eclipse.persistence.platform.server.JMXServerPlatformBase;
import org.eclipse.persistence.services.jboss.MBeanJBossRuntimeServices;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.transaction.jboss.JBossTransactionController;

/**
 * PUBLIC:
 *
 * This is the concrete subclass responsible for representing JBoss-specific server behavior.
 *
 * This platform overrides:
 *
 * getExternalTransactionControllerClass(): to use an JBoss-specific controller class
 *
 */
public class JBossPlatform extends JMXServerPlatformBase implements JMXEnabledPlatform {

    /**
     * INTERNAL:
     * Default Constructor: All behavior for the default constructor is inherited
     */
    public JBossPlatform(DatabaseSession newDatabaseSession) {
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
     * for JBoss. This is read-only.
     *
     * @return Class externalTransactionControllerClass
     *
     * @see org.eclipse.persistence.transaction.JTATransactionController
     * @see ServerPlatformBase.isJTAEnabled()
     * @see ServerPlatformBase.disableJTA()
     * @see ServerPlatformBase.initializeExternalTransactionController()
     */
    public Class getExternalTransactionControllerClass() {
    	if (externalTransactionControllerClass == null){
    		externalTransactionControllerClass = JBossTransactionController.class;
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
     *  @see org.eclipse.persistence.internal.helper.ClassLoaderHolder
     */
    public JPAClassLoaderHolder getNewTempClassLoader(PersistenceUnitInfo puInfo) {
        // Bug 6460732: Use real classLoader instead of getNewTempClassLoader for now to avoid a JBoss NPE on loadClass()
        ClassLoader realClassLoader = puInfo.getClassLoader();
        AbstractSessionLog.getLog().log(AbstractSessionLog.WARNING, "persistence_unit_processor_jboss_temp_classloader_bypassed",//
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
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #registerMBean()
     */
    @Override
    public void prepareServerSpecificServicesMBean() {
        // No check for an existing cached MBean - we will replace it if it exists
        if(shouldRegisterRuntimeBean) {
            this.setRuntimeServicesMBean(new MBeanJBossRuntimeServices(getDatabaseSession()));
        }
    }
}
