package org.example;

import java.awt.BorderLayout;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


    

class ArchiveFrame extends JFrame {
    private JTextArea textArea;
    private ConcurrentHashMap<String, Timer> messageTimers;
    private JButton restoreButton;
    public ArchiveFrame() {
        textArea = new JTextArea(20, 40);
        textArea.setEditable(false);
         // Archived messages should be read-only
         JScrollPane scrollPane = new JScrollPane(textArea);
         restoreButton = new JButton("Restore Selected Message");
        restoreButton.addActionListener(e -> restoreSelectedMessage());
        messageTimers = new ConcurrentHashMap<>();
        JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(restoreButton, BorderLayout.SOUTH);

        this.add(panel);
        setTitle("Archived Messages");
        pack();
    }
    public void archiveMessage(String message) {
        // Append the message and track it for auto-deletion
        textArea.append(message + "\n");
        scheduleMessageDeletion(message);
    }
    private void scheduleMessageDeletion(String message) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
               textArea.setText(textArea.getText().replace(message + "\n", ""));
                messageTimers.remove(message);
            }
        }, 120000); // 120000 milliseconds = 2 minutes
        messageTimers.put(message, timer);
    }
    private void restoreSelectedMessage() {
        // Retrieve the selected text from the JTextArea within the ArchiveFrame
        String selectedText = textArea.getSelectedText();
    
        if (selectedText == null || selectedText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No message selected for restoration.");
            return;
        }
// Cancel the deletion timer for the restored message
Timer timer = messageTimers.get(selectedText);
if (timer != null) {
    timer.cancel();
    messageTimers.remove(selectedText);
}
textArea.replaceRange("", textArea.getSelectionStart(), textArea.getSelectionEnd());


}
    
}
