package org.eclipse.persistence.testing.sdo.server.wls;

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
    
    
    public DeptServiceClientTestCases() {
        initialContextFactory = System.getProperty("wlsInitialCtxFactory");
        securityPrincipal = System.getProperty("wlsSecurityPrincipal");
        securityCredentials = System.getProperty("wlsSecurityCredentials");
        providerUrl = System.getProperty("wlsProviderUrl");
    }
    
    public void testDepartmentService() {
        DeptService svc = null;
        try {
            Context ctx = getInitialContext();
            svc = (DeptService) ctx.lookup("DeptServiceBean#" + DeptService.class.getName());
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
        return "org/eclipse/persistence/testing/sdo/server/wls/Dept.xsd";
    }
}