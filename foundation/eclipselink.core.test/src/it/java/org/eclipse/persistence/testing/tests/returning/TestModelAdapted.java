/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.returning;

import org.eclipse.persistence.testing.framework.TestCollection;
import org.eclipse.persistence.testing.framework.TestEntity;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSystem;

import java.util.Vector;

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
            TestEntity testEntity = (TestEntity)testsToAdd.get(i);
            testEntity.setContainer(this);
        }
        getTestModel().getTests().clear();
        setAdapter(adapter);
        setName(getTestModel().getName() + " using " + getAdapter().getClass().getSimpleName());
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

    @Override
    public void addForcedRequiredSystems() {
        Vector systems = getTestModel().buildForcedRequiredSystems();
        for (int i = 0; i < systems.size(); i++) {
            TestSystemAdapted testSystemAdapted = new TestSystemAdapted((TestSystem)systems.get(i), getAdapter());
            addForcedRequiredSystem(testSystemAdapted);
        }
    }

    @Override
    public void addRequiredSystems() {
        Vector systems = getTestModel().buildRequiredSystems();
        for (int i = 0; i < systems.size(); i++) {
            TestSystemAdapted testSystemAdapted = new TestSystemAdapted((TestSystem)systems.get(i), getAdapter());
            addRequiredSystem(testSystemAdapted);
        }
    }

    @Override
    public void reset() {
        getExecutor().removeConfigureSystem(new TestSystemAdapted());
    }

    @Override
    public void addTests() {
        getTestModel().addTests();
        Vector testsToAdd = (Vector)getTestModel().getTests().clone();
        for (int i = 0; i < testsToAdd.size(); i++) {
            TestEntity testEntity = (TestEntity)testsToAdd.get(i);
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
        StringBuilder suffix = new StringBuilder();
        Vector tests = testCollection.getTests();
        for (int i = tests.size() - 1; i >= 0; i--) {
            TestEntity testEntity = (TestEntity)tests.get(i);
            if (shouldBeExcluded(testEntity)) {
                tests.remove(i);
                suffix.append(testEntity.getName()).append(' ');
            } else if (testEntity instanceof TestCollection) {
                suffix.append(excludeTests((TestCollection) testEntity));
            }
        }
        if (!suffix.isEmpty()) {
            testCollection.setName(testCollection.getName() + " without " + suffix);
        }
        return suffix.toString();
    }

    protected boolean shouldBeExcluded(TestEntity testEntity) {
        for (String s : testNamesToExclude) {
            if (testEntity.getName().equals(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setExecutor(org.eclipse.persistence.testing.framework.TestExecutor anExecutor) {
        super.setExecutor(anExecutor);
        getTestModel().setExecutor(anExecutor);
    }
}
