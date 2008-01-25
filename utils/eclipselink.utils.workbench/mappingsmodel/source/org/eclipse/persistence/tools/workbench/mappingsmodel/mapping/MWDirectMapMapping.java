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


public interface MWDirectMapMapping extends MWConverterMapping {

	MWConverter getDirectKeyConverter();
	
	MWNullConverter setNullDirectKeyConverter();
	MWObjectTypeConverter setObjectTypeDirectKeyConverter();
	MWSerializedObjectConverter setSerializedObjectDirectKeyConverter();
	MWTypeConversionConverter setTypeConversionDirectKeyConverter();
	
	static final String DIRECT_KEY_CONVERTER_PROPERTY = "directKeyConverter";

}
