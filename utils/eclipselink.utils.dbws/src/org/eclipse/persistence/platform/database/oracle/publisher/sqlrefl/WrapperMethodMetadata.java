package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

public class WrapperMethodMetadata {
    public WrapperMethodMetadata(String name, String[] paramTypes, String[] paramNames,
        String returnType) {
        this.name = name;
        this.paramTypes = paramTypes;
        this.paramNames = paramNames;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public String[] getParamTypes() {
        return paramTypes;
    }

    public String[] getParamNames() {
        return paramNames;
    }

    public String getReturnType() {
        return returnType;
    }

    private String name;
    private String[] paramTypes;
    private String[] paramNames;
    private String returnType;
}
