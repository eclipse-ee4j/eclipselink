package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataMethod;
import org.eclipse.persistence.internal.jpa.metadata.accessors.AccessMethodsMetadata;
import org.eclipse.persistence.mappings.DatabaseMapping;

public abstract class MappingAccessor extends MetadataAccessor {
    private AccessMethodsMetadata m_accessMethods;

    public MappingAccessor() {
        super();
    }

    public MappingAccessor(MetadataAccessibleObject accessibleObject,
            ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
    }
    
    public AccessMethodsMetadata getAccessMethodsMetadata(){
        return m_accessMethods;
    }
    
    public void setAccessMethodsMetadata(AccessMethodsMetadata accessMethodsMetadata){
        this.m_accessMethods = accessMethodsMetadata;
    }
    
    /**
     * INTERNAL:
     * Returns the set method name of a method accessor. Note, this method
     * should not be called when processing field access.
     */
    protected String getSetMethodName() {
        if ( (this.m_accessMethods !=null) && (m_accessMethods.getSetMethodName() !=null)){
            return m_accessMethods.getSetMethodName();
        }
        return ((MetadataMethod) getAccessibleObject()).getSetMethodName();
    }
    
    /**
     * INTERNAL:
     * Returns the get method name of a method accessor. Note, this method
     * should not be called when processing field access.
     */
    protected String getGetMethodName() {
        if ( (this.m_accessMethods !=null) && (m_accessMethods.getGetMethodName() !=null)){
            return m_accessMethods.getGetMethodName();
        }
        return getAccessibleObjectName();
    }
    
    /**
     * INTERNAL:
     * Returns true if this class uses property access. In an inheritance 
     * hierarchy, the subclasses inherit their access type from the parent.
     * The metadata helper method caches the class access types for 
     * efficiency.
     * @see MetadataDescriptor usesPropertyAccess()
     */
    public boolean usesPropertyAccess(MetadataDescriptor descriptor) {
        if (this.m_accessMethods !=null){
            return true;
        }
        return descriptor.usesPropertyAccess();
    }
    
    /**
     * INTERNAL:
     * Set the getter and setter access methods for this accessor.
     */
    protected void setAccessorMethods(DatabaseMapping mapping) {
        if (usesPropertyAccess(getDescriptor())) {
            mapping.setGetMethodName(getGetMethodName());
            mapping.setSetMethodName(getSetMethodName());
        }
    }

}