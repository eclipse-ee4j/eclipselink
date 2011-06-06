/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.util.*;
import org.eclipse.persistence.testing.tests.TestRunModel ;

/**
 * Run a batch task, e.g., LightRunAll, MediumRunAll and NotInLightRunAll utilizing PromptTestRunner.
 *
 * @author Edwin Tang
 */
public class BatchTestRunner {
    protected static final String PROPERTIES_FILE_FLAG = "pfile=";
    protected static final String BATCH_NAME_FLAG = "batchname=";
    protected static final String LIGHT_RUN_ALL = "lightrunall";
    protected static final String MEDIUM_RUN_ALL = "mediumrunall";
    protected static final String TEST_NOT_IN_LIGHT_RUN_ALL = "testnotinlightrunall";
    private String pFileName;
    private String batchName;
    private Vector testList;

    /**
    * Util method to log output.
    */
    public static void log(String str) {
        System.out.println(str);
    }

    public static void main(String[] args) {
        new BatchTestRunner().run(args);
    }

    private void run(String[] args) {
        if (args.length < 2) {
            this.printUsageAndExit();
        }
        for (int i = 0; i < args.length; i++) {
            processArguments(args[i]);
        }

        long startTime = System.currentTimeMillis();
        log("*** Beginning of running " + batchName + ", model by model. ***");
        PromptTestRunner ptr = new PromptTestRunner();
        for (int index = 0; index < testList.size(); ++index) {
            String arg1 = "testmodel=" + testList.elementAt(index);
            String arg2 = "pfile=" + pFileName;
            String[] a = { arg1, arg2 };
            log("*** Running " + testList.elementAt(index));
            ptr.run(a);
        }
        long endTime = System.currentTimeMillis();
        log("*** End of running " + batchName + ", model by model. ***");
        log("*** " + testList.size() + " test models. ");
        log("*** Total running time is " + ((endTime - startTime) / (1000 * 60)) + "m " + (((endTime - startTime) / 1000) % 60) + "s.");
        System.exit(0);
    }

    public void printUsageAndExit() {
        log("Usage:\n\tBatchTestRunner " + PROPERTIES_FILE_FLAG + "<PropertiesFile> " + BATCH_NAME_FLAG + "<BatchName>");
        log("\n\t" + BATCH_NAME_FLAG + "<BatchName> The name could be " + LIGHT_RUN_ALL + MEDIUM_RUN_ALL + " or " + TEST_NOT_IN_LIGHT_RUN_ALL);
        log("\n\t" + PROPERTIES_FILE_FLAG + "<PropertiesFile> The specified properties file name");
        log("\n\t\t" + "Template of properties file:");
        log("\n\t\t\t" + "# properties for DatabseSession");
        log("\n\t\t\t" + "session.log=c:\\temp.txt");
        log("\n\t\t\t" + "session.logMessages=false");
        log("\n\n\t\t\t" + "# properties for TestExecutor");
        log("\n\t\t\t" + "executor.handleErrors=true");
        log("\n\n\t\t\t" + "# properties for DatabaseLogin");
        log("\n\t\t\t" + "login.databaseplatform=ORACLE");
        log("\n\t\t\t" + "login.driverClass=oracle.jdbc.OracleDriver");
        log("\n\t\t\t" + "login.databaseURL=jdbc:oracle:thin:@localhost:1521:ORCL");
        log("\n\t\t\t" + "login.username=scott");
        log("\n\t\t\t" + "login.password=tiger");
        log("\n\t" + "Example:  java org.eclipse.persistence.testing.Testframework.BatchTestRunner ");
        log("\n\t\t" + BATCH_NAME_FLAG + LIGHT_RUN_ALL);
        log("\n\t\t" + PROPERTIES_FILE_FLAG + "C:\\titlcore.properties");
        System.exit(1);
    }

    private void processArguments(String arg) {
        if (arg.startsWith(PROPERTIES_FILE_FLAG)) {
            pFileName = arg.substring(PROPERTIES_FILE_FLAG.length());
        } else if (arg.startsWith(BATCH_NAME_FLAG)) {
            batchName = arg.substring(BATCH_NAME_FLAG.length());
            if (batchName.equalsIgnoreCase(LIGHT_RUN_ALL)) {
                testList = TestRunModel.buildLRGTestList();
            } else if (batchName.equalsIgnoreCase(MEDIUM_RUN_ALL)) {
                testList = TestRunModel.buildAllTestModelsList() ;
            } else if (batchName.equalsIgnoreCase(TEST_NOT_IN_LIGHT_RUN_ALL)) {
                testList = TestRunModel.buildNonLRGTestList();
            } else {
                log("Invaild batch name.");
                log("Correct batch name should be " + LIGHT_RUN_ALL + ", " + MEDIUM_RUN_ALL + " or " + TEST_NOT_IN_LIGHT_RUN_ALL);
                System.exit(1);
            }
        }
    }
}
