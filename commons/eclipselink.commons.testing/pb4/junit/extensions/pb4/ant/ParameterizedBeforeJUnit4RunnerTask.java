/*
 * Copyright  2000-2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 * Contributors:
 *     Oracle - purpose: extended JUnit4 testing for EclipseLink
 ******************************************************************************/

package junit.extensions.pb4.ant;

// javase imports
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

// PB4 imports
import junit.extensions.pb4.ParameterizedBeforeJUnit4Runner;
import junit.extensions.pb4.ParameterizedBeforeRunner;

// Ant imports
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.Assertions;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Permissions;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.LoaderUtils;
import org.junit.runner.Result;

public class ParameterizedBeforeJUnit4RunnerTask extends Task {

	protected static final String BUILD_HALTED = "build halted on test failure";
	protected static final String PB4JUNIT4RUNNER =
		junit.extensions.pb4.ParameterizedBeforeJUnit4Runner.class.getName();

    protected CommandlineJava commandline = new CommandlineJava();
    protected TestClasses testClasses;
    protected File executeDir = null;
    protected File tmpDir;
    protected boolean newEnvironment = false;
    protected Environment env = new Environment();
    protected Permissions perm = null;
    protected boolean fork = false;
    protected Long timeout = null;
    protected boolean stopTestsOnfailure = false;
    protected boolean haltBuildOnFailure = false;
    protected boolean quiet = false;
    protected String failureProperty;
    protected boolean verboseSummary;
    protected String overrideProperty;
    protected Path runtimeClasses = null;
    protected AntClassLoader classLoader = null;
    protected boolean failedResult = false;
    protected boolean timedoutResult = false;
    
    /**
     * Creates a PB4JUnitRunner
     */
    public ParameterizedBeforeJUnit4RunnerTask() {
    	commandline.setClassname(PB4JUNIT4RUNNER);
    }

    /**
     * The directory to execute the VM in. Ignored if no JVM is forked.
     * @param   executeDir     the directory to execute the JVM from.
     */
    public void setDir(File executeDir) {
        this.executeDir = executeDir;
    }

    /**
     * Where Ant should place temporary files.
     *
     * @param   tmpDir     location where temporary files should go to
     */
    public void setTempdir(File tmpDir) {
        if (tmpDir != null) {
            if (!tmpDir.exists() || !tmpDir.isDirectory()) {
                throw new BuildException(tmpDir.toString()
                                         + " is not a valid temp directory");
            }
        }
        this.tmpDir = tmpDir;
    }

    /**
     * Set the timeout value (in milliseconds).
     *
     * <p>If the test is running for more than this value, the test
     * will be canceled. (works only when in 'fork' mode).</p>
     * @param value the maximum time (in milliseconds) allowed 
     */
    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    /**
     * If true, use a new environment when forked.
     *
     * <p>Will be ignored if we are not forking a new VM.</p>
     *
     * @param newEnvironment boolean indicating if setting a new environment is wished
     */
    public void setNewenvironment(boolean newEnvironment) {
        this.newEnvironment = newEnvironment;
    }

    /**
     * Adds an environment variable; used when forking.
     *
     * <p>Will be ignored if we are not forking a new VM.</p>
     * @param var environment variable to be added
     */
    public void addEnv(Environment.Variable var) {
        env.addVariable(var);
    }

    /**
     * Sets the permissions for the application run inside the same JVM.
     * @return .
     */
    public Permissions createPermissions() {
        if (perm == null) {
            perm = new Permissions();
        }
        return perm;
    }

    /**
     * If true, JVM should be forked 
     */
    public void setFork(boolean value) {
        this.fork = value;
    }

    /**
     * stop testing at first failure
     * @param   stopTestsOnfailure     if true, stop testing at first failure
     */
    public void setStopTestsOnfailure(boolean stopTestsOnfailure) {
        this.stopTestsOnfailure = stopTestsOnfailure;
    }

    /**
     * stop build process if any test fails
     * @param   haltBuildOnFailure     if true, halt build process if any test fails
     */
    public void setHaltBuildOnFailure(boolean haltBuildOnFailure) {
		this.haltBuildOnFailure = haltBuildOnFailure;
	}

