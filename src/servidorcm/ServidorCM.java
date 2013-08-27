/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorcm;

import java.net.*;
import java.io.*;
import loggercm.*;
import java.util.Properties;
import java.util.logging.Level;
/**
 *
 * @author usuario
 */
public class ServidorCM extends RegistroHilos {
    private static int puerto;
    public static int cantHilos = 0;
            
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Logger log = new Logger();
        boolean escuchar = true;
        ServerSocket servidor = null;
        java.util.Date fecha = new java.util.Date();
        
        log.vercarpeta();
        
        /*lee el archivo de propiedaes y sale del proceso si no lo encuentra o 
         *se produce algun error en la lectura                                 */ 
        ServidorCM srvcm = new ServidorCM();
        if (!srvcm.leeParms()){
            log.loguear(fecha, "@READ PARMS", "",
                        "localhost", fecha, 
                        "ERROR: No se pudo leer el archivo confcm.properties", 
                        "Servicio no iniciado");
            System.exit(-1);
        }
            
                
         try {
             servidor = new ServerSocket(puerto);    
             log.loguear(fecha, "@START SERVICE", " " , 
                         "localhost", fecha, 
                         "REALIZADO",  "escuchando en puerto " + puerto);
             cantHilos = 0;// inicia el contador de hilos
         }catch (IOException e) {
            log.loguear(fecha, "@START SERVICE", " " , 
                        "localhost", fecha, 
                        "ERROR: No se puede escuchar en el puerto " + puerto,
                        "Servicio no iniciado");
            System.exit(-1);
        }
         
        while (escuchar)
            new ServMultiHilo(servidor.accept()).start();
        
        servidor.close();
         
    }

    /*----------------------------------------------------------------------
    *lee el archivo de propiedades y retorna true si la lectura es correcta
    * lee el puerto donde escucha el proceso
    -------------------------------------------------------------------------*/
    private boolean leeParms() {
        try {
            Properties propiedades = new Properties();
            propiedades.load(new FileInputStream(System.getProperty("user.dir") + 
                             "\\src\\propiedades\\confcm.properties"));
            puerto = Integer.parseInt(propiedades.getProperty("puerto"));
            return true;
        } catch (IOException ex) {
          return false;  
        }
    }
    
}
