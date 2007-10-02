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
import java.util.ArrayList;
import java.util.List;

// JUnit imports
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class PB4Result extends Result {

    protected List<Failure> failures = new ArrayList<Failure>();
    protected int totalTestCount = 0;
    protected int passedTestCount = 0;
    protected int ignoredTestCount = 0;
    protected long runTime = 0;
    protected long startTime;

    @Override
    public long getRunTime() {
        return runTime;
    }

    @Override
    public int getFailureCount() {
        return failures.size();
    }

    @Override
    public List<Failure> getFailures() {
        return failures;
    }

    @Override
    public int getIgnoreCount() {
        return ignoredTestCount;
    }

    @Override
    public boolean wasSuccessful() {
        return failures.size() == 0;
    }

    @Override
    public RunListener createListener() {
        return new PB4RunListener(this);
    }
}