	/**
     * reduce amount of output
     * @param   quiet     if true, log fewer messages
     */
    public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	/**
     * Record if any failures occurred
     * @param   failureProperty   name of Ant property set to "true" if 
     *                            any tests produce failures
     */
    public void setFailureProperty(String failureProperty) {
        this.failureProperty = failureProperty;
    }

    /**
     * If true, summarize at end of test run which tests pass/ignore/failed
     * @param   verboseSummary   if true summarize pass/ignore/failed
     * 
     */
    public void setVerboseSummary(boolean verboseSummary) {
		this.verboseSummary = verboseSummary;
	}

	public void setOverrideProperty(String overrideProperty) {
		this.overrideProperty = overrideProperty;
	}

    /**
     * The command used to invoke the Java Virtual Machine,
     * default is 'java'. The command is resolved by
     * java.lang.Runtime.exec(). Ignored if fork is disabled.
     *
     * @param   value   the new VM to use instead of <tt>java</tt>
     */
    public void setJvm(String value) {
        commandline.setVm(value);
    }

    /**
     * Adds a JVM argument; ignored if not forking.
     *
     * @return create a new JVM argument so that any argument can be
     * passed to the JVM.
     */
    public Commandline.Argument createJvmarg() {
    	return commandline.createVmArgument();
    }

    /**
     * Adds a system property that tests can access.
     * This might be useful to tranfer Ant properties to the
     * testcases when JVM forking is not enabled.
     * @param sysp new environment variable to add
     * @since Ant 1.6
     */
    public void addConfiguredSysproperty(Environment.Variable sysp) {
        // get a build exception if there is a missing key or value
        // see bugzilla report 21684
        String testString = sysp.getContent();
        getProject().log("sysproperty added : " + testString, Project.MSG_DEBUG);
        commandline.addSysproperty(sysp);
    }

    /**
     * Adds a set of properties that will be used as system properties
     * that tests can access.
     *
     * This might be useful to tranfer Ant properties to the
     * testcases when JVM forking is not enabled.
     *
     * @param sysp set of properties to be added
     * @since Ant 1.6
     */
    public void addSyspropertyset(PropertySet sysp) {
        commandline.addSyspropertyset(sysp);
    }

    /**
     * Adds a path to the bootclasspath.
     * @return reference to the bootclasspath in the embedded java command line
     * @since Ant 1.6
     */
    public Path createBootclasspath() {
    	return commandline.createBootclasspath(getProject()).createPath();
    }

    /**
     * Assertions to enable in this program (if fork=true)
     * @since Ant 1.6
     * @param asserts assertion set
     */
    public void addAssertions(Assertions asserts) {
        if (commandline.getAssertions() != null) {
            throw new BuildException("Only one assertion declaration is allowed");
        }
        commandline.setAssertions(asserts);
    }
    
    /**
     * Adds a path to the classpath.
     * @return a classpath
     */
    public Path createClasspath() {
        return commandline.createClasspath(getProject()).createPath();
    }

    /**
     * Adds a reference to a classpath defined elsewhere.
     * @param r a classpath reference
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }
    
    public TestClasses createTestClasses() {
    	if (testClasses == null) {
    		testClasses = new TestClasses();
    	}
    	return testClasses;
    }

    /**
     * Adds the jars or directories containing Ant, this task and JUnit4 
     *
     */
    public void init() {
    	runtimeClasses = new Path(getProject());
        addClasspathEntry("/junit/framework/Test.class");
        addClasspathEntry("/org/apache/tools/ant/launch/AntMain.class");
        addClasspathEntry("/org/apache/tools/ant/Task.class");
        addClasspathEntry(PB4JUNIT4RUNNER.replace('.', '/') + ".class");
    }

