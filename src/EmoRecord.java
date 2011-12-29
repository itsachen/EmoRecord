import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.*;
import java.io.*;
import java.util.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.awt.Desktop;

//Additional functionality added by Anthony Chen

/** Simple example of JNA interface mapping and usage. */
public class EmoRecord extends JFrame implements KeyListener, ItemListener, ActionListener, Runnable
{
	public boolean run= true;
	
	//Checkbox flags
	private boolean exciteshort= true;
	private boolean excitelong= true;
	private boolean engagement= true;
	private boolean frustration= true;
	private boolean meditation= true;
	private boolean locationset= false;//
	
	//Checkboxes
	private JCheckBox exciteshortcheck = new JCheckBox("Instantaneous Excitement");
	private JCheckBox excitelongcheck = new JCheckBox("Long Term Excitement");
	private JCheckBox engagementcheck = new JCheckBox("Engagement/Boredom");
	private JCheckBox frustrationcheck = new JCheckBox("Frustration");
	private JCheckBox meditationcheck = new JCheckBox("Meditation");
	
	//Panels
	private JPanel pnlEast = new JPanel(); //East section
	private JPanel pnlWest = new JPanel(); //West section 
	private JPanel pnlSouth = new JPanel(); //South section
	
	private GroupLayout layout = new GroupLayout(getContentPane());
	
	//Buttons
	private JButton startbutton = new JButton("Start Record");
	private JButton stopbutton= new JButton("Stop Record");
	private JButton saveas= new JButton("Save as...");
	
	//Text entries
	private JTextField namefield= new JTextField();
	private JTextField datefield= new JTextField();
	private JTextField timefield= new JTextField();
	
	//Labels
	private JLabel namelabel= new JLabel("Participant name:");
	private JLabel datelabel= new JLabel("Date:");
	private JLabel timelabel= new JLabel("Time:");
	
	//Save location
	private String filename= "output.csv"; //Default filename
	
	//GUI
    public EmoRecord() throws AWTException, InterruptedException{
    	
    	//JFrame initialization
    	setTitle("EmoState Record v1.1");
		setPreferredSize(new Dimension(360, 290));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Checkbox initialization
		exciteshortcheck.setSelected(true);
		excitelongcheck.setSelected(true);
		engagementcheck.setSelected(true);
		frustrationcheck.setSelected(true);
		meditationcheck.setSelected(true);
		
		//Setting LayoutManagers
		getContentPane().setLayout(new BorderLayout());
    	pnlEast.setLayout(new BoxLayout(pnlEast, BoxLayout.PAGE_AXIS));
    	pnlWest.setLayout(new BoxLayout(pnlWest, BoxLayout.PAGE_AXIS));
    	pnlSouth.setLayout(new BoxLayout(pnlSouth, BoxLayout.PAGE_AXIS));
    	
    	//Padding
    	pnlEast.setBorder(new EmptyBorder(10, 10, 10, 10));
    	pnlWest.setBorder(new EmptyBorder(10, 10, 10, 10));
    	pnlSouth.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		//Panel assignments
    	getContentPane().add(pnlWest, BorderLayout.WEST);
    	getContentPane().add(pnlEast, BorderLayout.EAST);
    	getContentPane().add(pnlSouth, BorderLayout.SOUTH);
    	
    	//Adding components
    	pnlWest.add(startbutton);
    	pnlWest.add(stopbutton);
    	pnlWest.add(saveas);
    	
    	pnlEast.add(exciteshortcheck);
    	pnlEast.add(excitelongcheck);
    	pnlEast.add(engagementcheck);
    	pnlEast.add(frustrationcheck);
    	pnlEast.add(meditationcheck);
    	
    	pnlSouth.add(namelabel);
    	pnlSouth.add(namefield);
    	pnlSouth.add(datelabel);
    	pnlSouth.add(datefield);
    	pnlSouth.add(timelabel);
    	pnlSouth.add(timefield);
    	
		//Listeners
    	addKeyListener(this);
		exciteshortcheck.addItemListener(this);
		excitelongcheck.addItemListener(this);
		engagementcheck.addItemListener(this);
		frustrationcheck.addItemListener(this);
		meditationcheck.addItemListener(this);
		startbutton.addActionListener(this);
		startbutton.setEnabled(true);
		stopbutton.addActionListener(this);
		stopbutton.setEnabled(true);
		saveas.addActionListener(this);
		saveas.setEnabled(true);
		namefield.addActionListener(this);
		datefield.addActionListener(this);
		timefield.addActionListener(this);
    	
		pack();
		setVisible(true);
    }
    
