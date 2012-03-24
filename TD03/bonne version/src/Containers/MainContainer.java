package Containers;

import Agents.ChatAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;


public class MainContainer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			Runtime MC_Runtime = Runtime.instance();
			
			try {
				
				Profile MC_Profile = new ProfileImpl("MainContainer.properties");
	
				try {
					
					AgentContainer MC_MainContainer = MC_Runtime.createMainContainer(MC_Profile);
					
					//PeriphericalContainer MC_PeriphericalContainer = new PeriphericalContainer();
					
					try{
						AgentController CA1 = MC_MainContainer.createNewAgent("Maxime", "Agents.ChatAgent",new String[]{"darkgrey"});
						CA1.start();
						
						AgentController CA2 = MC_MainContainer.createNewAgent("Etienne", "Agents.ChatAgent",new String[]{"darkgrey"});
						CA2.start();
						
						AgentController CM = MC_MainContainer.createNewAgent("MultAgent","Agents.MultAgent",null);
						CM.start();
						
						}
					catch(Exception eMultAgent){
						System.err.println("Error - MultAgent creation : " + eMultAgent.getMessage());

					}
					
					
					
					//MC_PeriphericalContainer.createNewAgent("ChatAgent2", "Agents.ChatAgent",new Object[50]);
					
					
					//TODO create agent here
					
					
					
					
				} catch (Exception eContainer){
					System.err.println("Error - MainContainer creation : " + eContainer.getMessage());
				}
				
			} 
			catch (Exception eProfile){
				System.err.println("Error - Retrieving profile : " + eProfile.getMessage());
			}
		
		} catch (Exception eRuntime){
			System.err.println("Error - Setting Runtime : " + eRuntime.getMessage());
		}
 

	}

}