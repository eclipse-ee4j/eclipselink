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

import jakarta.resource.ResourceException;
import jakarta.resource.cci.Connection;
import jakarta.resource.cci.ConnectionFactory;
import jakarta.resource.cci.ConnectionSpec;
import jakarta.resource.cci.RecordFactory;
import jakarta.resource.cci.ResourceAdapterMetaData;
import oracle.nosql.driver.AuthorizationProvider;
import oracle.nosql.driver.NoSQLHandle;
import oracle.nosql.driver.NoSQLHandleConfig;
import oracle.nosql.driver.NoSQLHandleFactory;
import oracle.nosql.driver.iam.SignatureProvider;
import oracle.nosql.driver.kv.StoreAccessTokenProvider;
import oracle.nosql.driver.ops.Request;

import javax.naming.Reference;
import java.io.IOException;

/**
 * Connection factory for Oracle NoSQL SDK JCA adapter.
 *
 * @author Radek Felcman
 * @since EclipseLink 4.0
 */
public class OracleNoSQLConnectionFactory implements ConnectionFactory {

    /**
     * Default constructor.
     */
    public OracleNoSQLConnectionFactory() {
    }

    @Override
    public Connection getConnection() throws ResourceException {
        return getConnection(new OracleNoSQLJCAConnectionSpec());
    }

    @Override
    public Connection getConnection(ConnectionSpec spec) throws ResourceException {
        OracleNoSQLJCAConnectionSpec connectionSpec = (OracleNoSQLJCAConnectionSpec) spec;
        NoSQLHandle noSQLHandle = null;
        try {
            //TODO Extend this from Cloud Simulator into all available stores
            //TODO handle all available properties in NoSQLHandleConfig
            NoSQLHandleConfig config = new NoSQLHandleConfig(connectionSpec.getEndPoint());
            config.setAuthorizationProvider(getAuthorizationProvider(config, connectionSpec));

            noSQLHandle = NoSQLHandleFactory.createNoSQLHandle(config);
        } catch (Exception exception) {
            ResourceException resourceException = new ResourceException(exception.toString(), exception);
            throw resourceException;
        }
        return new OracleNoSQLConnection(noSQLHandle, connectionSpec);
    }

    @Override
    public ResourceAdapterMetaData getMetaData() {
        return new OracleNoSQLAdapterMetaData();
    }

    @Override
    public RecordFactory getRecordFactory() {
        return new OracleNoSQLRecordFactory();
    }

    @Override
    public Reference getReference() {
        return new Reference(getClass().getName());
    }

    @Override
    public void setReference(Reference reference) {
    }

    private AuthorizationProvider getAuthorizationProvider(NoSQLHandleConfig config, OracleNoSQLJCAConnectionSpec connectionSpec) {
        AuthorizationProvider authorizationProvider = null;
        try {
            switch (connectionSpec.getService()) {
                case CLOUD: {
                    switch (connectionSpec.getAuthPrincipalType()) {
                        case USER:
                            authorizationProvider = new SignatureProvider();
                            break;
                        case INSTANCE:
                            if (connectionSpec.getCompartment() == null) {
                                throw new IllegalArgumentException("Compartment is required for Instance/Resource " + "Principal authorization");
                            }
                            authorizationProvider = SignatureProvider.createWithInstancePrincipal();
                            break;
                        case RESOURCE:
                            if (connectionSpec.getCompartment() == null) {
                                throw new IllegalArgumentException("Compartment is required for Instance/Resource " + "Principal authorization");
                            }
                            authorizationProvider = SignatureProvider.createWithResourcePrincipal();
                            break;
                    }
                    break;
                }
                case ON_PREMISE:
                    authorizationProvider = new StoreAccessTokenProvider();
                    break;
                case CLOUD_SIMULATOR:
                    authorizationProvider = new AuthorizationProvider() {
                        @Override
                        public String getAuthorizationString(Request request) {
                            return "Bearer cloudsim";
                        }

                        @Override
                        public void close() {
                        }
                    };
                    break;
            }
        } catch (IOException ioe) {
            System.err.println("Unable to configure authentication: " + ioe);
        }
        return authorizationProvider;
    }
}
