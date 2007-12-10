/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
 package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.internal.oxm.ContainerValue;
import org.eclipse.persistence.internal.oxm.Reference;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;

public interface UnmarshalContext {

    public void startElement(UnmarshalRecord unmarshalRecord);

    public void characters(UnmarshalRecord unmarshalRecord);

    public void endElement(UnmarshalRecord unmarshalRecord);

    public void setAttributeValue(UnmarshalRecord unmarshalRecord, Object value, DatabaseMapping mapping);

    public void addAttributeValue(UnmarshalRecord unmarshalRecord, ContainerValue containerValue, Object value);

    public void reference(Reference reference);

    public void unmappedContent(UnmarshalRecord unmarshalRecord);

}