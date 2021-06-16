/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.eis.adapters.xmlfile;

import jakarta.resource.cci.*;
import org.eclipse.persistence.Version;

/**
 * Defines the meta-data for the XML file adapter
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class XMLFileAdapterMetaData implements ResourceAdapterMetaData {

    /**
     * Default constructor.
     */
    public XMLFileAdapterMetaData() {
    }

    @Override
    public String getAdapterName() {
        return "XML File Adapter";
    }

    @Override
    public String getAdapterShortDescription() {
        return "Emulated XML file JCA adapter.";
    }

    @Override
    public String getAdapterVendorName() {
        return "Oracle";
    }

    @Override
    public String getAdapterVersion() {
        return Version.getVersion();
    }

    @Override
    public String[] getInteractionSpecsSupported() {
        String[] specs = new String[1];
        specs[0] = "org.eclipse.persistence.eis.xml.file.XMLFileInteractionSpec";
        return specs;
    }

    @Override
    public String getSpecVersion() {
        return "1.5";
    }

    @Override
    public boolean supportsExecuteWithInputAndOutputRecord() {
        return true;
    }

    @Override
    public boolean supportsExecuteWithInputRecordOnly() {
        return true;
    }

    @Override
    public boolean supportsLocalTransactionDemarcation() {
        return true;
    }
}
