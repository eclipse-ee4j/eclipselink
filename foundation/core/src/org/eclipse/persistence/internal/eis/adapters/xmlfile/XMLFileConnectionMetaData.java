/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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

    public String getEISProductName() {
        return "TopLink XML File JCA Adapter";
    }

    public String getEISProductVersion() {
        return Version.getVersion();
    }

    public String getUserName() {
        return "";
    }
}