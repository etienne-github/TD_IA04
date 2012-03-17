/**
 * 
 * @author Etienne Girot
 *									 Implementation of class FactAgent
 *
 *	Organize the calculation of an integer factorial.
 */
package Agents;

import org.json.JSONException;
import org.json.JSONObject;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class FactAgent extends Agent {
	
	/**
	 * 	CLASS VARIABLES
	 * 
	 * 	base : stores the integer whose factorial has to be calculated.
	 * 	temp : stores the inbetween results of the factorial calculation.
	 * 	step : stores the current states of the agent state machine. 
	 * 	FA_states : declaration of the different states composing by the state machines.
	 */

	public enum FA_states {
		NOT_DEF,
		WAIT_INIT,
		ORGANIZE_CALC,
		WAIT_ANSWER
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
		System.out.println(getLocalName()+" : Agent created with initial request "+(String)args[0]+"!.");
		this.addBehaviour(new OrganizeCalcBehaviour((String)args[0]));

		
		
		/*Behaviours startup*/
			addBehaviour(new WaitMessageBehaviour());

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
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			
			
			ACLMessage message = myAgent.receive(mt);
			
			/* if incoming messages*/
			if (message!=null)
			{
				int perforative = message.getPerformative();
				String content = message.getContent();
				
				/* Act in accordance with message type */
				switch(perforative)
				{
			
			
						//TODO SWITCH ETC
					case ACLMessage.REQUEST:
						
						/* advice of message received*/
						System.out.println(myAgent.getLocalName()+" : receives the request  " + content+"!.");
						myAgent.addBehaviour(new OrganizeCalcBehaviour(content));
						break;
						
					default:
						break;
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

		private int req;
		private int base;
		private int temp;
		private FA_states step;
		private AID receiver;
		private String id;
		private String currentMirt;
		private JSONObject jO;
		public OrganizeCalcBehaviour(String content) {
			req = Integer.parseInt(content);
			base = Integer.parseInt(content);
			temp = Integer.parseInt(content);
			step=FA_states.ORGANIZE_CALC;
			id = String.valueOf(content) + System.currentTimeMillis();
			
			
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
				DFService.search(myAgent, template);
				if (result.length > 0) {
					receiver = result[(int) Math.round(Math.random())].getName();
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
		
		@Override
		public void action() {	
			
			/*-------------------------------------------------------*/

			
			/*retrieve agent's internal state*/
			FA_states state = this.getStep();
			
			/*If waiting for messages*/
			if (state==FA_states.WAIT_ANSWER)
			{
				
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchConversationId(id),
						MessageTemplate.MatchInReplyTo(currentMirt)
						);
				mt.MatchPerformative(ACLMessage.INFORM);

				/* Interrogate mail box*/
				
				//ACLMessage message = myAgent.receive();
				ACLMessage message = myAgent.receive(mt);
				
				/* if incoming messages*/
				if (message!=null)
				{
					int perforative = message.getPerformative();
					String content = message.getContent();
					
					/* Act in accordance with message type */
					switch(perforative)
					{
					
						/* if REQUEST message*/

						
						/*if INFORM message*/
						case ACLMessage.INFORM:
							
							/* advice of message received*/
							System.out.println(myAgent.getLocalName()+" : receives from "+message.getSender().getLocalName()+ " the answer \"" + content+"\".");
							
							/*update temp variable*/
							this.setTemp(Integer.parseInt(content));
							
							/*Switch to ORGANIZE_CALC state*/
							this.setStep(FA_states.ORGANIZE_CALC);
							break;

						default:
							System.err.println(myAgent.getLocalName()+" : Error occured : " + content);
							break;
					}
				} else{
					/*if no messages wait for it*/
					block();
					
				}
			}
					

			
			/*if in ORGANIZE_CALC*/
			if(this.getStep()==FA_states.ORGANIZE_CALC){
				
			/*if base lower than 1*/
			if (this.getBase()<=1){
				
				/*return result and swith to WAIT_INIT STATE*/
				System.out.println(myAgent.getLocalName()+" : result of "+ String.valueOf(req) +"! is " + Integer.valueOf(this.getTemp())+".");
				this.setStep(FA_states.WAIT_INIT); //TODO delete behaviour
				
			} else {
					/*Decrement base*/
					this.setBase(this.getBase()-1);
					

					/*Prepare multiplication request*/
					ACLMessage multRequest = new ACLMessage(ACLMessage.REQUEST);
					setReceiver();
					receiver = this.getReceiver();
					if (receiver != null){
						multRequest.addReceiver(receiver);
						
						/*With content Temp x base*/
						
						//JSON
						try {
							jO = new JSONObject();
							jO.put("operande1", this.getTemp());
							jO.put("operande2", this.getBase());
							multRequest.setContent(jO.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
						//WITHOUT JSON
						//multRequest.setContent(String.valueOf(this.getTemp())+" x "+String.valueOf(this.getBase()));
					
						/*Switch to WAIT_ANSWER state*/
						this.setStep(FA_states.WAIT_ANSWER);
						
						
						
						//IDENTIFIER LA REQUEST EN TANT QUE CONVERSATION
						multRequest.setConversationId(id);
						currentMirt = id+System.currentTimeMillis();
						multRequest.setReplyWith(currentMirt);

						
						/*Send request*/
						myAgent.send(multRequest);
						
						/*Advice user of message send*/
							System.out.println(myAgent.getLocalName()+" : sends requests \"" + String.valueOf(this.getTemp())+" x "+String.valueOf(this.getBase())+"\" to "+receiver.getLocalName()+" for calculation of "+String.valueOf(req)+"!.");			

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
			
			return (this.getStep()==FA_states.WAIT_INIT);
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
