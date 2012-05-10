/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import javax.swing.JFormattedTextField;

/**
 * This <code>IPAddressFormatter</code> is responsible to convert a string
 * representation of an IP address to its encapsulating object
 * {@link org.eclipse.persistence.tools.workbench.scplugin.ui.tools.IPAddress IPAddress} and vice
 * versa.
 * <p>
 * This formatter is used by a <code>JFormattedTextField</code> in order to
 * contain only a valid IP address.
 * <p>
 * TODO: Add range of IP address that are valid.
 *
 * @version 10.0.3
 * @author Pascal Filion
 */
public class IPAddressFormatter extends JFormattedTextField.AbstractFormatter
{
	/**
	 * Creates a new <code>IPAddressFormatter</code>.
	 */
	public IPAddressFormatter()
	{
		super();
	}

   /**
	 * Converts the given string into an {@link IPAddress}.
	 *
	 * @param ipAddress The string containing the IP address
	 * @return An <code>IPAddress</code> encapsulating the given string
	 * @throws ParseException The IP address is not a valid
	 */
	public Object stringToValue(String ipAddress) throws ParseException
	{
		return new IPAddress(ipAddress);
	}

	/**
	 * Converts the given <code>IPAddress</code> object into a string
	 * representation of an IP address.
	 * <p>
	 * If <code>null</code> is passed, then "0.0.0.0" is returned by default.
	 *
	 * @param value The object representing the IP address
	 * @return The string representation of an IP address, which is of the format
	 * "0.0.0.0"
	 * @throws ParseException The IP address is not a valid
	 */
	public String valueToString(Object value) throws ParseException
	{
		if (value == null)
			return "0.0.0.0";

		return ((IPAddress) value).ipAddress();
	}
}
