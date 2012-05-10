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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;

/**
 * Represents a mapping that can employ basic value holder indirection
 */
public interface MWIndirectableMapping extends MWNode
{
	/** No indirection is employed for the mapping */
	boolean usesNoIndirection();
	void setUseNoIndirection();
	
		/** May be used in implementation of the above for this interface */
		public final static String NO_INDIRECTION = "no-indirection";
	
	
	/** Value Holder indirection is employed for the mapping */
	boolean usesValueHolderIndirection();
	void setUseValueHolderIndirection();
	
		/** May be used in implementation of the above for this interface */
		public final static String VALUE_HOLDER_INDIRECTION = "value-holder-indirection";
	
	
	/** Property change notification for indirection */
	public final static String INDIRECTION_PROPERTY = "indirection";
}
