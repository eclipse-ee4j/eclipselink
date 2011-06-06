/*******************************************************************************
 * Copyright (c) 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.platform.server.sap;

import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.internal.helper.JPAClassLoaderHolder;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.transaction.sap.SAPNetWeaverTransactionController;

/**
 * Server platform for SAP NetWeaver AS Java 7.1 (including EhP 1), 7.2 and
 * follow-up releases.
 * <p>
 * Known limitations:
 * 
 * <ul>
 * <li>SAP NetWeaver AS Java (version 7.1 to 7.2) is a Java EE 5 server, hence
 * supporting only JPA 1.0. Namely, criteria queries cannot be used.</li>
 * <li>Dynamic weaving cannot be used inside SAP NetWeaver AS Java. Applications
 * should use static weaving instead.</li>
 * </ul>
 * @see <a href="http://wiki.eclipse.org/EclipseLink/Development/ServerPlatform/NetweaverPlatform">NetweaverPlatform</a>
 */
public class SAPNetWeaver_7_1_Platform extends ServerPlatformBase {

    private static final boolean NO_TEMP_CLASS_LOADER = false;

    public SAPNetWeaver_7_1_Platform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }

    @Override
    public Class getExternalTransactionControllerClass() {
        if (externalTransactionControllerClass == null){
            externalTransactionControllerClass = SAPNetWeaverTransactionController.class;
        }
        return externalTransactionControllerClass;
    }

    @Override
    public String getServerNameAndVersion() {
        String version = System.getProperty("SAP_J2EE_Engine_Version");
        if (version != null) {
            return version;
        }
        return super.getServerNameAndVersion();
    }

    @Override
    /**
     * SAP NetWeaver does not support dynamic byte code weaving. We return the original class loader
     * in order to prevent dynamic weaving. 
     */
    public JPAClassLoaderHolder getNewTempClassLoader(PersistenceUnitInfo puInfo) {
        ClassLoader realClassLoader = puInfo.getClassLoader();
        AbstractSessionLog.getLog().log(AbstractSessionLog.WARNING, "persistence_unit_processor_sap_temp_classloader_bypassed",//
                puInfo.getPersistenceUnitName(), realClassLoader);
        return new JPAClassLoaderHolder(realClassLoader, NO_TEMP_CLASS_LOADER);
    }    
}
