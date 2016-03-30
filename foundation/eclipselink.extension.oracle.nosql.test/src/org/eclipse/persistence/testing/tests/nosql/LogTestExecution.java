package org.eclipse.persistence.testing.tests.nosql;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Log the test being currently executed.
 * <p>
 * Sample usage:<ul>
 * <li>{@code @Rule public LogTestExecution logTestName = new LogTestExecution();}</li></ul>
 * See also:<ul>
 * <li>{@link org.junit.Rule}</li>
 * <li>{@link org.junit.rules.TestWatcher}</li></ul>
 */
public class LogTestExecution extends TestWatcher {

    /** Logger. */
    private static final SessionLog LOG = AbstractSessionLog.getLog();

    /**
     * Log test being currently executed.
     * @param description Test descriptor.
     */
    @Override
    protected void starting(Description description) {
        LOG.log(SessionLog.INFO, String.format(
                "Running test %s.%s", description.getTestClass().getSimpleName(), description.getMethodName()));
    }

}
