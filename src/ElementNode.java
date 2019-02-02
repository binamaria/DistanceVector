//package com.askam;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ElementNode extends Thread implements DVchangeListener {
	
	private String name;
	private String myUDP;
	private String node_filename = "C:\\Users\\richardthomas\\Desktop\\Spring 2018\\CN\\Project4Shikha\\";
	private BufferedWriter bw = null;
	private boolean isTableUpdated = false;
	List<ElementNode> nodes = new ArrayList<ElementNode>();
	List<Distance> distances = new ArrayList<Distance>();
	Map<String, Integer> costMap = new HashMap<String, Integer> ();
	List<CostDetails> costs = new ArrayList<CostDetails> ();
	
	StringBuffer nodesHeading = new StringBuffer("Nodes\t\t\t\t");
	
	public ElementNode(String name, String myUDP) {
		this.name = name;
		this.myUDP = myUDP;
		node_filename = node_filename + name+ "_output.txt";
		try {
			bw = new BufferedWriter(new FileWriter(node_filename));	
			System.out.println("Printtint to " + node_filename);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	public void initialize() {
		// Creating the file to write the update from each node
		try {
			
			bw.write("Output file for:" + name+ "\t\r\n");
			bw.write("\n\nInitializing Node: "+ name+ "\t\r\n");
			bw.write("********************"+ "\t\r\n");	
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		
		System.out.println("\n\nInitializing Node: "+ name+"\t");
		System.out.println("********************");
		
		StringBuffer costToNodes = new StringBuffer("Cost \t\t\t\t");
		StringBuffer nextHop = new StringBuffer("Next Hop \t\t\t");
		StringBuffer sourceUDP = new StringBuffer("Source UDP \t\t\t");		
		StringBuffer destinationUDP = new StringBuffer("Destination UDP \t\t");
		StringBuffer updateTime = new StringBuffer("Last Update \t\t\t");
		
		for(ElementNode eachNode: nodes) {
			nodesHeading = nodesHeading.append(eachNode.name + "\t\t");
			CostDetails cd = new CostDetails();
			cd.source = this.name;
			cd.dest = eachNode.name;
			cd.dUDP = eachNode.myUDP;
			if (this.name.equalsIgnoreCase(eachNode.name)) {
				
				cd.cost = 0;
				cd.via = "-";
				cd.updTime = "00:00:00";
			}
				
			else {
				cd.cost = 999;
				cd.via = "-";	
				cd.updTime = "00:00:00";
			}
			
			if (cd.cost == 999)
				costToNodes = costToNodes.append( "\u00a4\t\t");
			else
				costToNodes = costToNodes.append( cd.cost + "\t\t");

			costs.add(cd);
			nextHop.append(cd.via + "\t\t");
			sourceUDP.append(myUDP + "\t\t");
			destinationUDP.append(cd.dUDP + "\t\t");
			updateTime.append(cd.updTime + "\t");
		}
		try {
			bw.write(nodesHeading.toString()+ "\t\r\n");
			bw.write("------------------------------------------------------------------------------------------------------------------------\r\n");
			bw.write(costToNodes.toString()+ "\r\n");	
			bw.write(nextHop.toString()+ "\r\n");
			bw.write(sourceUDP.toString()+ "\t\r\n");
			bw.write(destinationUDP.toString()+ "\t\r\n");
			bw.write(updateTime.toString()+ "\t\r\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		System.out.println(nodesHeading.toString());
		System.out.println("--------------------------------------------------------------------------------------------------------------------");
		System.out.println(costToNodes.toString());	
		System.out.println(nextHop.toString());
		System.out.println(sourceUDP.toString());
		System.out.println(destinationUDP.toString());
		System.out.println(updateTime.toString());
	}
	
	public void iterateFirst() {
		try {
			bw.write("\n\nIteration at Node: "+ name+ "\r\n");
			bw.write("--------------------------------------------------------------------------------------------------------------------------\r\n");
			bw.write(nodesHeading.toString()+ "\r\n");
			bw.write("--------------------------------------------------------------------------------------------------------------------------\r\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		System.out.println("\n\nIteration at Node: "+ name);
		System.out.println("----------------------------------------------------------------------------------------------------------");
		System.out.println(nodesHeading.toString());
		System.out.println("----------------------------------------------------------------------------------------------------------");
		
		StringBuffer costToNodes = new StringBuffer("Cost \t\t\t");
		StringBuffer nextHop = new StringBuffer("Next Hop \t\t\t");
		StringBuffer sourceUDP = new StringBuffer("Source UDP \t\t\t");	
		StringBuffer destinationUDP = new StringBuffer("Destinantion UDP \t\t");
		StringBuffer updateTime = new StringBuffer("Last Update \t\t\t");
		
		for(CostDetails cd: costs) {
			
			if (! cd.dest.equalsIgnoreCase(this.name)) {
				
				for (Distance d:distances){
					
					if (d.source.equalsIgnoreCase(this.name) && d.destination.equalsIgnoreCase(cd.dest)) {
						if (d.distance < cd.cost)
							cd.cost = d.distance;
						cd.via = d.destination;
						//cd.sUDP = d.sUDP;
						cd.updTime = giveTime();
						isTableUpdated = true;
					}
					if (d.destination.equalsIgnoreCase(this.name) 
&& d.source.equalsIgnoreCase(cd.dest)) {
						if (d.distance < cd.cost)
							cd.cost = d.distance;
						cd.via = d.source;
						//cd.sUDP = d.sUDP;
						cd.updTime = giveTime();
						isTableUpdated = true;
					}
				}
			}
			if (cd.cost == 999)
				costToNodes = costToNodes.append( "\u00a4\t\t");
			else
				costToNodes = costToNodes.append( cd.cost + "\t\t");
			nextHop.append(cd.via + "\t\t");
			sourceUDP.append(myUDP + "\t\t");
			destinationUDP.append(cd.dUDP + "\t\t");	
			updateTime.append(cd.updTime + "\t");
		}
		try {
			bw.write(costToNodes.toString()+ "\r\n");	
			bw.write(nextHop.toString()+ "\r\n");
			bw.write(sourceUDP.toString()+ "\r\n");
			bw.write(destinationUDP.toString()+ "\r\n");
			bw.write(updateTime.toString()+ "\r\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		System.out.println(costToNodes.toString());	
		System.out.println(nextHop.toString());
		System.out.println(sourceUDP.toString());
		System.out.println(destinationUDP.toString());
		System.out.println(updateTime.toString());
	}
	public void run() {
		sendVectorToNeighbours();
		while(true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			periodicCheck();
		}
	}
	
	public void sendVectorToNeighbours() {

		List<CostDetails> propList = new ArrayList<CostDetails> ();
		for(CostDetails cd : costs) {
			if (cd.cost != 999) {
				propList.add(cd);
			}
		}
		new Initiator().triggerFunc(propList);
	}
    @Override
    public synchronized void updateDistanceVector(List<CostDetails> propList) {

    	int costToNeighbour = 0;
    	String propSource = propList.get(0).source;
    	boolean isNeighbour = false;
    	//if (propSource.equalsIgnoreCase(this.name))
    	//	return;
    	for (CostDetails myCd: costs) {
    		if (myCd.dest.equalsIgnoreCase(propSource) && myCd.cost != 999 && myCd.via.equalsIgnoreCase(propSource)){
    			isNeighbour = true;
    			
    			costToNeighbour = myCd.cost;
    			//break;
    		}
    	}
    	
    	if (isNeighbour) {
    		for (CostDetails myCd: costs) {
    			for (CostDetails propCd: propList) {		
    				if (myCd.dest.equalsIgnoreCase(propCd.dest)) {

    					if (myCd.cost>(costToNeighbour+propCd.cost)) {
    						
    						System.out.println("Updating cost at " + propList.get(0).source +" to neighbour:" + propList.get(0).dest);
    						myCd.cost = costToNeighbour + propCd.cost;
    						myCd.via = propCd.source;
    						myCd.sUDP = propCd.sUDP;
    					}
    				}
    			}
    		}
    	}
    	try {
    		bw.write("\n\n\r\nIteration at Node: "+ name + "\r\n");
    		bw.write("*********************\r\n");
    		bw.write(nodesHeading.toString()+ "\r\n");
    		bw.write("------------------------------------------------------------------------------------------------------------\r\n");
    		bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		System.out.println("\n\nIteration at Node: "+ name );
		System.out.println("*********************");
		System.out.println(nodesHeading.toString());
		System.out.println("-----------------------------------------------------------------------------------------------------------------");
		
		StringBuffer costToNodes = new StringBuffer("Cost \t\t\t\t");
		StringBuffer nextHop = new StringBuffer("Next Hop \t\t\t");
		StringBuffer sourceUDP = new StringBuffer("Source UDP \t\t\t");
		StringBuffer destinationUDP = new StringBuffer("Destination UDP \t\t");
		StringBuffer updateTime = new StringBuffer("Last Update \t\t\t");
		
		for (CostDetails myCd: costs) {
			if (myCd.cost == 999)
				costToNodes=costToNodes.append( "\u00a4\t\t");
			else
				costToNodes=costToNodes.append( myCd.cost + "\t\t");
			nextHop.append(myCd.via + "\t\t");
			sourceUDP.append(myUDP + "\t\t");
			destinationUDP.append(myCd.dUDP + "\t\t");
			updateTime.append(giveTime() + "\t"); 
		}
		try {
			bw.write(costToNodes.toString()+ "\r\n");	
			bw.write(nextHop.toString()+ "\r\n");
			bw.write(sourceUDP.toString()+ "\r\n");
			bw.write(destinationUDP.toString()+ "\r\n");
			bw.write(updateTime.toString()+ "\r\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		System.out.println(costToNodes.toString());	
		System.out.println(nextHop.toString());
		System.out.println(sourceUDP.toString());
		System.out.println(destinationUDP.toString());
		System.out.println(updateTime.toString());
		
    }
    
    public void periodicCheck() {
    	if (isTableUpdated) {
    		isTableUpdated = false;
    		sendVectorToNeighbours();
    	}
    }
   public String giveTime() {
	   Date now = new Date();		      
	      SimpleDateFormat dateFormatter = new 
			SimpleDateFormat("hh:mm:ss");
	      return dateFormatter.format(now);
   }
}

