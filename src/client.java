import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class client {
	//  use for storing client's file 
	static String SAVE_FILE = "./client/";
//	static String SAVE_FILE = "C:\\Users\\richardthomas\\Desktop\\out";
	static String HOME_directory = "./";
	
	//  use for connecting id4
	static String LOCAL_ID4 ="127.0.0.1";
	
	//use for writing logs
	static String LOG_CLIENT="logc.txt";
	static String LOG_SERVER="logs.txt";
	static int PORT = 50000; 
	
	static void Print(String str,BufferedWriter log_out,SimpleDateFormat df)
	{
		try {
			Date date =  new Date();
			log_out.write(df.format(date)+" INFO [Client.Main] "+str+"\n");
			log_out.flush();
			System.out.println(df.format(date)+" INFO [Client.Main] "+str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException
	{
		//the total number of data packets transmitted
		int total_all_packge = 0;

		//the total number of acknowledgments sent
		int s_ack_total = 0;
		
		//the total number of duplicate packets received
		int get_agin = 0;
		
		int file_size=0;
		
		BufferedWriter log_out=null;
		DatagramPacket datagramPacket1 = null;
		DatagramSocket datagramSocket1 = null;
		byte [] buf1 =new byte[1024];
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		
		DatagramPacket datagramPacket2 = null;
		DatagramSocket datagramSocket2 = null;
		byte [] buf2 =new byte[1024];
		int now_ack_id = 0;
		
		System.out.print("client <file_name>:");
		Scanner scanner = new Scanner(System.in);
		String pathname=scanner.nextLine();
		scanner.close();

		int total_packet = 0;
		int resend_packet = 0;
		BufferedOutputStream bos = null;
		try {
			File file = new File(HOME_directory + LOG_CLIENT);
			if(!file.exists())
				file.createNewFile();
			log_out = new BufferedWriter(new OutputStreamWriter(new  FileOutputStream(file)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                
                
		boolean  finished = false;
		datagramSocket1 = new DatagramSocket();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
				
				
		total_all_packge++;
		Packet temp = new Packet(1, 0, pathname.getBytes().length, pathname.getBytes());
		oos.writeObject(temp);
		buf1 = baos.toByteArray();
		datagramPacket1 = new DatagramPacket(buf1, buf1.length,InetAddress.getLocalHost(),PORT);
		datagramSocket1.send(datagramPacket1);
		baos.close();oos.close();baos = null;oos = null;
			
		Print("Sending -----> REQ packet for file: "+pathname, log_out, df);
				
		datagramSocket2  = new DatagramSocket(PORT + 1);
		datagramPacket2  = new DatagramPacket(buf2, buf2.length);
		
		now_ack_id = 0;
			
				
		while(!finished)
		{
			datagramSocket2.receive(datagramPacket2);
			total_all_packge++;
			ByteArrayInputStream bais = new ByteArrayInputStream(buf2);
			ObjectInputStream ois = new ObjectInputStream(bais);
			temp=(Packet)ois.readObject();
			bais.close();
			ois.close();
			bais = null;ois = null;
			switch(temp.getType())
			{
			case 2:
				Print("Received <----- DAT packet seqNo: "+temp.getSeqNo()+", containing "+temp.getSize()+"(bytes) of the file.", log_out, df);
				if(temp.getSeqNo() == now_ack_id+1)
				{
					if(bos == null)
					{
						File file = new File(SAVE_FILE);
						if(!file.exists()) file.mkdir();
						file= new File(SAVE_FILE + pathname);
						if(!file.exists())
							file.createNewFile();
						bos = new BufferedOutputStream(new FileOutputStream(file));
					}
					file_size+=temp.getSize();
					bos.write(temp.getData());
					bos.flush();
					now_ack_id++;
					byte buf[] = new byte[1024];
					total_all_packge ++;
					s_ack_total++;
					temp = new Packet(0, now_ack_id, 0, new byte[0]);
					baos = new ByteArrayOutputStream();
					oos = new ObjectOutputStream(baos);
					oos.writeObject(temp);
					buf=baos.toByteArray();
					datagramPacket1.setData(buf, 0, buf.length);
					datagramSocket1.send(datagramPacket1);
					Print("Sending -----> ACK packet for sequence number: "+temp.getSeqNo(), log_out, df);
					baos.close();oos.close();baos = null;oos = null;
					try {
						int td = new Random().nextInt(10)+5;
						Thread.sleep(5);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
						
				}
				else if(temp.getSeqNo() < now_ack_id)
				{
					get_agin++;
				}
				else
				{
					s_ack_total++;
					byte buf[] = new byte[1024];
					temp = new Packet(0, now_ack_id, 0, new byte[0]);
					baos = new ByteArrayOutputStream();
					oos = new ObjectOutputStream(baos);
					oos.writeObject(temp);
					buf=baos.toByteArray();
					datagramPacket1.setData(buf, 0, buf.length);
					datagramSocket1.send(datagramPacket1);
					Print("Sending -----> ACK packet for sequence number: "+temp.getSeqNo(), log_out, df);
					baos.close();oos.close();baos = null;oos = null;
					try {
						int td = new Random().nextInt(10)+5;
						Thread.sleep(5);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				break;
			case 3:
				finished = true;
				break;
			case 4:
				if(temp.getSeqNo() == now_ack_id+1){
				byte buf[] = new byte[1024];
				total_all_packge ++;
				s_ack_total++;
				now_ack_id++;
				temp = new Packet(0, now_ack_id, 0, new byte[0]);
				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				oos.writeObject(temp);
				buf=baos.toByteArray();
				datagramPacket1.setData(buf, 0, buf.length);
				datagramSocket1.send(datagramPacket1);
				Print("Sending -----> ACK packet for sequence number: "+temp.getSeqNo(), log_out, df);
				Print("Received <----- EOT packet", log_out, df);
				baos.close();oos.close();baos = null;oos = null;
				finished = true;
				}
				break;
			default:
				break;
			}
		}
		Print(" Received the file containing: "+file_size+"(bytes) of data", log_out, df);
		Print("the total number of data packets transmitted:"+(total_all_packge)+"    the total number of duplicate packets received "+get_agin+"   the total number of acknowledgments sent:"+(s_ack_total)+"\n", log_out, df);
		log_out.close();
		datagramSocket1.close();
		datagramSocket2.close();
		if(bos != null)
			bos.close();
	}
}