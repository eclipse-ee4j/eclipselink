/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework;

import java.io.*;
import java.util.*;

import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.tests.TestRunModel;

/**
 * Run a test model, test suite or test case from the DOS prompt.
 *
 * @author Edwin Tang
 */
public class PromptTestRunner {
    protected static final String SESSION_LOG_KEY = "session.log";
    protected static final String LOG_MESSAGES_KEY = "session.logMessages";
    protected static final String SESSION_LOG_LEVEL_KEY = "session.log.level";
    protected static final String HANDLE_ERRORS_KEY = "executor.handleErrors";
    protected static final String DATABASE_PLATFORM_KEY = "login.databaseplatform";
    protected static final String DRIVER_CLASS_KEY = "login.driverClass";
    protected static final String DATABASE_URL_KEY = "login.databaseURL";
    protected static final String USER_NAME_KEY = "login.username";
    protected static final String PASSWORD_KEY = "login.password";
    protected static final String DEFAULT_SESSION_LOG = "console";
    protected static final String DEFAULT_LOG_MESSAGES = "false";
    protected static final String DEFAULT_LOG_LEVEL = "INFO";
    protected static final String DEFAULT_HANDLE_ERRORS = "true";
    protected static final String DEFAULT_DATABASE_PLATFORM = "oracle";
    protected static final String DEFAULT_DRIVER_CLASS = "oracle.jdbc.OracleDriver";
    protected static final String DEFAULT_DATABASE_URL = "jdbc:oracle:thin:@localhost:1521:ORCL";
    protected static final String DEFAULT_USER_NAME = "scott";
    protected static final String DEFAULT_PASSWORD = "tiger";
    protected static final String TEST_MODEL_FLAG = "testmodel=";
    protected static final String TEST_ENTITY_FLAG = "testentity=";
    protected static final String PROPERTIES_FLAG = "pfile=";
    protected static final String REGRESSION_LOG_FLAG = "regressionlog=";
    protected static final String USE_NATIVE_SQL_FLAG = "usenativesql=";
    protected static final String SAVE_RESULTS_FLAG = "saveresults=";
    protected static final String CREATE_DB_CONNECTION_FLAG = "createdbconnection=";
    private Writer sessionLog;
    private Writer regressionLog;
    private boolean shouldHandleErrors = true;
    private boolean useNativeSQL = false;
    private boolean saveResults = false;
    private boolean createDbConnection = true;
    private String databasePlatform;
    private String driverClass;
    private String databaseURL;
    private String username;
    private String password;
    private TestModel testModel;
    private TestEntity testEntity;
    private DatabaseSession session;
    private int sessionLogLevel;

    /**
    * Util method to log output.
    */
    public static void log(String str) {
        System.out.println(str);
    }

    /**
     * This method creates a new DatabaseLogin and DatabaseSession and logs in to the database.
     */
    public void login() {
        DatabaseLogin login = new org.eclipse.persistence.sessions.DatabaseLogin();
        try {
            login.usePlatform((DatabasePlatform)Class.forName(this.databasePlatform).newInstance());
        } catch (Exception e) {
            e.printStackTrace();
            printUsageAndExit();
        }
        login.setDriverClassName(this.driverClass);
        login.setConnectionString(this.databaseURL);
        login.setUserName(this.username);
        login.setEncryptedPassword(this.password);
        if (useNativeSQL) {
            login.useNativeSQL();
        }
        this.session = new org.eclipse.persistence.sessions.Project(login).createDatabaseSession();
	this.session.setLogLevel(this.sessionLogLevel);
        this.session.setLog(this.sessionLog);
        this.session.login();
    }

    /**
     * This method logs out of the database.
     */
    public void logout() {
        this.session.logout();
    }

    public static void main(String[] args) {
        new PromptTestRunner().run(args);
        System.exit(0);
    }

    /**
     * This method logs in to the specified database and executes the specified test model.
     */
    public void run(String[] argv) {
        if ((argv.length < 2) || (argv.length > 6)) {
            this.printUsageAndExit();
        }
        for (int i = 0; i < argv.length; i++) {
            this.processArguments(argv[i]);
        }

        try {
            if (createDbConnection) {
                login();
            }
            TestExecutor executor = new TestExecutor();
            executor.setSession(this.session);
            if (this.regressionLog != null) {
                executor.setRegressionLog(this.regressionLog);
            }
            if (shouldHandleErrors) {
                executor.handleErrors();
            }
            if ((testEntity != null) && (testModel != null)) {
                this.testModel.setExecutor(executor);
                this.testModel.setupEntity();
                executor.execute(testEntity);
                executor.logResultForTestEntity(this.testEntity);
                executor.logRegressionResultForTestEntity(this.testEntity);
            } else if (testModel != null) {
                if (createDbConnection) {
                    LoadBuildSystem.loadBuild.userName = testModel.getName();
                    LoadBuildSystem.loadBuild.loginChoice = executor.getSession().getLogin().getConnectionString();
                }
                executor.runTest(this.testModel);
                if (createDbConnection) {
                    if (saveResults && (LoadBuildSystem.loadBuild != null) && (!LoadBuildSystem.loadBuild.isEmpty())) {
                        LoadBuildSystem loadBuildSystem = new LoadBuildSystem();
                        loadBuildSystem.saveLoadBuild();
                    }
                }
            } else {
                log("\nNo test model specified.");
            }
            if (createDbConnection) {
                logout();
            }
        } catch (Throwable exception) {
            exception.printStackTrace();
        }
    }

