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

import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;

/**
 * Simple interface used primarily for morphing in between 
 * XML mappings that have a single xml field.
 */
public interface MWXpathedMapping
{
	MWXmlField getXmlField();
}
