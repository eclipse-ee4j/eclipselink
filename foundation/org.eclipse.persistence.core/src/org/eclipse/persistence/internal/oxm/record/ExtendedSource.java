/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.record;

import javax.xml.transform.Source;

import org.eclipse.persistence.internal.oxm.Unmarshaller;

/**
 * This class is used to introduce new methods to the standard Source
 * interface that can be leveraged by MOXy.
 */
public abstract class ExtendedSource implements Source {

    private String systemId;
    
    public abstract XMLReader createReader(Unmarshaller unmarshaller);
    
    public abstract XMLReader createReader(Unmarshaller unmarshaller, Class unmarshalClass);

    @Override 
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public String getSystemId() {
        return systemId;
    }

}
