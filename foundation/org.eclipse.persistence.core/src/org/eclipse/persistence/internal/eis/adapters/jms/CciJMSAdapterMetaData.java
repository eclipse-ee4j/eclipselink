/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.eis.adapters.jms;


// JDK imports
import javax.resource.cci.*;

// TopLink imports
import org.eclipse.persistence.Version;

/**
 * INTERNAL:
 * Defines the meta-data for the Oracle JMS adapter
 *
 * @author Dave McCann
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class CciJMSAdapterMetaData implements ResourceAdapterMetaData {

    /**
     * The default constructor.
     */
    public CciJMSAdapterMetaData() {
    }

    /**
     * Return the adapter name
     *
     * @return the name of the adapter
     */
    public String getAdapterName() {
        return "Oracle JMS JCA Adapter";
    }

    /**
     * Return the adapter name
     *
     * @return the name of the adapter
     */
    public String getAdapterShortDescription() {
        return "Oracle JMS JCA adapter.";
    }

    /**
     * Return the vendor name
     *
     * @return the name of the adapter vendor
     */
    public String getAdapterVendorName() {
        return "Oracle";
    }

    /**
     * Return the adapter version
     *
     * @return the version of the adapter
     */
    public String getAdapterVersion() {
        return Version.getVersion();
    }

    /**
     * Return the interaction specifciations supported by the adapter
     *
     * @return supported interaction specifications
     */
    public String[] getInteractionSpecsSupported() {
        String[] specs = new String[2];
        specs[0] = "org.eclipse.persistence.internal.adapters.eis.jms.CciJMSSendInteractionSpec";
        specs[1] = "org.eclipse.persistence.internal.adapters.eis.jms.CciJMSReceiveInteractionSpec";
        specs[2] = "org.eclipse.persistence.internal.adapters.eis.jms.CciJMSSendReceiveInteractionSpec";
        return specs;
    }

    /**
     * Return the adapter spec version
     *
     * @return the spec version of the adapter
     */
    public String getSpecVersion() {
        return "1.5";
    }

    /**
     * Indicates if an execute can be performed with both an input record and an output
     * record
     *
     * @return true if an execute can be performed with both an input and output record, false otherwise
     */
    public boolean supportsExecuteWithInputAndOutputRecord() {
        return true;
    }

    /**
     * Indicates if an execute can be performed with only an input record
     *
     * @return true if an execute can be performed with only an input record, false otherwise
     */
    public boolean supportsExecuteWithInputRecordOnly() {
        return true;
    }

    /**
     * Indicates if local transaction demarcation is supported
     *
     * @return true if local transaction demarcation is supported, false otherwise
     */
    public boolean supportsLocalTransactionDemarcation() {
        return true;
    }
}
