package Containers;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class MainContainer
{
	public static void main(String[] args){
		
		try {
			
			Runtime MC_Runtime = Runtime.instance();
			Profile MC_Profile = null;
			try {
				MC_Profile = new ProfileImpl("MainContainer.properties");	
				try {					

					AgentContainer MC_AgentContainer = MC_Runtime.createMainContainer(MC_Profile);					
					
					
					AgentController MC_HelloWorld = MC_AgentContainer.createNewAgent("HelloWorld","Agents.HelloWorld",new Object[]{50});
					MC_HelloWorld.start();
					
				} catch (Exception e3) {
					System.out.println("Error occured while creating the main Container.");
					System.out.println(e3.getMessage());
				}
							
			} catch (Exception e2) {
				System.out.println("Error occured while retrieving the profile.");
				System.out.println(e2.getMessage());
			}
			
		} catch (Exception e) {
			
			System.out.println("Error occured while creating the runtime instance.");
			System.out.println(e.getMessage());
		}
		
	}
}


