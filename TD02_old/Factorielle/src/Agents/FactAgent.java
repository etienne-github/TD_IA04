/**
 * 
 * @author Etienne Girot
 *									 Implementation of class FactAgent
 *
 *	Organize the calculation of an integer factorial.
 */
package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;


public class FactAgent extends Agent {
	
	/**
	 * 	CLASS VARIABLES
	 * 
	 * 	base : stores the integer whose factorial has to be calculated.
	 * 	temp : stores the inbetween results of the factorial calculation.
	 * 	step : stores the current states of the agent state machine. 
	 * 	FA_states : declaration of the different states composing by the state machines.
	 */
	private int base;
	private int temp;
	private FA_states step;
	private AID receiver;

	public enum FA_states {
		NOT_DEF,
		WAIT_INIT,
		ORGANIZE_CALC,
		WAIT_ANSWER
	}
	
	/**
	 * 
	 * GETTERS AND SETTERS
	 * 
	 */
	public int getBase(){
		return this.base;
	}
	
	public void setBase(int value){
		this.base=value;
	}
	public AID getReceiver(){
		return this.receiver;
	}
	
	private void setReceiver() {
			
		DFAgentDescription template =
		new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Operations");
		sd.setName("Multiplication");
		template.addServices(sd);
		try {
			DFAgentDescription[] result =
			DFService.search(this, template);
			if (result.length > 0) {
			receiver = result[0].getName();
			}
		}
		catch(FIPAException fe) {
			System.err.println("Error - Setting receiver : " + fe.getMessage());
		}
		
	}
	
	public int getTemp(){
		return this.temp;
	}
	
	public void setTemp(int value){
		this.temp=value;
	}
	
	public void setStep(FA_states waitAnswer)
	{
		this.step= waitAnswer;
	}
	
	public FA_states getStep()
	{
		return this.step;
	}
	
	/**
	 *  OTHER METHODS
	 */
	
	/**
	 * 	setup()
	 * 	Initializes the agent.
	 * 	Sets its internal states to WAIT_INIT and start up the behaviours.
	 * 	
	 */
	protected void setup(){
		
		/* retrieve arguments*/
		Object[] args = getArguments();
		
		/*advice user of agent creation*/
		System.out.println(getLocalName()+" : Agent created.");
		
		/* initialize state machine*/
			step=FA_states.WAIT_INIT;
		
		/*Behaviours startup*/
			addBehaviour(new WaitMessageBehaviour());
			addBehaviour(new OrganizeCalcBehaviour());

}	
	/**
	 * 
	 * BEHAVIOUR METHODS
	 *
	 */

	/**
	 * 
	 * 	WaitMessageBehaviour
	 * 
	 * 	If in WAIT_INIT state and REQUEST incoming message, initialize the base variable and switch to ORGANIZE_CALC state.
	 * 	If in WAIT_ANSWER state and INFORM incoming message, update temp variable and switch to ORGANIZE_CALC state.
	 * 	If in WAIT states and no incoming message, blocked.
	 * 	Otherwise, do nothing. 
	 * 
	 */
	
	protected class WaitMessageBehaviour extends Behaviour{
		@Override
		public void action() {
			
			/*retrieve agent's internal state*/
			FA_states state = ((FactAgent)myAgent).getStep();
			
			/*If waiting for messages*/
			if ((state==FA_states.WAIT_ANSWER)||(state==FA_states.WAIT_INIT))
			{
				/* Interrogate mail box*/
				ACLMessage message = myAgent.receive();
				
				/* if incoming messages*/
				if (message!=null)
				{
					int perforative = message.getPerformative();
					String content = message.getContent();
					
					/* Act in accordance with message type */
					switch(perforative)
					{
					
						/* if REQUEST message*/
						case ACLMessage.REQUEST:
							
							/* advice of message received*/
							System.out.println(myAgent.getLocalName()+" : receives the request : " + content);
							
							/*Initialize calculation*/
							((FactAgent) myAgent).setBase(Integer.parseInt(content));
							((FactAgent) myAgent).setTemp(Integer.parseInt(content));
							
							/*Switch to ORGANIZE_CALC state*/
							((FactAgent) myAgent).setStep(FA_states.ORGANIZE_CALC);
							
							/*restart calculation behaviour*/
							myAgent.addBehaviour(new OrganizeCalcBehaviour());
							break;
						
						/*if INFORM message*/
						case ACLMessage.INFORM:
							
							/* advice of message received*/
							System.out.println(myAgent.getLocalName()+" : receives the message : " + content);
							
							/*update temp variable*/
							((FactAgent) myAgent).setTemp(Integer.parseInt(content));
							
							/*Switch to ORGANIZE_CALC state*/
							((FactAgent) myAgent).setStep(FA_states.ORGANIZE_CALC);
							break;

						default:
							System.err.println("Error occured : " + content);
							break;
					}
				} else{
					/*if no messages wait for it*/
						System.err.println(myAgent.getLocalName()+" : blocks waiting for message");
					block();
					
				}
			}
					
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	
	/**
	 * 
	 * OrganizeCalcBehaviour
	 * 
	 * If in ORGANIZE_CALC state,
	 * 		If base <=1 returns temp, and switch to WAIT_INIT state
	 *  	else decrements base and ask MultAgent to calculate temp x base then switches to WAIT_ANSWER state
	 * 
	 *
	 */
	
	@SuppressWarnings("serial")
	protected class OrganizeCalcBehaviour extends Behaviour{

		
		@Override
		public void action() {	
			
			/*if in ORGANIZE_CALC*/
			if(((FactAgent)myAgent).getStep()==FA_states.ORGANIZE_CALC){
				
			/*if base lower than 1*/
			if (((FactAgent) myAgent).getBase()<=1){
				
				/*return result and swith to WAIT_INIT STATE*/
				System.out.println(myAgent.getLocalName()+" : result is :" + Integer.valueOf(((FactAgent) myAgent).getTemp()));
				((FactAgent)myAgent).setStep(FA_states.WAIT_INIT);
				
			} else {
					/*Decrement base*/
					((FactAgent) myAgent).setBase(((FactAgent) myAgent).getBase()-1);
					

					/*Prepare multiplication request*/
					ACLMessage multRequest = new ACLMessage(ACLMessage.REQUEST);
					setReceiver();
					receiver = ((FactAgent) myAgent).getReceiver();
					if (receiver != null){
						multRequest.addReceiver(receiver);
						
						/*With content Temp x base*/
						multRequest.setContent(String.valueOf(((FactAgent) myAgent).getTemp())+" x "+String.valueOf(((FactAgent) myAgent).getBase()));
					
						/*Switch to WAIT_ANSWER state*/
						((FactAgent)myAgent).setStep(FA_states.WAIT_ANSWER);
						
						/*Send request*/
						myAgent.send(multRequest);
						
						/*Advice user of message send*/
							System.out.println(myAgent.getLocalName()+" : sends requests : " + String.valueOf(((FactAgent) myAgent).getTemp())+" x "+String.valueOf(((FactAgent) myAgent).getBase()));			

					}
					
					else{
							System.out.println(
							myAgent.getLocalName() + "--> No receiver");
							}

				}
			}
			
		}

		@Override
		/*Deactivate behavior if switched in WAIT_INIT state*/
		public boolean done() {
			
			return (((FactAgent)myAgent).getStep()==FA_states.WAIT_INIT);
		}
	}


		
}


//				attendre message initial 
//							|
//							| message re�u
//							v
//	        fin <--- organisation calcul <-----+
//							|				   |
//							| requete mult.    |
//							v				   | rŽponse re�ue
//					  attente reponse ---------+
