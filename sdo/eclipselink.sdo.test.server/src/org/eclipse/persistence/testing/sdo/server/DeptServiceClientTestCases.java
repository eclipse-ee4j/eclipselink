/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     etang - April 12/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.sdo.server;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;

import java.io.FileInputStream;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.persistence.sdo.helper.SDOXSDHelper;

import junit.framework.TestCase;

public class DeptServiceClientTestCases extends TestCase {
    public String initialContextFactory;
    public String securityPrincipal;
    public String securityCredentials;
    public String providerUrl;
    public String sessionBean;
    
    
    public DeptServiceClientTestCases() {
        initialContextFactory = System.getProperty("initialCtxFactory");
        securityPrincipal = System.getProperty("securityPrincipal");
        securityCredentials = System.getProperty("securityCredentials");
        providerUrl = System.getProperty("providerUrl");
        sessionBean = System.getProperty("sessionBean");
    }
    
    public void testDepartmentService() {
        DeptService svc = null;
        try {
            Context ctx = getInitialContext();
            svc = (DeptService) ctx.lookup(sessionBean);
        } catch (NamingException e) {
            fail("DeptService lookup failed: " + e);
        }
        defineXSD();
        Dept dept = svc.getDept(30);
        if (dept == null) {
            fail("Department [30] was not returned by the service.");
        }                
        dept.setDeptno(dept.getDeptno() + 1);
        dept.setDname(dept.getDname() + "'");
        dept.setLoc(dept.getLoc() + "'");
        assertTrue("Department [30] update was unsuccessful.", svc.updateDept(dept));
    }

    private Context getInitialContext() {
        Hashtable<String, String> env = new Hashtable<String, String>();
        try {
            env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
            env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
            env.put(Context.SECURITY_CREDENTIALS, securityCredentials);
            env.put(Context.PROVIDER_URL, providerUrl);
            env.put(Context.SECURITY_AUTHENTICATION, "none");
            return new InitialContext(env);
        } catch (Exception x) {
            fail("An exception occurred while attempting to get the InitialContext with settings [" + 
                    Context.INITIAL_CONTEXT_FACTORY + ":" +  initialContextFactory + ", " +
                    Context.SECURITY_PRINCIPAL + ":" +  securityPrincipal + ", " +
                    Context.SECURITY_CREDENTIALS + ":" +  securityCredentials + ", " +
                    Context.PROVIDER_URL + ":" +  providerUrl +
                    "]: " + x);
        }
        return null;
    }

    private void defineXSD() {
        try {
            FileInputStream is = new FileInputStream(getSchemaName());
            HelperContext ctx = HelperProvider.getDefaultContext();
            SDOXSDHelper helper = (SDOXSDHelper) ctx.getXSDHelper();  
            helper.define(is, null);
        } catch (Exception ioe) {
            fail("An exception occurred while attempting to define schema [" + getSchemaName() + "]: " + ioe);
        }
    }
    
    protected String getSchemaName() {
        return "org/eclipse/persistence/testing/sdo/server/Dept.xsd";
    }
}