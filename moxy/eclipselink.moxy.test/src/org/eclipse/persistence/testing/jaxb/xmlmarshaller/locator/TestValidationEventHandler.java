/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlmarshaller.locator;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

public class TestValidationEventHandler implements ValidationEventHandler {

    private List<ValidationEvent> validationEvents;

    public TestValidationEventHandler() {
        validationEvents = new ArrayList<ValidationEvent>();
    }

    public List<ValidationEvent> getValidationEvents() {
        return validationEvents;
    }

    public boolean handleEvent(ValidationEvent ve) {
        validationEvents.add(ve);
        return true;
    }

}
