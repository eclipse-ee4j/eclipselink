package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class WrapperPackageMetadata {
    public WrapperPackageMetadata(String name) {
        this.name = name;
    }

    public void addMethod(WrapperMethodMetadata method) {
        wrapperMethods.add(method);
    }

    public WrapperMethodMetadata[] getWrapperMethods() {
        return (WrapperMethodMetadata[])wrapperMethods.toArray(new WrapperMethodMetadata[0]);
    }

    public String getName() {
        return name;
    }

    private String name;
    private ArrayList wrapperMethods = new ArrayList();
}
