package org.eclipse.persistence.jpa.rs.metadata.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SessionBeanCall {

    private String jndiName = null;
    private String methodName = null;
    private String context = null;
    private List<Parameter> parameters = new ArrayList<Parameter>();
    
    public String getContext() {
        return context;
    }
    public void setContext(String context) {
        this.context = context;
    }
    public String getJndiName() {
        return jndiName;
    }
    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }
    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    public List<Parameter> getParameters() {
        return parameters;
    }
    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
}
