package jpql.query;

import java.util.Date;
import java.util.Vector;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMimeType;

@Embeddable
public final class ShelfLife
{
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