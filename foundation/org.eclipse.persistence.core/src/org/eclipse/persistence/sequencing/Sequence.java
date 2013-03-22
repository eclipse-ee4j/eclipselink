/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.sequencing;

import java.util.Vector;
import java.io.Serializable;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * <p>
 * <b>Purpose</b>: Abstract class to define sequencing.
 * <p>
 * <b>Description</b>
 * A sequence defines how generated ids are obtained.
 * The main sequence types are TableSequence and NativeSequence.
 * Descriptors using sequencing will use the sequence object defined in their session's
 * DatabaseLogin with the name matching their sequence name.  If a specific sequence is
 * not defined for the name the DatabaseLogin's default sequence will be used.
 * @see TableSequence
 * @see NativeSequence
 */
public abstract class Sequence implements Serializable, Cloneable {
    // name
    protected String name = "";

    // preallocation size
    protected int size = 50;

    // owner platform
    protected Platform platform;
    
    protected int initialValue = 1;

    // number of times onConnect was called - number of times onDisconnect was called
    protected int depth;

    protected String qualifier = "";
    // true indicates that qualifier was set through setQualifier method, 
    // false - copied from platform (or not set at all). 
    protected boolean isCustomQualifier;
    
    // indicates whether the existing pk value should always be overridden by the sequence.
    // note that even if set to false sequence always overrides if shouldAcquireValueAfterInsert returns true. 
    protected boolean shouldAlwaysOverrideExistingValue;
    
    public Sequence() {
        super();
        setName("SEQUENCE");
    }

    /**
     * Create a new sequence with the name.
     */
    public Sequence(String name) {
        this();
        setName(name);
    }
    
    /**
     * Create a new sequence with the name and sequence pre-allocation size.
     */
    public Sequence(String name, int size) {
        this();
        setName(name);
        setPreallocationSize(size);
    }
    
    public Sequence(String name, int size, int initialValue) {
        this();
        setName(name);
        setPreallocationSize(size);
        setInitialValue(initialValue);
    }
    
    public boolean isNative() {
        return false;
    }
    
    public boolean isTable() {
        return false;
    }
    
