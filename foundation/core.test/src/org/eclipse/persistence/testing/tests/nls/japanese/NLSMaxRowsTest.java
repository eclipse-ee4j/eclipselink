//package org.eclipse.persistence.testing.tests.workbenchintegration.QueryOptions;
package org.eclipse.persistence.testing.tests.nls.japanese;

import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import java.util.*;

public class NLSMaxRowsTest extends AutoVerifyTestCase {
    private Vector employees;

    public NLSMaxRowsTest() {
        setDescription("[NLS_Japanese] Set up the limit for the maximum number of rows the query returns");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
    }

    public void test() {
        employees = (Vector)getSession().executeQuery("maxRowsQuery", org.eclipse.persistence.testing.tests.nls.japanese.NLSEmployee.class);
    }

    public void verify() {
        if (employees.size() != 4) {
            throw new org.eclipse.persistence.testing.framework.TestErrorException("ReadAllQuery with setMaxRows test failed. Mismatched objects returned");
        }
    }
}