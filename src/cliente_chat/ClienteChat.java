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
    // Constantes para la dirección y el puerto del servidor
    private static final String SERVER_ADDRESS = "localhost"; 
    private static final int SERVER_PORT = 12345;

    // Componentes de la interfaz gráfica
    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField;
    private PrintWriter out;
    private String name;

    public ClienteChat() {
        createGUI();
    }

    private void createGUI() {
        // Crear y configurar ventana principal
        frame = new JFrame("Cliente Chat");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Área de texto para mostrar mensajes
        textArea = new JTextArea();
        textArea.setEditable(false);

        // Scroll para el área de texto
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con campo de texto y botón de enviar
        JPanel bottomPanel = new JPanel(new BorderLayout());
        textField = new JTextField();
        JButton sendButton = new JButton("Enviar");

        bottomPanel.add(textField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Acción al pulsar el botón de enviar
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Acción al pulsar Enter en el campo de texto
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        frame.setVisible(true);

        try {
            // Establecer conexión con el servidor
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);

            // Hilo para escuchar mensajes del servidor
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
                    } catch (SocketException se) {
                        System.out.println("Se perdió la conexión con el servidor.");
                        JOptionPane.showMessageDialog(frame, "Se perdió la conexión con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            receiveThread.start();

            // Pedir nombre del usuario para identificación
            name = JOptionPane.showInputDialog(frame, "Ingresa tu nombre:", "Conexión", JOptionPane.QUESTION_MESSAGE);
            String encryptedMessage = CryptoHelper.encrypt(name + " se ha conectado.");
            out.println(encryptedMessage);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error conectando con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        // Recoger y formatear el mensaje del usuario
        String message = textField.getText();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String formattedMessage = sdf.format(new Date()) +"- "+name+ ": " + message;
        String encryptedMessage = CryptoHelper.encrypt(formattedMessage);

        // Enviar mensaje cifrado al servidor
        out.println(encryptedMessage);
        textField.setText("");
    }

    public static void main(String[] args) {
        // Ejecutar la aplicación cliente en el hilo de despacho de eventos de Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClienteChat();
            }
        });
    }
}

