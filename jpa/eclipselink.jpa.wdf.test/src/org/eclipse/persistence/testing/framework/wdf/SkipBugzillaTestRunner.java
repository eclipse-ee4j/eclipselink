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

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;

public class SkipBugzillaTestRunner extends BlockJUnit4ClassRunner {

    final long bugid;
    final long issueid;
    final boolean runAllBugzilla;
    final boolean runAllIssues;
    final boolean runAllUnknown;
    final Class<? extends DatabasePlatform> databasePlatformClass;

    public SkipBugzillaTestRunner(Class<?> klass) throws Throwable {
        super(klass);

        Properties properties = loadProperties();

        String databasePlatformClassName = properties.getProperty(JUnitTestCaseHelper.DB_PLATFORM_KEY);

        databasePlatformClass = (Class<? extends DatabasePlatform>) Class.forName(databasePlatformClassName);

        String testBugzillaRun = (String) properties.get("test.bugzilla.run");
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

        String testIssueRun = (String) properties.get("test.issue.run");
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

        String testToBeInvestigatedRun = (String) properties.get("test.to-be-investigated.run");
        if ("all".equals(testToBeInvestigatedRun)) {
            runAllUnknown = true;
        } else {
            runAllUnknown = false;
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
            return;
        }

        if (skipper.runAll()) {
            return;
        }

        if (skipper.runThis(a)) {
            return;
        }

        Class<? extends DatabasePlatform>[] databases = skipper.getDatabases(a);
        if (databases == null || databases.length == 0) {
            // all databases are unsupported
            throw new SkipException();
        }

        for (Class<? extends DatabasePlatform> clazz : databases) {
            if (clazz.isAssignableFrom(databasePlatformClass)) {
                // the current database platform is not supported
                throw new SkipException();
            }
        }

        return;
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        File testPropertiesFile = new File(System.getProperty(JUnitTestCaseHelper.TEST_PROPERTIES_FILE_KEY,
                JUnitTestCaseHelper.TEST_PROPERTIES_FILE_DEFAULT));
        URL url = null;
        if (testPropertiesFile.exists()) {
            try {
                url = testPropertiesFile.toURI().toURL();
            } catch (MalformedURLException exception) {
                throw new RuntimeException("Error loading " + testPropertiesFile.getName() + ".", exception);
            }
        } else {
            // Load as a resource if from a jar.
            url = JUnitTestCaseHelper.class.getResource("/"
                    + System.getProperty(JUnitTestCaseHelper.TEST_PROPERTIES_FILE_KEY,
                            JUnitTestCaseHelper.TEST_PROPERTIES_FILE_DEFAULT));
        }
        if (url != null) {
            try {
                properties.load(url.openStream());
            } catch (java.io.IOException exception) {
                throw new RuntimeException("Error loading " + testPropertiesFile.getName() + ".", exception);
            }
        }

        return properties;

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
            return false;
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
            return runAllUnknown;
        }

        @Override
        public boolean runThis(ToBeInvestigated t) {
            return false;
        }

    };

    private interface Skipper<T extends Annotation> {
        T getAnnotation(Method method);

        boolean runAll();

        boolean runThis(T t);

        Class<? extends DatabasePlatform>[] getDatabases(T t);
    }

    private static DatabasePlatform getDatabasePlatform() {
        Platform platform = JUnitTestCase.getDbPlatform();
        if (platform instanceof DatabasePlatform) {
            return (DatabasePlatform) platform;
        }
        throw new IllegalStateException("wrong instance: " + platform.getClass().getName());
    }

}
