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
 *      gonural - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

import org.eclipse.persistence.exceptions.JPARSException.ErrorCode;

/*
 * English resource bundle for JPARSException
 * 
 */
public class JPARSExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
            { ErrorCode.ENTITY_NOT_FOUND.value(), "An entity of type {0} with id {1} could not be found in persistence unit {2}." }
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
