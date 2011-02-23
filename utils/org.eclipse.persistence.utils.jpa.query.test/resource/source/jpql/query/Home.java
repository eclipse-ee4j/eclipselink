package jpql.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Home implements Serializable
{
	private List<Phone> phones;
	@Id
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