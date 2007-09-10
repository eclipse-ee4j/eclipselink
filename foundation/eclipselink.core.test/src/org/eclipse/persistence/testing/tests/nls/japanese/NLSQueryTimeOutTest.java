//package org.eclipse.persistence.testing.tests.workbenchintegration.QueryOptions;
package org.eclipse.persistence.testing.tests.nls.japanese;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;

public class NLSQueryTimeOutTest extends AutoVerifyTestCase {
    private boolean limitExceed;

    public NLSQueryTimeOutTest() {
        setDescription("[NLS_Japanese] Test the query timeout setting");
        this.limitExceed = false;
    }

    public void test() {
        try {
            getSession().executeQuery("queryTimeOutQuery", org.eclipse.persistence.testing.tests.nls.japanese.NLSEmployee.class);
        } catch (Exception e) {
            if (e instanceof org.eclipse.persistence.exceptions.DatabaseException) {
                limitExceed = true;
            }
        }
    }

    public void verify() {
        if (!limitExceed) {
            throw new org.eclipse.persistence.testing.framework.TestProblemException("Query timeout test fails");
        }
    }
}