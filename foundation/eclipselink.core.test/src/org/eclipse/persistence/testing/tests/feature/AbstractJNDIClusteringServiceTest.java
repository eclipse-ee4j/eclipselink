/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.feature;

import javax.naming.Context;
import java.util.Properties;
import java.net.MulticastSocket;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.sessions.remote.RemoteConnection;
import org.eclipse.persistence.sessions.remote.AbstractJNDIClusteringService;

/**
 * Test the AbstractJNDIClusteringServiceTest setUserName() method
 * It used to set the username to the password when you called setUserName()
 *
 * CR# 4410, BUG# 2612665
 * 
 * @author Guy Pelletier
 */
public class AbstractJNDIClusteringServiceTest extends TestCase {
  FakeClusteringService m_cs;
  
  public AbstractJNDIClusteringServiceTest() {
    setDescription("Test the setUserName for AbstractJNDIClusteringService");
  }

  public void reset() {}

  protected void setup() throws Exception { }

  public void test () {
    m_cs = new FakeClusteringService(getSession());

    Properties props = new Properties();
    props.put(Context.SECURITY_PRINCIPAL, "");    // username
    props.put(Context.SECURITY_CREDENTIALS, "");  // password
    m_cs.setInitialContextProperties(props);

    m_cs.setPassword("password");    
    m_cs.setUserName("user");
  }

  protected void verify() {
    Properties props = (Properties) m_cs.getInitialContextProperties();
    String principal = (String) props.get(Context.SECURITY_PRINCIPAL);
    
    if (!principal.equals("user")) {
      throw new TestErrorException("The AbstractJNDIClusteringServiceTestd failed.  The userName was not set to: user, instead it was set to: " + principal);
    }
  }
}

class FakeClusteringService extends AbstractJNDIClusteringService {
  public FakeClusteringService(Session session) {
    super(session);
  }

  public RemoteConnection getLocalRemoteConnection() {
    return null;
  }

  public Object getDispatcher() throws java.rmi.RemoteException {
    return null;
  }

  public MulticastSocket getCommunicationSocket() {
    return null;
  }

  public RemoteConnection createRemoteConnection(String sessionId, String jndiHostURL) {
    return null;
  }

	public void registerDispatcher() {

  }
	public void deregisterDispatcher() {
		//BUG 2700381: deregister from JNDI 

  }
}
