/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.model.type;

import commonj.sdo.DataObject;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.helper.extension.SDOUtil;
import org.eclipse.persistence.sdo.SDOConstants;

import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class DefaultPackageFromTypeGenerationTestCases extends SDOTestCase {
	    public DefaultPackageFromTypeGenerationTestCases(String name) {
	        super(name);
	    }

	    private String getPackageName(String inputString) {
	    	String packageName = SDOUtil.getPackageNameFromURI(inputString);	    	
	    	return packageName;
	    }
	    
	    public void testPackageNameFromURI_001() {
	    	assertEquals(SDOConstants.JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME,// Expected package name
	    			getPackageName(null)); // Input target namespace
	    }
	    
	    public void testPackageNameFromURI_002() {
	    	assertEquals(SDOConstants.JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME,//
	    			getPackageName(SDOConstants.EMPTY_STRING));
	    }
	    
	    public void testPackageNameFromURI_003() {
	    	// Essentially empty string
	    	assertEquals(SDOConstants.JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME,//
	    			getPackageName("/"));
	    }
	    
	    public void testPackageNameFromURI_004() {
	    	assertEquals(SDOConstants.JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME,//
	    			getPackageName("http://"));
	    }
	    
	    public void testPackageNameFromURI_005() {
	    	assertEquals("com.topleveldomain.layer.app.file",//
	    			getPackageName("http://app.layer.topleveldomain.com/file.xsd"));
	    }
	    
	    public void testPackageNameFromURI_006() {
	    	// html - 4 char
	    	assertEquals("com.topleveldomain.layer.app.file",//
	    			getPackageName("http://app.layer.topleveldomain.com/file.html"));
	    }
	    
	    public void testPackageNameFromURI_007() {
	    	// html - 3 char
	    	assertEquals("com.topleveldomain.layer.app.file",//
	    			getPackageName("http://app.layer.topleveldomain.com/file.htm"));
	    }
	    
	    public void testPackageNameFromURI_008() {
	    	assertEquals("com.topleveldomain.layer.app.file_xsd_action_xsd",//
	    			getPackageName("http://app.layer.topleveldomain.com/file.xsd?action=xsd"));
	    }
	    
	    public void testPackageNameFromURI_009() {
	    	assertEquals("com.topleveldomain1.layer.app.file_xsd_action_xsd_param2_2nd",//
	    			getPackageName("http://app.layer.topleveldomain1.com/file.xsd?action=xsd&param2=2nd"));
	    }
	    
	    public void testPackageNameFromURI_010() {
	    	// urn
	    	assertEquals("com.topleveldomain2.layer.app.file_xsd_action_xsd_param2_2nd",//
	    			getPackageName("urn://app.layer.topleveldomain2.com/file.xsd?action=xsd&param2=2nd"));
	    }
	    
	    public void testPackageNameFromURI_011() {
	    	assertEquals("com.topleveldomain3.layer.app.file_xsd_action_xsd_param2_2nd",//
	    			getPackageName("://app.layer.topleveldomain3.com/file.xsd?action=xsd&param2=2nd"));
	    }
	    
	    public void testPackageNameFromURI_012() {
	    	assertEquals("com.topleveldomain4.layer.app.file_xsd_action_xsd_param2_2nd",//
	    			getPackageName("//app.layer.topleveldomain4.com/file.xsd?action=xsd&param2=2nd"));
	    }
	    
	    public void testPackageNameFromURI_013() {
	    	assertEquals("com.topleveldomain5.layer.app.file_xsd_action_xsd_param2_2nd",//
	    			getPackageName("app.layer.topleveldomain5.com/file.xsd?action=xsd&param2=2nd"));
	    }
	    
	    public void testPackageNameFromURI_014() {
	    	assertEquals("_",//
	    			getPackageName(" "));
	    }
	    
	    public void testPackageNameFromURI_015() {
	    	assertEquals("_", //
	    			getPackageName("&"));
	    }
	    
	    public void testPackageNameFromURI_016() {
	    	// Single slash
	    	assertEquals("eclipse.apps.fnd.security.model.applicationmodule.common.types",//
	    			getPackageName("/eclipse/apps/fnd/security/model/applicationModule/common/types/"));
	    }
	    
	    public void testPackageNameFromURI_017() {
	    	// Verify that the hostname is not truncated (treated as a disposable file ext as defined in the JAXB 2.0 spec D.5.1 step 2
	    	assertEquals("org.eclipse",//
	    			getPackageName("http://eclipse.org/"));
	    }
	    
	    public void testPackageNameFromURI_018() {
	    	// Test ports and dynamic urls - the 1.233 will be treated as a file ext - we dont need the uri to actually resolve
	    	assertEquals("_233.org_action_welcome_value_1.eclipse",//	    			
	    			getPackageName("http://eclipse.org?action=welcome&value=1.233"));
	    }
	    
	    public void testPackageNameFromURI_019() {
	    	// Test query strings
	    	assertEquals("org_.eclipse",//	    			
	    			getPackageName("http://eclipse.org?"));
	    }
	    
	    public void testPackageNameFromURI_020() {
	    	// Test invalid URI's but ones that nevertheless require a package name
   	    	assertEquals("ftp.eclipse",//
	    			getPackageName("ftp://eclipse.org"));
	    }
	    
	    public void testPackageNameFromURI_021() {
   	    	assertEquals("mailto.eclipse",//
	    			getPackageName("mailto://eclipse.org"));
	    }
	    
	    public void testPackageNameFromURI_022() {
   	    	assertEquals("news.eclipse",//
	    			getPackageName("news://eclipse.org"));
	    }
	    
	    public void testPackageNameFromURI_023() {
   	    	// Test escape sequences and java reserved words _00
   	    	assertEquals("gopher.eclipse_org._00.weather.north_america",//
	    			getPackageName("gopher://eclipse.org/00/Weather/North%20America.htm"));
	    }
	    
	    public void testPackageNameFromURI_024() {
   	    	assertEquals("gopher.class_package_int_super_com._00.weather.north_america",//
	    			getPackageName("gopher://class.package.int.super.com/00/Weather/North%20America.htm"));
	    }
	    
	    public void testPackageNameFromURI_025() {
   	    	// Test reserved package names in JLS 7.7 have an underscore appended - but we are allowing them here
   	    	assertEquals("java.util.arraylist",//
	    			getPackageName("http://ArrayList.util.java"));
	    }
	    
	    public void testPackageNameFromURI_026() {
   	    	assertEquals("else.float.abstract.class",//
	    			getPackageName("http://class.abstract.float.else"));   	    	
	    }
	    
	    public void testPackageNameFromURI_027() {
   	    	assertEquals("veronica.eclipse",//
	    			getPackageName("veronica://eclipse.org"));
	    }
	    
	    public void testPackageNameFromURI_028() {
   	    	assertEquals("archie.eclipse",//
	    			getPackageName("archie://eclipse.org"));
	    }
	    
	    public void testPackageNameFromURI_029() {
   	    	assertEquals("telnet.eclipse_org",//
	    			getPackageName("telnet://eclipse.org/"));
	    }
	    
	    public void testPackageNameFromURI_030() {
	    	// trailing slash will retain "org"
   	    	assertEquals("org.eclipse",//
	    			getPackageName("http://eclipse.org"));
	    }
	    
	    public void testPackageNameFromURI_031() {
	    	// test item 6 in the spec "www" must be dropped but not com
	    	assertEquals("com.topleveldomain.layer.app",//
	    			getPackageName("http://www.app.layer.topleveldomain.com"));
	    }
	    
	    public void testPackageNameFromURI_032() {
	    	// should be oracle.com
	    	assertEquals("org.eclipse",//
	    			getPackageName("http://www.eclipse.org"));
	    }
	    
	    public void testPackageNameFromURI_033() {
	    	// ftp
	    	assertEquals("ftp.app_layer_topleveldomain_com.file",//
	    			getPackageName("ftp://app.layer.topleveldomain.com/file.htm"));
	    }
	    
	    public void testPackageNameFromURI_034() {
	    	assertEquals("file.app_layer_topleveldomain_com.file_xsd_action_xsd_param2_2nd",//
	    			getPackageName("file://app.layer.topleveldomain.com/file.xsd?action=xsd&param2=2nd"));
	    }
	    
	    public void testPackageNameFromURI_035() {
	    	// Test metacharacter package separator handling
	    	assertEquals(SDOConstants.JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME,// was _
	    			getPackageName("."));
	    }
	    
	    public void testPackageNameFromURI_036() {
	    	// Test metacharacter package separator handling	    	
	    	assertEquals(SDOConstants.JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME,//
	    			getPackageName("/."));
	    }
	    
	    public void testPackageNameFromURI_037() {
	    	// Test metacharacter package separator handling
	    	assertEquals(SDOConstants.JAVA_TYPEGENERATION_DEFAULT_PACKAGE_NAME,//
	    			getPackageName(".."));
	    }
      public void testPackageNameFromUriWithDotsButNoScheme() {	    	
        assertEquals("uri2.my", getPackageName("my.uri2"));

	    	assertEquals("uri.my", getPackageName("my.uri"));    
        
        assertEquals("uri.my.test.file", getPackageName("my.uri/test/file.xsd"));            
	    }
      
      public void testPackageNameFromUriWithDotsAndScheme() {	    	
        assertEquals("uri2.my", getPackageName("http://my.uri2"));

	    	assertEquals("uri.my", getPackageName("http://my.uri"));            
        
        assertEquals("uri.my.file", getPackageName("http://my.uri/file.xsd"));            
	    }
      
      public void testPackageNameFromUriWithDotsAndInvalidScheme() {	    	       
	    	assertEquals("abc.my", getPackageName("abc://my.uri"));              
	    }
 
	public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.type.DefaultPackageFromTypeGenerationTestCases" };
        TestRunner.main(arguments);        
	}

}
