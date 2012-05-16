/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.eis.nosql;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.EISLogin;
import org.eclipse.persistence.nosql.adapters.nosql.OracleNoSQLConnectionSpec;
import org.eclipse.persistence.nosql.adapters.nosql.OracleNoSQLPlatform;
import org.eclipse.persistence.eis.interactions.XMLInteraction;
import org.eclipse.persistence.eis.mappings.EISDirectMapping;
import org.eclipse.persistence.internal.nosql.adapters.nosql.OracleNoSQLOperation;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.models.order.Order;

/**
 * Test EclipseLink EIS with the Oracle NoSQL database.
 */
public class NoSQLTestModel extends TestModel {
    protected Session oldSession;

    public NoSQLTestModel() {
        super();
        setDescription("Test EclipseLink EIS with the Oracle NoSQL database.");
    }

    public void addTests() {

        addTest(new ConnectTest());
        addTest(new NativeTest());
        addTest(new ReadWriteTest());
        addTest(new ReadUOWTest());
    }

    public void setup() {
        this.oldSession = getSession();
        DatabaseSession session = XMLProjectReader.read("org/eclipse/persistence/testing/models/order/eis/nosql/order-project.xml", getClass().getClassLoader()).createDatabaseSession();
        addToOrderDescriptor(session.getDescriptor(Order.class));
        session.setSessionLog(getSession().getSessionLog());

        EISLogin eisLogin = (EISLogin)session.getDatasourceLogin();
        eisLogin.setConnectionSpec(new OracleNoSQLConnectionSpec());
        eisLogin.setProperty(OracleNoSQLConnectionSpec.STORE, "kvstore");
        eisLogin.setProperty(OracleNoSQLConnectionSpec.HOST, "localhost:5000");
        session.login();

        getExecutor().setSession(session);
    }
    
    public static void addToOrderDescriptor(ClassDescriptor descriptor) {
        descriptor.getPrimaryKeyFields().clear();
        descriptor.addPrimaryKeyField(new XMLField("@id"));
        ((EISDirectMapping)descriptor.getMappingForAttributeName("id")).setFieldName("@id");
        
        // Insert
        XMLInteraction insertCall = new XMLInteraction();
        insertCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.PUT);
        descriptor.getQueryManager().setInsertCall(insertCall);
        
        // Update
        XMLInteraction updateCall = new XMLInteraction();
        updateCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.PUT);
        descriptor.getQueryManager().setUpdateCall(updateCall);

        // Read
        XMLInteraction readCall = new XMLInteraction();
        readCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.GET);
        readCall.addArgument("@id");
        descriptor.getQueryManager().setReadObjectCall(readCall);
        
        // Delete
        XMLInteraction deleteCall = new XMLInteraction();
        deleteCall.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.DELETE);
        deleteCall.addArgument("@id");
        descriptor.getQueryManager().setDeleteCall(deleteCall);
    }

    public void reset() {
        getDatabaseSession().logout();
        getExecutor().setSession(this.oldSession);
    }

    /**
     * Return the JUnit suite to allow JUnit runner to find it.
     */
    public static junit.framework.TestSuite suite() {
        return new NoSQLTestModel();
    }
    
}