    /**
     * Search for the given resource and add the directory or archive
     * that contains it to the classpath.
     *
     * <p>Doesn't work for archives in JDK 1.1 as the URL returned by
     * getResource doesn't contain the name of the archive.</p>
     *
     * @param resource resource that one wants to lookup
     * @since Ant 1.4
     */
    protected void addClasspathEntry(String resource) {
        /*
         * pre Ant 1.6 this method used to call getClass().getResource
         * while Ant 1.6 will call ClassLoader.getResource().
         *
         * The difference is that Class.getResource expects a leading
         * slash for "absolute" resources and will strip it before
         * delegating to ClassLoader.getResource - so we now have to
         * emulate Class's behavior.
         */
        if (resource.startsWith("/")) {
            resource = resource.substring(1);
        } else {
            resource = "org/apache/tools/ant/taskdefs/optional/junit/"
                + resource;
        }

        File f = LoaderUtils.getResourceSource(getClass().getClassLoader(),
                                               resource);
        if (f != null) {
            log("Found " + f.getAbsolutePath(), Project.MSG_DEBUG);
            runtimeClasses.createPath().setLocation(f);
        } else {
            log("Couldn\'t find " + resource, Project.MSG_DEBUG);
        }
    }
    
    /**
     * Runs the testcase.
     *
     */
    public void execute() throws BuildException {
        CommandlineJava.SysProperties sysProperties = commandline.getSystemProperties();
        if (overrideProperty != null) {
        	Environment.Variable overrideVar = new Environment.Variable();
        	overrideVar.setKey(ParameterizedBeforeRunner.SYSPROP_OVERRIDE_PROPERTY_RESOURCE) ;
        	overrideVar.setValue(overrideProperty);
        	sysProperties.addVariable(overrideVar);
        }
    	if (!fork) {
    		// in same VM
	        if (executeDir != null) {
	            log("dir attribute ignored if running in the same VM",
	                Project.MSG_WARN);
	        }
	        if (newEnvironment || null != env.getVariables()) {
	            log("Changes to environment variables are ignored if running in "
	                + "the same VM.", Project.MSG_WARN);
	        }
	        if (commandline.getBootclasspath() != null) {
	            log("bootclasspath is ignored if running in the same VM.",
	                Project.MSG_WARN);
	        }
	        try {
	            if (perm != null) {
	                perm.setSecurityManager();
	            }
		        if (sysProperties != null) {
		            sysProperties.setSystem();
		        }
	            log("Using System properties " + System.getProperties(),
	            	Project.MSG_VERBOSE);
	            log("Implicitly adding " + runtimeClasses + " to CLASSPATH",
	                    Project.MSG_VERBOSE);
	            createClasspath().append(runtimeClasses);
	            createClassLoader();
	            if (classLoader != null) {
	                classLoader.setThreadContextLoader();
	            }
	            ParameterizedBeforeJUnit4Runner pb4Runner = 
	            	new ParameterizedBeforeJUnit4Runner();
	            int numTestClasses = testClasses.nestedTestClassNames.size();
	            String[] classNames = new String[numTestClasses];
	            for (int i = 0; i < numTestClasses; i++){
	            	classNames[i] = testClasses.nestedTestClassNames.get(i).testClassName;
	            }
	            Result result = pb4Runner.runWithArgs(classNames, System.out,
	            	quiet, verboseSummary, stopTestsOnfailure);
	            if (!result.wasSuccessful()) {
	            	failedResult = true;
	            	if (result.getFailureCount() > 0) {
	            		if (failureProperty != null) {
	            			getProject().setNewProperty(failureProperty, "true");
	            		}
		            	if (haltBuildOnFailure) {
		    				throw new BuildException(BUILD_HALTED, getLocation());
		            	}	
	            	}
	            }
	        } catch (Exception e) {
				throw new BuildException("Problem executing testcase(s) " +
					e.getMessage(), getLocation());
			}
	        finally {
	            if (perm != null) {
	                perm.restoreSecurityManager();
	            }
	        	sysProperties.restoreSystem();
	        	classLoader.resetThreadContextLoader();
	        }
    	}
    	else {
            ExecuteWatchdog watchdog = createWatchdog();
            int exitCode = executeForked(commandline, watchdog);
            boolean wasKilled = false;
            if (watchdog != null) {
                wasKilled = watchdog.killedProcess();
            }
	        if (wasKilled) {
	        	timedoutResult = true;
	            String errorMessage = "PB4JUnitRunner FAILED - Timed out";
	            if  (haltBuildOnFailure) {
	                throw new BuildException(errorMessage, getLocation());
	            } else {
	                log(errorMessage, Project.MSG_ERR);
	            }
	        }
	        if (exitCode != 0) {
            	failedResult = true;
	        	if (failureProperty != null) {
            		getProject().setNewProperty(failureProperty, "true");
	        	}
            	if (haltBuildOnFailure) {
    				throw new BuildException(BUILD_HALTED, getLocation());
            	}	
	        }
    	}
    }
    
