package jpql.query;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries
({
	@NamedQuery(name="home.bad1", query="SELECT FROM Home h"),
	@NamedQuery(name="home.bad2", query="SELEC")
})
public final class Home
{
	private List<Phone> phones;
	private String city;

	public Home()
	{
		super();
		phones = new ArrayList<Phone>();
	}

	public String getCity()
	{
		return city;
	}

	public List<Phone> getPhones()
	{
		return phones;
	}

	public void addPhone(Phone phone)
	{
		phones.add(phone);
	}

	public void setCity(String city)
	{
		this.city = city;
	}
}