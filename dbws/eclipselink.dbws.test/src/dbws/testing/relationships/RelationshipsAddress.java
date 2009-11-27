package dbws.testing.relationships;

public class RelationshipsAddress {

    public int addressId;
    public String street;
    public String city;
    public String province;
    public String country;
    public String postalCode;

    public RelationshipsAddress() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RelationshipsAddress");
        sb.append("[");
        sb.append(addressId);
        sb.append("]");
        sb.append(street);
        sb.append(" ");
        sb.append(city);
        sb.append(" ");
        sb.append(province);
        sb.append(" ");
        sb.append(postalCode);
        sb.append(" ");
        sb.append(country);
        return sb.toString();
    }
}