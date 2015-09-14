package org.eclipse.persistence.json.bind.model;

/**
 * A model for class field.
 *
 * @author Dmitry Kornilov
 */
public class FieldModel {
    /**
     * Field name as in class.
     */
    private String name;

    /**
     * Field type.
     */
    private Class type;

    /**
     * Field name as it is written in JSON document during marshalling.
     * @JsonbProperty customization on getter. Defaults to {@see name} if not set.
     */
    private String writeName;

    /**
     * Field name to read from JSON document during unmarshalling.
     * @JsonbProperty customization on setter. Defaults to {@see name} if not set.
     */
    private String readName;

    /**
     * Indicates that this field is nillable (@JsonbProperty(nillable=true)).
     */
    private boolean nillable;

    public FieldModel(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    public String getWriteName() {
        if (writeName == null) {
            return name;
        }
        return writeName;
    }

    public void setWriteName(String writeName) {
        this.writeName = writeName;
    }

    public String getReadName() {
        if (readName == null) {
            return name;
        }
        return readName;
    }

    public void setReadName(String readName) {
        this.readName = readName;
    }

    public boolean isNillable() {
        return nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }
}
