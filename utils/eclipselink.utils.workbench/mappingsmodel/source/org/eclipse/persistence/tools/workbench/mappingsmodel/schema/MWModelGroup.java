/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

public interface MWModelGroup
	extends MWSchemaComponent, MWParticle
{
	/** Return one of SEQUENCE, CHOICE, or ALL */
	String getCompositor();
		public final static String SEQUENCE = "sequence";
		public final static String CHOICE = "choice";
		public final static String ALL = "all";
	
	boolean containsWildcard();
}
