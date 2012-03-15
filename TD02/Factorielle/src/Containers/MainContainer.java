package Containers;

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
					
					try {
						
						AgentController MC_MultAgent = MC_MainContainer.createNewAgent("M1","Agents.MultAgent", new Object[]{50});
						MC_MultAgent.start();
					} catch (Exception eMultAgent){
						System.err.println("Error - MultAgent creation : " + eMultAgent.getMessage());
					}
					
					try {
						
						AgentController MC_MultAgent = MC_MainContainer.createNewAgent("M2","Agents.MultAgent", new Object[]{50});
						MC_MultAgent.start();
					} catch (Exception eMultAgent){
						System.err.println("Error - MultAgent creation : " + eMultAgent.getMessage());
					}
					
					try {
						AgentController MC_FactAgent = MC_MainContainer.createNewAgent("FactAgent","Agents.FactAgent",new String[]{"5"});
						MC_FactAgent.start();
					} catch (Exception eFactAgent){
						System.err.println("Error - FactAgent creation : " + eFactAgent.getMessage());
					}
					
					
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
