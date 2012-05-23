/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.framework.wdf;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.testing.framework.wdf.customizer.AdjustArrayTypeCustomizer;
import org.eclipse.persistence.testing.framework.wdf.server.Notification;
import org.eclipse.persistence.testing.framework.wdf.server.ServerTestRunner;
import org.junit.Assert;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;

public class SkipBugzillaTestRunner extends BlockJUnit4ClassRunner {

    private static final String TEST_TO_BE_INVESTIGATED_RUN = "test.to-be-investigated.run";
    private static final String TEST_ISSUE_RUN = "test.issue.run";
    private static final String TEST_BUGZILLA_RUN = "test.bugzilla.run";


    @Override
    public void run(RunNotifier notifier) {
        if (Boolean.valueOf(System.getProperty("servertest"))) {
            runOnServer(notifier);
        } else {
            super.run(notifier);
        }
    }

    /**
     * Delegates test execution to the server. On the server, JUnit will be
     * invoked to run the tests with a special run listener, which collects
     * the notifications (events). On the client, the recorded notifications will be
     * replayed on the run notifier passed to this message.
     * @param notifier the run notifier to replay the notifications recorded on the server
     */
    private void runOnServer(RunNotifier notifier) {
        Properties properties = new Properties();
        String url = getMandatorySystemProperty("server.url");
        properties.put("java.naming.provider.url", url);
        Context context;
        try {
            context = new InitialContext(properties);
            String testrunner = getMandatorySystemProperty("server.testrunner.wdf");
            String dataSourceName = getMandatorySystemProperty("datasource.name");

            Object object = context.lookup(testrunner);
            ServerTestRunner runner = (ServerTestRunner) PortableRemoteObject.narrow(object, ServerTestRunner.class);
            String testClassName = getTestClass().getJavaClass().getName();
            List<Notification> notifications = runner.runTestClass(testClassName, dataSourceName, testProperties);
            
            for (Notification notification : notifications) {
                notification.notify(notifier);
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getMandatorySystemProperty(final String propertyName) {
        String url = System.getProperty(propertyName);
        if (url == null) {
            Assert.fail("System property '" + propertyName + "' must be set.");
        }
        return url;
    }

    final long bugid;
    final long issueid;
    final boolean runAllBugzilla;
    final boolean runAllIssues;
    final boolean runAllUnknown;
    final boolean runOnlyUnknown;
    final Class<? extends DatabasePlatform> databasePlatformClass;
    private final Map<String, String> testProperties;
    
    
    @SuppressWarnings("unchecked")
    public SkipBugzillaTestRunner(Class<?> klass) throws Throwable {
        super(klass);
        
        testProperties = AbstractBaseTest.getTestProperties();
        
        
        addProperty(TEST_BUGZILLA_RUN);
        addProperty(TEST_ISSUE_RUN);
        addProperty(TEST_TO_BE_INVESTIGATED_RUN);

        String databasePlatformClassName = testProperties.get(PersistenceUnitProperties.TARGET_DATABASE); 
        

		if (databasePlatformClassName != null) {
			databasePlatformClass = (Class<? extends DatabasePlatform>) Class.forName(databasePlatformClassName);
			AdjustArrayTypeCustomizer.setDatabasePlatformClass(databasePlatformClass);
		} else {
			databasePlatformClass = null; // FIXME
		}        

        String testBugzillaRun = (String) testProperties.get(TEST_BUGZILLA_RUN);
        if ("all".equals(testBugzillaRun)) {
            runAllBugzilla = true;
            bugid = -1;
        } else {
            runAllBugzilla = false;
            if (testBugzillaRun != null) {
                bugid = Long.parseLong(testBugzillaRun);
            } else {
                bugid = -1;
            }
        }

        String testIssueRun = (String) testProperties.get(TEST_ISSUE_RUN);
        if ("all".equals(testIssueRun)) {
            runAllIssues = true;
            issueid = -1;
        } else {
            runAllIssues = false;
            if (testIssueRun != null) {
                issueid = Long.parseLong(testIssueRun);
            } else {
                issueid = -1;
            }
        }

        String testToBeInvestigatedRun = (String) testProperties.get(TEST_TO_BE_INVESTIGATED_RUN);
        if ("all".equals(testToBeInvestigatedRun)) {
            runAllUnknown = true;
            runOnlyUnknown = false;
        } else if ("only".equals(testToBeInvestigatedRun)) {
            runAllUnknown = false;
            runOnlyUnknown = true;
        } else {
            runAllUnknown = false;
            runOnlyUnknown = false;
        }
        

    }

    private void addProperty(final String name) {
        String value = System.getProperty(name);
        if(value != null) {
            testProperties.put(name, value);
        }
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        try {
            skipMethod(method, notifier, SKIP_SKIPPER);
            skipMethod(method, notifier, BUGZILLA_SKIPPER);
            skipMethod(method, notifier, ISSUE_SKIPPER);
            skipMethod(method, notifier, TO_BE_INVESTIGATED_SKIPPER);
            super.runChild(method, notifier);
        } catch (SkipException ex) {
            Description description = describeChild(method);
            notifier.fireTestIgnored(description);
        }
    }

    private <T extends Annotation> void skipMethod(FrameworkMethod method, RunNotifier notifier, Skipper<T> skipper)
            throws SkipException {
        Method reflectMethod = method.getMethod();
        T a = skipper.getAnnotation(reflectMethod);
        if (a == null) {
            if (skipper.skipOthers()) {
                throw new SkipException();
            }
            return;
        }

        if (skipper.runAll()) {
            return;
        }

        if (skipper.runThis(a)) {
            return;
        }

        Class<? extends DatabasePlatform>[] databases = skipper.getDatabases(a);
        String[] databaseNames = skipper.getDatabaseNames(a);
        
        if ((databases == null || databases.length == 0) && (databaseNames == null || databaseNames.length == 0)) {
            // all databases are unsupported
            throw new SkipException();
        }

        if (databasePlatformClass != null) {
            if (databases != null) {
                for (Class<? extends DatabasePlatform> clazz : databases) {
                    if (clazz.isAssignableFrom(databasePlatformClass)) {
                        // the current database platform is not supported
                        throw new SkipException();
                    }
                }
            }

            if (databaseNames != null) {
                for (String name : databaseNames) {
                    if (databasePlatformClass.getName().equals(name)) {
                        // the current database platform is not supported
                        throw new SkipException();
                    }
                }
            }
        }

        return;
    }

    private static class SkipException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    private Skipper<Skip> SKIP_SKIPPER = new Skipper<Skip>() {

        @Override
        public Skip getAnnotation(Method method) {
            return method.getAnnotation(Skip.class);
        }

        @Override
        public Class<? extends DatabasePlatform>[] getDatabases(Skip skip) {
            return skip.databases();
        }

        @Override
        public boolean runAll() {
            return false;
        }

        @Override
        public boolean runThis(Skip skip) {
            
            if(skip.server()) {
                return !ServerInfoHolder.isOnServer();
            }
            
            return false;
        }

        @Override
        public boolean skipOthers() {
            return false;
        }

        @Override
        public String[] getDatabaseNames(Skip skip) {
            return skip.databaseNames();
        }

    };

    private final Skipper<Bugzilla> BUGZILLA_SKIPPER = new Skipper<Bugzilla>() {

        @Override
        public Bugzilla getAnnotation(Method method) {
            return method.getAnnotation(Bugzilla.class);
        }

        @Override
        public Class<? extends DatabasePlatform>[] getDatabases(Bugzilla t) {
            return t.databases();
        }

        @Override
        public boolean runAll() {
            return runAllBugzilla;
        }

        @Override
        public boolean runThis(Bugzilla t) {
            return t.bugid() == bugid;
        }

        @Override
        public boolean skipOthers() {
            return false;
        }

        @Override
        public String[] getDatabaseNames(Bugzilla t) {
            return t.databaseNames();
        }

    };

    private final Skipper<Issue> ISSUE_SKIPPER = new Skipper<Issue>() {

        @Override
        public Issue getAnnotation(Method method) {
            return method.getAnnotation(Issue.class);
        }

        @Override
        public Class<? extends DatabasePlatform>[] getDatabases(Issue t) {
            return t.databases();
        }

        @Override
        public boolean runAll() {
            return runAllIssues;
        }

        @Override
        public boolean runThis(Issue t) {
            return t.issueid() == issueid;
        }

        @Override
        public boolean skipOthers() {
            return false;
        }

        @Override
        public String[] getDatabaseNames(Issue t) {
            return t.databaseNames();
        }

    };

    private final Skipper<ToBeInvestigated> TO_BE_INVESTIGATED_SKIPPER = new Skipper<ToBeInvestigated>() {

        @Override
        public ToBeInvestigated getAnnotation(Method method) {
            return method.getAnnotation(ToBeInvestigated.class);
        }

        @Override
        public Class<? extends DatabasePlatform>[] getDatabases(ToBeInvestigated t) {
            return t.databases();
        }

        @Override
        public boolean runAll() {
            return runAllUnknown || runOnlyUnknown;
        }

        @Override
        public boolean runThis(ToBeInvestigated t) {
            return false;
        }

        @Override
        public boolean skipOthers() {
            return runOnlyUnknown;
        }

        @Override
        public String[] getDatabaseNames(ToBeInvestigated t) {
            return t.databaseNames();
        }

    };

    private interface Skipper<T extends Annotation> {
        T getAnnotation(Method method);

        boolean runAll();

        boolean runThis(T t);
        
        boolean skipOthers();

        Class<? extends DatabasePlatform>[] getDatabases(T t);
        
        String[] getDatabaseNames(T t);
    }


}
