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
package org.eclipse.persistence.internal.nosql.adapters.sdk;

import jakarta.resource.cci.ConnectionSpec;

import java.util.Arrays;
import java.util.Optional;

/**
 * Defines connection information for connecting to Oracle NoSQL.
 *
 * @author Radek Felcman
 * @since EclipseLink 4.0
 */
public class OracleNoSQLJCAConnectionSpec implements ConnectionSpec {

    /** Cloud authorization mechanisms.
     * <ul>
     * <li>user - Use User Principal authorization using a config file in $HOME/.oci/config</li>
     * <li>instance - Create a SignatureProvider using an instance principal.</li>
     * <li>resource - Create a SignatureProvider using a resource principal.</li>
     * </ul>
     **/
    public enum AuthPrincipalType {
        USER("user"),
        INSTANCE("instance"),
        RESOURCE("resource");

        private final String authPrincipalName;

        AuthPrincipalType(String authPrincipalName) {
            this.authPrincipalName = authPrincipalName;
        }

        public String getAuthPrincipalName() {
            return this.authPrincipalName;
        }

        public static Optional<AuthPrincipalType> get(String authPrincipalName) {
            return Arrays.stream(AuthPrincipalType.values())
                    .filter(authPrincipalType -> authPrincipalType.authPrincipalName.equals(authPrincipalName))
                    .findFirst();
        }
    };


    /** NoSQL service type.
     * <ul>
     * <li>cloud - Cloud service</li>
     * <li>onprem - On-premise proxy and Oracle NoSQL Database instance</li>
     * <li>cloudsim - Cloud simulator (for development)</li>
     * </ul>
     **/
    public enum ServiceType {
        CLOUD("cloud"),
        ON_PREMISE("onpremise"),
        CLOUD_SIMULATOR("cloudsim");

        private final String serviceName;

        ServiceType(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getServiceName() {
            return this.serviceName;
        }

        public static Optional<ServiceType> get(String serviceName) {
            return Arrays.stream(ServiceType.values())
                    .filter(service -> service.serviceName.equals(serviceName))
                    .findFirst();
        }
    };

    private ServiceType service;

    /** NoSQL endpoint e.g.
     * <ul>
     * <li>eu-frankfurt-1 (for cloud))</li>
     * <li>http://localhost:8080 (for onprem))</li>
     * <li>http://localhost:8080 (for cloudsim))</li>
     * </ul>
     **/
    private String endPoint;

    private AuthPrincipalType authPrincipalType;

    /* required for Instance/Resource Principal auth */
    private String compartment = null; // an OCID

    /**
     * PUBLIC:
     * Default constructor.
     */
    public OracleNoSQLJCAConnectionSpec() {
        this.service = ServiceType.CLOUD_SIMULATOR;
    }

    public ServiceType getService() {
        return service;
    }

    public void setService(ServiceType service) {
        this.service = service;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public AuthPrincipalType getAuthPrincipalType() {
        return authPrincipalType;
    }

    public void setAuthPrincipalType(AuthPrincipalType authPrincipalType) {
        this.authPrincipalType = authPrincipalType;
    }

    public String getCompartment() {
        return compartment;
    }

    public void setCompartment(String compartment) {
        this.compartment = compartment;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + this.service + ", " + this.endPoint + ")";
    }
}
