/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.dynamic.sessionsxml;

//javase imports
import java.util.Map;
import org.w3c.dom.Document;

//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.factories.SessionsFactory;
import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject_11_1_1;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.login.LoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.SessionConfig;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;

//domain-specific (testing) imports
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createLogin;
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.logLevel;

public class ComicsConfigTestSuite {
    
    public static final String PACKAGE_PATH = 
        ComicsConfigTestSuite.class.getPackage().getName().replace('.', '/');
    public static final String COMICS_SESSION_XML = PACKAGE_PATH + "/sessions.xml";
    public static final String COMICS_SESSION_NAME = "dynamic-comics";
    
    // test fixtures
    static DatabaseSession session = null;
    @BeforeClass
    public static void setUp() {
        session = buildComicsSession();
        assertNotNull(session);
    }
    
    public static DatabaseSession buildComicsSession() {
        DynamicClassLoader dcl = new DynamicClassLoader(ComicsConfigTestSuite.class.getClassLoader());
        new DynamicTypeBuilder(dcl.createDynamicClass("model.Issue"), null);
        new DynamicTypeBuilder(dcl.createDynamicClass("model.Publisher"), null);
        new DynamicTypeBuilder(dcl.createDynamicClass("model.Title"), null);
        XMLSessionConfigLoader loader = new XMLSessionConfigLoader(COMICS_SESSION_XML) {
            @SuppressWarnings("unchecked")
            public boolean load(SessionManager sessionManager, ClassLoader loader) {
                Document document = loadDocument(loader);
                if(getExceptionStore().isEmpty()){
                    if (document.getDocumentElement().getTagName().equals("sessions")) {
                        XMLContext context = new XMLContext(new XMLSessionConfigProject_11_1_1());
                        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
                        SessionConfigs configs = (SessionConfigs)unmarshaller.unmarshal(document);
                        SessionsFactory factory = new SessionsFactory() {
                            @Override
                            protected Project loadProjectConfig(ProjectConfig projectConfig) {
                                if (projectConfig.isProjectXMLConfig()) {
                                    projectConfig.setProjectString(PACKAGE_PATH + "/" +
                                        projectConfig.getProjectString());
                                }
                                return super.loadProjectConfig(projectConfig);
                            }
                            @Override
                            protected Login buildLogin(LoginConfig loginConfig) {
                                return createLogin();
                            }
                            @Override
                            protected AbstractSession buildSession(SessionConfig sessionConfig) {
                                AbstractSession s = super.buildSession(sessionConfig);
                                if (SessionLog.OFF == logLevel) {
                                    s.dontLogMessages();
                                }
                                else {
                                    s.setLogLevel(logLevel); 
                                }
                                return s;
                            }
                            
                        };
                        Map<String, Session> sessions = factory.buildSessionConfigs(configs, loader);
                        for (Map.Entry<String, Session> entry : sessions.entrySet()) {
                            if (!sessionManager.getSessions().containsKey(entry.getKey())) {
                                sessionManager.addSession(entry.getKey(), entry.getValue());
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        };
        loader.setClassLoader(dcl);
        loader.setSessionName(COMICS_SESSION_NAME);
        return (DatabaseSession)SessionManager.getManager().getSession(loader);
        
    }

    @Test
    public void verifyDescriptorClasses() {
        ClassDescriptor descriptor = session.getClassDescriptorForAlias("Issue");
        assertNotNull(descriptor);
        assertTrue(DynamicEntity.class.isAssignableFrom(descriptor.getJavaClass()));
        descriptor = session.getClassDescriptorForAlias("Publisher");
        assertNotNull(descriptor);
        assertTrue(DynamicEntity.class.isAssignableFrom(descriptor.getJavaClass()));
        descriptor = session.getClassDescriptorForAlias("Title");
        assertNotNull(descriptor);
        assertTrue(DynamicEntity.class.isAssignableFrom(descriptor.getJavaClass()));
    }

}