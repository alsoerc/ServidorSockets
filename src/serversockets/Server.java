package serversockets;

/**
 *
 * @author alsorc
 */
//
//Servidor encargado de recibir clientes y realizar envío de mensajes de difusión (broadcast)
//
import java.io.*;
import java.util.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// Server class 
public class Server {

    public static void main(String[] args) throws IOException {
        // server is listening on port 5056 
        ServerSocket ss = new ServerSocket(5056);
        List<Socket> listaSockets = new ArrayList<>();

        // running infinite loop for getting 
        // client request 
        while (true) {
            Socket s = null;

            try {
                // socket object to receive incoming client requests 
                s = ss.accept();
                listaSockets.add(s);
                System.out.println("Nuevo cliente conectado : " + s);

                // obtaining input and out streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Hilo asignado a cliente");

                // create a new thread object 
                Thread t = new ClientHandler(listaSockets, s, dis, dos);

                // Invoking the start() method 
                t.start();

            } catch (IOException e) {
                s.close();
                e.printStackTrace();
            }
        }
    }
}

// ClientHandler class 
class ClientHandler extends Thread {

    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    private final List<Socket> listaSockets;

    // Constructor 
    public ClientHandler(List<Socket> listaSockets, Socket s, DataInputStream dis, DataOutputStream dos) {
        this.listaSockets = listaSockets;
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run() {
        String received;
        while (true) {

            try {

                // receive the answer from client
                received = dis.readUTF();
                if (received != null || received.equals("")) {
                    System.out.println(java.time.LocalDateTime.now() + "\n" + received);
                }

                for (Socket socket : listaSockets) {
                    try {
                        new DataOutputStream(
                                socket.getOutputStream()
                        ).writeUTF(received);
                    } catch (IOException ex) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
