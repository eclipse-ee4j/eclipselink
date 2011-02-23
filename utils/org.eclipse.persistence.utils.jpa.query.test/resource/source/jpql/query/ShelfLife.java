package jpql.query;

import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMimeType;

@Embeddable
public class ShelfLife
{
	@Temporal(TemporalType.DATE)
	private Date soldDate;

	public ShelfLife()
	{
		super();
	}

	public Date getSoldDate()
	{
		return soldDate;
	}

	@XmlElementRefs( { @XmlElementRef, @XmlElementRef} )
	public void setSoldDate(@XmlMimeType("ddd") Date soldDate)
	{
		this.soldDate = soldDate;
	}
}