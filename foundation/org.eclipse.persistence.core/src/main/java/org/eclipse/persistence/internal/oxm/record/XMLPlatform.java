/*
 * Copyright (c) 2013, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.6 - initial implementation
package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.internal.oxm.XMLUnmarshaller;

import java.util.Map;

public interface XMLPlatform<XML_UNMARSHALLER extends XMLUnmarshaller> {

    PlatformUnmarshaller newPlatformUnmarshaller(XML_UNMARSHALLER xmlUnmarshaller, Map<String, Boolean> parserFeatures);

}
