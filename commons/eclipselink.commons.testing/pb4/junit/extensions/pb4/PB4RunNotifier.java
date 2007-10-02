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
import java.util.Iterator;
import java.util.List;

// JUnit imports
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

public class PB4RunNotifier extends RunNotifier {

    protected List<PB4Listener> pb4Listeners = new ArrayList<PB4Listener>();
    protected boolean stopTestsOnFailure;
    public boolean shadowCopyOfPleaseStop;

    public boolean stopTestsOnFailure() {
		return stopTestsOnFailure;
	}
	public void setStopTestsOnFailure(boolean stopTestsOnFailure) {
		this.stopTestsOnFailure = stopTestsOnFailure;
	}
	
	@Override
	public void pleaseStop() {
		super.pleaseStop();
		shadowCopyOfPleaseStop = true;
	}

	@Override
	public void fireTestFailure(Failure failure) {
		super.fireTestFailure(failure);
		if (stopTestsOnFailure()) {
			pleaseStop();
		}
	}

	@Override
    public void addListener(RunListener listener) {
        if (listener instanceof PB4Listener) {
            pb4Listeners.add((PB4Listener) listener);
        }
        super.addListener(listener);
    }

    @Override
    public void addFirstListener(RunListener listener) {
        if (listener instanceof PB4Listener) {
            pb4Listeners.add((PB4Listener) listener);
        }
        super.addFirstListener(listener);
    }

    @Override
    public void removeListener(RunListener listener) {
        if (listener instanceof PB4Listener) {
            pb4Listeners.remove((PB4Listener) listener);
        }
        super.removeListener(listener);
    }
    
    public List<PB4Listener> getPB4Listeners() {
    	return pb4Listeners;
    }

    private abstract class SafeNotifier {
        void run() {
            for (Iterator<PB4Listener> pb4ListenersIter = pb4Listeners.iterator(); pb4ListenersIter.hasNext();) {
                try {
                    notifyListener(pb4ListenersIter.next());
                } catch (Exception e) {
                    pb4ListenersIter.remove(); // Remove the offending listener first to avoid an infinite loop
                    fireTestFailure(new Failure(Description.TEST_MECHANISM, e));
                }
            }
        }
        abstract protected void notifyListener(PB4Listener each) throws Exception;
    }

    public void fireTestIgnoredAtRuntime(final PB4Description description) {
        new SafeNotifier() {
            @Override
            protected void notifyListener(PB4Listener each) throws Exception {
                each.testIgnoredAtRuntime(description);
            };
        }.run();
    }
}
