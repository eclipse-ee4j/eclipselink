/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

/**
 * Represents a mapping that can employ basic value holder indirection or transparent indirection
 */
public interface MWIndirectableContainerMapping extends MWIndirectableMapping
{
	/** Transparent indirection is employed for the mapping */
	boolean usesTransparentIndirection();	
	void setUseTransparentIndirection();
	
		/** Should in implementation of the above for this interface */
		public final static String TRANSPARENT_INDIRECTION = "transparentIndirection";
}
