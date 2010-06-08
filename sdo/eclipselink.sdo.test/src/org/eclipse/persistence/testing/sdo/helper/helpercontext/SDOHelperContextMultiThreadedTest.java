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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
/*
   DESCRIPTION
    Test multi HelperContext scenarios in a single JVM, single classloader, multi-threaded client
    Based on TestConcurrency.java by Tim Gleason.
 */

package org.eclipse.persistence.testing.sdo.helper.helpercontext;

import java.io.FileInputStream;
import java.util.List;

import junit.textui.TestRunner;

import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.impl.HelperProvider;


import java.io.FileReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SDOHelperContextMultiThreadedTest extends SDOHelperContextTestCases {

    //public static final String XML_PATH = "org/eclipse/persistence/testing/sdo/model/sequence/CompanyWithSequenceExt.xml";
    public static final String XSD_PATH = "org/eclipse/persistence/testing/sdo/schemas/CustomerDataSDOHelperContext.xsd";
    public static final int ITERATIONS = 50;
    public static final int THREADS = 20;
    public static final int TIMEOUT = 4000;
    
	
	private static HelperContext aStaticHelperContext2 = new SDOHelperContext();
	//private static HelperContext aStaticHelperContext2 = HelperProvider.getDefaultContext();	
	private static List<Type> types = null;
	private static boolean errors = false;

	static {
		try {
			types = aStaticHelperContext2.getXSDHelper().define(getXSDString2(XSD_PATH));
		} catch (Exception e) {
			
		}
	}
 	
    static int count = 0;

    static String testDoc = "<customerDataSDO xmlns=\"http://www.example.com/\">\n" +
    "    <FirstName>John</FirstName>\n" +
    "    <LastName>Smith</LastName>\n" +
    "    <Email>jsmith@yahoo.com</Email>\n" +
    "    <MobilePhoneNumber>123-456-7890</MobilePhoneNumber>\n" +
    "    <MembershipTypeCode>Gold</MembershipTypeCode>\n" +
    "</customerDataSDO>";
    
    public SDOHelperContextMultiThreadedTest(String name) {
        super(name);//, (HelperContext)SDOHelperContext.getInstance());
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.helpercontext.SDOHelperContextMultiThreadedTest" };
        //TestRunner.main(arguments);        
    }

   public void testMulitThreadedLoad() {
    	try {
            int numThreads = THREADS;
            CountDownLatch cdl = new CountDownLatch(numThreads);
            for (int i=0; i<numThreads; i++) {
                new Thread(new TestRunner(cdl)).start();
            }

            cdl.await(TIMEOUT, TimeUnit.SECONDS);

            log("SDOHelperContextMultiThreaded Expected: " + (numThreads * ITERATIONS) + " threads, got: " + count + " (less " + ((numThreads * ITERATIONS) - count) + ")");
        	} catch (Exception e) {
        		assertFalse(true);
        		e.printStackTrace();
        	}
        assertFalse(errors);
    }
    
    public void setUp()  {
        super.setUp();
    	count = 0;
    }
 
    public static String getXSDString2(String filename) {
        try {
            FileInputStream inStream = new FileInputStream(filename);
            byte[] bytes = new byte[inStream.available()];
            inStream.read(bytes);
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
static class TestRunner implements Runnable {
    CountDownLatch cdl;

    public TestRunner(CountDownLatch cdl) {
        this.cdl = cdl;
    }

    public void run() {
        for (int i=0; i< ITERATIONS; i++) {
            DataObject anObject = null;
            try {
                XMLDocument doc = aStaticHelperContext2.getXMLHelper().load(testDoc);
                anObject = doc.getRootObject();
                // the following line tests SDOTypeHelperDelegate.initOpenContentProps() synchronization block
                anObject.set("myOpen", "ocValue");
                // we used to collide on a shared XMLUnMarshaller instance
                count++;
            } catch (Exception e) {
            	errors = true;
                e.printStackTrace();
            }
        }
        cdl.countDown();
    }
}
}