    /* Quits logging */
    public void keyPressed(KeyEvent e) {
    	if (e.getKeyChar() == 'q'){
            quit();
        }
	}
    
    /* Quit function */
    public void quit(){
    	run= false;
    }
    
    /* Start function */
    public void start(){
    	run= true;
    }
    
    /* Runs logging */
    public void run(){
    	try{
    	//Output file location
    	
    	FileWriter fstream= new FileWriter(filename);
		BufferedWriter out= new BufferedWriter(fstream);
		
		//Write log info
		if (!namefield.getText().equals("")){
			out.write(namefield.getText() + "\n");
		}
		if (!datefield.getText().equals("")){
			out.write(datefield.getText() + "\n");
		}
		if (!timefield.getText().equals("")){
			out.write(timefield.getText() + "\n");
		}
		
		
		//Categories
		out.write("Time");
		if (exciteshort) out.write(",Short Term Excitementt");
		if (excitelong)	out.write(",Long Time Excitement");
		if (engagement) out.write(",Engagement/Boredom");
		if (frustration) out.write(",Frustration");
		if (meditation)	out.write(",Meditation");
		out.write("\n");
		
		Pointer eEvent			= Edk.INSTANCE.EE_EmoEngineEventCreate();
    	Pointer eState			= Edk.INSTANCE.EE_EmoStateCreate();
    	IntByReference userID 	= null;
    	short composerPort		= 1726;
    	int option 				= 2;
    	int state  				= 0;
    	Short port= 3008;
    	
    	userID = new IntByReference(0);
    	
    	switch (option) {
		case 1:
		{
			if (Edk.INSTANCE.EE_EngineConnect("Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
				System.out.println("Emotiv Engine start up failed.");
				return;
			}
			break;
		}
		//Remote connect, port 3008 for Emotiv CP
		case 2:
		{
			System.out.println("Target IP of EmoComposer: [127.0.0.1] ");

			if (Edk.INSTANCE.EE_EngineRemoteConnect("127.0.0.1", port, "Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
				System.out.println("Cannot connect to EmoComposer on [127.0.0.1]");
				out.close(); //Cannot connect, close the buffer
				return;
			}
			System.out.println("Connected to EmoComposer on [127.0.0.1]");
			out.close(); //Cannot connect, close the buffer
			break;
		}
		default:
			System.out.println("Invalid option...");
			return;
    	}
    	
    	boolean first= true;
    	float init= 0;
    	long initsystem= 0;
    	double timestampsystem= 0;
    	float timestamp= 0;
    		
    	long former= -1;
    	
		while (run) 
		{
			state = Edk.INSTANCE.EE_EngineGetNextEvent(eEvent);

			// New event needs to be handled
			if (state == EdkErrorCode.EDK_OK.ToInt()) {

				int eventType = Edk.INSTANCE.EE_EmoEngineEventGetType(eEvent);
				Edk.INSTANCE.EE_EmoEngineEventGetUserId(eEvent, userID);

				// Log the EmoState if it has been updated
				if (eventType == Edk.EE_Event_t.EE_EmoStateUpdated.ToInt()) {

					Edk.INSTANCE.EE_EmoEngineEventGetEmoState(eEvent, eState);
					
					//Time initialization
					if (first){
						timestamp= 0;
						init= EmoState.INSTANCE.ES_GetTimeFromStart(eState);
						initsystem= System.currentTimeMillis();
						first= false;
					}
					else{
						timestamp = EmoState.INSTANCE.ES_GetTimeFromStart(eState) - init;
						timestampsystem= (System.currentTimeMillis() - initsystem) * .001;
					}
					
					int current= (int)((timestampsystem % 1) * 10);
					double realtimestamp= (double)((int)(timestampsystem*10))/10; 
					
					if (current != former){
						System.out.println(realtimestamp);
						former= current;
						 
						String dataline = realtimestamp + "";
						
						if (exciteshort) dataline= dataline + "," + EmoState.INSTANCE.ES_AffectivGetExcitementShortTermScore(eState);
						if (excitelong)	dataline= dataline + "," + EmoState.INSTANCE.ES_AffectivGetExcitementLongTermScore(eState);
						if (engagement) dataline= dataline + "," +  EmoState.INSTANCE.ES_AffectivGetEngagementBoredomScore(eState);
						if (frustration) dataline= dataline + "," +  EmoState.INSTANCE.ES_AffectivGetFrustrationScore(eState);
						if (meditation)	dataline= dataline + "," +  EmoState.INSTANCE.ES_AffectivGetMeditationScore(eState);
						dataline= dataline + "\n";
						
						out.write(dataline);
					}
				}
			}
			else if (state != EdkErrorCode.EDK_NO_EVENT.ToInt()) {
				System.out.println("Internal error in Emotiv Engine!");
				break;
			}
		}
    	
    	Edk.INSTANCE.EE_EngineDisconnect();
    	System.out.println("Disconnected!");
    	out.close();
    	}
    	catch (IOException e) {
    		e.printStackTrace();
		}
    }
	
	public static void main(String[] args){
		try {
			EmoRecord logger= new EmoRecord();
		} 
		catch (InterruptedException e){
			e.printStackTrace();
		} 
		catch (AWTException e){
			e.printStackTrace();
		}
    }
    
	public void keyReleased(KeyEvent arg0){
	}

	public void keyTyped(KeyEvent arg0){
	}

	public String saveAs(){
    	//File chooser stuffs
    	JFileChooser chooser = new JFileChooser();
    	chooser.setVisible(true);
    	chooser.setCurrentDirectory(new java.io.File("."));
    	
    	chooser.setDialogTitle("Choose a file name for the .jpg");
    	
    	chooser.setAcceptAllFileFilterUsed(false);
    	
    	if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
    		System.out.println(chooser.getSelectedFile().toString());
    		return chooser.getSelectedFile().toString();
    	}
    	else {
    		System.out.println("No Selection!");
    		return null;
    	}
    }
	
	//Checkbox
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();

	    if (source == excitelongcheck) {
	    	if (e.getStateChange() == ItemEvent.SELECTED) excitelong= true;
	    	else if (e.getStateChange() == ItemEvent.DESELECTED) excitelong= false;
	    } else if (source == exciteshortcheck) {
	    	if (e.getStateChange() == ItemEvent.SELECTED) exciteshort= true;
	    	else if (e.getStateChange() == ItemEvent.DESELECTED) exciteshort= false;
	    } else if (source == engagementcheck) {
	    	if (e.getStateChange() == ItemEvent.SELECTED) engagement= true;
	    	else if (e.getStateChange() == ItemEvent.DESELECTED) engagement= false;
	    } else if (source == frustrationcheck) {
	    	if (e.getStateChange() == ItemEvent.SELECTED) frustration= true;
	    	else if (e.getStateChange() == ItemEvent.DESELECTED) frustration= false;
	    } else if (source == meditationcheck) {
	    	if (e.getStateChange() == ItemEvent.SELECTED) meditation= true;
	    	else if (e.getStateChange() == ItemEvent.DESELECTED) meditation= false;
	    }
	}

	//Button
	public void actionPerformed(ActionEvent e){
		//Start button pressed
		if (e.getSource() == startbutton) {
			start();

			Thread thread = new Thread(this);
			thread.start();
		}
		//End button
		else if (e.getSource() == stopbutton){
			quit();
		}
		//Save as button
		else if (e.getSource() == saveas){
			System.out.println("Save as...");
			String temp= saveAs(); //Absolute loc
			
			if (temp != null){
				filename= temp;
			}
		}
	}
}
