package org.eclipse.persistence.dynamic;

public interface EclipseLinkClassWriter {
    
    public byte[] writeClass(DynamicClassLoader loader, String className) throws ClassNotFoundException;
    
    public boolean isCompatible(EclipseLinkClassWriter writer);
    
    public Class<?> getParentClass();
    
    public String getParentClassName();
}