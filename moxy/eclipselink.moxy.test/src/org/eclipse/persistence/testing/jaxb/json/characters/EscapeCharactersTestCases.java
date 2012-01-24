/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.characters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class EscapeCharactersTestCases extends JSONMarshalUnmarshalTestCases {
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/characters/escapeCharacters.json";
	
	public EscapeCharactersTestCases(String name) throws Exception {
		super(name);	
		setClasses(new Class[]{EscapeCharacterHolder.class});
		setControlJSON(JSON_RESOURCE);		
	}
	
	protected Object getControlObject() {
		EscapeCharacterHolder holder = new EscapeCharacterHolder();
		
		holder.stringValue = "a\"a\\a/a\ba\fa\na\ra\t\b\u0003\u001Caaa\\";
		
		List<Character> characters = new ArrayList<Character>(); 
		characters.add(new Character('a'));
		characters.add(new Character('"'));
		characters.add(new Character('a'));
		characters.add(new Character('\\'));
		characters.add(new Character('a'));
		characters.add(new Character('/'));
		characters.add(new Character('a'));
		characters.add(new Character('\b'));
		characters.add(new Character('a'));
		characters.add(new Character('\f'));
		characters.add(new Character('a'));
		characters.add(new Character('\n'));
		characters.add(new Character('a'));
		characters.add(new Character('\r'));
		characters.add(new Character('a'));
		characters.add(new Character('\t'));
		characters.add(new Character('\b'));
		characters.add(new Character('\u0003'));
		characters.add(new Character('\u001C'));
		characters.add(new Character('a'));
		characters.add(new Character('a'));
		characters.add(new Character('a'));
		characters.add(new Character('\\'));
		holder.characters = characters;
		return holder;
	}

}
