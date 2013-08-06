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
 *     Blaise Doughan - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.record;

import java.util.Map;

import org.eclipse.persistence.internal.oxm.XMLUnmarshaller;

public interface XMLPlatform<XML_UNMARSHALLER extends XMLUnmarshaller> {

    public PlatformUnmarshaller newPlatformUnmarshaller(XML_UNMARSHALLER xmlUnmarshaller, Map<String, Boolean> parserFeatures);

}
