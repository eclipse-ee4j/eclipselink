/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
    protected ValueHolderInterface<Vector<TestResultsSummary>> summaries = new ValueHolder<>();
    protected ValueHolderInterface<Vector<TestResult>> results = new ValueHolder<>();

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
            results = new ValueHolder<>(new Vector<>());
        } else if (results.getValue() == null) {
            results.setValue(new Vector<>());
        }
        getResults().addElement(result);
    }

    /**
     *
     * @param summary org.eclipse.persistence.testing.framework.TestResultsSummary
     */
    public void addSummary(TestResultsSummary summary) {
        if (summaries == null) {
            summaries = new ValueHolder<>(new Vector<>());
        } else if (summaries.getValue() == null) {
            summaries.setValue(new Vector<>());
        }
        for (Enumeration<TestResultsSummary> enumtr = getSummaries().elements(); enumtr.hasMoreElements();) {
            TestResultsSummary element = enumtr.nextElement();
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
        Vector<TestResultsSummary> rootSummaries = new Vector<>();
        numberOfTests = 0;
        errors = 0;
        fatalErrors = 0;
        for (Enumeration<TestResultsSummary> enumtr = getSummaries().elements(); enumtr.hasMoreElements();) {
            TestResultsSummary summary = enumtr.nextElement();
            if (summary.getParent() == null) {
                rootSummaries.addElement(summary);
            }
        }
        for (Enumeration<TestResultsSummary> enumtr = rootSummaries.elements(); enumtr.hasMoreElements();) {
            TestResultsSummary summary = enumtr.nextElement();
            numberOfTests += summary.getTotalTests();
            errors += summary.getErrors();
            fatalErrors += summary.getFatalErrors();
        }

        for (Enumeration<TestResult> enumtr = getResults().elements(); enumtr.hasMoreElements();) {
            TestResult result = enumtr.nextElement();
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
    public Vector<TestResult> getResults() {
        if (results == null) {
            results = new ValueHolder<>(new Vector<>());
        } else if (results.getValue() == null) {
            results.setValue(new Vector<>());
        }
        return results.getValue();
    }

    public ValueHolderInterface<? extends Vector<TestResult>> getResultsHolder() {
        return results;
    }

    public Vector<TestResultsSummary> getSummaries() {
        if (summaries == null) {
            summaries = new ValueHolder<>(new Vector<TestResultsSummary>());
        } else if (summaries.getValue() == null) {
            summaries.setValue(new Vector<>());
        }
        return summaries.getValue();
    }

    public ValueHolderInterface<? extends Vector<TestResultsSummary>> getSummariesHolder() {
        return summaries;
    }

    public void initializeLoadBuild() {
        if (getResults() != null) {
            for (Enumeration<TestResult> enumtr = getResults().elements(); enumtr.hasMoreElements();) {
                TestResult result = enumtr.nextElement();
                result.setLoadBuildSummary(this);
            }
        }
        if (getSummaries() != null) {
            for (Enumeration<TestResultsSummary> enum1 = getSummaries().elements(); enum1.hasMoreElements();) {
                TestResultsSummary summary = enum1.nextElement();
                summary.setLoadBuildSummary(this);
                for (Enumeration enum2 = summary.getResults().elements(); enum2.hasMoreElements();) {
                    TestResult result = (TestResult)enum2.nextElement();
                    result.setLoadBuildSummary(this);
                }
            }
        }

        // remove test result that belongs to a testSummary
        for (int i = 0; i < getResults().size(); i++) {
            if ((getResults().elementAt(i)).getSummary() != null) {
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
            for (Enumeration<TestResult> enumtr = getResults().elements(); enumtr.hasMoreElements();) {
                TestResult result = enumtr.nextElement();
                result.setLoadBuildSummary(this);
            }
        }
        if (getSummaries() != null) {
            for (Enumeration<TestResultsSummary> enum1 = getSummaries().elements(); enum1.hasMoreElements();) {
                TestResultsSummary summary = enum1.nextElement();
                summary.setLoadBuildSummary(this);
                for (Enumeration enum2 = summary.getResults().elements(); enum2.hasMoreElements();) {
                    TestResult result = (TestResult)enum2.nextElement();
                    result.setLoadBuildSummary(this);
                }
            }
        }

        // remove test result that belongs to a testSummary
        for (int i = 0; i < getResults().size(); i++) {
            if ((getResults().elementAt(i)).getSummary() != null) {
                getResults().removeElementAt(i);
                i--;
            }
        }
    }

    public void setResults(Vector<TestResult> theResults) {
        results.setValue(theResults);
    }

    public void setResultsHolder(ValueHolderInterface<Vector<TestResult>> holder) {
        results = holder;
    }

    /**
     *
     * @return java.util.Vector
     */
    public void setSummaries(Vector<TestResultsSummary> theSummaries) {
        summaries.setValue(theSummaries);
    }

    public void setSummariesHolder(ValueHolderInterface<Vector<TestResultsSummary>> holder) {
        summaries = holder;
    }
}
