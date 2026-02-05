# FTPProyecto
Este proyecto consiste en una arquitectura Cliente-Servidor diseñada para la transferencia de archivos mediante el protocolo FTP. Está desarrollado en Java y permite gestionar de forma remota el sistema de ficheros de un servidor.

Componentes Principales

Servidor (ClienteS.java): Es un servidor multihilo que utiliza ServerSocket para escuchar conexiones y Socket para la transferencia de datos. Gestiona la lógica de comandos como LIST (listar), RETR (descargar), STOR (subir) y DELE (borrar).


Cliente (ClienteFTP.java): Utiliza la librería org.apache.commons.net.ftp para conectarse al servidor, permitiendo al usuario interactuar mediante un menú de consola.

Funcionalidades y Características

Modos de Conexión: El programa soporta tanto el modo Activo como el modo Pasivo para el intercambio de datos.


Gestión de Usuarios: Implementa un sistema de autenticación basado en un archivo Usuarios.txt. Dependiendo del usuario, el programa aplica diferentes niveles de permisos (por ejemplo, solo el "admin" puede borrar archivos o crear directorios).


Operaciones de Archivos: Permite realizar un CRUD completo sobre archivos y carpetas, incluyendo renombrar elementos y navegar por la estructura de directorios (CWD y CDUP).


Autoría: El código fue desarrollado por Jorge Alonso Fernández.
