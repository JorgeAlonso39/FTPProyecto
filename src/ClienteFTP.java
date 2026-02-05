import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class ClienteFTP {

	public static void main(String[] args) {
		FTPClient client = new FTPClient();
		try {
			//Conectamos al servidor FTP
			client.connect("ftp.gnu.org");
			if(client.isConnected()) {
				System.out.println("Conexión existosa al servidor");
			}else {
				System.out.println("Conexion fallida");
				return;
			}
			
			//Hacemos login (usuario,password)
			boolean login = client.login("anonymous", "");
			if(login) {
				System.out.println("Login existosa al servidor");
			}else {
				System.out.println("Login fallida");
				return;
			}
			//Hacer lo que tenga que hacer el clientes
			
			//Vamos a listar todos los ficheros del servidor
			FTPFile[] files = client.listFiles();
			for(FTPFile file : files) {
				System.out.println(file.getName());
			}
			//Vamos a bajarnos el fichero MISSING-FILES.README
			String remoteFile = "/MISSING-FILES.README";
			String localFile = "C:/Users/Jorge/Desktop/MISSING-FILES.README";
			
			try (FileOutputStream fos = new FileOutputStream(localFile)){
				boolean success = client.retrieveFile(remoteFile, fos);
				if(success) {
					System.out.println("Fichero descargado con exito");
				}else {
					System.out.println("Error al descargar el fichero");
					System.out.println("Codigo de error: " + client.getReplyCode());
					System.out.println("Mensaje de error: " + client.getReplyString());
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
			
			//Subir un fichero
			String loadfile = "C:/Users/Jorge/Desktop/ejemplo.db";
			String remoteFileUpload = "";
			try (FileInputStream fis = new FileInputStream(loadfile)){
			boolean successUpload =client.storeFile(remoteFileUpload, fis);
			if(successUpload) {
				System.out.println("Fichero subido con exito");
			}else {
				System.out.println("Error al subir el fichero");
				System.out.println("Codigo de error: " + client.getReplyCode());
				System.out.println("Mensaje de error: " + client.getReplyString());
			}
			}catch(IOException e) {
				
			}
			//Hacemos logout
			client.logout();
		}catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				client.disconnect();
			} catch(IOException e2) {
				e2.printStackTrace();
			}
		}

	}

}
