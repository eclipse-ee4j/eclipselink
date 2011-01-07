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

import java.sql.Timestamp;
import java.util.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.sessions.DatabaseLogin;
import java.net.*;
import org.eclipse.persistence.*;

/**
 * <b>Purpose</b>: holds all the test results and test summary of a loadBuild<p>
 * <b>Description</b>: This class is used to save a load build to database<p>
 * <b>Responsibilities</b>:<ul>
 * <li>
 * </ul>
 * @author Steven Vo
 */
public class LoadBuildSummary {
    public long id;
    public String userName;
    public String loginChoice;
    public String os;
    public String jvm;
    public String machine;
    public String toplinkVersion;
    public int numberOfTests;
    public int errors;
    public int fatalErrors;
    public DatabaseLogin databaseLogin;
    public Timestamp timestamp;
    protected ValueHolderInterface summaries = new ValueHolder();
    protected ValueHolderInterface results = new ValueHolder();

    /**
     * LoadBuildSummary constructor comment.
     */
    public LoadBuildSummary() {
        try {
            this.os = System.getProperty("os.name");
            this.jvm = System.getProperty("java.vm.version");
            this.toplinkVersion = Version.getVersion() + " :" + Version.getBuildNumber();
            this.machine = InetAddress.getLocalHost().getHostName();
            // Trim the machine name only because of network issues.
            if (this.machine.indexOf(".") != -1) {
                this.machine = this.machine.substring(0, this.machine.indexOf("."));
            }
        } catch (Exception ignore) {
        }
    }

    public String getMachine() {
        return machine;
    }

    public String getToplinkVersion() {
        return toplinkVersion;
    }

    public String getJVM() {
        return jvm;
    }

    public String getOS() {
        return os;
    }

    public String getLoginChoice() {
        return loginChoice;
    }

    /**
     *
     * @param summary org.eclipse.persistence.testing.framework.TestResultsSummary
     */
    public void addResult(TestResult result) {
        if (results == null) {
            results = new ValueHolder(new Vector());
        } else if (results.getValue() == null) {
            results.setValue(new Vector());
        }
        getResults().addElement(result);
    }

    /**
     *
     * @param summary org.eclipse.persistence.testing.framework.TestResultsSummary
     */
    public void addSummary(TestResultsSummary summary) {
        if (summaries == null) {
            summaries = new ValueHolder(new Vector());
        } else if (summaries.getValue() == null) {
            summaries.setValue(new Vector());
        }
        for (Enumeration enumtr = getSummaries().elements(); enumtr.hasMoreElements();) {
            TestResultsSummary element = (TestResultsSummary)enumtr.nextElement();
            if (element.getName().equals(summary.getName())) {
                getSummaries().removeElement(element);
            }
        }
        getSummaries().addElement(summary);
    }

    /**
     *
     * @return int
     */
    public void computeNumberOfTestsAndErrors() {
        Vector rootSummaries = new Vector();
        numberOfTests = 0;
        errors = 0;
        fatalErrors = 0;
        for (Enumeration enumtr = getSummaries().elements(); enumtr.hasMoreElements();) {
            TestResultsSummary summary = (TestResultsSummary)enumtr.nextElement();
            if (summary.getParent() == null) {
                rootSummaries.addElement(summary);
            }
        }
        for (Enumeration enumtr = rootSummaries.elements(); enumtr.hasMoreElements();) {
            TestResultsSummary summary = (TestResultsSummary)enumtr.nextElement();
            numberOfTests += summary.getTotalTests();
            errors += summary.getErrors();
            fatalErrors += summary.getFatalErrors();
        }

        for (Enumeration enumtr = getResults().elements(); enumtr.hasMoreElements();) {
            TestResult result = (TestResult)enumtr.nextElement();
            if (result.hasError()) {
                errors++;
            } else if (result.hasFatalError()) {
                fatalErrors++;
            }
        }
        numberOfTests += getResults().size();
    }

    /**
     *
     * @return java.util.Vector
     */
    public Vector getResults() {
        if (results == null) {
            results = new ValueHolder(new Vector());
        } else if (results.getValue() == null) {
            results.setValue(new Vector());
        }
        return (Vector)results.getValue();
    }

    public ValueHolderInterface getResultsHolder() {
        return results;
    }

    public Vector getSummaries() {
        if (summaries == null) {
            summaries = new ValueHolder(new Vector());
        } else if (summaries.getValue() == null) {
            summaries.setValue(new Vector());
        }
        return (Vector)summaries.getValue();
    }

    public ValueHolderInterface getSummariesHolder() {
        return summaries;
    }

    public void initializeLoadBuild() {
        if (getResults() != null) {
            for (Enumeration enumtr = getResults().elements(); enumtr.hasMoreElements();) {
                TestResult result = (TestResult)enumtr.nextElement();
                result.setLoadBuildSummary(this);
            }
        }
        if (getSummaries() != null) {
            for (Enumeration enum1 = getSummaries().elements(); enum1.hasMoreElements();) {
                TestResultsSummary summary = (TestResultsSummary)enum1.nextElement();
                summary.setLoadBuildSummary(this);
                for (Enumeration enum2 = summary.getResults().elements(); enum2.hasMoreElements();) {
                    TestResult result = (TestResult)enum2.nextElement();
                    result.setLoadBuildSummary(this);
                }
            }
        }

        // remove test result that belongs to a testSummary
        for (int i = 0; i < getResults().size(); i++) {
            if (((TestResult)getResults().elementAt(i)).getSummary() != null) {
                getResults().removeElementAt(i);
                i--;
            }
        }
    }

    /**
     *
     * @return java.lang.Boolean
     */
    public boolean isEmpty() {
        return ((getSummaries().size() == 0) && (getResults().size() == 0));
    }

    public void setLoadBuildSummaryForTests() {
        if (getResults() != null) {
            for (Enumeration enumtr = getResults().elements(); enumtr.hasMoreElements();) {
                TestResult result = (TestResult)enumtr.nextElement();
                result.setLoadBuildSummary(this);
            }
        }
        if (getSummaries() != null) {
            for (Enumeration enum1 = getSummaries().elements(); enum1.hasMoreElements();) {
                TestResultsSummary summary = (TestResultsSummary)enum1.nextElement();
                summary.setLoadBuildSummary(this);
                for (Enumeration enum2 = summary.getResults().elements(); enum2.hasMoreElements();) {
                    TestResult result = (TestResult)enum2.nextElement();
                    result.setLoadBuildSummary(this);
                }
            }
        }

        // remove test result that belongs to a testSummary
        for (int i = 0; i < getResults().size(); i++) {
            if (((TestResult)getResults().elementAt(i)).getSummary() != null) {
                getResults().removeElementAt(i);
                i--;
            }
        }
    }

    public void setResults(Vector theResults) {
        results.setValue(theResults);
    }

    public void setResultsHolder(ValueHolderInterface holder) {
        results = holder;
    }

    /**
     *
     * @return java.util.Vector
     */
    public void setSummaries(Vector theSummaries) {
        summaries.setValue(theSummaries);
    }

    public void setSummariesHolder(ValueHolderInterface holder) {
        summaries = holder;
    }
}
