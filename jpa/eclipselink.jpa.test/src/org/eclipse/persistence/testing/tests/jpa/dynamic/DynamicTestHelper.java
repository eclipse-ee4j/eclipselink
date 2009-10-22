/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *               http://wiki.eclipse.org/EclipseLink/Development/Dynamic
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.dynamic;

//java eXtension imports
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

//domain-specific (testing) imports
import static org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper.getDatabaseProperties;

public class DynamicTestHelper {

    public static final String DYNAMIC_PERSISTENCE_NAME = "dynamic";
    static final String DYNAMIC_PERSISTENCE_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<persistence version=\"1.0\" xmlns=\"http://java.sun.com/xml/ns/persistence\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xsi:schemaLocation=\"http://java.sun.com/xml/ns/persistence " +
            "http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd\">" +
            "<persistence-unit name=\"" + DYNAMIC_PERSISTENCE_NAME + 
                "\" transaction-type=\"RESOURCE_LOCAL\">" +
                "<exclude-unlisted-classes>true</exclude-unlisted-classes>" +
            "</persistence-unit>" +
        "</persistence>";
    
    //TODO - create custom javax.persistence.spi.PersistenceProvider
    public static EntityManagerFactory createEMF(String emName) {
         PersistenceProvider provider = new PersistenceProvider() {
            @Override
            public EntityManagerFactory createContainerEntityManagerFactory(
                PersistenceUnitInfo info, Map map) {
                return null;
            }
            @Override
            public EntityManagerFactory createEntityManagerFactory(String emName, Map map) {
                return null;
            }
            @Override
            public ProviderUtil getProviderUtil() {
                return null;
            }
         };
         return provider.createEntityManagerFactory(emName, getDatabaseProperties());
    }
}