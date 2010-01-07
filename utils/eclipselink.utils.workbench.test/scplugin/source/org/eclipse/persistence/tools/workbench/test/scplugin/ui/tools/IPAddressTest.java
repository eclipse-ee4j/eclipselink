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
package org.eclipse.persistence.tools.workbench.test.scplugin.ui.tools;

import org.eclipse.persistence.tools.workbench.scplugin.ui.tools.IPAddress;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @version 10.0.3
 * @author Pascal Filion
 */
public class IPAddressTest extends TestCase
{
	public IPAddressTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		return new TestSuite(IPAddressTest.class, "IPAddress Test");
	}

	public void testConstructor1()
	{
		try
		{
			IPAddress ipAddress = new IPAddress("138.2.91.78");

			assertEquals("138.2.91.78", ipAddress.ipAddress());

			assertEquals(ipAddress.ipAddresses()[0], 138);
			assertEquals(ipAddress.ipAddresses()[1],   2);
			assertEquals(ipAddress.ipAddresses()[2],  91);
			assertEquals(ipAddress.ipAddresses()[3],  78);
		}
		catch (Exception e)
		{
			fail("138.2.91.78 is valid");
		}
	}

	public void testConstructor2()
	{
		try
		{
			IPAddress ipAddress = new IPAddress(new int[] { 138, 2, 91, 78 });

			assertEquals("138.2.91.78", ipAddress.ipAddress());

			assertEquals(ipAddress.ipAddresses()[0], 138);
			assertEquals(ipAddress.ipAddresses()[1],   2);
			assertEquals(ipAddress.ipAddresses()[2],  91);
			assertEquals(ipAddress.ipAddresses()[3],  78);
		}
		catch (Exception e)
		{
			fail("138.2.91.78 is valid");
		}
	}

	public void testConstructor3()
	{
		try
		{
			new IPAddress(new int[] { -1, 2, 91, 78 });
			fail("-1.2.91.78 is invalid");
		}
		catch (Exception e)
		{
			// Test pass
		}
	}

	public void testConstructor4()
	{
		try
		{
			new IPAddress("138.2.91.-1");
			fail("138.2.91.-1 is invalid");
		}
		catch (Exception e)
		{
			// Test pass
		}
	}

	public void testConstructor5() throws Exception
	{
		try
		{
			IPAddress ipAddress = new IPAddress((String) null);

			assertEquals(ipAddress.ipAddress(), "0.0.0.0");

			int[] ipAddresses = ipAddress.ipAddresses();
			assertEquals(ipAddresses.length, 4);
			assertEquals(ipAddresses[0], 0);
			assertEquals(ipAddresses[1], 0);
			assertEquals(ipAddresses[2], 0);
			assertEquals(ipAddresses[3], 0);
		}
		catch (Exception e)
		{
			// Test passs
		}
	}

	public void testConstructor6() throws Exception
	{
		try
		{
			IPAddress ipAddress = new IPAddress((int[]) null);

			assertEquals(ipAddress.ipAddress(), "0.0.0.0");

			int[] ipAddresses = ipAddress.ipAddresses();
			assertEquals(ipAddresses.length, 4);
			assertEquals(ipAddresses[0], 0);
			assertEquals(ipAddresses[1], 0);
			assertEquals(ipAddresses[2], 0);
			assertEquals(ipAddresses[3], 0);
		}
		catch (Exception e)
		{
			// Test passs
		}
	}

	public void testConvertIntArray1() throws Exception
	{
		try
		{
			String ipAddress = IPAddress.convert(new int [] { 0, 0, 0, 0 });
			assertEquals("0.0.0.0", ipAddress);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testConvertIntArray10() throws Exception
	{
		try
		{
			IPAddress.convert(new int [] { 0, 0, 0, -1 });
			fail("0.0.0.-1 is invalid");
		}
		catch (Exception e)
		{
			// Test pass
		}
	}

	public void testConvertIntArray2() throws Exception
	{
		try
		{
			String ipAddress = IPAddress.convert(new int [] { 255, 255, 255, 255 });
			assertEquals("255.255.255.255", ipAddress);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	public void testConvertIntArray3() throws Exception
	{
		try
		{
			IPAddress.convert(new int [] { 256, 0, 0, 0 });
			fail("256.0.0.0 is invalid");
		}
		catch (Exception e)
		{
			// Test pass
		}
	}

	public void testConvertIntArray4() throws Exception
	{
		try
		{
			IPAddress.convert(new int [] { 0, 256, 0, 0 });
			fail("0.256.0.0 is invalid");
		}
		catch (Exception e)
		{
			// Test pass
		}
	}

	public void testConvertIntArray5() throws Exception
	{
		try
		{
			IPAddress.convert(new int [] { 0, 0, 256, 0 });
			fail("0.0.256.0 is invalid");
		}
		catch (Exception e)
		{
			// Test pass
		}
	}

	public void testConvertIntArray6() throws Exception
	{
		try
		{
			IPAddress.convert(new int [] { 0, 0, 0, 256 });
			fail("0.0.0.256 is invalid");
		}
		catch (Exception e)
		{
			// Test pass
		}
	}

	public void testConvertIntArray7() throws Exception
	{
		try
		{
			IPAddress.convert(new int [] { -1, 0, 0, 0 });
			fail("-1.0.0.0 is invalid");
		}
		catch (Exception e)
		{
			// Test pass
		}
	}

	public void testConvertIntArray8() throws Exception
	{
		try
		{
			IPAddress.convert(new int [] { 0, -1, 0, 0 });
			fail("0.-1.0.0 is invalid");
		}
		catch (Exception e)
		{
			// Test pass
		}
	}

	public void testConvertIntArray9() throws Exception
	{
		try
		{
			IPAddress.convert(new int [] { 0, 0, -1, 0 });
			fail("0.0.-1.0 is invalid");
		}
		catch (Exception e)
		{
			// Test pass
		}
	}

	public void testConvertString1() throws Exception
	{
		int[] ipAddresses = IPAddress.convert("0.0.0.0");

		assertEquals(ipAddresses.length, 4);

		assertEquals(ipAddresses[0], 0);
		assertEquals(ipAddresses[1], 0);
		assertEquals(ipAddresses[2], 0);
		assertEquals(ipAddresses[3], 0);
	}

	public void testConvertString2() throws Exception
	{
		int[] ipAddresses = IPAddress.convert("255.255.255.255");

		assertEquals(ipAddresses.length, 4);

		assertEquals(ipAddresses[0], 255);
		assertEquals(ipAddresses[1], 255);
		assertEquals(ipAddresses[2], 255);
		assertEquals(ipAddresses[3], 255);
	}

	public void testConvertString3() throws Exception
	{
		int[] ipAddresses = IPAddress.convert("138.2.91.78");

		assertEquals(ipAddresses.length, 4);

		assertEquals(ipAddresses[0], 138);
		assertEquals(ipAddresses[1],   2);
		assertEquals(ipAddresses[2],  91);
		assertEquals(ipAddresses[3],  78);
	}

	public void testConvertString4() throws Exception
	{
		try
		{
			IPAddress.convert("-1.2.91.78");
			fail("-1.2.91.78 is invalid");
		}
		catch (Exception e)
		{
			// Test passs
		}
	}

	public void testConvertString5() throws Exception
	{
		try
		{
			IPAddress.convert("138.-1.91.78");
			fail("138.-1.91.78 is invalid");
		}
		catch (Exception e)
		{
			// Test passs
		}
	}

	public void testConvertString6() throws Exception
	{
		try
		{
			IPAddress.convert("138.2.-1.78");
			fail("138.2.-1.78 is invalid");
		}
		catch (Exception e)
		{
			// Test passs
		}
	}

	public void testConvertString7() throws Exception
	{
		try
		{
			IPAddress.convert("138.2.91.-1");
			fail("138.2.91.-1 is invalid");
		}
		catch (Exception e)
		{
			// Test passs
		}
	}
}
