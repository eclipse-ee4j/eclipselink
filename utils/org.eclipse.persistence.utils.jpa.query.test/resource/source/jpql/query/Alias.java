package jpql.query;

import java.util.Date;
import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries
({
   @NamedQuery(name="alias.param1", query="SELECT a.alias FROM Alias AS a WHERE (a.alias IS NULL AND :param1 IS NULL) OR a.alias = :param1"),
   @NamedQuery(name="alias.key1",   query="SELECT KEY(k) FROM Alias a JOIN a.ids k"),
   @NamedQuery(name="alias.key2",   query="SELECT KEY(e).firstName FROM Alias a JOIN a.addresses e"),
   @NamedQuery(name="alias.value1", query="SELECT VALUE(v) FROM Alias a JOIN a.ids v"),
   @NamedQuery(name="alias.value2", query="SELECT v FROM Alias a JOIN a.ids v"),
   @NamedQuery(name="alias.value3", query="SELECT VALUE(e).zip.code FROM Alias a JOIN a.addresses e"),
   @NamedQuery(name="alias.entry",  query="SELECT ENTRY(e) FROM Alias a JOIN a.ids e")
})
@SuppressWarnings("unused")
public class Alias
{
	@Id
	private int id;
	private String alias;
	@ElementCollection
	@Temporal(TemporalType.DATE)
	private Map<String, Date> ids;
	private Customer customer;
	@JoinColumn(name="ID", referencedColumnName="ALIAS.ALIAS")
	private Map<Customer, Address> addresses;

	public Alias()
	{
		super();
	}

	public String getAlias()
	{
		return alias;
	}

	public void setAlias(String alias)
	{
		this.alias = alias;
	}
}