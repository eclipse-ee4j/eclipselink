/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.nosql.adapters.sdk;

import java.util.Properties;

import jakarta.resource.cci.Connection;

import org.eclipse.persistence.internal.nosql.adapters.sdk.OracleNoSQLConnectionFactory;
import org.eclipse.persistence.internal.nosql.adapters.sdk.OracleNoSQLJCAConnectionSpec;
import org.eclipse.persistence.eis.EISAccessor;
import org.eclipse.persistence.eis.EISConnectionSpec;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * Provides connection information to the Oracle NoSQL database.
 *
 * @author Radek Felcman
 * @since EclipseLink 4.0
 */
public class OracleNoSQLConnectionSpec extends EISConnectionSpec {

    /**
     * Connection spec properties.
     * NoSQL service type.
     * <ul>
     * <li>cloud - Cloud service</li>
     * <li>onprem - On-premise proxy and Oracle NoSQL Database instance</li>
     * <li>cloudsim - Cloud simulator (for development)</li>
     * </ul>
     **/
    public static String SERVICE = "nosql.service";

    /**
     * NoSQL endpoint e.g.
     * <ul>
     * <li>eu-frankfurt-1 (for cloud nosql.service))</li>
     * <li>http://localhost:8080 (for onprem nosql.service))</li>
     * <li>http://localhost:8080 (for cloudsim nosql.service))</li>
     * </ul>
     **/
    public static String END_POINT = "nosql.endpoint";

    /**
     * Compartment is an OCID. This is required if using Instance Principal or Resource Principal authorization.
     * It's walid for cloud, onprem nosql.service types.
     */
    public static String COMPARTMENT = "nosql.compartment";

    /** Cloud authorization mechanisms. Possible values are.
     * <ul>
     * <li>user - Use User Principal authorization using a config file in $HOME/.oci/config</li>
     * <li>instance - Create a SignatureProvider using an instance principal.</li>
     * <li>resource - Create a SignatureProvider using a resource principal.</li>
     * </ul>
     **/
    public static String AUTHORIZATION_PRINCIPAL = "nosql.authprincipal";

    /**
     * PUBLIC:
     * Default constructor.
     */
    public OracleNoSQLConnectionSpec() {
        super();
    }

    /**
     * Connect with the specified properties and return the Connection.
     */
    @Override
    public Connection connectToDataSource(EISAccessor accessor, Properties properties) throws DatabaseException, ValidationException {
        if ((this.connectionFactory == null) && (this.name == null)) {
            this.connectionFactory = new OracleNoSQLConnectionFactory();
        }
        if (!properties.isEmpty()) {
            if (this.connectionSpec == null) {
                this.connectionSpec = new OracleNoSQLJCAConnectionSpec();
            }
            OracleNoSQLJCAConnectionSpec spec = (OracleNoSQLJCAConnectionSpec)this.connectionSpec;
            String serviceName = (String)properties.get(SERVICE);
            if (serviceName != null) {
                spec.setService(OracleNoSQLJCAConnectionSpec.ServiceType.get(serviceName).get());
            }
            String endPoint = (String)properties.get(END_POINT);
            if (endPoint != null) {
                spec.setEndPoint(endPoint);
            }
            String compartment = (String)properties.get(COMPARTMENT);
            if (compartment  != null) {
                spec.setCompartment(compartment);
            }
            String authPrincipalType = (String)properties.get(AUTHORIZATION_PRINCIPAL);
            if (authPrincipalType  != null) {
                spec.setAuthPrincipalType(OracleNoSQLJCAConnectionSpec.AuthPrincipalType.get(authPrincipalType).get());
            }
        }
        return super.connectToDataSource(accessor, properties);
    }
}
