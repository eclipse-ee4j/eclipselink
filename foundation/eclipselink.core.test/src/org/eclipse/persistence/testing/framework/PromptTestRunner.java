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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework;

import java.io.*;

import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.tests.TestRunModel;

/**
 * Run a test model, test suite or test case from the DOS prompt.
 *
 * @author Edwin Tang
 */
public class PromptTestRunner {
    public static final String DB_DRIVER_KEY = "db.driver";
    public static final String DB_DRIVER_DEFAULT = "oracle.jdbc.OracleDriver";
    public static final String DB_URL_KEY = "db.url";
    public static final String DB_URL_DEFAULT = "jdbc:oracle:thin:@localhost:1521:ORCL";
    public static final String DB_USER_KEY = "db.user";
    public static final String DB_USER_DEFAULT = "scott";
    public static final String DB_PWD_KEY = "db.pwd";
    public static final String DB_PWD_DEFAULT = "tiger";
    public static final String DB_PLATFORM_KEY = "db.platform";
    public static final String DB_PLATFORM_DEFAULT = "org.eclipse.persistence.platform.database.OraclePlatform";
    public static final String LOGGING_LEVEL_KEY = "eclipselink.logging.level";
    public static final String LOGGING_LEVEL_DEFAULT = "info";

    protected static final String TEST_MODEL_FLAG = "testmodel=";
    protected static final String TEST_ENTITY_FLAG = "testentity=";
    protected static final String REGRESSION_LOG_FLAG = "regressionlog=";
    protected static final String USE_NATIVE_SQL_FLAG = "usenativesql=";
    protected static final String SAVE_RESULTS_FLAG = "saveresults=";
    protected static final String CREATE_DB_CONNECTION_FLAG = "createdbconnection=";

    private String databasePlatform;
    private String driverClass;
    private String databaseURL;
    private String username;
    private String password;

    private Writer regressionLog;
    private boolean shouldHandleErrors = true;
    private boolean useNativeSQL = false;
    private boolean saveResults = false;
    private boolean createDbConnection = true;
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
        getLoginProperties();
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
        this.session.setLog(new OutputStreamWriter(System.out));
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
        if ((argv.length < 1) || (argv.length > 5)) {
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
        log("\nUsage:\t java <Java VM arguments> org.eclipse.persistence.testing.framework.PromptTestRunner " + TEST_MODEL_FLAG + "<testModel> [" + TEST_ENTITY_FLAG + "<testEntity> " + REGRESSION_LOG_FLAG + "<regressionLogFile> " + USE_NATIVE_SQL_FLAG + "<shouldUseNativeSQL> " + SAVE_RESULTS_FLAG + "<shouldSaveResults>]" + CREATE_DB_CONNECTION_FLAG + "<shouldCreateDbConnection>]");
        log("\n\tWhere:");
        log("\n\t<Java VM arguments>: Required, specifies Database Login properties");
        log("\n\t<testModel>: Required, specifies test model class name");
        log("\n\t" + "<testEntity>: Optional, specifies test entity class name(test model, test suite or test case). " + "When specified, the runner will setup the test model and run the test entity only");
        log("\n\t" + "<regressionLogFile>: Optional, specifies file name of regression log, in which there is no label-related info.");
        log("\n\t<shouldUseNativeSQL>: Optional, valid values are 'true' and 'false', default value is 'false'.");
        log("\n\t<shouldSaveResults>: Optional, valid values are 'true' and 'false', default value is 'false'.");
        log("\n\t<shouldCreateDbConnection>: Optional, valid values are 'true' and 'false', default value is 'true'.");
        log("\n\t" + "Example:");
        log("\n\t" + "java");
        log("\n\t\t-D" + DB_DRIVER_KEY + "=" + DB_DRIVER_DEFAULT);
        log("\n\t\t-D" + DB_URL_KEY + "=" + DB_URL_DEFAULT);
        log("\n\t\t-D" + DB_USER_KEY + "=" + DB_USER_DEFAULT);
        log("\n\t\t-D" + DB_PWD_KEY + "=" + DB_PWD_DEFAULT);
        log("\n\t\t-D" + DB_PLATFORM_KEY + "=" + DB_PLATFORM_DEFAULT);
        log("\n\t\t-D" + LOGGING_LEVEL_KEY + "=" + LOGGING_LEVEL_DEFAULT);
        log("\n\t" + "org.eclipse.persistence.testing.Testframework.PromptTestRunner");
        log("\n\t\t" + TEST_MODEL_FLAG + "org.eclipse.persistence.testing.FeatureTests.FeatureTestModel ");
        log("\n\t\t" + TEST_ENTITY_FLAG + "org.eclipse.persistence.testing.FeatureTests.OptimisticLockingDeleteRowTest ");
        log("\n\t\t" + REGRESSION_LOG_FLAG + "C:\\temp\\OptimisticLockingDeleteRowTest.log");
        System.exit(0);
    }

    public void getLoginProperties() {
        //set sessionLogLevel - value used in setSessionLogLevel(int), default is INFO (5)
        String loggingLevel = System.getProperty(LOGGING_LEVEL_KEY, LOGGING_LEVEL_DEFAULT);
        if (loggingLevel.equalsIgnoreCase("off")) {
            this.sessionLogLevel = 8;
        }
        else if (loggingLevel.equalsIgnoreCase("severe")) {
            this.sessionLogLevel = 7;
        }
        else if (loggingLevel.equalsIgnoreCase("warning")) {
            this.sessionLogLevel = 6;
        }
        else if (loggingLevel.equalsIgnoreCase("config")) {
            this.sessionLogLevel = 4;
        }
        else if (loggingLevel.equalsIgnoreCase("fine")) {
            this.sessionLogLevel = 3;
        }
        else if (loggingLevel.equalsIgnoreCase("finer")) {
            this.sessionLogLevel = 2;
        }
        else if (loggingLevel.equalsIgnoreCase("finest")) {
            this.sessionLogLevel = 1;
        }
        else if (loggingLevel.equalsIgnoreCase("all")) {
            this.sessionLogLevel = 0;
        }
        else {
            this.sessionLogLevel = 5;
        }
        this.driverClass = System.getProperty(DB_DRIVER_KEY, DB_DRIVER_DEFAULT);
        this.databaseURL = System.getProperty(DB_URL_KEY, DB_URL_DEFAULT);
        this.username = System.getProperty(DB_USER_KEY, DB_USER_DEFAULT);
        this.password = System.getProperty(DB_PWD_KEY, DB_PWD_DEFAULT);
        this.databasePlatform = System.getProperty(DB_PLATFORM_KEY, DB_PLATFORM_DEFAULT);
    }
}
