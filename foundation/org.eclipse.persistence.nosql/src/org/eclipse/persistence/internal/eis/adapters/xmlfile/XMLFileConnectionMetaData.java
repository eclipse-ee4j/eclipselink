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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.eis.adapters.xmlfile;

import javax.resource.cci.*;
import org.eclipse.persistence.Version;

/**
 * Defines the meta-data for the XML file adaptor
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class XMLFileConnectionMetaData implements ConnectionMetaData {

    /**
     * Default constructor.
     */
    public XMLFileConnectionMetaData() {
    }

    @Override
    public String getEISProductName() {
        return "EclipseLink XML File JCA Adapter";
    }

    @Override
    public String getEISProductVersion() {
        return Version.getVersion();
    }

    @Override
    public String getUserName() {
        return "";
    }
}
