/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.ucp;

import java.sql.SQLException;

import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

import org.eclipse.persistence.descriptors.partitioning.RoundRobinPartitioningPolicy;
import org.eclipse.persistence.platform.database.oracle.ucp.UCPDataPartitioningCallback;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.Server;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkClientSessionTestModel;


/**
 * This model is used to test Oracle UCP.
 * To test a RAC, URL should be a composite like:
 * "@(DESCRIPTION=(LOAD_BALANCE=on) (ADDRESS=(PROTOCOL=TCP)(HOST=host1) (PORT=1521)) (ADDRESS=(PROTOCOL=TCP)(HOST=host2) (PORT=1521)) (CONNECT_DATA=(SERVICE_NAME=service)))"
 * 
 */
public class UCPCallbackUnitOfWorkTests extends UnitOfWorkClientSessionTestModel {

    @Override
    public Server buildServerSession() {
        try {
            PoolDataSourceFactory factory = new oracle.ucp.jdbc.PoolDataSourceFactory();
            PoolDataSource dataSource = factory.getPoolDataSource();
            //dataSource.setONSConfiguration("nodes=adcdbc01-r.us.oracle.com:6200");
            dataSource.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
            dataSource.setURL(getSession().getLogin().getConnectionString());
            dataSource.setUser(getSession().getLogin().getUserName());
            dataSource.setPassword(getSession().getLogin().getPassword());
            dataSource.setInitialPoolSize(5);
            dataSource.setMinPoolSize(5);
            dataSource.setMaxPoolSize(10);
            dataSource.setFastConnectionFailoverEnabled(true);
            Project project = getSession().getProject().clone();
            project.setLogin(project.getLogin().clone());
            project.getLogin().setConnector(new JNDIConnector(dataSource));
            project.getLogin().setUsesExternalConnectionPooling(true);
            project.getLogin().setPartitioningCallback(new UCPDataPartitioningCallback());
            Server server = project.createServerSession();
            server.setPartitioningPolicy(new RoundRobinPartitioningPolicy("node1", "node2"));
            server.setSessionLog(getSession().getSessionLog());
            server.login();
            return server;
        } catch (SQLException error) {
            throw new TestErrorException("UCP failed to initialize.", error);
        }
    }
}
