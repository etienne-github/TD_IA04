package Agents;

import jade.core.AID;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.json.JSONException;
import org.json.JSONObject;

import Agents.ChatAgent.TextConversationBehaviour;

public class ChatWindow extends JFrame {
	
	ChatAgent magent;
	String myAgent;
	String RemoteAgent;
	
	String myConversationID;
	AID receiver;
	
	TextConversationBehaviour myBehaviour;
	
	InstantMessagePane conversation;
	JScrollPane conversationScroll;
	
	JTextPane input;
	JScrollPane inputScroll;
	
	JButton sendButton;
	
	public ChatWindow(String myConversationID, AID receiver, TextConversationBehaviour b,String myAgent,ChatAgent magent,String remoteAgent,PropertyChangeSupport supp,Color Colour){
			
		this.myAgent = myAgent;
		this.RemoteAgent = remoteAgent;
		this.myBehaviour = b;
		this.magent=magent;
		this.myConversationID=myConversationID;
		this.receiver=receiver;
		
		
		this.setSize(new Dimension(400,400));
		this.setTitle("Chat with "+remoteAgent);
		Container content = this.getContentPane();
		content.setLayout(new FlowLayout());
		
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setBackground(Colour);
		
		conversation= new InstantMessagePane();
		conversation.setEditable(false);
		conversationScroll = new JScrollPane(conversation);
		conversationScroll.setPreferredSize(new Dimension(350,150));
		
		input = new JTextPane();
		inputScroll = new JScrollPane(input);
		inputScroll.setPreferredSize(new Dimension(350,150));
		
		sendButton = new JButton("Envoyer");
		sendButton.setSize(new Dimension(350,25));
		sendButton.repaint();
		
		input.addKeyListener(new ActionJTextPane(input));
		
		sendButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = input.getText();
				conversation.setText(conversation.getText()+"\nMe :"+msg);
				input.setText("");
				
			}
			
			
		});
		
		
		content.add(conversationScroll,"North");
		content.add(inputScroll,"Center");
		content.add(sendButton,"South");

		
		this.setVisible(true);
	}
	
	private void toAgent(String info,String myConversationID,AID receiver) {
		GuiEvent ev = new GuiEvent(this,ChatAgent.TEXT_EVENT);
		ev.addParameter(info);
		ev.addParameter(myConversationID);
		ev.addParameter(receiver);
		magent.postGuiEvent(ev);
	}
	
	class InstantMessagePane extends JTextPane implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent arg0) {
			if(arg0.getPropertyName().compareTo("newIM") == 0){
				this.setText(this.getText()+"\n"+RemoteAgent+" : "+(String)arg0.getNewValue());
				String msg = new String((String) arg0.getNewValue());
				if(msg.contains("x")){
					String args[]=msg.split("x");
					JSONObject jO= new JSONObject();
					try {
						jO.put("operande1",args[0]);
						jO.put("operande2",args[1]);
						ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
						req.setContent(jO.toString());
						myBehaviour.sendMultRequest(req); 
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}
				else
				{
					//si le message reçu est un résultat de multiplication
					if (arg0.getPropertyName().compareTo("resMult") == 0)
					{
						
						ACLMessage iMessage = new ACLMessage(ACLMessage.INFORM);
						
						iMessage.setConversationId(myConversationID);
						iMessage.setContent("Le résultat de votre multiplication est "+arg0.getNewValue());
						iMessage.addReceiver(receiver);
						magent.send(iMessage);
					}
				}
			}
			
			
			
		}
		
	
	
	class ActionJTextPane extends KeyAdapter{
		  protected JTextPane jtP;
		    
		  public ActionJTextPane(JTextPane l){
		   jtP = l;
		   }
		 
		  @Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			super.keyReleased(e);
			if(e.getKeyCode()==KeyEvent.VK_ENTER){
				String msg = jtP.getText();
				msg = msg.substring(0,msg.length()-1);
				conversation.setText(conversation.getText()+"\nMe :"+msg);
				toAgent(msg,myConversationID,receiver);
				jtP.setText("");
			}
		}
		    
		  
		   
		}


}