    public boolean isUnaryTable() {
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPreallocationSize() {
        return size;
    }

    public void setPreallocationSize(int size) {
        this.size = size;
    }
    
    public int getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(int initialValue) {
        this.initialValue = initialValue;
    }    

    public Object clone() {
        try {
            Sequence clone = (Sequence)super.clone();
            if (isConnected()) {
                clone.depth = 1;
                clone.onDisconnect();
                clone.setDatasourcePlatform(null);
            }
            return clone;
        } catch (Exception exception) {
            throw new InternalError("Clone failed");
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof Sequence) {
            return equalNameAndSize(this, (Sequence)obj);
        } else {
            return false;
        }
    }

    /**
     * INTERNAL:
     * Used in equals.
     */
    public static boolean equalNameAndSize(Sequence seq1, Sequence seq2) {
        if (seq1 == seq2) {
            return true;
        }
        return seq1.getName().equals(seq2.getName()) && (seq1.getPreallocationSize() == seq2.getPreallocationSize());
    }

    protected void setDatasourcePlatform(Platform platform) {
        this.platform = platform;
    }

    public Platform getDatasourcePlatform() {
        return platform;
    }

    /**
     * INTERNAL:
     * Indicates whether sequencing value should be acquired after INSERT.
     * Note that preallocation could be used only in case sequencing values
     * should be acquired before insert (this method returns false).
     * In default implementation, it is true for table sequencing and native
     * sequencing on Oracle platform, false for native sequencing on other platforms.
     */
    public abstract boolean shouldAcquireValueAfterInsert();

    /**
     * INTERNAL:
     * Indicates whether several sequencing values should be acquired at a time
     * and be kept by TopLink. This in only possible in case sequencing numbers should
     * be acquired before insert (shouldAcquireValueAfterInsert()==false).
     * In default implementation, it is true for table sequencing and native
     * sequencing on Oracle platform, false for native sequencing on other platforms.
     */
    public boolean shouldUsePreallocation() {
        return !shouldAcquireValueAfterInsert();
    }

    /**
     * INTERNAL:
     * Indicates whether TopLink should internally call beginTransaction() before
     * getGeneratedValue/Vector, and commitTransaction after.
     * In default implementation, it is true for table sequencing and
     * false for native sequencing.
     */
    public abstract boolean shouldUseTransaction();

    /**
     * INTERNAL:
     * Return the newly-generated sequencing value.
     * Used only in case preallocation is not used (shouldUsePreallocation()==false).
     * Accessor may be non-null only in case shouldUseSeparateConnection()==true.
     * Even in this case accessor could be null - if SequencingControl().shouldUseSeparateConnection()==false;
     * Therefore in case shouldUseSeparateConnection()==true, implementation should handle
     * both cases: use a separate connection if provided (accessor != null), or get by
     * without it (accessor == null).
     * @param accessor Accessor is a separate sequencing accessor (may be null);
     * @param writeSession Session is a Session used for writing (either ClientSession or DatabaseSession);
     * @param seqName String is sequencing number field name
     */
    public abstract Object getGeneratedValue(Accessor accessor, AbstractSession writeSession, String seqName);

    /**
     * INTERNAL:
     * Return the newly-generated sequencing value.
     * Used only in case preallocation is not used (shouldUsePreallocation()==false).
     * Accessor may be non-null only in case shouldUseSeparateConnection()==true.
     * Even in this case accessor could be null - if SequencingControl().shouldUseSeparateConnection()==false;
     * Therefore in case shouldUseSeparateConnection()==true, implementation should handle
     * both cases: use a separate connection if provided (accessor != null), or get by
     * without it (accessor == null).
     * @param accessor Accessor is a separate sequencing accessor (may be null);
     * @param writeSession Session is a Session used for writing (either ClientSession or DatabaseSession);
     */
    public Object getGeneratedValue(Accessor accessor, AbstractSession writeSession) {
        return getGeneratedValue(accessor, writeSession, getName());
    }

    /**
     * INTERNAL:
     * Return a Vector of newly-generated sequencing values.
     * Used only in case preallocation is used (shouldUsePreallocation()==true).
     * Accessor may be non-null only in case shouldUseSeparateConnection()==true.
     * Even in this case accessor could be null - if SequencingControl().shouldUseSeparateConnection()==false;
     * Therefore in case shouldUseSeparateConnection()==true, implementation should handle
     * both cases: use a separate connection if provided (accessor != null), or get by
     * without it (accessor == null).
     * @param accessor Accessor is a separate sequencing accessor (may be null);
     * @param writeSession Session is a Session used for writing (either ClientSession or DatabaseSession);
     * @param seqName String is sequencing number field name
     * @param size int number of values to preallocate (output Vector size).
     */
    public abstract Vector getGeneratedVector(Accessor accessor, AbstractSession writeSession, String seqName, int size);

    /**
     * INTERNAL:
     * Return a Vector of newly-generated sequencing values.
     * Used only in case preallocation is used (shouldUsePreallocation()==true).
     * Accessor may be non-null only in case shouldUseSeparateConnection()==true.
     * Even in this case accessor could be null - if SequencingControl().shouldUseSeparateConnection()==false;
     * Therefore in case shouldUseSeparateConnection()==true, implementation should handle
     * both cases: use a separate connection if provided (accessor != null), or get by
     * without it (accessor == null).
     * @param accessor Accessor is a separate sequencing accessor (may be null);
     * @param writeSession Session is a Session used for writing (either ClientSession or DatabaseSession);
     */
    public Vector getGeneratedVector(Accessor accessor, AbstractSession writeSession) {
        return getGeneratedVector(accessor, writeSession, getName(), getPreallocationSize());
    }

    /**
     * INTERNAL:
     * This method is called when Sequencing object is created.
     * Don't override this method.
     */
    public void onConnect(Platform platform) {
        setDatasourcePlatform(platform);
        if(!isCustomQualifier) {
            qualifier = getDatasourcePlatform().getTableQualifier();
        }
        onConnect();
        depth++;
    }

    /**
     * INTERNAL:
     * This method is called when Sequencing object is created.
     * If it requires initialization, subclass should override this method.
     */
    public abstract void onConnect();

    /**
     * INTERNAL:
     * This method is called when Sequencing object is destroyed.
     * Don't override this method.
     */
    public void onDisconnect(Platform platform) {
        if (isConnected()) {
            depth--;
            if(depth==0 && !isCustomQualifier) {
                qualifier = "";
            }
            // Can no longer disconnect sequences, as they are part of descriptor shared meta-data.
        }
    }

    /**
     * INTERNAL:
     * This method is called when Sequencing object is destroyed.
     * If it requires deinitialization, subclass should override this method.
     */
    public abstract void onDisconnect();

    /**
     * PUBLIC:
     * Indicates that Sequence is connected.
     */
    public boolean isConnected() {
        return platform != null;
    }

    /**
     * INTERNAL:
     * Make sure that the sequence is not used by more than one platform.
     */
    protected void verifyPlatform(Platform otherPlatform) {
        if (getDatasourcePlatform() != otherPlatform) {
            String hashCode1 = Integer.toString(System.identityHashCode(getDatasourcePlatform()));
            String name1 = ((DatasourcePlatform)getDatasourcePlatform()).toString() + '(' + hashCode1 + ')';

            String hashCode2 = Integer.toString(System.identityHashCode(otherPlatform));
            String name2 = ((DatasourcePlatform)otherPlatform).toString() + '(' + hashCode2 + ')';

            throw ValidationException.sequenceCannotBeConnectedToTwoPlatforms(getName(), name1, name2);
        }
    }

    /**
     * INTERNAL:
     */
    public void setQualifier(String qualifier) {
        if(qualifier == null) {
            qualifier = "";
        }
        this.isCustomQualifier = qualifier.length() > 0;
        this.qualifier = qualifier;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isCustomQualifier() {
        return isCustomQualifier;
    }

    /**
     * INTERNAL:
     */
    public String getQualifier() {
        return qualifier;
    }
    
    /**
     * INTERNAL:
     */
    public String getQualified(String str) {
        if (qualifier.equals("")) {
            return str;
        } else {
            return qualifier + "." + str;
        }
    }
    
    /**
     * ADVANCED:
     * Set that to true if the sequence should always override the existing pk value.
     */
    public void setShouldAlwaysOverrideExistingValue(boolean shouldAlwaysOverrideExistingValue) {
        this.shouldAlwaysOverrideExistingValue = shouldAlwaysOverrideExistingValue;
    }

    /**
     * INTERNAL:
     * Indicates whether the existing pk value should always be overridden by the sequence.
     * As always the version of the method taking seqName is provided for the benefit
     * of DefaultSequence. 
     */
    public boolean shouldAlwaysOverrideExistingValue() {
        return shouldAlwaysOverrideExistingValue(getName());
    }

    /**
     * INTERNAL:
     * Indicates whether the existing pk value should always be overridden by the sequence.
     */
    public boolean shouldAlwaysOverrideExistingValue(String seqName) {
        return this.shouldAlwaysOverrideExistingValue || shouldAcquireValueAfterInsert();
    }
    
    public String toString() {
        return getClass().getSimpleName() + "(" + getName() + ")";
    }
}
