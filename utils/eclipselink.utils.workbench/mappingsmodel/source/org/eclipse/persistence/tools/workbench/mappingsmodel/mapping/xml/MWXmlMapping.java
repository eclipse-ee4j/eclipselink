/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;

public interface MWXmlMapping
	extends MWXmlNode
{
	/** 
	 * Return the first xml data field to which this mapping is mapped.
	 * This may be null.
	 */
	MWXmlField firstMappedXmlField();
	
	/**
	 * Return the schema context associated with this mapping
	 */
	MWSchemaContextComponent schemaContext();
	
}
