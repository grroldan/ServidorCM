package servidorcm;

import java.net.*;
import java.io.*;
import operadorescm.*;
import loggercm.*;

/**
 *
 * @author usuario
 */
public class ServMultiHilo extends Thread{  
    private Socket socket = null;
    java.util.Date fechain = new java.util.Date();
    java.util.Date fechaout = null;
    String remoto = null;    
    Logger log = new Logger();
    
    public ServMultiHilo(Socket socket) throws IOException {        
	super("ServMultiHilo");
        fechaout = new java.util.Date();
	this.socket = socket;    
        remoto = socket.getRemoteSocketAddress().toString();
        ServidorCM.cantHilos ++;
        log.loguear(fechain, "@CONNECT", "", remoto, fechaout,
                    "ACEPTADO", "Conexion Iniciada");
    }

    public void run() {
        
	try {
	    PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
	    BufferedReader entrada = new BufferedReader(
				    new InputStreamReader(
				    socket.getInputStream()));

	    String lineaEntrada, lineaSalida;
	    OperadorCM miop = new OperadorCM();
	    
	    while (((lineaEntrada = entrada.readLine()) != null) && 
                    (!lineaEntrada.equals("@DISCONNECT"))) {
                    lineaSalida = miop.operar(lineaEntrada, remoto);                
		    salida.println(lineaSalida);
	    }
            ServidorCM.cantHilos--;
            salida.println("Conexion Cerrada");
	    salida.close();
	    entrada.close();
	    socket.close();
            fechaout = new java.util.Date();
            log.loguear(fechain, "@DISCONNECT", "", remoto, fechaout,
                    "ACEPTADO", "Conexion Cerrada");

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }    
    
}