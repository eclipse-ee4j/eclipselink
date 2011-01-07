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
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import java.io.PrintWriter;
import java.lang.reflect.Modifier;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.internal.codegen.AccessLevel;


/**
 * This class is heavier than java.lang.reflect.Modifier,
 * but it allows us to delegate a bit of behavior to it.
 */
public final class MWModifier extends MWModel {

	/**
	 * the modifiers are bit flags in this int;
	 * these are the same as the Java bit flags;
	 * except that we don't keep the "interface" setting here
	 * @see java.lang.reflect.Modifier
	 */
	private volatile int code;
		public static final String CODE_PROPERTY = "code";


	/** access level "virtual" property */
	public static final String ACCESS_LEVEL_PROPERTY = "accessLevel";
		public static final String PUBLIC = "public";
		public static final String PACKAGE = "package";
		public static final String PROTECTED = "protected";
		public static final String PRIVATE = "private";
	

	// ********** constructors **********
		
	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWModifier() {
		super();
	}
	
	/**
	 * construct the default modifier,
	 * which is simply "public"
	 */
	MWModifier(MWModifiable parent) {
		super((MWModel) parent);
	}
	
	/**
	 * to set up more than one modifier, "OR" (|) them together; e.g.
	 *     public static => Modifier.PUBLIC | Modifier.STATIC
	 * @see java.lang.reflect.Modifier
	 */
	MWModifier(MWModifiable parent, int code) {
		this(parent);
		this.setCode(code);
	}

	
	// ********** initialization **********
	
	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {	// private-protected
		super.initialize(parent);
		this.code = defaultCode();
	}
	
	/**
	 * this is used when renaming a type
	 */
	void initializeFrom(MWModifier oldModifier) {
		this.setCode(oldModifier.getCode());
	}
	
	/**
	 * re-initialize the modifer to its default settings
	 */
	void clear() {
		this.setCode(defaultCode());
	}

	
	// ********** accessors **********
	
	/**
	 * @see java.lang.reflect.Modifier
	 */
	public int getCode() {
		return this.code;
	}
	
	/**
	 * @see java.lang.reflect.Modifier
	 */
	public void setCode(int code) {
		int old = this.code;
		this.setCodeInternal(code);
		this.firePropertyChanged(CODE_PROPERTY, old, this.code);
		if (old != this.code) {
			this.modifiableParent().modifierChanged(old, this.code);
		}
	}

	private void setCodeInternal(int code) {
		// always clear the interface bit - it is maintained in MWClass
		code &= ~Modifier.INTERFACE;
		this.code = code;
	}
	
	/**
	 * allow the code to be set unchecked
	 */
	void setCodeForTopLink(int code) {
		this.setCodeInternal(code);
	}
	
	private void setBit(boolean value, int mask) {
		int temp = this.code;
		if (value) {
			temp |= mask;
		} else {
			temp &= ~mask;
		}
		this.setCode(temp);
	}
	
	/**
	 * abstract
	 */
	public boolean supportsAbstract() {
		return this.modifiableParent().supportsAbstract();
	}
	
	public boolean canBeSetAbstract() {
		return this.modifiableParent().canBeSetAbstract();
	}
	
	public boolean isAbstract() {
		return Modifier.isAbstract(this.code);
	}
	
	public void setAbstract(boolean value) {
		this.setBit(value, Modifier.ABSTRACT);
	}
	
	/**
	 * final
	 */
	public boolean canBeSetFinal() {
		return this.modifiableParent().canBeSetFinal();
	}
	
	public boolean isFinal() {
		return Modifier.isFinal(this.code);
	}
	
	public void setFinal(boolean value) {
		this.setBit(value, Modifier.FINAL);
	}
	
	/**
	 * interface
	 * we keep this setting in MWClass...
	 */
	private boolean supportsInterface() {
		return this.modifiableParent().supportsInterface();
	}
	
	private boolean canBeSetInterface() {
		return this.modifiableParent().canBeSetInterface();
	}
	
	private boolean isInterface() {
		return Modifier.isInterface(this.code);
	}
	
	private void setInterface(boolean value) {
		this.setBit(value, Modifier.INTERFACE);
	}
	
	/**
	 * native
	 */
	public boolean supportsNative() {
		return this.modifiableParent().supportsNative();
	}
	
	public boolean canBeSetNative() {
		return this.modifiableParent().canBeSetNative();
	}
	
	public boolean isNative() {
		return Modifier.isNative(this.code);
	}
	
	public void setNative(boolean value) {
		this.setBit(value, Modifier.NATIVE);
	}
	
	/**
	 * access level (virtual)
	 */
	public String getAccessLevel() {
		if (this.isPublic()) {
			return PUBLIC;
		} else if (this.isProtected()) {
			return PROTECTED;
		} else if (this.isPackage()) {
			return PACKAGE;
		} else if (this.isPrivate()) {
			return PRIVATE;
		} else {
			throw new IllegalStateException(this.toString());
		}
	}

