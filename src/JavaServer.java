/**
 * @author Group 1
 */
import java.awt.*;//Abstract Window ToolKit included to Support GUI 
import java.awt.event.*;//included to handle certain events that occurs in swing 3 categories 1:Events 2:Listeners 3: Adapters so here we are using Listeners:-These interfaces define the methods that must be implemented by an object that want to notified when a particular event occurs
import java.io.*;//to handle Input and output Stream
import java.net.*;//Here we are using TCP Server API:will typically accepts connections from client socket 
import javax.swing.*;
import javax.swing.text.DefaultCaret;//cursor(caret) position while autoscrolling at end,beginning of the document 
public class JavaServer extends JFrame implements ActionListener{//JFrame Class is type of Container which inherits the java.awt.Frames class it works like a main window where components like labels,buttons,textfields are added creates GUI 
	static String message= "";//Actionlistener:it Responsible to handle all action events when a user clicks on the component 
	static String username= "";//to get user name

	static ServerSocket server=null;//null:becouse it is initailized separately rather complier to initialize
	static Socket socket=null;
	static PrintWriter writer=null;//PrintWriter class is used to print the formatted representation of objects to the text-output stream 
    
	static JTextArea msgRec=new JTextArea(100,50);//syntax:JTextArea(int row,int Col) creates textarea and displays no text initially for msg to Receive
	static JTextArea msgSend=new JTextArea(100,50);
	JButton send= new JButton("Send");
	JScrollPane pane2, pane1;//providing a Scrollable view 
	
	JMenuBar bar=new JMenuBar();//class used to implement menu which contains one or more JMenu objects
	
	JMenu messanger=new JMenu("ChatBox V1.0");
	JMenuItem logOut=new JMenuItem("Log Out");
	
	JMenuItem about=new JMenuItem("about");
	
	public JavaServer() {
		super("Java Server");//This invokes the Thread constructor by the name Java Server
		setBounds(0, 0, 407, 495);//Syntax:setBounds(int x-coordinate,int y-cor,int width,int height) used to set the position and size of added components
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Exit the application
		setResizable(false);//The end user are not allowed to resize the frame
		setLayout(null);//we are positioning our components obsolutely
		
		msgRec.setEditable(false);//here msgRec is a Variable that refers to a JTextField.flase then the user cannot types into the field
		msgRec.setBackground(Color.BLACK);//Background Color
		msgRec.setForeground(Color.WHITE);//Text Color
	
		msgRec.setText("");//setting the current text as space
		
		msgRec.setWrapStyleWord(true);//wrapping the lines at word boundaries
		msgRec.setLineWrap(true);//wrapping the lines of JTextArea
		
		pane2=new JScrollPane(msgRec);//message Receiving area
		pane2.setBounds(0, 0, 400, 200);//setting the size and position of pane2
		pane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);//getting scroll bar in pane2 which is always on
		add(pane2);//add into a stage
		
		msgSend.setBackground(Color.WHITE);//Background area where message is typed and sent
		msgSend.setForeground(Color.BLACK);//Text Color
		msgSend.setLineWrap(true);//Wrapping the lines of JTextArea
		msgSend.setWrapStyleWord(true);//Wrraping the lines at word boundaries
		
		msgSend.setText("Write Message Here");//a simple message at pane1
		
		pane1=new JScrollPane(msgSend);//sendind area with scroll
		pane1.setBounds(0, 200, 400, 200);//size and position of pane1
		pane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);//scroll bar is always on
		add(pane1);//add into a stage
		
		send.setBounds(0, 400, 400, 40);//setting the size of send button
		add(send);
		send.addActionListener(this);//to check for event using addActionListener
		
		bar.add(messanger);//Menubar with menu name chatBox 
		messanger.add(logOut);//submenu logout
		logOut.addActionListener(this);//action occurs
		bar.add(about);//about menu
		about.addActionListener(this);//action occurring on current class
		
		setJMenuBar(bar);//adding MenuBar to the Frame
		
		addWindowFocusListener(new WindowFocusListener() {//Checking the focus of server window i.e A end user is in particular window are out of the window  
			@Override
			public void windowGainedFocus(WindowEvent e) {//Invoked when the window is set to be the focused window i.e will receive keyboard events
				if( !msgRec.getText().equals("")) {
					System.out.println("Yes Focus");//printing the message that window is focused
					writer.flush();
				}
			}
			@Override
			public void windowLostFocus(WindowEvent e) {//window won't receive events from keyboard 
				if(!msgRec.getText().equals("")) {
					writer.flush();
				}
				
			}
		
	});
	if((username)!=null) {//making a frame appear on the screen
		setVisible(true);
	}
	else {
		System.exit(0);
	}
  }
	public static void main(String[] args) {
		username=JOptionPane.showInputDialog("User Name (Server)");//prompting the user for username by customizing dialog window 
		
		(new Thread(new Runnable() {//Creating and Starting a Thread
			public void run() {
				new JavaServer();
			}
		})).start();
		try {
			
		server=new ServerSocket(8888);//Server socket with port number 8888
		System.out.println(server.getInetAddress().getLocalHost());//getInetAddress method returns the address to which socket is connected:getLocalHost() returns the instance of InetAddess Containing name and address 
		
		socket=server.accept();//accepting the incoming request to the socket
		}
		catch(Exception e)//if exception accurs within the try block
		{
			System.out.println(e);
		}
		msgRec.setText("Connected");//Message is printed on the TextField
		
		(new Thread(new Runnable() {//Thread to manage Streams 
			public void run() {
				try {
					BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));//Reading the inputstream of Characters using BufferedReader class
					
					String line=null;//initially to null
					boolean testFlag=true;
					while((line=reader.readLine())!=null) {
						msgRec.append("\n"+line);//newline and append the message received
						
						cursorUpdate();//method to update the position of cursor to beginning
					}
				}
				catch(IOException ee)//handling the exceptions
				{
					try {
						server.close();
						socket.close();
					}
					catch(IOException eee)
					{
						eee.printStackTrace();
					}
				}
			}
		})).start();
		try {
			writer=new PrintWriter(socket.getOutputStream(), true);//printing the msg of an outputstream attached to it,true is to autoflush the buffer when new line character is found(don't need to call flush method)   
		}
		catch(IOException e) {
			try {
				server.close();
				socket.close();
			}
			catch(IOException eee) {
				
			}
		}
	}
	public void actionPerformed(ActionEvent e) {//Event handling
		
		Object scr=e.getSource();//returns the object on which event had occured 
		if(scr==send) {
			sendMessage();
		}
		else if(scr==logOut) {
			System.exit(0);//terminate the program
		}
		else if(scr==about) {
			JOptionPane.showMessageDialog(this, "Chat Box 1.0\n");
		}
	}
	public void sendMessage() {
		writer.println(username+":"+msgSend.getText());//Console output 
		writer.flush();
		msgRec.append("\nMe: "+msgSend.getText());
		cursorUpdate();
		msgSend.setText("");
		msgSend.setCaretPosition(0);//setCursorposition to the top
	}
	public static void cursorUpdate() {
		DefaultCaret caret=(DefaultCaret) msgRec.getCaret();//getting cursor position
		caret.setDot(msgRec.getDocument().getLength());//setting the cursor position with set dot where thing can be inserted
		
		DefaultCaret caret2=(DefaultCaret) msgSend.getCaret();//getting a cursor position for msgSend 
		caret2.setDot(msgSend.getDocument().getLength());
	}
}
