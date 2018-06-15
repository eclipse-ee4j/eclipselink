/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - 2.4 - April 2012
package org.eclipse.persistence.testing.jaxb.json.attribute;

import org.eclipse.persistence.jaxb.MarshallerProperties;

public class JSONAttributeNoXmlRootElementIncludeRootFalseTestCases extends JSONAttributeNoXmlRootElementTestCases{

    public JSONAttributeNoXmlRootElementIncludeRootFalseTestCases(String name) throws Exception {
        super(name);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);

    }

}
