package Practica;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Jorge Alonso
 */
public class ServidorFTP {

	public static void main(String[] args) {
		try (ServerSocket serverSocket = new ServerSocket(21)) {
            System.out.println("Servidor FTP escuchando en el puerto 21...");
            /*
             * Inicio de las conexiones
             */
            while (true) {
            	Socket clientSocket = serverSocket.accept();
                System.out.println("Nueva conexión aceptada. " + clientSocket.getPort());
             // Crear y lanzar un nuevo hilo para manejar la conexión del cliente
                Thread hiloCliente = new Thread(new ClienteS(clientSocket));
                hiloCliente.start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }

	}

}
