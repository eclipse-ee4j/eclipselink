/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.tools.workbench.utility.Model;


/**
 * Names are often used to uniquely identify a model object
 * within a set of objects of the same type. This interface
 * allows those objects to be handled by client code that
 * is only interested in that uniquely-identifying name
 * (in particular, persistence).
 */
public interface MWNominative extends Model {

	/**
	 * Return the object's name.
	 */
	String getName();

}