    protected void processArguments(String arg) {
        if (arg.startsWith(TEST_MODEL_FLAG)) {
            String testModelName = arg.substring(TEST_MODEL_FLAG.length());
            if (testModelName.toLowerCase().equals("lightrunalltestmodel")) {
                this.testModel = TestRunModel.buildLRGTestModel();
            } else {
                try {
                    this.testModel = (TestModel)(Class.forName(testModelName).newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                    printUsageAndExit();
                }
            }
        } else if (arg.startsWith(TEST_ENTITY_FLAG)) {
            String testEntityName = arg.substring(TEST_ENTITY_FLAG.length());
            try {
                this.testEntity = (TestEntity)Class.forName(testEntityName).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                printUsageAndExit();
            }
        } else if (arg.startsWith(REGRESSION_LOG_FLAG)) {
            String regressionLogFileName = arg.substring(REGRESSION_LOG_FLAG.length());
            try {
                this.regressionLog = new FileWriter(regressionLogFileName, true);
            } catch (IOException e) {
                log("\nWrite regression log file " + regressionLogFileName + " error!");
                System.exit(1);
            }
        } else if (arg.startsWith(PROPERTIES_FLAG)) {
            String propertiesFileName = arg.substring(PROPERTIES_FLAG.length());
            readPropertiesFile(propertiesFileName);
        } else if (arg.startsWith(USE_NATIVE_SQL_FLAG)) {
            useNativeSQL = arg.substring(USE_NATIVE_SQL_FLAG.length()).equalsIgnoreCase("true");
        } else if (arg.startsWith(SAVE_RESULTS_FLAG)) {
            saveResults = arg.substring(SAVE_RESULTS_FLAG.length()).equalsIgnoreCase("true");
        } else if (arg.startsWith(CREATE_DB_CONNECTION_FLAG)) {
            createDbConnection = !(arg.substring(CREATE_DB_CONNECTION_FLAG.length()).equalsIgnoreCase("false"));
        } else {
            log("Invalid argument: " + arg);
            this.printUsageAndExit();
        }
    }

    public void printUsageAndExit() {
        log("\nUsage:\t java org.eclipse.persistence.testing.framework.PromptTestRunner " + TEST_MODEL_FLAG + "<testModel> " + PROPERTIES_FLAG + "<propertiesFile> [" + TEST_ENTITY_FLAG + "<testEntity> " + REGRESSION_LOG_FLAG + "<regressionLogFile> " + USE_NATIVE_SQL_FLAG + "<shouldUseNativeSQL> " + SAVE_RESULTS_FLAG + "<shouldSaveResults>]" + CREATE_DB_CONNECTION_FLAG + "<shouldCreateDbConnection>]");
        log("\n\tWhere:");
        log("\n\t<testModel>: Required, specifies test model class name");
        log("\n\t" + "<testEntity>: Optional, specifies test entity class name(test model, test suite or test case). " + "When specified, the runner will setup the test model and run the test entity only");
        log("\n\t" + "<regressionLogFile>: Optional, specifies file name of regression log, in which there is no label-related info.");
        log("\n\t<propertiesFile>: Required, specifis properties file name");
        log("\n\t<shouldUseNativeSQL>: Optional, valid values are 'true' and 'false', default value is 'false'.");
        log("\n\t<shouldSaveResults>: Optional, valid values are 'true' and 'false', default value is 'false'.");
        log("\n\t<shouldCreateDbConnection>: Optional, valid values are 'true' and 'false', default value is 'true'.");
        log("\n\t" + "Template of properties file:");
        log("\n\t\t" + "# properties for DatabseSession");
        log("\n\t\t" + "session.log=c:\\temp.txt");
        log("\n\t\t" + "session.logMessages=false");
        log("\n\n\t\t" + "# properties for TestExecutor");
        log("\n\t\t" + "executor.handleErrors=true");
        log("\n\n\t\t" + "# properties for DatabaseLogin");
        log("\n\t\t" + "login.databaseplatform=org.eclipse.persistence.platform.database.oracle.OralcePlatform");
        log("\n\t\t" + "login.driverClass=oracle.jdbc.OracleDriver");
        log("\n\t\t" + "login.databaseURL=jdbc:oracle:thin:@localhost:1521:ORCL");
        log("\n\t\t" + "login.username=scott");
        log("\n\t\t" + "login.password=tiger");
        log("\n\t" + "Example:  java org.eclipse.persistence.testing.Testframework.PromptTestRunner ");
        log("\n\t\t" + TEST_MODEL_FLAG + "org.eclipse.persistence.testing.FeatureTests.FeatureTestModel ");
        log("\n\t\t" + TEST_ENTITY_FLAG + "org.eclipse.persistence.testing.FeatureTests.OptimisticLockingDeleteRowTest ");
        log("\n\t\t" + PROPERTIES_FLAG + "C:\\titl.properties");
        log("\n\t\t" + REGRESSION_LOG_FLAG + "C:\\temp\\OptimisticLockingDeleteRowTest.log");
        System.exit(0);
    }

    /**
     * This method reads the specified properties file
     *
     * @param pFileName the file name to read
     */
    public void readPropertiesFile(String pFileName) {
        Properties p = new Properties();

        try {
            p.load(new FileInputStream(pFileName));
        } catch (IOException e) {
            log("\nFile read error: " + pFileName);
            System.exit(1);
        }

        //set sessionLog
        try {
            if (p.getProperty(SESSION_LOG_KEY, DEFAULT_SESSION_LOG).equalsIgnoreCase("console")) {
                this.sessionLog = new OutputStreamWriter(System.out);
            } else {
                this.sessionLog = new FileWriter(p.getProperty(SESSION_LOG_KEY, DEFAULT_SESSION_LOG), true);
            }
        } catch (IOException e) {
            log("\nSession log file write error: " + p.getProperty(SESSION_LOG_KEY, DEFAULT_SESSION_LOG));
            System.exit(1);
        }

        //set sessionLogLevel - value used in setSessionLogLevel(int), default is INFO (5)
        if (p.getProperty(SESSION_LOG_LEVEL_KEY,DEFAULT_LOG_LEVEL).equalsIgnoreCase("off")) {
            this.sessionLogLevel = 8;
        }
	else if (p.getProperty(SESSION_LOG_LEVEL_KEY,DEFAULT_LOG_LEVEL).equalsIgnoreCase("severe")) {
            this.sessionLogLevel = 7;
        }
	else if (p.getProperty(SESSION_LOG_LEVEL_KEY,DEFAULT_LOG_LEVEL).equalsIgnoreCase("warning")) {
            this.sessionLogLevel = 6;
        }
	else if (p.getProperty(SESSION_LOG_LEVEL_KEY,DEFAULT_LOG_LEVEL).equalsIgnoreCase("config")) {
            this.sessionLogLevel = 4;
        }
	else if (p.getProperty(SESSION_LOG_LEVEL_KEY,DEFAULT_LOG_LEVEL).equalsIgnoreCase("fine")) {
            this.sessionLogLevel = 3;
        }
	else if (p.getProperty(SESSION_LOG_LEVEL_KEY,DEFAULT_LOG_LEVEL).equalsIgnoreCase("finer")) {
            this.sessionLogLevel = 2;
        }
	else if (p.getProperty(SESSION_LOG_LEVEL_KEY,DEFAULT_LOG_LEVEL).equalsIgnoreCase("finest")) {
            this.sessionLogLevel = 1;
        }
	else if (p.getProperty(SESSION_LOG_LEVEL_KEY,DEFAULT_LOG_LEVEL).equalsIgnoreCase("all")) {
            this.sessionLogLevel = 0;
        }
	else {
            this.sessionLogLevel = 5;
        }

        //set shouldHandleErrors
        if (p.getProperty(HANDLE_ERRORS_KEY, DEFAULT_HANDLE_ERRORS).equalsIgnoreCase("true") || p.getProperty(HANDLE_ERRORS_KEY, DEFAULT_HANDLE_ERRORS).equalsIgnoreCase("false")) {
            this.shouldHandleErrors = Boolean.valueOf(p.getProperty(HANDLE_ERRORS_KEY, DEFAULT_HANDLE_ERRORS)).booleanValue();
        }
        this.databasePlatform = p.getProperty(DATABASE_PLATFORM_KEY, DEFAULT_DATABASE_PLATFORM);
        this.driverClass = p.getProperty(DRIVER_CLASS_KEY, DEFAULT_DRIVER_CLASS);
        this.databaseURL = p.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
        this.username = p.getProperty(USER_NAME_KEY, DEFAULT_USER_NAME);
        this.password = p.getProperty(PASSWORD_KEY, DEFAULT_PASSWORD);
    }
}