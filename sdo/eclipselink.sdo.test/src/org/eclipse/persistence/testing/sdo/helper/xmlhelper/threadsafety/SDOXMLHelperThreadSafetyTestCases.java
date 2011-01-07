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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.threadsafety;

import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.delegates.SDOXMLHelperDelegator;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDOXMLHelperThreadSafetyTestCases extends SDOTestCase {

	public SDOXMLHelperThreadSafetyTestCases(String name) {
        super(name);
    }

	/*
	 * If a single thread calls getXmlMarshaller() multiple times, it should
	 * be returned the same XMLMarshaller object every time.
	 */
	public void testMarshallerSingleThreadedAccess() {
		SDOXMLHelperDelegator xmlHelper = (SDOXMLHelperDelegator) SDOXMLHelper.INSTANCE;
		
		XMLMarshaller marshaller1 = xmlHelper.getXmlMarshaller(); 
		XMLMarshaller marshaller2 = xmlHelper.getXmlMarshaller(); 

		assertSame(marshaller1, marshaller2);
	}

	/*
	 * If a single thread calls getXmlUnarshaller() multiple times, it should
	 * be returned the same XMLUnmarshaller object every time.
	 */
	public void testUnmarshallerSingleThreadedAccess() {
		SDOXMLHelperDelegator xmlHelper = (SDOXMLHelperDelegator) SDOXMLHelper.INSTANCE;
		
		XMLUnmarshaller unmarshaller1 = xmlHelper.getXmlUnmarshaller(); 
		XMLUnmarshaller unmarshaller2 = xmlHelper.getXmlUnmarshaller(); 

		assertSame(unmarshaller1, unmarshaller2);
	}

	/*
	 * If multiple threads call getXmlMarshaller(), they should each
	 * be returned a unique XMLMarshaller object.
	 */
	public void testMarshallerMultiThreadedAccess() {
		GetMarshallersRunnable thread1 = new GetMarshallersRunnable();
		GetMarshallersRunnable thread2 = new GetMarshallersRunnable();
		
		try {
			thread1.start();
			thread1.join();
			
			thread2.start();
			thread2.join();
		} catch (Exception e) {
			e.printStackTrace();
		}

		XMLMarshaller marshaller1 = thread1.marshaller; 
		XMLMarshaller marshaller2 = thread2.marshaller; 


		assertNotSame(marshaller1, marshaller2);
	}

	/*
	 * If multiple threads call getXmlUnmarshaller(), they should each
	 * be returned a unique XMLUnmarshaller object.
	 */
	public void testUnmarshallerMultiThreadedAccess() {
		GetMarshallersRunnable thread1 = new GetMarshallersRunnable();
		GetMarshallersRunnable thread2 = new GetMarshallersRunnable();
		
		try {
			thread1.start();
			thread1.join();
			
			thread2.start();
			thread2.join();
		} catch (Exception e) {
			e.printStackTrace();
		}

		XMLUnmarshaller unmarshaller1 = thread1.unmarshaller; 
		XMLUnmarshaller unmarshaller2 = thread2.unmarshaller; 

		assertNotSame(unmarshaller1, unmarshaller2);
	}

	//=========================================================================
	
	public class GetMarshallersRunnable extends Thread {
		public XMLMarshaller marshaller;
		public XMLUnmarshaller unmarshaller;		
		
		public void run() {
			System.out.println(Thread.currentThread());
			SDOXMLHelperDelegator xmlHelper = (SDOXMLHelperDelegator) SDOXMLHelper.INSTANCE;
			marshaller = xmlHelper.getXmlMarshaller();
			unmarshaller = xmlHelper.getXmlUnmarshaller();
		}
	}	

}
