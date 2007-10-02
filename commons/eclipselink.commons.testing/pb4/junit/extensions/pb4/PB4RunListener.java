/*******************************************************************************
 * Copyright (c) 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - purpose: extended JUnit4 testing for Oracle TopLink
 ******************************************************************************/

package junit.extensions.pb4;

// javase imports
import java.util.HashMap;
import java.util.Map;

// JUnit imports
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class PB4RunListener extends RunListener implements PB4Listener {

    protected PB4Result pb4Result;
    
    public PB4RunListener(PB4Result pb4Result) {
        super();
        this.pb4Result = pb4Result;
    }

    // to maintain pass/ignore/fail counts
    protected Map<Description, Description> startfinish = new HashMap<Description, Description>();
    protected Map<Description, Description> ignored = new HashMap<Description, Description>();

    @Override
    public void testRunStarted(Description description) throws Exception {
        pb4Result.startTime = System.currentTimeMillis();
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        long endTime = System.currentTimeMillis();
        pb4Result.runTime += endTime - pb4Result.startTime;
    }

    @Override
    public void testStarted(Description description) {
        startfinish.put(description, description);
        pb4Result.totalTestCount++;
    }

    @Override
    public void testIgnored(Description description) {
        pb4Result.ignoredTestCount++;
        Description startDescription = startfinish.remove(description);
        if (startDescription == null) {
            // ignored by annotation
            pb4Result.totalTestCount++;
        } else {
            ignored.put(description, description);
        }
    }

    public void testIgnoredAtRuntime(PB4Description pb4Description) {
        testIgnored(pb4Description.getNestedDescription());
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        pb4Result.failures.add(failure);
        startfinish.remove(failure.getDescription());
    }

    @Override
    public void testFinished(Description description) throws Exception {
        if (ignored.get(description) != null) {
            ignored.remove(description);
        } else if (startfinish.get(description) != null) {
            startfinish.remove(description);
            pb4Result.passedTestCount++;
        }
    }

}
