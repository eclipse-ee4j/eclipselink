/*******************************************************************************
 * Copyright (c) 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - purpose: extended JUnit4 testing for Oracle TopLink
 ******************************************************************************/

package junit.extensions.pb4;

// javase imports
import java.io.*;
import java.util.*;

// JUnit imports
import org.junit.runner.*;
import org.junit.runner.manipulation.*;
import org.junit.runner.notification.*;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.internal.runners.CompositeRunner;
import org.junit.internal.runners.InitializationError;

/**
 * <b><p>A custom JUnit4 Runner</b><p>
 * <code>junit.extensions.pb4.ParameterizedBeforeRunner</code> in <tt>junit4-ext-pb4.jar</tt><p>
 *
 * Additional features:<ul>
 * <li>a <tt>-quiet</tt> flag</li>
 * <li>a count summary at the end that distinguishes between tests that actually
 *     pass vs. tests attempted</li>
 * <li>a <tt>-verboseSummary</tt> flag</li>
 * <li>a <tt>-stopTestsOnFailure</tt> flag: at the first failure, stop all testing</li>
 * <li>add parameters for <b>&#064;BeforeClass</b>'s - typical usage: pass in database properties
 *   <ul>
 *     <li>for a test class <code>TestSuite</code> in package <code>test</code>, the Java properties
 *     in the file<br><tt>testsuite.properties</tt> in the directory <tt>test</tt> are passed-in to
 *     each <br><b>&#064;BeforeClass</b> (and <b>&#064;AfterClass</b>) methods:
<pre>
// javase imports
import java.util.Properties;
<br>
// JUnit imports
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
<br>
// Parameterized Before JUnit extension
import junit.extensions.pb4.ParameterizedBeforeRunner;
<br>
&#064;RunWith(ParameterizedBeforeRunner.class)
public class TestSuite {
<br>
&nbsp;&nbsp;protected final static String DATABASE_USERNAME_KEY = "login.username";
&nbsp;&nbsp;protected final static String DATABASE_PASSWORD_KEY = "login.password";
&nbsp;&nbsp;protected final static String DATABASE_URL_KEY = "login.databaseURL";
&nbsp;&nbsp;protected final static String DATABASE_DRIVER_KEY = "login.driverClass";
&nbsp;&nbsp;protected static String DEFAULT_DATABASE_USERNAME = "scott";
&nbsp;&nbsp;protected static String DEFAULT_DATABASE_PASSWORD = "tiger";
&nbsp;&nbsp;protected static String DEFAULT_DATABASE_URL = "jdbc:oracle:thin:@localhost:1521:ORCL";
&nbsp;&nbsp;protected static String DEFAULT_DATABASE_DRIVER = "oracle.jdbc.OracleDriver";
<br>
&nbsp;&nbsp;// testcase fixtures
&nbsp;&nbsp;public static String username = null;
&nbsp;&nbsp;public static String password = null;
&nbsp;&nbsp;public static String url = null;
&nbsp;&nbsp;public static String driver = null;
<br>
&nbsp;&nbsp;&#064;BeforeClass
&nbsp;&nbsp;public static void setUpSession(Properties p) {
&nbsp;&nbsp;&nbsp;&nbsp;username = p.getProperty(DATABASE_USERNAME_KEY,DEFAULT_DATABASE_USERNAME);
&nbsp;&nbsp;&nbsp;&nbsp;password = p.getProperty(DATABASE_PASSWORD_KEY,DEFAULT_DATABASE_PASSWORD);
&nbsp;&nbsp;&nbsp;&nbsp;url = p.getProperty(DATABASE_URL_KEY,DEFAULT_DATABASE_URL);
&nbsp;&nbsp;&nbsp;&nbsp;driver = p.getProperty(DATABASE_DRIVER_KEY,DEFAULT_DATABASE_DRIVER);
&nbsp;&nbsp;}
}
</pre>
 *     </li>
 *     <li>regular JUnit4 <b>&#064;BeforeClass</b> and <b>&#064;AfterClass</b>'s without properties
 *     are also supported</li>
 *     <li>a global override for local test properties is also supported - set JVM argument:<br>
 *     &nbsp;&nbsp;<code>-Djunit.extensions.pb4.properties=some/path/to/foo.properties</code>
 *   </ul>
 * <li>JUnit4 has a <b>&#064;Suite</b> helper class: ParameterizedBeforeRunner also supports it</li>
 * <li>in addition to <b>&#064;Ignored</b>'s at design-time, add <code>ignore(boolean flag)</code>
 * method so that it can be <i>computed</i> at runtime</li>
 * </ul>
 */
