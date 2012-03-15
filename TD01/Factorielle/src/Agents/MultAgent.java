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
		addBehaviour(new GeneralMultBehaviour());
		
		/*advice user of creation*/
		System.out.println(getLocalName()+" : Agent created.");
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

	
	
	protected class GeneralMultBehaviour extends Behaviour{

		@Override
		public void action() {
			
			/*Check mail box*/
			ACLMessage message = myAgent.receive();
			
			/*if incoming message*/
			if (message != null)
			{
				/*answer it*/
				answer(message);
			} else {
				
				/*Otherwise wait for it*/
				block();
			}
		}

		/*Answer a multiplication request*/
		private void answer(ACLMessage message) {

			/*Get message content and prepare reply*/
			String msgContent = message.getContent();
			ACLMessage reply = message.createReply();
			
			/*if multiplication request*/
			if (msgContent.contains("x"))
			{
				/*advice user of multiplication request, received*/
				System.out.println(myAgent.getLocalName()+" : received request : " + msgContent);
				
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
			/*Send reply*/
			myAgent.send(reply);
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
