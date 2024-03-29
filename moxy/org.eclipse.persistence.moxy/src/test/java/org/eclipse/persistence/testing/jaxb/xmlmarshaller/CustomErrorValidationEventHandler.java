/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.ValidationEvent;

public class CustomErrorValidationEventHandler implements ValidationEventHandler {

    private int errorCount = 0;
    private int ignore = 1;

    public CustomErrorValidationEventHandler() {
    }

    public CustomErrorValidationEventHandler(int numberOfErrorsToIgnore) {
        ignore = numberOfErrorsToIgnore;
    }

    public int getErrorCount() {
        return errorCount;
    }

    @Override
    public boolean handleEvent(ValidationEvent event) {
        if (event.getSeverity() < ValidationEvent.FATAL_ERROR) {
            return true;
        }

        errorCount++;

        if (errorCount <= ignore) {
            return true;
        }

        return false;
    }

}
