package org.eclipse.persistence.testing.framework.server;

import java.rmi.RemoteException;
import java.util.Properties;

/**
 * Remote business interface for TestRunner session bean.
 * 
 * @author mschinca
 */
public interface TestRunner {
    public Throwable runTest(String className, String test, Properties props) throws RemoteException;
}
