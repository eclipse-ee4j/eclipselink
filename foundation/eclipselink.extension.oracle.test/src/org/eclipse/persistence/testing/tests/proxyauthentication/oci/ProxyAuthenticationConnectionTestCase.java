/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 * Andrei Ilitchev May 28, 2008. Bug 224964: Provide support for Proxy Authentication through JPA.
 *     Changed the was Proxy Authentication supported in case of thin driver, but support for oci case remains the same.
 *     That caused re-arranging of the tests: before the fix all the tests were directly in proxiauthentication package;
 *     now the old tests (minus thin-specific setup) were moved into the new proxyauthentication.oci package,
 *     and the new tests defined in the new proxyauthentication.thin package.
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.proxyauthentication.oci;

import java.util.Properties;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.sessions.server.*;

import org.eclipse.persistence.testing.framework.*;

// Used as a base class for a set of sets verifying that the proxyUser is actually
// by the writeConnection of ClientSession
public class ProxyAuthenticationConnectionTestCase extends AutoVerifyTestCase {

    Properties proxyProperties;
    SessionEventListener listener;
    ConnectionPolicy connectionPolicy;
    String readConnectionSchema;
    String writeConnectionSchema;

    public ProxyAuthenticationConnectionTestCase(Properties proxyProperties) {
        this.proxyProperties = proxyProperties;
    }

    // Override this method to set proxy properties into some login.
    // As a result of this method either customary connectionPolicy is created:
    // connectionPolicy = myConnectionPolicy;
    // or/and a listener:
    // listener = myListener;

    protected void proxySetup() {
    }

    public void setup() {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("Supports Oracle platform only");
        }
        proxySetup();
        if (listener != null) {
            getSession().getEventManager().addListener(listener);
        }
    }

    public void test() {
        ClientSession cs;
        if (connectionPolicy == null) {
            cs = getServerSession().acquireClientSession();
        } else {
            cs = getServerSession().acquireClientSession(connectionPolicy);
        }

        // The query reads current schema name
        ValueReadQuery query = new ValueReadQuery();
        query.setSQLString("SELECT SYS_CONTEXT ('USERENV', 'CURRENT_SCHEMA') FROM DUAL");

        // Outside transaction read query is executed through read connection
        readConnectionSchema = (String)cs.executeQuery(query);

        cs.beginTransaction();

        // Inside transaction read query is executed through write connection
        writeConnectionSchema = (String)cs.executeQuery(query);

        cs.rollbackTransaction();

        cs.release();
    }

    public void verify() {
        String readConnectionSchemaExpected = readConnectionSchemaExpected();
        if (!readConnectionSchema.equals(readConnectionSchemaExpected)) {
            throw new TestErrorException("Wrong readConnectionSchema " + readConnectionSchema + " was used. " + readConnectionSchemaExpected + " was expected");
        }

        String writeConnectionSchemaExpected = writeConnectionSchemaExpected();
        if (!writeConnectionSchema.equals(writeConnectionSchemaExpected)) {
            throw new TestErrorException("Wrong writeConnectionSchema " + writeConnectionSchema + " was used. " + writeConnectionSchemaExpected + " was expected");
        }
    }

    protected String writeConnectionSchemaExpected() {
        return proxyProperties.getProperty("PROXY_USER_NAME");
    }

    protected String readConnectionSchemaExpected() {
        return getServerSession().getLogin().getUserName();
    }

    public void reset() {
        if (listener != null) {
            getSession().getEventManager().removeListener(listener);
            listener = null;
        }
        connectionPolicy = null;
    }

    protected void addProxyPropertiesToLogin(Login login) {
        ((DatabaseLogin)login).getProperties().putAll(proxyProperties);
    }
}
