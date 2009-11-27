package dbws.testing.relationships;

public class RelationshipsPhone {

	public String areaCode;
	public String phonenumber;
	public String type;
	public int empId;

    public RelationshipsPhone() {
    	super();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RelationshipsPhone");
        sb.append("(");
        sb.append(areaCode);
        sb.append(") ");
        sb.append(phonenumber);
        sb.append(" {");
        sb.append(type);
        sb.append("}");
        return sb.toString();
    }

}
