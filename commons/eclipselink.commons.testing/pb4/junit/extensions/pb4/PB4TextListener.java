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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// JUnit imports
import org.junit.internal.runners.TextListener;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class PB4TextListener extends TextListener implements PB4Listener {

    protected PrintStream regularWriter;
    protected boolean quiet = false;
    protected boolean verboseSummary = false;
    protected StringBuilder passedSummary = null;
    protected StringBuilder ignoredSummary = null;
    protected StringBuilder failedSummary = null;
    // manage state-machine for 'proper' ./I/E reporting
    protected Map<Description, Description> startfinish =
    	new HashMap<Description, Description>();
    protected Map<Description, Description> ignored =
    	new HashMap<Description, Description>();

    public PB4TextListener(boolean quiet, boolean verboseSummary) {
        this(System.out, quiet, verboseSummary);
    }

    public PB4TextListener(PrintStream regularWriter, boolean quiet,
    	boolean verboseSummary) {
        super(regularWriter);
        this.regularWriter = regularWriter;
        this.verboseSummary = verboseSummary;
        if (verboseSummary) {
            passedSummary = new StringBuilder("passed:\n");
            ignoredSummary = new StringBuilder("ignored:\n");
            failedSummary = new StringBuilder("failed:\n");
        }
        this.quiet = quiet;
    }

    @Override
    public void testStarted(Description description) {
        startfinish.put(description, description);
    }

    public void testIgnoredAtRuntime(PB4Description pb4Description) {
        // ignored at runtime
        Description nestedDescription = pb4Description.getNestedDescription();
        ignored.put(nestedDescription, nestedDescription);
        if (!quiet) {
            regularWriter.append('i');
        }
        if (verboseSummary) {
            ignoredSummary.append("\t(runtime)");
            ignoredSummary.append(nestedDescription.getDisplayName());
            ArrayList<Description> children = nestedDescription.getChildren();
            if (children.size() > 0) {
                ignoredSummary.append(" -");
                for (Description childDescription : children) {
                    ignoredSummary.append(" ");
                    ignoredSummary.append(childDescription.getDisplayName());
                }
            }
            ignoredSummary.append("\n");
        }
        startfinish.remove(nestedDescription);
    }

    @Override
    public void testIgnored(Description description) {
        Description startDescription = startfinish.remove(description);
        if (startDescription != null) {
          ignored.put(startDescription, startDescription);
        }
        if (!quiet) {
            super.testIgnored(description);
        }
        if (verboseSummary) {
            ignoredSummary.append("\t");
            ignoredSummary.append(description);
            ignoredSummary.append("\n");
        }
    }

    @Override
    public void testFailure(Failure failure) {
        if (!quiet) {
            regularWriter.append('E');
        }
        if (verboseSummary) {
            failedSummary.append("\t");
            failedSummary.append(failure.getDescription());
            failedSummary.append("\n");
        }
        startfinish.remove(failure.getDescription());
    }

    @Override
    public void testFinished(Description description) throws Exception {
        if (ignored.get(description) != null) {
            ignored.remove(description);
        } else if (startfinish.get(description) != null) {
            if (!quiet) {
                regularWriter.append('.');
            }
            if (verboseSummary) {
                passedSummary.append("\t");
                passedSummary.append(description);
                passedSummary.append("\n");
            }
            startfinish.remove(description);
        }
    }

    @Override
    protected void printHeader(long runTime) {
        if (!quiet) {
            super.printHeader(runTime);
        }
    }

    @Override
    protected void printFailures(Result result) {
        if (!quiet) {
            super.printFailures(result);
        }
    }

    @Override
    protected void printFooter(Result result) {
        PB4Result pb4Result = (PB4Result) result;
        if (!quiet) {
            boolean successful = pb4Result.wasSuccessful();
            if (successful) {
                regularWriter.println("\nOK");
            } else {
                regularWriter.println("\nFAILURES!!!");
            }
            regularWriter.println(countSummary(pb4Result));
        }
        if (verboseSummary) {
        	regularWriter.println(verboseSummary());
        }
        regularWriter.flush();
    }

    protected String countSummary(PB4Result pb4Result) {
        StringBuilder sb = new StringBuilder();
        boolean successful = pb4Result.wasSuccessful();
        int passed = pb4Result.passedTestCount;
        sb.append(passed);
        sb.append(" test");
        sb.append(passed == 1 ? "" : "s");
        sb.append(" passed, ");
        sb.append(pb4Result.ignoredTestCount);
        sb.append(" ignored");
        if (!successful) {
            sb.append(", ");
            sb.append(pb4Result.getFailureCount());
            sb.append(" failed");
        }
        sb.append(", total tests ");
        sb.append(pb4Result.totalTestCount);
        return sb.toString();
    }

    protected String verboseSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(passedSummary.toString());
        sb.append(ignoredSummary.toString());
        sb.append(failedSummary.toString());
        return sb.toString();
    }

}
