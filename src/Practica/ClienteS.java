package Practica;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


/**
 * @author Jorge Alonso
 */
public class ClienteS implements Runnable {
	private final Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private ServerSocket dataSocket;
	private Socket dataSocket2;
	private String permisos;
	//private Socket socketActivo;
	private File directorioActual = new File(".").getAbsoluteFile();

	public ClienteS(Socket clientSocket) {
		this.socket = clientSocket;
	}

	/*
	 * Metodo principal
	 */
	@Override
	public void run() {
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);

			writer.println("220 Welcome to FTP server");
			writer.flush();
			if (autenticarUsuario()) {
				writer.println("230 User logged in, proceed.");
				writer.flush();
				String linea;
				while ((linea = reader.readLine()) != null) {
					System.out.println("Comando recibido: " + linea);
					switch (linea.split(" ")[0]) {
					case "SYST":
						writer.println("215 UNIX Type: L8");
						writer.flush();
						break;
					case "PASV":
						System.out.println("Se mete al pasivo");
						pasive();
						break;
					case "LIST":
						System.out.println("Se mete al list");
						writer.println("150 Here comes the directory listing.");
						writer.flush();
						list();
						break;
					case "STOR":
						String nombre = linea.split(" ")[1];
						System.out.println("Archivo recibido: " + nombre);
						upload(nombre);
						break;
					case "RETR":
						String descargar = linea.split(" ")[1];
						System.out.println("Archivo recibido: " + descargar);
						download(descargar);
						break;
					case "DELE":
						String borrar = linea.split(" ")[1];
						System.out.println("Archivo recibido: " + borrar);
						delete(borrar);
						break;
					case "MKD":
						String directorio = linea.split(" ")[1];
						System.out.println("directorio recibido: " + directorio);
						mkdir(directorio);
						break;
					case "RMD":
						String directorioDel = linea.split(" ")[1];
						System.out.println("directorio recibido: " + directorioDel);
						borrarDirectorio(directorioDel);
						break;
					case "RNFR":
						String renombrar = linea.split(" ")[1];
						System.out.println("Recibido: " + renombrar);
						renombrarArchivoODirectorio(renombrar);
						break;
					case "CWD":
						String nuevoDir = linea.split(" ")[1];
						System.out.println("Recibido: " + nuevoDir);
						cambiarDirectorio(nuevoDir);
						break;
					case "CDUP":
						cambiarDirectorioSuperior();
						break;
					case "PORT":
						modoActivo(linea);
						break;
					}
				}

			} else {
				writer.println("530 Authentication failed.");
				writer.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Metodo para activar el modo pasivo
	 */
	private void pasive() {
		try {

			// Obtener la IP del servidor
			String ipAddress = InetAddress.getLocalHost().getHostAddress().replace(".", ",");

			// Abrir un ServerSocket para esperar la conexión de datos en el puerto
			// aleatorio
			dataSocket = new ServerSocket(0);
			int puerto = dataSocket.getLocalPort();

			// Calcular p1 y p2 correctamente
			int p1 = puerto / 256;
			int p2 = puerto % 256;
			System.out.println(
					"Esperando conexión en el puerto " + dataSocket.getLocalPort() + " para transferencia de datos...");
			// Enviar el mensaje al cliente con el puerto de datos
			String response = "227 Entering Passive Mode (" + ipAddress + "," + p1 + "," + p2 + ").";
			System.out.println(response);
			writer.println(response);
			writer.flush();
			dataSocket2 = dataSocket.accept();
			System.out.println("Aceptada");

		} catch (IOException e) {
			System.out.println("Error al manejar el modo pasivo: " + e.getMessage());
			writer.println("425 Can't open passive connection.");
			writer.flush();
		}

	}
	/*
	 * Metodo para activar el modo activo
	 */
	private void modoActivo(String comando) {
        try {
            String[] partes = comando.split(" ")[1].split(",");
            String clientIp = partes[0] + "." + partes[1] + "." + partes[2] + "." + partes[3];
            int port = (Integer.parseInt(partes[4]) * 256) + Integer.parseInt(partes[5]);

            dataSocket2 = new Socket(clientIp, port);
            writer.println("200 Active mode set.");
            writer.flush();
        } catch (IOException e) {
            writer.println("425 Can't open active connection.");
            writer.flush();
        }
    }

	/*
	 * Metodo para autenticar un usuario
	 * Formato de los usuarios
	 * nombre:password
	 */
	private boolean autenticarUsuario() throws IOException {
		// Primero, lee el nombre de usuario del cliente
		String usuario = reader.readLine();
		// Luego, responde con el código que indica que el nombre de usuario está bien
		writer.println("331 Username okay, need password.");
		writer.flush();

		// Ahora lee la contraseña
		String contrasena = reader.readLine();
		// Verifica las credenciales
		return verificarCredenciales(usuario, contrasena);
	}

	/*
	 * MEtodo para verificar las credenciales
	 */
	private boolean verificarCredenciales(String usuario, String contrasena) {
		try (BufferedReader br = new BufferedReader(new FileReader("Usuarios.txt"))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split(":");
				System.out.println(partes[1]);
				System.out.println(partes[0]);
				if (partes.length == 2 && partes[0].trim().equals(usuario) && partes[1].trim().equals(contrasena)) {
					permisos = usuario;
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * MEtodo para listar los archivos y ficheros
	 */
	public void list() {

		try {
			// Obtener el flujo de salida del socket de datos

			PrintWriter dataWriter = new PrintWriter(dataSocket2.getOutputStream(), true);
			//File directorioActual = new File("."); // Cambia esto según el directorio actual del servidor
			File[] archivos = directorioActual.listFiles();

			if (archivos != null) {
				for (File archivo : archivos) {
					if (archivo.isDirectory()) {
						dataWriter.println("drwxr-xr-x 1 user group 0 Jan 1 00:00 " + archivo.getName());
					} else {
						dataWriter.println(
								"-rw-r--r-- 1 user group " + archivo.length() + " Jan 1 00:00 " + archivo.getName());
					}
				}
			}

			cerrarConexiones();
			writer.println("226 Directory send OK.");
			writer.flush();
		} catch (IOException e) {
			writer.println("426 Connection closed; transfer aborted.");
			writer.flush();
		}
	}

	/*
	 * Metodo para subir un archivo
	 */
	public void upload(String nombre) {
		System.out.println("Se mete al subir archivos");
		try {
			System.out.println(nombre);
			File fichero = new File("./" + nombre);
			writer.println("150 Ready to receive the file.");
			writer.flush();
			if (permisos.equalsIgnoreCase("mid") || permisos.equalsIgnoreCase("admin")) {
				FileOutputStream fos = new FileOutputStream(fichero);
				BufferedInputStream dataInput = new BufferedInputStream(dataSocket2.getInputStream());
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = dataInput.read(buffer)) != -1) {
					fos.write(buffer, 0, bytesRead);
				}
				writer.println("226 File transfer complete.");
				writer.flush();
			} else {
				writer.println("550 Permission denied.");
				writer.flush();
			}

			dataSocket.close();
			dataSocket = null;
			dataSocket2.close();
		} catch (IOException e) {
			writer.println("450 Error saving file.");
		}

	}

	/*
	 * Metodo para descargar un archivo
	 */
	public void download(String nombre) {
		File archivo = new File("./" + nombre);
		if (!archivo.exists() || archivo.isDirectory()) {
			writer.println("550 File not found.");
			writer.flush();
			return;
		}
		writer.println("150 Opening data connection for file transfer.");
		writer.flush();
		try (FileInputStream fis = new FileInputStream(archivo); OutputStream out = dataSocket2.getOutputStream()) {

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = fis.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
		} catch (IOException e) {
			System.out.println("Error en la transferencia: " + e.getMessage());
			writer.println("451 Requested action aborted. Local error in processing.");
			writer.flush();
		}

		// Mensaje de éxito
		writer.println("226 File transfer complete.");
		writer.flush();
		cerrarConexiones();

	}

	/*
	 * MEtodo para borrar un fichero
	 */
	public void delete(String nombre) {
		File archivo = new File(nombre);
		if (archivo.exists() && archivo.isFile()) {
			if (permisos.equalsIgnoreCase("admin")) {
				if (archivo.delete()) {
					writer.println("250 File deleted successfully.");
				} else {
					writer.println("450 Error deleting file.");
				}
			} else {
				writer.println("550 Permission denied.");
				writer.flush();
			}

		} else {
			writer.println("550 File not found.");
		}
		writer.flush();
		cerrarConexiones();
	}

	/*
	 * Metodo para crear un directorio
	 */
	public void mkdir(String nombre) {
		File directorio = new File(nombre);
		if (permisos.equalsIgnoreCase("admin")) {
			if (directorio.mkdir()) {
				writer.println("257 Directory created successfully.");
			} else {
				writer.println("550 Failed to create directory.");
			}
			writer.flush();
		} else {
			writer.println("550 Permission denied.");
			writer.flush();
		}

		cerrarConexiones();
	}

	/*
	 * Metodo para borrar un directorio
	 */
	public void borrarDirectorio(String nombre) {
		try {
			File directorio = new File(nombre);
			if (directorio.exists() && directorio.isDirectory()) {
				if (permisos.equalsIgnoreCase("admin")) {
					if (directorio.delete()) {
						writer.println("250 Directory removed successfully.");
					} else {
						writer.println("550 Failed to remove directory. Directory may not be empty.");
					}
				} else {
					writer.println("550 Directory does not exist.");
				}
				writer.flush();
			} else {
				writer.println("550 Permission denied.");
				writer.flush();
			}

		} catch (Exception e) {
			writer.println("550 Error occurred while removing directory: " + e.getMessage());
			writer.flush();
		}
		cerrarConexiones();

	}

	/*
	 * Metodo para renombrar un archivo o directorio
	 */
	private void renombrarArchivoODirectorio(String nombre) {
		try {
			File archivo = new File(nombre);
			if (archivo.exists()) {
				writer.println("350 File exists, ready for destination name.");
				String nuevoNombre = reader.readLine().split(" ")[1];
				System.out.println(nuevoNombre);
				File nuevoArchivo = new File(nuevoNombre);
				if (archivo.renameTo(nuevoArchivo)) {
					writer.println("250 Rename successful");
				} else {
					writer.println("550 Rename failed");
				}
			} else {
				writer.println("550 File or directory not found.");
			}
			writer.flush();
		} catch (IOException e) {
			writer.println("550 Error processing rename request.");
			writer.flush();
		}
		cerrarConexiones();
	}

	/*
	 * Metodo para cambiar de directorio
	 */
	private void cambiarDirectorio(String nombre) {
	    try {
	        File nuevoDirectorio = new File(directorioActual, nombre);
	        
	        if (!nuevoDirectorio.exists() || !nuevoDirectorio.isDirectory()) {
	            writer.println("550 Failed to change directory.");
	        } else if (permisos.equalsIgnoreCase("mid") || permisos.equalsIgnoreCase("admin")) {
	            directorioActual = nuevoDirectorio;
	            writer.println("250 Directory successfully changed.");
	        } else {
	            writer.println("550 Permission denied.");
	        }
	        writer.flush();
	    } catch (Exception e) {
	        writer.println("550 Error processing CWD command.");
	        writer.flush();
	    }
	    cerrarConexiones();
	}


	/*
	 * Metodo para cambiar a un directorio superior
	 */
	private void cambiarDirectorioSuperior() {
	    try {
	        if (permisos.equalsIgnoreCase("mid") || permisos.equalsIgnoreCase("admin")) {
	            File direc = directorioActual.getParentFile();

	            if (direc != null && direc.exists()) {
	                directorioActual = direc;
	                writer.println("250 Directory successfully changed.");
	            } else {
	                writer.println("550 Failed to change directory.");
	            }
	        } else {
	            writer.println("550 Permission denied.");
	        }
	        writer.flush();
	    } catch (Exception e) {
	        writer.println("550 Error processing CDUP command.");
	        writer.flush();
	    }
	    cerrarConexiones();
	}


	/*
	 * Metodo para cerrar las conexiones
	 */
	public void cerrarConexiones() {
	    try {
	        if (dataSocket != null) {
	            dataSocket.close();
	            dataSocket = null;
	        }
	        if (dataSocket2 != null) {
	            dataSocket2.close();
	        }
	    } catch (IOException e) {
	        System.out.println("Error al cerrar los sockets");
	    }
	}


}
