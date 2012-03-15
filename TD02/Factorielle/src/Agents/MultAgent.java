/**
 * 
 * @author Etienne Girot
 *									 Implementation of class MultAgent
 *
 *	Calculate multiplication.
 */

package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;


public class MultAgent extends Agent {
	
	
	/**
	 *  OTHER METHODS
	 */
	
	/**
	 * 	setup()
	 * 	Initializes the agent and set up the behaviors.
	 * 	.
	 * 	
	 */
	protected void setup(){
		/*Setup behaviour*/
		
		
	
		addBehaviour(new ReceiveRequest());
		
		/*advice user of creation*/
		System.out.println(getLocalName()+" : Agent created.");
	
		/* Enregistrement dans les pages jaunes */
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Operations");
		sd.setName("Multiplication");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}


	/**
	 * 
	 * BEHAVIOUR METHODS
	 *
	 */
	
	/**
	 * 
	 * 	GeneralMultBehaviour
	 * 
	 * 	If incoming message, answer it
	 * 	Otherwise wait for it.
	 * 
	 */

	
	protected class ReceiveRequest extends Behaviour {

		@Override
		public void action() {
			/*Check mail box*/

			ACLMessage message = myAgent.receive();
			
			/*if incoming message*/
			if (message != null)
			{
				
				/*Wait a moment... */
				int lower = 5000;
				int higher = 100000;
				long delay=(long) Math.random() * (higher-lower) + lower;
				System.out.println(myAgent.getLocalName()+" : received request : " + message.getContent());
				myAgent.addBehaviour(new CalculateMultBehaviour (myAgent,delay,message));
			} else {
				
				/*Otherwise wait for it*/
				block();
			}
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	
	protected class CalculateMultBehaviour extends WakerBehaviour{

		private ACLMessage pendingMessage;
		public CalculateMultBehaviour(Agent a, long timeout,ACLMessage pendingMessage) {
			super(a, timeout);
			this.pendingMessage=pendingMessage;
			// TODO Auto-generated constructor stub
		}





		public void onWake() {
			
			answer(pendingMessage);
			
		}

		/*Answer a multiplication request*/
		private void answer(ACLMessage message) {

			
			/*Get message content and prepare reply*/
			String msgContent = message.getContent();
			ACLMessage reply = message.createReply();
			
			/*if multiplication request*/
			if (msgContent.contains("x"))
			{

				
				/*retrieve operands from message "A x B" and set reply content to A*B */
				String[] parameters = msgContent.split("x");
				int result = Integer.parseInt(parameters[0].trim()) * Integer.parseInt(parameters[1].trim());
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent(String.valueOf(result));
				
				
					System.out.println(myAgent.getLocalName()+" : answers " + String.valueOf(result));
				
			/*if not multiplication request*/	
			} else {
				
				reply.setPerformative(ACLMessage.FAILURE);
				reply.setContent("Unknown operator");
			}
			
				myAgent.send(reply);

			/*Send reply*/
			
			
		}



		
	}
}
