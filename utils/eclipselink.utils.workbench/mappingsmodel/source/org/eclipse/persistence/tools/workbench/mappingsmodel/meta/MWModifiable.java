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
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

/**
 * Used by MWModifier to interact with its parents (MWClass,
 * MWClassAttribute, and MWMethod).
 * 
 * The #supports___() methods indicate whether a type of modifiable object even
 * supports the given modifier.
 * For example, a class attribute can *never* be set to "abstract".
 * 
 * The #canBeSet___() methods indicate whether a modifiable object can *currently*
 * be set with the indicated modifier.
 * For example, a method can only be set "abstract" when it's declaring
 * class is "abstract" and the method is not native or synchronized etc.
 * 
 * All three types of of modifiable objects "support" all the "access" modifiers
 * (public, protected, [package], private); although there are certain restrictions
 * for classes.
 * 
 * All three types of of modifiable objects also "support" the "final" and "static"
 * modifiers.
 * 
 * The methods #modifierChanged(int, int) and #accessLevelChanged(String, String)
 * are used to by the MWModifier to notify the MWModifiable
 * owner that it has changed and may alter its display properties.
 */
public interface MWModifiable {

	/**
	 * This property can be used to notify listeners of a change to the
	 * MWModifiable's modifier 'code'.
	 */
	public static final String MODIFIER_CODE_PROPERTY = "modifierCode";

	/**
	 * This property can be used to notify listeners of a change to the
	 * MWModifiable's modifier 'accessLevel', this notification will be in addition
	 * to the modifier 'code' notification above.
	 */
	public static final String MODIFIER_ACCESS_LEVEL_PROPERTY = "modifierAccessLevel";

	MWModifier getModifier();

	boolean supportsAbstract();
	boolean canBeSetAbstract();

	boolean canBeSetFinal();

	boolean supportsInterface();
	boolean canBeSetInterface();

	boolean supportsNative();
	boolean canBeSetNative();

	boolean canBeSetPackage();
	boolean canBeSetPrivate();
	boolean canBeSetProtected();
	boolean canBeSetPublic();

	boolean canBeSetStatic();

	boolean supportsStrict();
	boolean canBeSetStrict();

	boolean supportsSynchronized();
	boolean canBeSetSynchronized();

	boolean supportsTransient();
	boolean canBeSetTransient();

	boolean supportsVolatile();
	boolean canBeSetVolatile();

	void modifierChanged(int oldCode, int newCode);
	void accessLevelChanged(String oldValue, String newValue);

}
