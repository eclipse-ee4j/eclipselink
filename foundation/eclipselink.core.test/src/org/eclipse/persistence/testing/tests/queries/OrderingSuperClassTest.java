package org.eclipse.persistence.testing.tests.queries;

import java.util.Vector;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.models.employee.domain.Project;

import junit.framework.Test;
import junit.framework.TestResult;

public class OrderingSuperClassTest extends OrderingMutipleTableTest {
    protected void setup() {
        customSQLRows = getSession().executeSelectingCall(new org.eclipse.persistence.queries.SQLCall(
        		"SELECT t0.PROJ_NAME FROM PROJECT t0 ORDER BY t0.PROJ_NAME"));
    }

    public void test() {
        ReadAllQuery query = new ReadAllQuery();
        query.addAscendingOrdering("name");
        query.setReferenceClass(Project.class);

        orderedQueryObjects = executeOrderingQuery(Project.class, "name");

    }

}