	public void setAccessLevel(String accessLevel) {
		String old = this.getAccessLevel();
		if (accessLevel == PUBLIC) {
			this.setPublic(true);
		} else if (accessLevel == PROTECTED) {
			this.setProtected(true);
		} else if (accessLevel == PRIVATE) {
			this.setPrivate(true);
		} else if (accessLevel == PACKAGE) {
			this.setPackage(true);
		} else {
			throw new IllegalArgumentException("Allowable values: MWModifier.PUBLIC, MWModifier.PROTECTED, MWModifier.PRIVATE, MWModifier.PACKAGE");
		}
		this.firePropertyChanged(ACCESS_LEVEL_PROPERTY, old, accessLevel);
		if (this.attributeValueHasChanged(old, accessLevel)) {
			this.modifiableParent().accessLevelChanged(old, accessLevel);
		}
	}
	
	/**
	 * package
	 * there is no mask for package/default access;
	 * it is implied by the absence of all the other access modifiers
	 */
	public boolean canBeSetPackage() {
		return this.modifiableParent().canBeSetPackage();
	}
	
	public boolean isPackage() {
		return ! this.isPublic()
				&& ! this.isPrivate()
				&& ! this.isProtected();
	}
	
	public void setPackage(boolean value) {
		int temp = this.code;
		if (value) {
			temp &= ~Modifier.PUBLIC;
		} else {
			// if false, then the access modifier is set to public by default
			temp |= Modifier.PUBLIC;
		}
		temp &= ~Modifier.PRIVATE;
		temp &= ~Modifier.PROTECTED;
		this.setCode(temp);
	}
	
	/**
	 * private
	 */
	public boolean canBeSetPrivate() {
		return this.modifiableParent().canBeSetPrivate();
	}
	
	public boolean isPrivate() {
		return Modifier.isPrivate(this.code);
	}
	
	public void setPrivate(boolean value) {
		int temp = this.code;
		if (value) {
			temp |= Modifier.PRIVATE;
			temp &= ~Modifier.PROTECTED;
			temp &= ~Modifier.PUBLIC;
		} else {
			temp &= ~Modifier.PRIVATE;
		}
		this.setCode(temp);
	}
	
	/**
	 * protected
	 */
	public boolean canBeSetProtected() {
		return this.modifiableParent().canBeSetProtected();
	}
	
	public boolean isProtected() {
		return Modifier.isProtected(this.code);
	}
	
	public void setProtected(boolean value) {
		int temp = this.code;
		if (value) {
			temp |= Modifier.PROTECTED;
			temp &= ~Modifier.PRIVATE;
			temp &= ~Modifier.PUBLIC;
		} else {
			temp &= ~Modifier.PROTECTED;
		}
		this.setCode(temp);
	}
	
	/**
	 * public
	 */
	public boolean canBeSetPublic() {
		return this.modifiableParent().canBeSetPublic();
	}
	
	public boolean isPublic() {
		return Modifier.isPublic(this.code);
	}
	
	public void setPublic(boolean value) {
		int temp = this.code;
		if (value) {
			temp |= Modifier.PUBLIC;
			temp &= ~Modifier.PRIVATE;
			temp &= ~Modifier.PROTECTED;
		} else {
			temp &= ~Modifier.PUBLIC;
		}
		this.setCode(temp);
	}
	
	/**
	 * static
	 */
	public boolean canBeSetStatic() {
		return this.modifiableParent().canBeSetStatic();
	}
	
	public boolean isStatic() {
		return Modifier.isStatic(this.code);
	}
	
	public void setStatic(boolean value) {
		this.setBit(value, Modifier.STATIC);
	}
	
	/**
	 * strict
	 */
	public boolean supportsStrict() {
		return this.modifiableParent().supportsStrict();
	}
	
	public boolean canBeSetStrict() {
		return this.modifiableParent().canBeSetStrict();
	}
	
	public boolean isStrict() {
		return Modifier.isStrict(this.code);
	}
	
	public void setStrict(boolean value) {
		this.setBit(value, Modifier.STRICT);
	}
	
	/**
	 * synchronized
	 */
	public boolean supportsSynchronized() {
		return this.modifiableParent().supportsSynchronized();
	}
	
	public boolean canBeSetSynchronized() {
		return this.modifiableParent().canBeSetSynchronized();
	}
	
	public boolean isSynchronized() {
		return Modifier.isSynchronized(this.code);
	}
	
	public void setSynchronized(boolean value) {
		this.setBit(value, Modifier.SYNCHRONIZED);
	}
	
	/**
	 * transient
	 */
	public boolean supportsTransient() {
		return this.modifiableParent().supportsTransient();
	}
	
