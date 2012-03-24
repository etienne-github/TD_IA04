package Agents;

import java.awt.Color;
import java.beans.PropertyChangeSupport;

import javax.swing.JList;

import Agents.ChatWindow.InstantMessagePane;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.ConversationList;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

public class ChatAgent extends GuiAgent {
	
	public static int TEXT_EVENT = 0;
	PropertyChangeSupport changes = new PropertyChangeSupport(this);
	Color myColor;
	
	
	protected void setup(){
		/* retrieve arguments*/
		Object[] args = getArguments();
		myColor= lookupColor((String) args[0]);
		/*advice user of agent creation*/
		
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Communication");
		sd.setName("Chat");
		dfd.addServices(sd);
		System.out.println(getLocalName()+" : Agent created.");
		
		
		DFAgentDescription template = new DFAgentDescription();

		sd.setType("Communication");
		sd.setName("Chat");
		template.addServices(sd);
		try {
			DFService.register(this, dfd);
			new ChatRoom(this,myColor,changes);
			this.addBehaviour(new ReceiveConnectionBehaviour());
			this.addBehaviour(new RefreshChatRoomBehaviour());
			
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		ACLMessage connectionNotification = new ACLMessage(ACLMessage.PROPAGATE);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			if (result.length > 0) {
					for(int i=0;i<result.length;i++){
						connectionNotification.addReceiver(result[i].getName());
						send(connectionNotification);
					}

				}
			}
	catch(FIPAException fe) {
				System.err.println("Error - Setting receiver : " + fe.getMessage());

		}
		
		
		
	}
	
	
    private static Color lookupColor(String color) {
        String s = color.toLowerCase();
	          if (s.equals("black")) {
            return Color.black;
        } else if (s.equals("blue")) {
            return Color.blue;
        } else if (s.equals("cyan")) {
            return Color.cyan;
        } else if (s.equals("darkgray")) {
            return Color.darkGray;
        } else if (s.equals("darkgrey")) {
            return Color.darkGray;
        } else if (s.equals("gray")) {
            return Color.gray;
        } else if (s.equals("grey")) {
            return Color.gray;
        } else if (s.equals("green")) {
            return Color.green;
        } else if (s.equals("lightgray")) {
            return Color.lightGray;
        } else if (s.equals("lightgrey")) {
            return Color.lightGray;
        } else if (s.equals("magenta")) {
            return Color.magenta;
        } else if (s.equals("orange")) {
            return Color.orange;
        } else if (s.equals("pink")) {
            return Color.pink;
        } else if (s.equals("red")) {
            return Color.red;
        } else if (s.equals("white")) {
            return Color.white;
        } else if (s.equals("yellow")) {
            return Color.yellow;
        } else {
            return Color.black;
        }

    }
    
    
    protected void takeDown() 
    {
       try { DFService.deregister(this); }
       catch (Exception e) {}
    }
	
	public PropertyChangeSupport getSupport(){
		System.out.println("hola");
		return changes;
	}
	
	
	public void openChatWithAgent(String agent){
		
		ACLMessage Connectquery = new ACLMessage(ACLMessage.QUERY_IF);
		Connectquery.addReceiver(new AID(agent,AID.ISLOCALNAME));
		this.send(Connectquery);
		System.out.println(this.getLocalName()+": connect query sent to "+agent+".");
		
		
	}
	
