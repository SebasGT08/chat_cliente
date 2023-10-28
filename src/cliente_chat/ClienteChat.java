package cliente_chat;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author guama
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClienteChat {
    private static final String SERVER_ADDRESS = "localhost";  // Asume que el servidor se ejecuta en la misma máquina. Cambia si es necesario.
    private static final int SERVER_PORT = 12345;

    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private PrintWriter out;
    private String name;

    public ClienteChat() {
        createGUI();
    }

    private void createGUI() {
        frame = new JFrame("Cliente Chat");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        textField = new JTextField();
        JButton sendButton = new JButton("Enviar");

        bottomPanel.add(textField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Evento al pulsar el botón de enviar
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Evento al pulsar Enter en el campo de texto
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        frame.setVisible(true);

        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);

            // Crear un hilo para escuchar mensajes del servidor
            Thread receiveThread = new Thread() {
                @Override
                public void run() {
                    try {
                        InputStream input = socket.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        String message;
                        while ((message = reader.readLine()) != null) {
                            String decryptedMessage = CryptoHelper.decrypt(message);
                            textArea.append(decryptedMessage + "\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            receiveThread.start();

            name = JOptionPane.showInputDialog(frame, "Ingresa tu nombre:", "Conexión", JOptionPane.QUESTION_MESSAGE);
            String encryptedMessage = CryptoHelper.encrypt(name + " se ha conectado.");
            out.println(encryptedMessage);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error conectando con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        String message = textField.getText();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String formattedMessage = sdf.format(new Date()) +"- "+name+ ": " + message;
        String encryptedMessage = CryptoHelper.encrypt(formattedMessage);
        out.println(encryptedMessage);
        textField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClienteChat();
            }
        });
    }
}
