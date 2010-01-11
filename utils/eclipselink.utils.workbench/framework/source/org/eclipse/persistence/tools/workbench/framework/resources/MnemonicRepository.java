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
package org.eclipse.persistence.tools.workbench.framework.resources;

/**
 * This interface provides a convenient protocol for clients
 * to fetch the mnemonic for a given key. The key is typically the
 * same key used to fetch the mnemonic's string from a
 * StringRepository.
 * 
 * @see org.eclipse.persistence.tools.workbench.utility.StringRepository
 */
public interface MnemonicRepository {

	/**
	 * Return whether a mnemonic exists for this key.
	 * Use this before calling getMnemonic(String) to 
	 * avoid creating numerous MissingMnemonicExceptions
	 */
    boolean hasMnemonic(String key);
    
	/**
	 * Return the mnemonic character for the specified key.
	 * The character will be uppercase (as required by Swing...).
	 * 
	 * @see javax.swing.Jlabel#setDisplayedMnemonic(int)
	 */
	int getMnemonic(String key);

	/**
	 * Return the index of the mnemonic character for the specified key.
	 * Use this method if you want to use a character as the
	 * mnemonic which appears more than once in the resource string.
	 * This way you can choose something other than the first
	 * occurrence of the letter in the string.
	 * 
	 * @see javax.swing.Jlabel#setDisplayedMnemonicIndex(int)
	 */
	int getMnemonicIndex(String key);


	// ********** null implementation **********

	/**
	 * This instance will throw an exception for any non-null key
	 * and return -1 for any null key.
	 */
	MnemonicRepository NULL_INSTANCE =
		new MnemonicRepository() {
            public boolean hasMnemonic(String key) {
                return key == null;
            }
			public int getMnemonic(String key) {
				return this.getMnemonicIndex(key);
			}
			public int getMnemonicIndex(String key) {
				if (key == null) {
					return -1;
				}
				throw new MissingMnemonicException("Missing mnemonic: " + key, key);
			}
			public String toString() {
				return "NullMnemonicRepository";
			}
		};

}