    public int executeForked(CommandlineJava commandline, ExecuteWatchdog watchdog)
    	throws BuildException {
        log("Implicitly adding " + runtimeClasses + " to CLASSPATH",
                Project.MSG_VERBOSE);
    	createClasspath().append(runtimeClasses);
		// -quiet -verboseSummary -stopTestsOnFailure classNames
    	if (quiet) {
    		commandline.createArgument().setValue("-quiet");
    	}
    	if (verboseSummary) {
    		commandline.createArgument().setValue("-verboseSummary");
    	}
    	if (stopTestsOnfailure) {
    		commandline.createArgument().setValue("-stopTestsOnFailure");
    	}
        int numTestClasses = testClasses.nestedTestClassNames.size();
        for (int i = 0; i < numTestClasses; i++){
        	commandline.createArgument().setValue(
        		testClasses.nestedTestClassNames.get(i).testClassName);
        }
    	Execute execute = new Execute(new LogStreamHandler(this,
    		Project.MSG_INFO, Project.MSG_WARN), watchdog);
    	execute.setCommandline(commandline.getCommandline());
    	if (executeDir != null) {
    		execute.setWorkingDirectory(executeDir);
    	}
    	else {
    		execute.setWorkingDirectory(getProject().getBaseDir());
    	}
        String[] environment = env.getVariables();
        if (environment != null) {
            for (int i = 0; i < environment.length; i++) {
                log("Setting environment variable: " + environment[i],
                    Project.MSG_VERBOSE);
            }
        }
        execute.setNewenvironment(newEnvironment);
        execute.setEnvironment(environment);
		log(commandline.describeCommand(), Project.MSG_VERBOSE);
		try {
			return execute.execute();
		} catch (IOException e) {
			throw new BuildException("Process fork failed.", e, getLocation());
		}
	}

    /**
     * @return <tt>null</tt> if there is a timeout value, otherwise the
     * watchdog instance.
     *
     * @throws BuildException under unspecified circumstances
     */
    protected ExecuteWatchdog createWatchdog() throws BuildException {
        if (timeout == null) {
            return null;
        }
        return new ExecuteWatchdog((long) timeout.intValue());
    }

    /**
     * Creates and configures an AntClassLoader instance from the
     * nested classpath element.
     *
     * @since Ant 1.6
     */
    protected void createClassLoader() {
        Path userClasspath = commandline.getClasspath();
        if (userClasspath != null) {
            if (classLoader == null) {
                Path classpath = (Path)userClasspath.clone();
                classLoader = getProject().createClassLoader(classpath);
                if (getClass().getClassLoader() != null
                    && getClass().getClassLoader() != Project.class.getClassLoader()) {
                    classLoader.setParent(getClass().getClassLoader());
                }
                classLoader.setParentFirst(false);
                classLoader.addJavaLibraries();
                log("Using CLASSPATH " + classLoader.getClasspath(), 
                    Project.MSG_VERBOSE);
            }
        }
    }


    /**
     * Used for nested testClass definitions.
     */
    public static class TestClasses extends DataType {
    	public static class TestClass extends DataType {
    		public String testClassName;
    		public void setName(String testClassName) {
    			this.testClassName = testClassName;
    		}
    	}
        protected ArrayList<TestClass> nestedTestClassNames = new ArrayList<TestClass>();
        public TestClass createTestClass() {
        	TestClass testClass = new TestClass();
        	nestedTestClassNames.add(testClass);
        	return testClass;
        }
    }
}
