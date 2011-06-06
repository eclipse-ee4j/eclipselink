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
package org.eclipse.persistence.testing.tests.returning;

import java.util.Vector;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.framework.*;

public class TestModelAdapted extends TestModel {
    public TestModelAdapted() {
        super();
    }

    public TestModelAdapted(TestModel testModelToBeAdapted, ProjectAndDatabaseAdapter adapter) {
        this();
        setTestModel(testModelToBeAdapted);
        // move original tests
        Vector testsToAdd = (Vector)getTestModel().getTests().clone();
        for (int i = 0; i < testsToAdd.size(); i++) {
            TestEntity testEntity = (TestEntity)testsToAdd.elementAt(i);
            testEntity.setContainer(this);
        }
        getTestModel().getTests().clear();
        setAdapter(adapter);
        setName(getTestModel().getName() + " using " + Helper.getShortClassName(getAdapter()));
    }

    public TestModelAdapted(TestModel testModelToBeAdapted, ProjectAndDatabaseAdapter adapter, String[] testNamesToExclude) {
        this(testModelToBeAdapted, adapter);
        setTestNamesToExclude(testNamesToExclude);
    }

    public TestModelAdapted(TestModel testModelToBeAdapted, ProjectAndDatabaseAdapter adapter, String testNameToExclude) {
        this(testModelToBeAdapted, adapter, new String[1]);
        getTestNamesToExclude()[0] = testNameToExclude;
    }

    protected ProjectAndDatabaseAdapter adapter;
    protected TestModel testModel;
    protected String[] testNamesToExclude;

    public void setAdapter(ProjectAndDatabaseAdapter adapter) {
        this.adapter = adapter;
    }

    public ProjectAndDatabaseAdapter getAdapter() {
        return adapter;
    }

    public void setTestModel(TestModel testModelToBeAdapted) {
        testModel = testModelToBeAdapted;
    }

    public TestModel getTestModel() {
        return testModel;
    }

    public void setTestNamesToExclude(String[] testNamesToExclude) {
        this.testNamesToExclude = testNamesToExclude;
    }

    public String[] getTestNamesToExclude() {
        return testNamesToExclude;
    }

    public void addForcedRequiredSystems() {
        Vector systems = getTestModel().buildForcedRequiredSystems();
        for (int i = 0; i < systems.size(); i++) {
            TestSystemAdapted testSystemAdapted = new TestSystemAdapted((TestSystem)systems.elementAt(i), getAdapter());
            addForcedRequiredSystem(testSystemAdapted);
        }
    }

    public void addRequiredSystems() {
        Vector systems = getTestModel().buildRequiredSystems();
        for (int i = 0; i < systems.size(); i++) {
            TestSystemAdapted testSystemAdapted = new TestSystemAdapted((TestSystem)systems.elementAt(i), getAdapter());
            addRequiredSystem(testSystemAdapted);
        }
    }

    public void reset() {
        getExecutor().removeConfigureSystem(new TestSystemAdapted());
    }

    public void addTests() {
        getTestModel().addTests();
        Vector testsToAdd = (Vector)getTestModel().getTests().clone();
        for (int i = 0; i < testsToAdd.size(); i++) {
            TestEntity testEntity = (TestEntity)testsToAdd.elementAt(i);
            testEntity.setContainer(this);
        }
        getTestModel().getTests().clear();
        addTests(testsToAdd);
        if (shouldExcludeTests()) {
            excludeTests(this);
        }
    }

    protected boolean shouldExcludeTests() {
        return getTestNamesToExclude() != null && getTestNamesToExclude().length != 0;
    }

    protected String excludeTests(TestCollection testCollection) {
        String suffix = "";
        Vector tests = testCollection.getTests();
        for (int i = tests.size() - 1; i >= 0; i--) {
            TestEntity testEntity = (TestEntity)tests.elementAt(i);
            if (shouldBeExcluded(testEntity)) {
                tests.removeElementAt(i);
                suffix = suffix + testEntity.getName() + ' ';
            } else if (testEntity instanceof TestCollection) {
                suffix = suffix + excludeTests((TestCollection)testEntity);
            }
        }
        if (suffix.length() != 0) {
            testCollection.setName(testCollection.getName() + " without " + suffix);
        }
        return suffix;
    }

    protected boolean shouldBeExcluded(TestEntity testEntity) {
        for (int i = 0; i < testNamesToExclude.length; i++) {
            if (testEntity.getName().equals(testNamesToExclude[i])) {
                return true;
            }
        }
        return false;
    }

    public void setExecutor(org.eclipse.persistence.testing.framework.TestExecutor anExecutor) {
        super.setExecutor(anExecutor);
        getTestModel().setExecutor(anExecutor);
    }
}
