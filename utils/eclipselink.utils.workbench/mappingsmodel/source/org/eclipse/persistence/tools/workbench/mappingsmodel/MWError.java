/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.mappingsmodel;

public class MWError {
	
	private volatile String errorId;
	private volatile Object[] arguments;

	public MWError(String errorId) {
		this(errorId, new Object[0]);
	}
	
	public MWError(String errorId, Object argument) {
		this(errorId, new Object[] { argument });
	}

	public MWError(String errorId, Object argument1, Object argument2) {
		this(errorId, new Object[] { argument1, argument2 });
	}

	public MWError(String errorId, Object[] arguments) {
		super();
		this.errorId = errorId;
		this.arguments = arguments;
	}

	public String getErrorId() {
		return errorId;
	}
	
	public Object[] getArguments() {
		return arguments;
	}

	public boolean equals(Object anObject) {
		if (this == anObject)
			return true;
		else if (anObject instanceof MWError) {
			MWError anError = (MWError) anObject;
			if (getErrorId() == null)
				return anError.getErrorId() == null;
			else
				return getErrorId().equals(anError.getErrorId());
		} else
			return false;
	}
	
	public int hashCode() {
		// TODO: TEMPORARY
		if (errorId == null)
			return super.hashCode();
		return getErrorId().hashCode();
	}
}
