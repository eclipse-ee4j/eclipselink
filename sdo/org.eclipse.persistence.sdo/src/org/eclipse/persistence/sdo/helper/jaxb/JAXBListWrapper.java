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
*     mmacivor - Jan 27/2009 - 1.1 - Initial implementation
*     bdoughan - Mar 31/2009 - 2.0 - Majority of behaviour ported to JAXBList
******************************************************************************/
package org.eclipse.persistence.sdo.helper.jaxb;

import java.util.ArrayList;

import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.helper.ListWrapper;

public class JAXBListWrapper extends ListWrapper {

    public JAXBListWrapper(JAXBValueStore aJAXBValueStore, SDOProperty aProperty) {
        super(aJAXBValueStore.getDataObject(), aProperty);
        currentElements = new JAXBList(aJAXBValueStore, aProperty);
    }

    @Override
    protected void copyElements() {
        if (isLogging() && (!dataObject.getChangeSummary().isDirty(this))) {
            dataObject.getChangeSummary().getOriginalElements().put(this, new ArrayList(currentElements));
        } 
    }

    @Override
    public JAXBList getCurrentElements() {
        return (JAXBList) currentElements;
    }

}
