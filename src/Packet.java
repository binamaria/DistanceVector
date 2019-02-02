import java.io.Serializable;

public class Packet implements Serializable{
	 /*  Packet Constructor:      
	  *  t    = type      
	  *  i    = seqno      
	  *  j    = size of the packet      
    */
	public Packet(int t, int i, int j, byte abyte[])
	{
		type =  t;
		seqno = i;
		size =  j;         
		data =  new byte[size];         
		data = abyte;
	}
	public byte[] getData()
	{         
		return data;     
	}
	public int getSeqNo()
	{
		return seqno;
	}
	public int getSize()
	{         
		return size;     
	}
	public int getType()
	{         
		return type;     
	}
	public String toString()
	{         
		return "type: " + type + "seq: " + seqno + " size: " + size + " data: " + data;     
	} 
	private int type;     
	private int seqno;    
	private int size;     
	private byte data[]; 
}