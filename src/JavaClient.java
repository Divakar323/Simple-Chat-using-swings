import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
 
public class JavaClient extends JFrame implements ActionListener
{
 
    // Extra variables
    static String message = "";
    static String userName = "";
    static String iPAddress = null;
 
    // Networking Variables
    static Socket socket = null;
    static PrintWriter writer = null;
 
    // // Graphics Variables
    static JTextArea msgRec = new JTextArea(100, 50);
    static JTextArea msgSend = new JTextArea(100, 50);
    JButton send = new JButton("Send");
    JScrollPane pane2, pane1;
 
    JMenuBar bar = new JMenuBar();
 
    JMenu messanger = new JMenu("ChatBox V1.0");
    JMenuItem logOut = new JMenuItem("Log Out");
    JMenuItem about = new JMenuItem("about");
 
    public JavaClient() {
        super("Java Client");
        setBounds(0, 0, 407, 495);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
 
        msgRec.setEditable(false);
        msgRec.setBackground(Color.BLACK);
        msgRec.setForeground(Color.WHITE);
        msgRec.setText("");
 
        msgRec.setWrapStyleWord(true);
        msgRec.setLineWrap(true);
 
        pane2 = new JScrollPane(msgRec);
        pane2.setBounds(0, 0, 400, 200);
        pane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(pane2);
 
        msgSend.setBackground(Color.WHITE);
        msgSend.setForeground(Color.BLACK);
        msgSend.setLineWrap(true);
        msgSend.setWrapStyleWord(true);
 
        msgSend.setText("Write Message here");
        
        
        pane1 = new JScrollPane(msgSend);
        pane1.setBounds(0, 200, 400, 200);
        pane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(pane1);
 
        send.setBounds(0, 400, 400, 40);
        add(send);
        send.addActionListener(this);
 
        bar.add(messanger);
        messanger.add(logOut);
        logOut.addActionListener(this);
 
        bar.add(about);
        about.addActionListener(this);
 
        setJMenuBar(bar);
 
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
 
                if (!msgRec.getText().equals("")) {
                    System.out.println("Yes Focus");
                    writer.flush();
 
                }
            }
 
            @Override
            public void windowLostFocus(WindowEvent e) {
                if (!msgRec.getText().equals("")) {
                    writer.flush();
                }
            }
 
        });
 
        if ((userName) != null) {
            setVisible(true);
        } else {
            System.exit(0);
        }
    }
 
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
 
        userName = JOptionPane.showInputDialog("User Name (Client)");
        iPAddress = JOptionPane.showInputDialog("Enter Server IpAddress");
 
        // swing thread
        (new Thread(new Runnable() {
            public void run() {
                new JavaClient();
 
            }
 
        })).start();
        socket = new Socket(iPAddress, 8888);
        msgRec.setText("Connected! Welcome to chatBox V1.0");
 
        // listening port thread
        (new Thread(new Runnable() {
            public void run() {
 
                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
 
                    String line = null;
                    boolean testFlag = true;
                    while ((line = reader.readLine()) != null) {
                        msgRec.append("\n" + line);
                        cursorUpdate();
 
                        if (!reader.ready()) {
                            testFlag = true;
                        }
                    }
 
                } catch (IOException ee) {
                    try {
                        socket.close();
                    } catch (IOException eee) {
                        eee.printStackTrace();
                    }
                    ee.printStackTrace();
                }
            }
        })).start();
 
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
 
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException eee) {
            }
        }
    }
 
    // ActionEvents
 
    @Override
    public void actionPerformed(ActionEvent e) {
        Object scr = e.getSource();
 
        if (scr == send) {
            sendMessage();
        } else if (scr == logOut) {
 
            System.exit(0);
 
        } 
 
        else if (scr == about) {
            JOptionPane.showMessageDialog(this,
                    "Chat Bot 1.0\n");
        }
    }
 
    private void sendMessage() {
        writer.println(userName + " :" + msgSend.getText());
 
        msgRec.append("\nMe: " + msgSend.getText());
        writer.flush();
        cursorUpdate();
 
        msgSend.setText("");
        msgSend.setCaretPosition(0);
    }
 
    private static void cursorUpdate() {
        // Update cursor position
        DefaultCaret caret = (DefaultCaret) msgRec.getCaret();
        caret.setDot(msgRec.getDocument().getLength());
 
        DefaultCaret caret2 = (DefaultCaret) msgSend.getCaret();
        caret2.setDot(msgSend.getDocument().getLength());
    }
}
 
// A Java Client program will take a Server Ip address
