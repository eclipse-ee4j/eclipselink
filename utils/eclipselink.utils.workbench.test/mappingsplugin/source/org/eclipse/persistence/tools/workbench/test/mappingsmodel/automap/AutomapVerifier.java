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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.automap;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;

interface AutomapVerifier
{
	/**
	 * Asks this <code>AutomapVerifier</code> to verify the execution of the
	 * automap on the objects.
	 *
	 * @param project The root of the object hierarchy
	 */
	public void verify(MWProject project);
}
