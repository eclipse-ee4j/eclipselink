package org.eclipse.persistence.testing.tests.failover;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;

public class FailoverTestModel extends TestModel {
        public FailoverTestModel() {
            setDescription("This suite tests TopLink's handling of failover/connection timeout situations.");
        }

        public void addRequiredSystems() {
            addRequiredSystem(new EmployeeSystem());
        }

        public void addTests() {
            addTest(new SingleConnectionFailureTest());
            addTest(new ConnectionPoolFailureTest());
        }

}
