//package com.askam;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class Initiator {
	
	int nodesCount = 0;
	int linksCount = 0;
	static List<ElementNode> nodes = new ArrayList<ElementNode>();
	static List<Distance> distances = new ArrayList<Distance>();

	public static void main(String[] args) throws Exception {
		Initiator myInitiator = new Initiator();
		myInitiator.initiate();
	}
	
	private void initiate() throws NumberFormatException, IOException, InterruptedException{
		System.out.println("Distance Vector Implementation\n");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		readFromFile();
		
		for(ElementNode n: nodes) {
			n.nodes = Initiator.nodes;
			n.distances = Initiator.distances;
			n.initialize();
			n.iterateFirst();
		}

		for(ElementNode n: nodes) {
			n.start();
		}
		
		Thread.sleep(50000);
		
		//System.out.println("To change the link cost between nodes, enter the new cost in the format: A,B,2");
		while(true) {
			String costChange = br.readLine();			
			String[] changeData = costChange.split(",");
			CostDetails updatedCostDetails = new CostDetails();
			updatedCostDetails.source = changeData[0];
			updatedCostDetails.dest = changeData[1];
			updatedCostDetails.cost = Integer.parseInt(changeData[2]);
			updatedCostDetails.via = changeData[0];
			updatedCostDetails.updTime = "0";
			
			List<CostDetails> temp = new ArrayList<CostDetails>();
			temp.add(updatedCostDetails);
			triggerFunc(temp);
			
		}
	}
    
	public synchronized void triggerFunc(List<CostDetails> propList) {
		
        for (ElementNode dvcl : nodes) {
        	dvcl.updateDistanceVector(propList);
	    	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
	}
	
	public void readFromFile(){
		String fileName = "C:\\Users\\richardthomas\\Desktop\\Spring 2018\\CN\\Project4Shikha\\temp.txt";
		String line = null;
		Set<String> nodeSet = new HashSet<String>();
		Map<String, String> sourceMap = new HashMap<String, String> ();
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			System.out.println("Reading the data file:"+ fileName);
			while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
				String[] inputline = line.split(",");
				
				if (inputline.length == 4) {
					String s = inputline[0];
					String d = inputline[1];
					String dUDP = inputline[2];
					int dis = Integer.parseInt(inputline[3]);
					Distance dist = new Distance(s, d, dUDP, dis);
					distances.add(dist);
					
					//nodeSet.add(s);
					nodeSet.add(d);
					sourceMap.put(d, dUDP);
				}
				else
					System.out.println("Please enter valid data");
            }
			bufferedReader.close();
			
			for (String a: nodeSet) {
				ElementNode node = new ElementNode(a, sourceMap.get(a));
				nodes.add(node);
			}
			System.out.println("Found "+ nodeSet.size()+" Nodes in file");
			System.out.println("The nodes are ");
			for (String nodeName: nodeSet){
				System.out.println(nodeName);
			}
			System.out.println("There are "+distances.size()+" links");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
