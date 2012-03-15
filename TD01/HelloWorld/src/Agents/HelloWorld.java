package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class HelloWorld extends Agent{
	protected void setup()
	{
		System.out.println("Hello World !");
		//this.addBehaviour(new AllMessagesBehaviour());
		this.addBehaviour(new INFORMMessageBehaviour());
	}
	
	protected class INFORMMessageBehaviour extends Behaviour{

		@Override
		public void action() {
			ACLMessage message = myAgent.receive();
			if(message !=null)
			{
				int msgPerforative = message.getPerformative();
				if(msgPerforative==ACLMessage.INFORM)
				{
					String msgContent = message.getContent();	
					String msgSender = message.getSender().getLocalName();
					System.out.println(getLocalName() + " : Message of type INFORM received from "+msgSender+" with content \""+msgContent+"\".");
				}
			}
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	
	protected class AllMessagesBehaviour extends Behaviour{

		@Override
		public void action() {
			ACLMessage message = myAgent.receive();
			if (message != null)
			{
				String msgContent = message.getContent();
				int msgPerforative = message.getPerformative();
				String msgSender = message.getSender().getLocalName();
				System.out.println(getLocalName() + " : Message of type "+Integer.valueOf(msgPerforative)+" received from "+msgSender+" with content \""+msgContent+"\".");
			}
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
	
	}
	
	
}