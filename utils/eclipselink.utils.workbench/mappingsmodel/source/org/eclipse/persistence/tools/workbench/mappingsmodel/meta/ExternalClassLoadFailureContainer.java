/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyIterator;


/**
 * An "ExternalClassLoadFailureListener" that simply gathers up the
 * failures and makes them available for later investigation.
 */

public class ExternalClassLoadFailureContainer 
	implements ExternalClassLoadFailureListener 
{
	/** failures, keyed by class name */
	private Map failures = new HashMap();

	public void externalClassLoadFailure(ExternalClassLoadFailureEvent e) {
		this.failures.put(e.getClassName(), e.getCause());
	}

	/** return the names of the classes that failed to load */
	public Iterator failureClassNames() {
		return new ReadOnlyIterator(this.failures.keySet());
	}

	/** return the failures */
	public Iterator failures() {
		return new ReadOnlyIterator(this.failures.values());
	}

	/** return the load failure cause for the specified class */
	public Throwable failureForClassNamed(String className) {
		return (Throwable) this.failures.get(className);
	}

	/** return whether the specified class failed to load */
	public boolean containsFailureForClassNamed(String className) {
		return this.failures.get(className) != null;
	}

	/** return whether any failures occurred */
	public boolean isEmpty() {
		return this.failures.isEmpty();
	}

	/** return whether any failures occurred */
	public boolean containsFailures() {
		return ! this.failures.isEmpty();
	}
}
