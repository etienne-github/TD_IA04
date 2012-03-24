package Agents;

import jade.util.leap.ArrayList;
import jade.util.leap.HashMap;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;


public class ChatRoom extends JFrame implements PropertyChangeListener {
	
	JList contactList;
	myListModel model;
	JScrollPane contactScroll;
	ChatAgent myAgent;

	
	public ChatRoom(ChatAgent myAgent,Color Colour,PropertyChangeSupport supp){
		
		this.myAgent=myAgent;
		
		this.setSize(new Dimension(200,600));
		this.setTitle(myAgent.getLocalName().toString());
		Container content = this.getContentPane();
		content.setLayout(new FlowLayout());
		
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setBackground(Colour);

        
        
		model = new myListModel();
		
		model = myAgent.getChatAgents(model);
		
		contactList = new JList(model); 
		
		
		contactScroll = new JScrollPane(contactList);
		contactScroll.setPreferredSize(new Dimension(150,550));
	
		contactList.addMouseListener(new ActionJList(contactList));
		
		content.add(contactScroll,"Center");
		
		this.setVisible(true);
		
		System.out.println(myAgent.getLocalName()+": ChatRoom view created.");
		

		supp.addPropertyChangeListener((PropertyChangeListener) this);
		
		
	}
	
	
	class ActionJList extends MouseAdapter{
		  protected JList list;
		    
		  public ActionJList(JList l){
		   list = l;
		   }
		    
		  public void mouseClicked(MouseEvent e){
		   if(e.getClickCount() == 2){
		     int index = list.locationToIndex(e.getPoint());
		     myListModel dlm = (myListModel) list.getModel();
		     Object item = dlm.getElementAt(index);;
		     list.ensureIndexIsVisible(index);
		     myAgent.openChatWithAgent(item.toString());
		     }
		   }
		}


	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if(arg0.getPropertyName().compareTo("refreshContact")==0){
			model = myAgent.getChatAgents(model);
			contactList.setModel(model);
			contactList.repaint();
		}
		
	}
	

}