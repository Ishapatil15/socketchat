
import InputHelpers.GetAndStringify;
import InputHelpers.GetMessages;
import OutputHelpers.ParseAndCreateMsg;
import OutputHelpers.SendMessages;
import SharedHelpers.MessageManager;
import ClientHelpers.UniqueIdManagement;
import ClientHelpers.ReadOlderMessages;
import SharedHelpers.ManageContactsForClient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.HashSet;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Box;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import java.nio.file.Files;
import java.nio.file.Path;

public class Client extends javax.swing.JPanel {
    private String uniqueId;
    private SendMessages senderObj;
    private ParseAndCreateMsg parser;
    public Client() {
        initComponents();

        JPanel contactsPanel = new JPanel();
        contactsPanel.setLayout(new BoxLayout(contactsPanel, BoxLayout.Y_AXIS));
        contactsPanel.setBackground(new Color(240, 240, 240));

        contactsPanel.setPreferredSize(new Dimension(250, 500));
        userScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        userScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        userScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        userScrollPane.setViewportView(contactsPanel);
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));

        try {
            // Start a Client Socket, Connect to the given port.
            Socket socket = new Socket("127.0.0.1", 8765);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            // Configure an ID for this socket / Client.
            this.uniqueId = new UniqueIdManagement().configureId(br, input, output);

            // Get All Contacts.
            HashSet<String> users = new ManageContactsForClient(input).getContacts();

            for (String user : users) {
                // Should not be able to text to self.
                if(!user.equalsIgnoreCase(uniqueId))
                    contactsPanel.add(createContactPanel(user));
            }

            // Add all contacts to the sidebar.
            contactsPanel.revalidate();
            contactsPanel.repaint();

            parser = new ParseAndCreateMsg();
            this.senderObj = new SendMessages(output, uniqueId);

            GetAndStringify extracter = new GetAndStringify(input);
            new Thread(() -> {
                // Client.this::addMessage adds the message in the chat section(GUI).
                GetMessages getMessagesObj = new GetMessages(extracter, Client.this::addMessage);
                while (true) {
                    // Exit Loop when the Client wants to exit.
                    boolean exitLoop = getMessagesObj.getMessage();
                    if (exitLoop) break;
                }
            }).start();

        } catch (ConnectException e) {
            System.out.println("Server is not running yet!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                javax.swing.JFrame frame = new javax.swing.JFrame("Employee Management");
                frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                frame.setUndecorated(true);
                frame.setContentPane(new Client()); // Add the panel
                frame.pack(); // Sizes the frame
                frame.setLocationRelativeTo(null); // Center on screen
                frame.setVisible(true); // Show the GUI
            }
        });
    }
    
    // To add messages in the GUI chat section.
    private void addMessage(String message, boolean isSentByCurrentUser) {
        JLabel messageLabel = new JLabel("<html><body style='width: fit-content'>" + message + "</body></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageLabel.setOpaque(true);
        messageLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));

        if (isSentByCurrentUser) {
            messageLabel.setBackground(new Color(204, 255, 204));
        } else {
            messageLabel.setBackground(new Color(204, 255, 255));
        }
        
        Dimension prefSize = messageLabel.getPreferredSize();
        
        if (prefSize.width > 300) {
            messageLabel.setText("<html><body stNyle='width: 270px'>" + message + "</body></html>");
        }
        messageLabel.setMaximumSize(messageLabel.getPreferredSize());
        JPanel bubblePanel = new JPanel();
        bubblePanel.setLayout(new BoxLayout(bubblePanel, BoxLayout.X_AXIS));
        bubblePanel.setOpaque(false);
        bubblePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));

        if (isSentByCurrentUser) {
            bubblePanel.add(Box.createHorizontalGlue());
            bubblePanel.add(messageLabel);
        } else {
            bubblePanel.add(messageLabel);
            bubblePanel.add(Box.createHorizontalGlue());
        }

        jPanel1.add(bubblePanel);
        jPanel1.revalidate();
        jPanel1.repaint();

        // Scroll to the maximum value (to newer messages).
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = jScrollPane1.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
    
    // Divider to distinguish between read and unread messages.
    private void addUnreadDivider() {
        JPanel dividerPanel = new JPanel();
        dividerPanel.setLayout(new BorderLayout());
        dividerPanel.setOpaque(false);
        dividerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel unreadLabel = new JLabel("Unread Messages");
        unreadLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        unreadLabel.setForeground(new Color(100, 100, 100));
        unreadLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        centerPanel.setOpaque(false);

        JSeparator leftLine = new JSeparator();
        leftLine.setForeground(new Color(150, 150, 150));
        leftLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JSeparator rightLine = new JSeparator();
        rightLine.setForeground(new Color(150, 150, 150));
        rightLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        centerPanel.add(leftLine);
        centerPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        centerPanel.add(unreadLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        centerPanel.add(rightLine);

        dividerPanel.add(centerPanel, BorderLayout.CENTER);

        jPanel1.add(dividerPanel);
        jPanel1.revalidate();
        jPanel1.repaint();
    }

    

    // Sidebar that shows all contacts.
    private JPanel createContactPanel(String username) {
        JPanel clientSidebar = new JPanel();
        clientSidebar.setBackground(Color.WHITE);
        clientSidebar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        clientSidebar.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel userProfilePanel = new JPanel();
        userProfilePanel.setBackground(new Color(204, 204, 204));
        userProfilePanel.setPreferredSize(new Dimension(45, 45));

        JLabel userLabel = new JLabel(username);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        userLabel.setVerticalAlignment(SwingConstants.CENTER);

        GroupLayout layout = new GroupLayout(clientSidebar);
        clientSidebar.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addComponent(userProfilePanel, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(userLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(8)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(userProfilePanel, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                    .addComponent(userLabel))
                .addGap(8)
        );
        clientSidebar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel1.removeAll();
                jPanel1.revalidate();
                jPanel1.repaint();
                ReadOlderMessages reader = new ReadOlderMessages();
                reader.bringMessages(username, uniqueId, Client.this::addMessage, Client.this::addUnreadDivider);
                CurrentChatClientId.setText(username);
            }
        });

        return clientSidebar;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Navbar = new javax.swing.JPanel();
        Title = new javax.swing.JLabel();
        CloseBtn = new javax.swing.JLabel();
        allUsers = new javax.swing.JPanel();
        HeadingForClients = new javax.swing.JLabel();
        userScrollPane = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        CurrentClientChat = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        CurrentChatClientId = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        SendBtn = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        MessageBox = new javax.swing.JTextArea();

        Navbar.setBackground(new java.awt.Color(102, 102, 102));

        Title.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        Title.setForeground(new java.awt.Color(255, 255, 255));
        Title.setText("ChatSocket");

        CloseBtn.setBackground(new java.awt.Color(255, 0, 0));
        CloseBtn.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        CloseBtn.setForeground(new java.awt.Color(255, 255, 255));
        CloseBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CloseBtn.setText("X");
        CloseBtn.setOpaque(true);
        CloseBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        CloseBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CloseBtnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout NavbarLayout = new javax.swing.GroupLayout(Navbar);
        Navbar.setLayout(NavbarLayout);
        NavbarLayout.setHorizontalGroup(
            NavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NavbarLayout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(Title, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(CloseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        NavbarLayout.setVerticalGroup(
            NavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NavbarLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(NavbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Title, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CloseBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        allUsers.setBackground(new java.awt.Color(204, 204, 204));

        HeadingForClients.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        HeadingForClients.setText(" Contacts/Users");
        HeadingForClients.setMaximumSize(new java.awt.Dimension(263, 48));
        HeadingForClients.setMinimumSize(new java.awt.Dimension(263, 48));
        HeadingForClients.setPreferredSize(new java.awt.Dimension(263, 48));

        javax.swing.GroupLayout allUsersLayout = new javax.swing.GroupLayout(allUsers);
        allUsers.setLayout(allUsersLayout);
        allUsersLayout.setHorizontalGroup(
            allUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(userScrollPane)
            .addGroup(allUsersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(HeadingForClients, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        allUsersLayout.setVerticalGroup(
            allUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(allUsersLayout.createSequentialGroup()
                .addComponent(HeadingForClients, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5.setBackground(new java.awt.Color(153, 153, 153));

        CurrentClientChat.setBackground(new java.awt.Color(255, 255, 255));

        jPanel7.setBackground(new java.awt.Color(204, 204, 204));
        jPanel7.setPreferredSize(new java.awt.Dimension(45, 45));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 45, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 45, Short.MAX_VALUE)
        );

        CurrentChatClientId.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        CurrentChatClientId.setText("Demo");

        javax.swing.GroupLayout CurrentClientChatLayout = new javax.swing.GroupLayout(CurrentClientChat);
        CurrentClientChat.setLayout(CurrentClientChatLayout);
        CurrentClientChatLayout.setHorizontalGroup(
            CurrentClientChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CurrentClientChatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(CurrentChatClientId, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        CurrentClientChatLayout.setVerticalGroup(
            CurrentClientChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CurrentClientChatLayout.createSequentialGroup()
                .addGroup(CurrentClientChatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CurrentClientChatLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(CurrentClientChatLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(CurrentChatClientId)))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jPanel1.setMaximumSize(new java.awt.Dimension(1056, 445));
        jPanel1.setMinimumSize(new java.awt.Dimension(1056, 445));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1056, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 445, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel1);

        SendBtn.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        SendBtn.setText("Send");
        SendBtn.setPreferredSize(new java.awt.Dimension(60, 32));
        SendBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SendBtnMouseClicked(evt);
            }
        });
        SendBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendBtnActionPerformed(evt);
            }
        });

        MessageBox.setColumns(20);
        MessageBox.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        MessageBox.setRows(5);
        MessageBox.setMinimumSize(new java.awt.Dimension(232, 84));
        MessageBox.addKeyListener(new java.awt.event.KeyAdapter(){
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER && !evt.isShiftDown()) {
                    evt.consume();
                    SendBtnMouseClicked(
                        new java.awt.event.MouseEvent(
                            SendBtn, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
                            0, 0, 0, 1, false
                        )
                    );
                }
            }
        });
        jScrollPane2.setViewportView(MessageBox);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(CurrentClientChat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 963, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SendBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane1)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(CurrentClientChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(9, 9, 9)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(SendBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(allUsers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(Navbar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Navbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(allUsers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void SendBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SendBtnActionPerformed

    private void CloseBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CloseBtnMouseClicked
        senderObj.sendMessage("server", "exit");  
        sendLocalDataToServer();
        System.exit(0);
    }//GEN-LAST:event_CloseBtnMouseClicked

    private void sendLocalDataToServer(){
        if(Files.exists(Path.of( "./Unread.xml"))){
            String msg = parser.createMsg(uniqueId, "server", "status", "101");
            senderObj.sendParsedMsg(msg);
            MessageManager manageMessages = new MessageManager();
            manageMessages.sendUnreadMessages(uniqueId, senderObj);
        }
        String msg = parser.createMsg(uniqueId, "server", "status", "201" );
        senderObj.sendParsedMsg(msg);
    }

    private void SendBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SendBtnMouseClicked
        String msg = MessageBox.getText();
        senderObj.sendMessage(CurrentChatClientId.getText(), msg);
        addMessage( msg, true);
        MessageBox.setText("");
    }//GEN-LAST:event_SendBtnMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CloseBtn;
    private javax.swing.JLabel CurrentChatClientId;
    private javax.swing.JPanel CurrentClientChat;
    private javax.swing.JLabel HeadingForClients;
    private javax.swing.JTextArea MessageBox;
    private javax.swing.JPanel Navbar;
    private javax.swing.JButton SendBtn;
    private javax.swing.JLabel Title;
    private javax.swing.JPanel allUsers;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane userScrollPane;
    // End of variables declaration//GEN-END:variables
}
