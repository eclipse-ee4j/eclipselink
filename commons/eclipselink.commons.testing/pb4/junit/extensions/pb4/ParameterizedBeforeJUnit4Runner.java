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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

// JUnit imports
import junit.runner.Version;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.StoppedByUserException;

public class ParameterizedBeforeJUnit4Runner {

    protected PB4RunNotifier pb4RunNotifier;

    public ParameterizedBeforeJUnit4Runner() {
        pb4RunNotifier = new PB4RunNotifier();
    }

    // -quiet -verboseSummary -stopTestsOnfailure classNames ...
    public static void main(String[] args) {

        boolean quiet = false;
        boolean verboseSummary = false;
        boolean stopTestsOnFailure = false;
        ArrayList<String> classNames = new ArrayList<String>();
        int i = 0;
        int l = args.length;
        while (i < l) {
            String arg = args[i++];
            if (arg.startsWith("-")) {
                String trimmedArg = arg.substring(1);
                if ("quiet".equalsIgnoreCase(trimmedArg)) {
                    quiet = true;
                }
                if ("verboseSummary".equalsIgnoreCase(trimmedArg)) {
                    verboseSummary = true;
                }
                if ("stopTestsOnfailure".equalsIgnoreCase(trimmedArg)) {
                	stopTestsOnFailure = true;
                }
                continue;
            } else {
                classNames.add(arg);
            }
        }
        String[] classNamesArray = classNames.toArray(new String[classNames.size()]);
        PrintStream regularWriter = new PrintStream(System.out, true);
        Result result = new ParameterizedBeforeJUnit4Runner().runWithArgs(classNamesArray,
        	regularWriter, quiet, verboseSummary, stopTestsOnFailure);
        System.exit(result.wasSuccessful() ? 0 : 1);
    }

    public Result runWithArgs(String[] classNames, PrintStream regularWriter,
    	boolean quiet, boolean verboseSummary, boolean stopTestsOnFailure) {

        if (!quiet) {
            regularWriter.println("JUnit version " + Version.id());
            regularWriter.flush();
        }
        List<Class<?>> classes = new ArrayList<Class<?>>();
        List<Failure> missingClasses = new ArrayList<Failure>();
        for (String className : classNames) {
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                if (verboseSummary) {
                    regularWriter.println("Could not find class: " + className);
                    regularWriter.flush();
                }
                Description description = Description.createSuiteDescription(className);
                Failure failure = new Failure(description, e);
                missingClasses.add(failure);
            }
        }
        RunListener listener = new PB4TextListener(regularWriter, quiet,
            verboseSummary);
        pb4RunNotifier.addListener(listener);
        pb4RunNotifier.setStopTestsOnFailure(stopTestsOnFailure);
        Result result = run(classes.toArray(new Class[0]), pb4RunNotifier);
        for (Failure each : missingClasses) {
            result.getFailures().add(each);
        }
        return result;
    }

    protected Result run(Class<?> classes[], PB4RunNotifier pb4RunNotifier) {
        return run(Request.classes("All", classes), pb4RunNotifier);
    }

    protected Result run(Request request, PB4RunNotifier pb4RunNotifier) {
        return run(request.getRunner(), pb4RunNotifier);
    }

    protected Result run(Runner runner, PB4RunNotifier pb4RunNotifier) {
        Result result = new PB4Result();
        RunListener listener = result.createListener();
        pb4RunNotifier.addFirstListener(listener);
        try {
            pb4RunNotifier.fireTestRunStarted(runner.getDescription());
            runner.run(pb4RunNotifier);
            pb4RunNotifier.fireTestRunFinished(result);
        }
        catch (StoppedByUserException sbue) {
        	for (PB4Listener pb4Listener : pb4RunNotifier.getPB4Listeners()) {
        		if (pb4Listener instanceof PB4TextListener) {
        			PB4TextListener pb4TextListener = (PB4TextListener)pb4Listener;
        			 // halted on first failure
        			Failure failure = result.getFailures().get(0);
        			StringWriter sw = new StringWriter();
        			failure.getException().printStackTrace(new PrintWriter(sw));
        			String stacktrace = sw.toString();
        			pb4TextListener.regularWriter.println("\nstopped on failure\n" + 
        				failure.getTestHeader() + "\n\t" + stacktrace);
        		}
        	}
		}
        finally {
            pb4RunNotifier.removeListener(listener);
        }
        return result;
    }
}
