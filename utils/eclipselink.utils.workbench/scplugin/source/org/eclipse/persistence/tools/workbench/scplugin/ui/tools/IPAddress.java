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
package org.eclipse.persistence.tools.workbench.scplugin.ui.tools;

// JDK
import java.text.ParseException;
import java.util.StringTokenizer;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * An <code>IPAddress</code> is an identifying number that enables any computer
 * on the Internet to find any other computer on the network. It consists of four
 * sets of numbers separated by periods  for example, 255.255.255.255.
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
public final class IPAddress
{
	/**
	 * The IP address, four sets of numbers separated by periods.
	 */
	private final String ipAddress;

	/**
	 * The IP address stored in an array of integers.
	 */
	private final int[] ipAddresses;

	/**
	 * Creates a new <code>IPAddress</code> object with the default value
	 * "0.0.0.0".
	 */
	public IPAddress()
	{
		super();

		this.ipAddress = "0.0.0.0";
		this.ipAddresses = new int[4];
	}

	/**
	 * Creates a new <code>IPAddress</code> with the given array of addresses.
	 *
	 * @param ipAddress The IP address to be contained by this object
	 * @exception NullPointerException The IP address cannot be <code>null</code>
	 * @throws ParseException The IP address is not valid, the length has to be
	 * 4 with values between 0 and 255
	 */
	public IPAddress(int[] ipAddresses) throws ParseException
	{
		super();

		if (ipAddresses == null)
		{
			this.ipAddress = "0.0.0.0";
			this.ipAddresses = new int[4];
		}
		else
		{
			this.ipAddress = convert(ipAddresses);
			this.ipAddresses = ipAddresses;
		}
	}

	/**
	 * Creates a new <code>IPAddress</code> object with the given string
	 * representation of an IP address.
	 *
	 * @param ipAddress The IP address to be contained by this object,
	 * <code>null</code> can be passed, which is represented by "0.0.0.0"
	 * @throws ParseException The IP address is not a valid
	 */
	public IPAddress(String ipAddress) throws ParseException
	{
		super();

		if (ipAddress == null)
		{
			this.ipAddress = "0.0.0.0";
			this.ipAddresses = new int[4];
		}
		else
		{
			this.ipAddress = ipAddress;
			this.ipAddresses = convert(ipAddress);
		}
	}

	/**
	 * Converts the given array of 4 integers values into a string.
	 *
	 * @param ipAddress The IP address to be converted into a string
	 * @return String The string representatio of an IP address
	 * @exception IPAddressException The IP address is not valid, then cannot be
	 * converted into an array of integers
	 * @throws IPAddressException The IP address is not valid, then cannot be
	 * converted into an array of integers
	 */
	public static String convert(int[] ipAddresses) throws ParseException
	{
		if (ipAddresses == null)
			throw new ParseException("The array of integers cannot be null", 0);

		if (ipAddresses.length != 4)
			throw new ParseException("The array does not have a length of 4", 0);

		if (!isValid(ipAddresses))
			throw new ParseException("The values are not in the range [0, 255]", 0);

		StringBuffer sb = new StringBuffer(15);

		sb.append(ipAddresses[0]);
		sb.append(".");
		sb.append(ipAddresses[1]);
		sb.append(".");
		sb.append(ipAddresses[2]);
		sb.append(".");
		sb.append(ipAddresses[3]);

		return sb.toString();
	}

	/**
	 * Converts the given string into an array of 4 integer values.
	 *
	 * @param ipAddress The IP address to be converted into an array of integers
	 * @return int[] The array of integers parsed from the given string
	 * @exception IPAddressException The IP address is not valid, then cannot be
	 * converted into an array of integers
	 * @throws ParseException The IP address is not valid, then cannot be
	 * converted into an array of integers
	 */
	public static int[] convert(String ipAddress) throws ParseException
	{
		if (ipAddress == null)
			throw new ParseException("The value cannot be null", 0);

		StringTokenizer tokenizer = new StringTokenizer(ipAddress, ".");

		if (tokenizer.countTokens() != 4)
			throw new ParseException("The IP address is not valid, it does not have 4 sets of digits", 0);

		int[] ipAddresses = new int[4];

		try
		{
			for (int index = 0; index < 4; index++)
				ipAddresses[index] = new Integer(tokenizer.nextToken()).intValue();
		}
		catch (Throwable throwable)
		{
			throw new ParseException("The IP address is not valid, then cannot be converted into an array of integers", 0);
		}

		if (!isValid(ipAddresses))
			throw new ParseException("The values are not in the range [0, 255]", 0);

		return ipAddresses;
	}

	/**
	 * Determines whether the given IP address is valid.
	 *
	 * @param ipAddresses The addressed to be validated
	 * @return <code>true</code> if the given value follow the IP address
	 * convention; <code>false</code> if the given value is <code>null</code>,
	 * is does not have a length of 4, one of the value is smaller than 0 or
	 * greater than 255
	 */
	public static boolean isValid(int[] ipAddresses)
	{
		if ((ipAddresses == null) ||
			 (ipAddresses.length != 4))
		{
			return false;
		}

		for (int index = ipAddresses.length; --index >= 0;)
		{
			if ((ipAddresses[index] < 0) ||
				 (ipAddresses[index] > 255))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Determines whether the given IP address is valid.
	 *
	 * @param ipAddress The address to be validated
	 * @return <code>true</code> if the given value follow the IP address
	 * convention
	 */
	public static boolean isValid(String ipAddress)
	{
		try
		{
			return isValid(convert(ipAddress));
		}
		catch (ParseException exception)
		{
			return false;
		}
	}

	/**
	 * Returns the IP address stored in this object.
	 *
	 * @return The IP address stored in this object
	 */
	public String ipAddress()
	{
		return ipAddress;
	}

	/**
	 * Returns the IP address stored in this object as an array of integers.
	 *
	 * @return The IP address stored in this object as an array of integers
	 */
	public int[] ipAddresses()
	{
		return ipAddresses;
	}

	/**
	 * Returns a String representation of this <code>IPAddress</code>.
	 *
	 * @return The short description of this class and its values
	 */
	public final String toString()
	{
		StringBuffer sb = new StringBuffer();
		StringTools.buildSimpleToStringOn(this, sb);
		sb.append(" (");
		toString(sb);
		sb.append(')');
		return sb.toString();
	}

	/**
	 * Appends more information about this <code>IPAddress</code> to the given
	 * buffer.
	 *
	 * @param buffer The buffer used to add extra information
	 */
	public void toString(StringBuffer buffer)
	{
		buffer.append("ipAddress=");
		buffer.append(ipAddress);
	}
}
