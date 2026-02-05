package Practica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * @author Jorge Alonso
 */
public class ClienteFTP {

	/*
	 * Main del cliente
	 */
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese IP del servidor: ");
        String server = scanner.nextLine();
        System.out.print("Ingrese usuario: ");
        String usuario = scanner.nextLine();
        System.out.print("Ingrese contraseña: ");
        String contrasena = scanner.nextLine();
        
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, 21);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("530 Could not connect to server.");
                return;
            }
            if (ftpClient.login(usuario, contrasena)) {
                String modo = pedirModo();
                validarModo(modo, ftpClient);
            } else {
                System.out.println("530 Authentication failed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		try {
			ftpClient.disconnect();
		} catch (IOException e2) {
			System.out.println("Error al desconectar al servidor: " + e2.getMessage());
		}

	}
	
	/*
	 * Metodo para validar el modo introducido
	 */
	public static void validarModo(String modo,FTPClient client) {
		if(modo.equals("pasivo")) {
			pasivo(client);
		}else if(modo.equals("activo")) {
			activo(client);
		}else {
			System.out.println("Modo erroneo");
		}
	}
	
	/*
	 * MEtodo para pedir un modo
	 */
	public static String pedirModo() {
		Scanner sc = new Scanner(System.in);
		System.out.print("\nIntroduce el modo de conexion(ACTIVO O PASIVO): ");
		String modo = sc.nextLine().toLowerCase();
		return modo;
	}
	
	/*
	 * MEtodo principal para activar el modo pasivo
	 */
	public static void pasivo(FTPClient client) {
		client.enterLocalPassiveMode();
		System.out.println("Entrando en modo pasivo...");
		Scanner sc2 = new Scanner(System.in);
		boolean salir = false;
		do {
			mostarMenu();
			System.out.println("Selecciona una opcion: ");
			int opcion = sc2.nextInt();
			switch (opcion) {
			case 1:
				System.out.println("Listar archivos en el directorio actual");
				listarArchivos(client);
				break;
			case 2:
				System.out.println("Subir un archivo al servidor");
				subirArchivo(client);
				break;
			case 3:
				System.out.println("Descargar un archivo desde el servidor");
				bajarArchivo(client);
				break;
			case 4:
				System.out.println("Eliminar un archivo en el servidor");
				eliminarArchivos(client);
				break;
			case 5:
				System.out.println("Crear un nuevo directorio en el servidor");
				crearDirectorio(client);
				break;
			case 6:
				System.out.println("Eliminar un directorio en el servidor");
				eliminarDirectorio(client);
				break;
			case 7:
				System.out.println("Renombrar un archivo o directorio");
				renombrarArchivoODirectorio(client);
				break;
			case 8:
				System.out.println("Cambiar el directorio de trabajo en el servidor");
				cambiarDirectorio(client);
				break;
			case 9:
				System.out.println("Volver al directorio superior");
				cambiarDirectorioSuperior(client);
				break;
			case 10:
				System.out.println("Salir del servidor FTP");
				salir = true;
				break;
			default:
				System.out.println("Opción inválida");
				break;
			}
		} while (!salir);
	}
	
	/*
	 * MEtodo principal para activar el modo activo
	 */
	public static void activo(FTPClient client) {
	    System.out.println("Entrando en modo activo...");
	    client.enterLocalActiveMode();
	    
	    Scanner sc2 = new Scanner(System.in);
	    boolean salir = false;
	    do {
	        mostarMenu();
	        System.out.println("Selecciona una opción: ");
	        int opcion = sc2.nextInt();
	        switch (opcion) {
	            case 1:
	                System.out.println("Listar archivos en el directorio actual");
	                listarArchivos(client);
	                break;
	            case 2:
	                System.out.println("Subir un archivo al servidor");
	                subirArchivo(client);
	                break;
	            case 3:
	                System.out.println("Descargar un archivo desde el servidor");
	                bajarArchivo(client);
	                break;
	            case 4:
	                System.out.println("Eliminar un archivo en el servidor");
	                eliminarArchivos(client);
	                break;
	            case 5:
	                System.out.println("Crear un nuevo directorio en el servidor");
	                crearDirectorio(client);
	                break;
	            case 6:
	                System.out.println("Eliminar un directorio en el servidor");
	                eliminarDirectorio(client);
	                break;
	            case 7:
	                System.out.println("Renombrar un archivo o directorio");
	                renombrarArchivoODirectorio(client);
	                break;
	            case 8:
	                System.out.println("Cambiar el directorio de trabajo en el servidor");
	                cambiarDirectorio(client);
	                break;
	            case 9:
	                System.out.println("Volver al directorio superior");
	                cambiarDirectorioSuperior(client);
	                break;
	            case 10:
	                System.out.println("Salir del servidor FTP");
	                salir = true;
	                break;
	            default:
	                System.out.println("Opción inválida");
	                break;
	        }
	    } while (!salir);
	}



	/*
	 * Metodo para listar archivos
	 */
	public static void listarArchivos(FTPClient client) {
		//System.out.println("llega");
		 // Listar los archivos en el directorio actual del servidor
        FTPFile[] files;
		try {
			files = client.listFiles();
			System.out.println("Mostar archivos");
			// Mostrar información de los archivos
	        for (FTPFile file : files) {
	        	if(file.isDirectory()) {
	        		System.out.println("[DIRECTORIO] " + file.getName());
	        	}else {
	        		System.out.println("[ARCHIVO] " + file.getName());
	        	}
	            System.out.println("--------------------------------------------------");
	        }
		} catch (IOException e) {
			System.out.println("Error al mostrar los archivos");
		}
        
	}
	
	/*
	 * MEtodo para subir un archivo
	 */
	public static void subirArchivo(FTPClient client) {
	    Scanner scanner = new Scanner(System.in);
	    System.out.print("Escribe la ruta del archivo a subir: ");
	    String archivoLocal = scanner.nextLine();
	    File archivo = new File(archivoLocal);
	    if (archivo.exists()) {
	        try (FileInputStream inputStream = new FileInputStream(archivo)) {
	            if (client.storeFile(archivo.getName(), inputStream)) {
	                System.out.println("Archivo subido exitosamente.");
	            } else {
	                System.out.println("Error al subir el archivo.");
	            }
	        } catch (IOException e) {
	            System.out.println("Error al subir el archivo: " + e.getMessage());
	        }
	    } else {
	        System.out.println("El archivo no existe.");
	    }
	}
	
	/*
	 * Metodo para descargar un archivo
	 */
	public static void bajarArchivo(FTPClient client) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Escribe el nombre del archivo que quieres descargar");
		String archivo = sc.nextLine();
		System.out.println("Escribe la ruta donde quieres guardar el archivo + el nombre del archivo");
		String ruta = sc.nextLine();
		File archivoFinal = new File(ruta);
		try {
			FileOutputStream output = new FileOutputStream(archivoFinal);
			boolean descargado = client.retrieveFile(archivo, output);
			if(descargado) {
				System.out.println("Archivo descargado con éxito");
			}else {
				System.out.println("Error al descargar el archivo");
			}
		} catch (FileNotFoundException e) {
			System.out.println("Fichero no encontrado");
		} catch (IOException e) {
			System.out.println("Error al descargar el archivo");
		}
		
	}
	
	/*
	 * Metodo para eliminar archivos
	 */
	public static void eliminarArchivos(FTPClient client) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Escribe el nombre del archivo que quieras eliminar");
		String archivo = sc.nextLine();
		try {
			if(client.deleteFile(archivo)) {
				System.out.println("Archivo eliminado exitosamente");
			}else {
				System.out.println("Error al eliminar el archivo");
			}
			
		} catch (IOException e) {
			System.out.println("Error al eliminar el archivo " + e.getMessage());
		}
	}
	
	/*
	 * Metodo para crear un directorio
	 */
	public static void crearDirectorio(FTPClient client) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Escribe el nombre del nuevo directorio");
		String directorio = sc.nextLine();
		
		try {
			if(client.makeDirectory(directorio)) {
				System.out.println("Directorio creado correctamente");
			}else {
				System.out.println("Error al crear el directorio");
			}
		}catch(IOException e) {
			System.out.println("Error al crear el directorio: " + e.getMessage());
		}
	}
	
	
	/*
	 * Metodo para eliminar un directorio
	 */
	public static void eliminarDirectorio(FTPClient client) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Escribe el nombre del directorio que quieres borrar");
		String directorio = sc.nextLine();
		try {
			if (client.removeDirectory(directorio)) {
	            System.out.println("Directorio eliminado exitosamente.");
	        } else {
	            System.out.println("Error al eliminar el directorio. Puede que el directorio no esté vacío.");
	        }
		} catch (IOException e) {
			System.out.println("Error al eliminar el directorio: " + e.getMessage());
		}
	}
	
	/*
	 * MEtodo para renombrar un archivo o directorio
	 */
	public static void renombrarArchivoODirectorio(FTPClient client) {
	    Scanner sc = new Scanner(System.in);
	    System.out.print("Ingrese el nombre del archivo o directorio a renombrar: ");
	    String original = sc.nextLine();
	    System.out.print("Ingrese el nuevo nombre: ");
	    String nuevoNombre = sc.nextLine();

	    try {
	        if (client.rename(original, nuevoNombre)) {
	            System.out.println("Archivo o directorio renombrado exitosamente.");
	        } else {
	            System.out.println("Error al renombrar el archivo o directorio.");
	        }
	    } catch (IOException e) {
	        System.out.println("Error al renombrar: " + e.getMessage());
	    }
	}
	
	
	/*
	 * Metodo para cambiar de directorios
	 */
	public static void cambiarDirectorio(FTPClient client) {
		Scanner sc = new Scanner(System.in);
	    System.out.print("Ingrese la ruta del nuevo directorio: ");
	    String directorio = sc.nextLine();
	    try {
			if(client.changeWorkingDirectory(directorio)) {
				System.out.println("Directorio de trabajo cambiado con éxito");
			}else {
				System.out.println("Error al cambiar el directorio de trabajo");
			}
		} catch (IOException e) {
			System.out.println("Error al cambiar el directorio de trabajo: " + e.getMessage());
		}
	}
	
	/*
	 * MEtodo para cambiar al directorio superior
	 */
	public static void cambiarDirectorioSuperior(FTPClient client) {
	    try {
	        if (client.changeToParentDirectory()) {
	            System.out.println("Cambiado al directorio superior.");
	        } else {
	            System.out.println("Error al cambiar al directorio superior.");
	        }
	    } catch (IOException e) {
	        System.out.println("Error al cambiar al directorio superior: " + e.getMessage());
	    }
	}

	/*
	 * MEtodo para mostrar el menu
	 */
	public static void mostarMenu() {
		System.out.println("1. Listar archivos");
		System.out.println("2. Subir archivos");
		System.out.println("3. Descargar archivos");
		System.out.println("4. Eliminar archivos");
		System.out.println("5. Crear directorios");
		System.out.println("6. Eliminar directorios");
		System.out.println("7. Renombrar archivos y directorios");
		System.out.println("8. Cambiar directorio de trabajo");
		System.out.println("9. Cambiar al directorio superior");
		System.out.println("10. Salir");
	}

}
