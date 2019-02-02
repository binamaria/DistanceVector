import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Server {
//	static String HOME_directory = "C:\\Users\\richardthomas\\Desktop\\CN_project3_working";
static String HOME_directory = "./";
	//static String GET_FILE = "C:\\Users\\richardthomas\\Desktop\\in";
	static String GET_FILE = "./";
	static String LOCAL_ID4 ="127.0.0.1";
	static String LOG_CLIENT="logc.txt";
	static String LOG_SERVER="logs.txt";
	static int PORT = 50040;

	static void Print(String str,BufferedWriter log_out,SimpleDateFormat df)
	{
		try {
			Date date =  new Date();
			log_out.write(df.format(date)+" INFO [Server.main] "+str+"\n");
			log_out.flush();
			System.out.println(df.format(date)+" INFO [Server.main] "+str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException
	{
		int total_all_packge = 0;
		int send_again = 0;
		int s_ack_total = 0;
		int get_ack = 0;
		int get_agin = 0;
		int total_data =0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Scanner scanner = new Scanner(System.in);
		BufferedWriter log_out=null;
		DatagramPacket datagramPacket1 = null;
		DatagramSocket datagramSocket1 = null;
		byte [] buf1 =new byte[1024];
		
		
		DatagramPacket datagramPacket2 = null;
		DatagramSocket datagramSocket2 = null;
		byte [] buf2 =new byte[1024];
		int now_ack_id = 1;
		int op_id=0;
		boolean finished = false;
		datagramSocket1 = new DatagramSocket();
		datagramPacket1 = new DatagramPacket(buf1, buf1.length,InetAddress.getLocalHost(),PORT+1);
				
		datagramSocket2  = new DatagramSocket(PORT);
		datagramPacket2  = new DatagramPacket(buf2, buf2.length);
				
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;


		RandomAccessFile readfile = null;
		File file = null;
		file = new File(HOME_directory + LOG_SERVER);
		if(!file.exists())
			//file.mkdirs();
			file.createNewFile();
		log_out = new BufferedWriter(new OutputStreamWriter(new  FileOutputStream(file)));
		int d = 0;
		Print("Starting", log_out, df);
		System.out.print("server (0,1,2): ");
		op_id = scanner.nextInt();
		if(op_id == 0)
		{
			Print("Skipping First Packet in Window: false", log_out, df);
			Print("Skipping All Packets in Window: false", log_out, df);
		}
		else if(op_id == 1)
		{
			Print("Skipping First Packet in Window: true", log_out, df);
			Print("Skipping All Packets in Window: false", log_out, df);
		}
		else if(op_id == 2)
		{
			Print("Skipping First Packet in Window: false", log_out, df);
			Print("Skipping All Packets in Window: true", log_out, df);
		}
		 
		Print("Starting Server on Port: " + (PORT+1), log_out, df);
		int last_num_ack = -1;
		
		
		datagramSocket2.receive(datagramPacket2);
		ByteArrayInputStream bais = new ByteArrayInputStream(buf2);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Packet p = (Packet) ois.readObject();
		bais.close();
		ois.close();
		bais = null;ois = null;
		total_all_packge ++;
		String file_name = new String(p.getData());
		file = new File(GET_FILE+file_name);
		Print("Received <----- REQ packet for file: "+file_name, log_out, df);
		if(!file.exists())
		{
			Packet temp = new Packet(3, now_ack_id, 0, new byte[0]);
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(temp);
			buf1 = baos.toByteArray();
			datagramPacket1.setData(buf1, 0, buf1.length);
			datagramSocket1.send(datagramPacket1);
			total_all_packge ++;
			Print("File "+file_name + " not exist", log_out, df);
			finished = true;
			baos.close();oos.close();baos=null;oos=null;
		}
		else 
		{
			Print("Established connection with: "+LOCAL_ID4+", Port:"+PORT, log_out, df);
			readfile = new RandomAccessFile(file, "r");
			Print(" Sending File: "+file_name+", Size: "+readfile.length()+"(bytes)", log_out, df);
		}
		int wfirst = 1;
		int wlast = wfirst+3;
		int nowsend = 1;
		int time =0;
		int ack_id = 2;
		        
		while(!finished)
		{
			try
			{
				datagramSocket2.setSoTimeout(2);
				datagramSocket2.receive(datagramPacket2);
			}catch(SocketTimeoutException e)
			{
				if(nowsend <= wlast )
				{
					time = 0;
					byte[] buf= new byte[40];
					int num=readfile.read(buf,0,16);
					// sending data to client
					if(num != -1)
					{
						total_data +=num;
						total_all_packge++;
						byte b[]=new byte[num];
						for(int i=0;i<num;i++) b[i]=buf[i];
						p=new Packet(2, nowsend, num, b);
						Print("Sending -----> DAT packet seqNo: "+p.getSeqNo()+", containing "+p.getSize()+"(bytes) of the file. ", log_out, df);
						nowsend++;
						if(op_id == 1){op_id =0;continue;}
						else if (op_id == 2){continue;}
						else {op_id =0;
							baos = new ByteArrayOutputStream();
							oos = new ObjectOutputStream(baos);
							oos.writeObject(p);
							buf1 = baos.toByteArray();
							datagramPacket1.setData(buf1, 0, buf1.length);
							datagramSocket1.send(datagramPacket1);
							baos.close();oos.close();baos = null; oos= null;
							
						}
					}
					// num == -1  data is over
					else 
					{
						total_all_packge++;
						p=new Packet(4, nowsend, 0, new byte[0]);
						Print("Sending -----> EOT packet\n Data transmission complete, waiting for outstanding ACKs", log_out, df);
						if(last_num_ack == -1)  last_num_ack = nowsend ;
						nowsend++;
						wlast = nowsend -1;
						if(op_id == 2 || op_id == 1){continue;}
						baos = new ByteArrayOutputStream();
						oos = new ObjectOutputStream(baos);
						oos.writeObject(p);
						buf1 = baos.toByteArray();
						datagramPacket1.setData(buf1, 0, buf1.length);
						datagramSocket1.send(datagramPacket1);
						baos.close();oos.close();baos = null; oos= null;
					}
				}
				else
				{
					time++;
					if(time%50 == 0)
					{
						send_again = nowsend - wfirst;
						nowsend = wfirst;
						readfile.seek((wfirst-1)*16);
					}
				}
				op_id = 0;
				continue;
			}
			// to use for converting object into stream
			bais = new ByteArrayInputStream(buf2);
			ois = new ObjectInputStream(bais);
			p = (Packet) ois.readObject();
			bais.close();
			ois.close();
			bais = null;ois = null;
			total_all_packge ++;
		        	
			log_out.write("client-->server:"+p.toString()+"\n");
			log_out.flush();
			switch(p.getType())
			{
			case 0:
				get_ack ++ ;
				Print("Received <----- ACK packet for sequence number: "+(p.getSeqNo()), log_out, df);
				if(last_num_ack == p.getSeqNo()) {finished=true;break;}
				if(ack_id < p.getSeqNo()+1) break;
				else if(ack_id == p.getSeqNo()+1 ) ack_id ++;
				else
					break;
				
				wfirst = p.getSeqNo()+1;
				if(last_num_ack == -1) wlast = wfirst +3;
				break;
			default:
				break;
			}
			
		}
		if(readfile != null)
		{
			Print("File sent. "+readfile.length()+"(bytes) ", log_out, df);
			readfile.close();
		}
		Print("the total number of data packets transmitted:"+total_all_packge+"    the total number of retransmissions:"+send_again+"   the total number of acknowledgments received:"+get_ack+"  data total:"+total_data, log_out, df);
		log_out.close();
		scanner.close();
		datagramSocket1.close();
		datagramSocket2.close();			
	}
}