	public  myListModel getChatAgents(myListModel model){
		
		System.out.println(this.getLocalName()+": retrieving ChatAgents.");

		
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Communication");
		sd.setName("Chat");
		template.addServices(sd);
		model.clear();
		try {
				DFAgentDescription[] result = DFService.search(this, template);
				if (result.length > 0) {
						for(int i=0;i<result.length;i++){
							if(result[i].getName().getLocalName().compareTo(this.getLocalName()) != 0){
								//System.out.println("je check : "+result[i].getName().getLocalName().toString()+"Je suis : "+this.getLocalName().toString()+"retourne : "+String.valueOf((result[i].getName().getLocalName().toString())!=(this.getLocalName().toString())));
								model.addElement(result[i].getName().getLocalName().toString());
							}
						}
						return model;
					}
				}
		catch(FIPAException fe) {
					System.err.println("Error - Setting receiver : " + fe.getMessage());
					return model;
			}
		return model;
	}
	
	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		// Tester le type (il peut y en avoir plusieurs)
		if (arg0.getType() == TEXT_EVENT) {
			
			// Récupération de l’information
			String line = arg0.getParameter(0).toString();
			System.out.println(this.getLocalName()+" : texte reçu");
			System.out.println(line);
			// Instructions
			
			ACLMessage iMessage = new ACLMessage(ACLMessage.INFORM);
			
			iMessage.setConversationId(arg0.getParameter(1).toString());
			iMessage.setContent(line);
			iMessage.addReceiver((AID)arg0.getParameter(2));
			send(iMessage);
			
		}
		
	}
	
	class TextConversationBehaviour extends CyclicBehaviour{
		
		String myConversationID;
		AID receiver;

		public TextConversationBehaviour(ChatAgent mA, AID receiver) {
			
			myConversationID = mA.getAID().toString()+"-"+receiver.toString();
			System.out.println("ID : "+myConversationID);
			this.receiver=receiver;
			ChatWindow cw = new ChatWindow(myConversationID,receiver,this, mA.getLocalName().toString(),mA, receiver.getLocalName().toString(),mA.getSupport(),myColor);
			changes.addPropertyChangeListener(cw.conversation);
		}
		
		public TextConversationBehaviour(AID sender, ChatAgent mA) {
			myConversationID = sender.toString()+"-"+mA.getAID().toString();
			System.out.println("ID : "+myConversationID);
			this.receiver=sender;
			ChatWindow cw = new ChatWindow(myConversationID,receiver,this,mA.getLocalName().toString(),mA,sender.getLocalName().toString(),mA.getSupport(),myColor);
			changes.addPropertyChangeListener(cw.conversation);
		}

		public PropertyChangeSupport getAgentSupport(){
			return  ((ChatAgent) myAgent).getSupport();
		}
		
		@Override
		public void action() {
			
			MessageTemplate mt1 = MessageTemplate.MatchConversationId(myConversationID);
			MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			
			//message provenant de MultAgent
			MessageTemplate mt3 = MessageTemplate.MatchConversationId(myConversationID);
			MessageTemplate mt4 = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
			
			MessageTemplate mt5 = MessageTemplate.and(mt1, mt2);
			MessageTemplate mt6 = MessageTemplate.and(mt3, mt4);
			
			ACLMessage message2 = myAgent.receive(mt5);
			ACLMessage message = myAgent.receive(mt6);
			
			
			if(message != null){
				changes.firePropertyChange("resMult", null, message.getContent());
				/*ACLMessage iMessage = new ACLMessage(ACLMessage.INFORM);
				
				iMessage.setConversationId(myConversationID);
				iMessage.setContent("Le résultat de votre multiplication est "+message.getContent());
				iMessage.addReceiver(receiver);
				send(iMessage);*/
				
			}else if(message2!=null){
				
				changes.firePropertyChange("newIM", null, message2.getContent());
	
			}else{
				block();
			}
			
		}

		public void sendMultRequest(ACLMessage req) {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("Operations");
			sd.setName("Multiplication");
			template.addServices(sd);
			try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					if (result.length > 0) {
								req.addReceiver(result[0].getName());
							}
					req.setConversationId(this.myConversationID);
					myAgent.send(req);
			}
			catch(FIPAException fe) {
						System.err.println("Error - Setting receiver : " + fe.getMessage());
				}
			
		}


		
	}
	
	
	class RefreshChatRoomBehaviour extends Behaviour{

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
			ACLMessage message = myAgent.receive(mt);
			
			if(message!=null){
				
				changes.firePropertyChange("refreshContact", null, null);
				
			}else{
				block();
			}
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
		
		
	}
	
	class ReceiveConnectionBehaviour extends Behaviour {

		@Override
		public void action() {
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
			ACLMessage message = myAgent.receive(mt);
			
			MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
			ACLMessage message2 = myAgent.receive(mt2);
			
			if(message!=null){
				System.out.println(myAgent.getLocalName()+": connect query received from "+message.getSender().getLocalName()+".");
				myAgent.addBehaviour(new TextConversationBehaviour((ChatAgent) myAgent,message.getSender()));
				ACLMessage reply = message.createReply();	
				reply.setPerformative(ACLMessage.AGREE);
				myAgent.send(reply);				
			}else if(message2!=null){
				
			myAgent.addBehaviour(new TextConversationBehaviour(message2.getSender(),((ChatAgent) myAgent)));

				
			}else{
				System.out.println(myAgent.getLocalName()+": blocks.");
				block();
			}	
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}



}