public class ParameterizedBeforeRunner extends Runner implements Filterable, Sortable {

    public static final String SYSPROP_OVERRIDE_PROPERTY_RESOURCE =
    	ParameterizedBeforeRunner.class.getPackage().getName() + ".properties";

    protected Runner enclosedRunner;
    protected Class<?> testClass;

    public ParameterizedBeforeRunner(Class<?> clz) throws InitializationError {
        SuiteClasses annotation = clz.getAnnotation(SuiteClasses.class);
        if (annotation == null) {
            setTestClass(clz);
            PB4TestClassMethodsRunner pftcmr = new PB4TestClassMethodsRunner(clz);
            setUpRunner(clz, pftcmr);
            setEnclosedRunner(pftcmr);
        } else {
            Class<?>[] suiteClasses = annotation.value();
            CompositeRunner compositeRunner = 
            	new CompositeRunner("ParameterizedBeforeSuiteClasses");
            for (Class<?> suiteClass : suiteClasses) {
                PB4TestClassMethodsRunner pftcmr = new PB4TestClassMethodsRunner(suiteClass);
                setUpRunner(suiteClass, pftcmr);
                compositeRunner.add(pftcmr);
            }
            setEnclosedRunner(compositeRunner);
        }
    }

    protected void setUpRunner(Class<?> clz, Runner runner) throws InitializationError {
        PB4MethodValidator pfMethodValidator = new PB4MethodValidator(clz);
        validate(pfMethodValidator);
        pfMethodValidator.assertValid();
    }

    protected PB4BeforeAndAfterRunner getBeforeAndAfterRunner(final RunNotifier notifier,
        Class<?> clz, final Runner runner) {

        Properties p = new Properties();
        String propertyResource = null;
        // Get the lowercase'd-unqualified name of the testclass
        String propertiesName = clz.getName().toLowerCase();
        // get the package name, replacing .'s with /'s
        String packageName = clz.getPackage().getName().replace('.', '/');
        if (propertiesName.lastIndexOf('.') > 0) {
            propertiesName = propertiesName.substring(propertiesName.lastIndexOf('.') + 1);
        }
        // converted $ to a .
        propertiesName = propertiesName.replace('$', '.');
        propertyResource = packageName + "/" + propertiesName + ".properties";
        InputStream is = clz.getClassLoader().getResourceAsStream(propertyResource);
        if (is != null) {
            try {
                p.load(is);
            } catch (IOException e) {
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        // allow properties to be overriden: -Djunit.extensions.paramfixture.properties=
        // {an_accessible_path_to_an_overriding_dot_properties_file}
        if ((propertyResource = System.getProperty(SYSPROP_OVERRIDE_PROPERTY_RESOURCE)) != null) {
            try {
                is = new FileInputStream(propertyResource);
                p.load(is);
            } catch (Exception e) {
            }
        }
        return new PB4BeforeAndAfterRunner(clz, null) {
            protected void runUnprotected() {
                runner.run(notifier);
            }

            protected void addFailure(Throwable targetException) {
                notifier.fireTestFailure(new Failure(getDescription(), targetException));
            }
        }.setProperties(p);
    }

    protected void validate(PB4MethodValidator pfMethodValidator) {
        pfMethodValidator.validateAllMethods();
    }

    public void run(final RunNotifier notifier) {
        Runner tmpEnclosedRunner = getEnclosedRunner();
        if (tmpEnclosedRunner instanceof CompositeRunner) {
            CompositeRunner compositeRunner = (CompositeRunner) tmpEnclosedRunner;
            for (Runner runner : compositeRunner.getRunners()) {
                if (runner instanceof PB4TestClassMethodsRunner) {
                    PB4TestClassMethodsRunner pftcmr = (PB4TestClassMethodsRunner) runner;
                    getBeforeAndAfterRunner(notifier, pftcmr.getTestClass(), pftcmr).runProtected();
                }
            }
        } else {
            getBeforeAndAfterRunner(notifier, getTestClass(), tmpEnclosedRunner).runProtected();
        }
    }

    public Description getDescription() {
        return getEnclosedRunner().getDescription();
    }

    public void filter(Filter filter) throws NoTestsRemainException {
        filter.apply(enclosedRunner);
    }

    public void sort(Sorter sorter) {
        sorter.apply(enclosedRunner);
    }

    protected Runner getEnclosedRunner() {
        return enclosedRunner;
    }

    protected void setEnclosedRunner(Runner enclosedRunner) {
        this.enclosedRunner = enclosedRunner;
    }

    protected Class<?> getTestClass() {
        return testClass;
    }

    protected void setTestClass(Class<?> testClass) {
        this.testClass = testClass;
    }

}