	public boolean canBeSetTransient() {
		return this.modifiableParent().canBeSetTransient();
	}
	
	public boolean isTransient() {
		return Modifier.isTransient(this.code);
	}
	
	public void setTransient(boolean value) {
		this.setBit(value, Modifier.TRANSIENT);
	}
	
	/**
	 * volatile
	 */
	public boolean supportsVolatile() {
		return this.modifiableParent().supportsVolatile();
	}
	
	public boolean canBeSetVolatile() {
		return this.modifiableParent().canBeSetVolatile();
	}
	
	public boolean isVolatile() {
		return Modifier.isVolatile(this.code);
	}
	
	public void setVolatile(boolean value) {
		this.setBit(value, Modifier.VOLATILE);
	}

		
	// ********** queries **********
	
	/**
	 * convenience method - cast the parent
	 */
	private MWModifiable modifiableParent() {
		return (MWModifiable) this.getMWParent();
	}
	
	public static int defaultCode() {
		return Modifier.PUBLIC;
	}
	
	public boolean isDefaultValue() {
		return this.code == defaultCode();
	}
	
	/**
	 * used by Wallace code gen
	 */
	AccessLevel accessLevel() {
		AccessLevel accessLevel = new AccessLevel();
		if (this.isPublic()) {
			accessLevel.setLevel(AccessLevel.PUBLIC);
		} else if (this.isProtected()) {
			accessLevel.setLevel(AccessLevel.PROTECTED);
		} else if (this.isPackage()) {
			accessLevel.setLevel(AccessLevel.PACKAGE);
		} else {
			accessLevel.setLevel(AccessLevel.PRIVATE);
		}
	
		accessLevel.setIsAbstract(this.isAbstract());
		accessLevel.setIsFinal(this.isFinal());
		accessLevel.setIsNative(this.isNative());
		accessLevel.setIsStatic(this.isStatic());
		accessLevel.setIsSynchronized(this.isSynchronized());
		accessLevel.setIsTransient(this.isTransient());
		accessLevel.setIsVolatile(this.isVolatile());
	
		return accessLevel;
	}

	
	// ********** behavior **********
	
	void refresh(int javaModifiers) {
		this.setCode(javaModifiers);
	}
	
	/**
	 * notify listeners of a change to the
	 * modifier's "allowable" settings; i.e. this notification
	 * will be sent out when the modifier's response to the
	 * #canBeSet___() methods has changed.
	 */
	void allowedModifiersChanged() {
		this.fireStateChanged();
	}

	/**
	 * currently unused...
	 * check the code for validity; these checks only apply
	 * to source code declarations, not declarations that are
	 * compiler-generated (e.g. anonymous inner classes)
	 */
	private void checkCode() {
		if ((this.isPublic() && ! this.canBeSetPublic())
			|| (this.isProtected() && ! this.canBeSetProtected())
			|| (this.isPackage() && ! this.canBeSetPackage())
			|| (this.isPrivate() && ! this.canBeSetPrivate())
			|| (this.isInterface() && ! this.canBeSetInterface())
			|| (this.isFinal() && ! this.canBeSetFinal())
			|| (this.isStatic() && ! this.canBeSetStatic())
			|| (this.isAbstract() && ! this.canBeSetAbstract())
			|| (this.isNative() && ! this.canBeSetNative())
			|| (this.isStrict() && ! this.canBeSetStrict())
			|| (this.isSynchronized() && ! this.canBeSetSynchronized())
			|| (this.isTransient() && ! this.canBeSetTransient())
			|| (this.isVolatile() && ! this.canBeSetVolatile())) {
					throw new IllegalStateException(this.toString());
		}
	}
	

	// ********** displaying and printing **********
	
	public String displayString() {
		return Modifier.toString(this.code);
	}		
	
	public void toString(StringBuffer sb) {
		sb.append(this.displayString());
	}
		
	/**
	 * append the modifier's source representation to the writer;
	 * return true if any source was actually written to the writer
	 * (it is possible that no source was written out);
	 * this return value allows callers to know whether to append
	 * a space if necessary
	 */
	boolean writeSource(PrintWriter pw) {
		String source = Modifier.toString(this.code);
		if (source.length() == 0) {
			return false;
		}
		pw.print(source);
		return true;
	}
	
	
	// ********** static methods **********
	
	/**
	 * Return whether *all* of the specified flags in the specified
	 * codes are the same.
	 * @see java.lang.reflect.Modifier
	 */
	static boolean flagsAreSame(int flags, int code1, int code2) {
		return (code1 & flags) == (code2 & flags);
	}
	
	/**
	 * Return whether *any* of the specified flags in the specified
	 * codes are different.
	 * @see java.lang.reflect.Modifier
	 */
	static boolean anyFlagsAreDifferent(int flags, int code1, int code2) {
		return ! flagsAreSame(flags, code1, code2);
	}
	
}
