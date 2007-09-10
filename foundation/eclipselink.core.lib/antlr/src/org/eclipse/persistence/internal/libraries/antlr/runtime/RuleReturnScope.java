/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.libraries.antlr.runtime;

/** Rules can return start/stop info as well as possible trees and templates */
public class RuleReturnScope {
	/** Return the start token or tree */
	public Object getStart() { return null; }
	/** Return the stop token or tree */
	public Object getStop() { return null; }
	/** Has a value potentially if output=AST; */
	public Object getTree() { return null; }
	/** Has a value potentially if output=template; Don't use StringTemplate
	 *  type as it then causes a dependency with ST lib.
	 */
	public Object getTemplate() { return null; }
}
