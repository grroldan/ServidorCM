/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loggercm;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 *
 * @author usuario
 */
public class Logger {
    private BufferedWriter bw = null;
    Calendar cal = new GregorianCalendar();
    
    
    /*-------------------------------------------------------------------------
     * Verifica si existe el archivo con logs del dia en ejecucion, 
     * si no existe, lo crea con la cabecera
     ------------------------------------------------------------------------*/
    private void verFile() throws UnknownHostException {
       InetAddress localHost = InetAddress.getLocalHost();
       String archivo = this.getarchivo();                                      
                                                  
       File fichero = new File(archivo); 
       if (!fichero.exists())
           try {
            fichero.createNewFile();
            bw = new BufferedWriter(new FileWriter(fichero));
            bw.write("<html>" + "\n");
            bw.write("<head>" + "\n");
            bw.write("  <title>Log ServidorCM " + "</title>");
            bw.write("<style type=\"text/css\">" + "\n");
            bw.write("<!--" + "\n");
            bw.write("body, table {font-family: arial,sans-serif; font-size: x-small;}" + "\n");
            bw.write("th {background: #336699; color: #FFFFFF; text-align: left;}" + "\n");
            bw.write("p {text-align: center;font-size: x-large;}" + "\n");
            bw.write("-->" + "\n");            
            bw.write("</style>" + "\n");
            bw.write("</head>" + "\n");
            bw.write("<body style=\"background-color: rgb(255, 255, 255);\"" + "\n");
            bw.write(" topmargin=\"6\" leftmargin=\"6\">" + "\n");
            bw.write("<hr noshade=\"noshade\" size=\"1\">" + "\n");
            bw.write("<p>  Log ServidorCM " +  Integer.toString(cal.get(Calendar.DATE)) + "/" + 
                     Integer.toString(cal.get(Calendar.MONTH))  + "/" + 
                     Integer.toString(cal.get(Calendar.YEAR)) + "</p>" + "\n");
            bw.write("<br>" + "\n");
            bw.write("Ejecutandose en " + localHost.getHostName() + " (" + localHost.getHostAddress() + ")<br>" + "\n"); 
            bw.write("<br>" + "\n");
            bw.write("<table border=\"1\" bordercolor=\"#224466\" cellpadding=\"4\"" + "\n");
            bw.write(" cellspacing=\"0\" width=\"100%\">" + "\n");
            bw.write("  <tbody>" + "\n");
            bw.write("    <tr>" + "\n");
            bw.write("      <th>Hora Ingreso</th>" + "\n");
            bw.write("      <th>Accion</th>" + "\n");
            bw.write("      <th>Parametros</th>" + "\n");
            bw.write("      <th>Equipo Remoto</th>" + "\n");
            bw.write("      <th>Hora Respuesta</th>" + "\n");
            bw.write("      <th>Resultado</th>" + "\n");
            bw.write("      <th>Respuesta</th>" + "\n");
            bw.write("    </tr>" + "\n");
            bw.close();
           }catch(IOException e){}
    }
    
        
    /*--------------------------------------------------------------------------
    * Comprueba si existe la carpeta de logs,
    * si no existe, la crea
    -------------------------------------------------------------------------- */
    public void vercarpeta() {
      String directorio = System.getProperty("user.dir") + "\\logs";
      File folder = new File(directorio);
      if (!folder.exists()){
          folder.mkdir();
      }
    }
    
    public synchronized void loguear(Date horain, String accion, String parms, 
                                     String equipo, Date horaout, 
                                     String resultado, String respuesta) 
                                     throws IOException {
        this.verFile();
        File fichero = new File(this.getarchivo());        
        bw = new BufferedWriter(new FileWriter(fichero, true));
        bw.write("    <tr>" + "\n");
        bw.write("      <td>" + horain.toString() + "</td>" + "\n");
        bw.write("      <td>" + accion + "</td>" + "\n");
        bw.write("      <td>" + parms + "</td>" + "\n");
        bw.write("      <td>" + equipo + "</td>" + "\n");
        bw.write("      <td>" + horaout.toString() + "</td>" + "\n");
        bw.write("      <td>" + resultado + "</td>" + "\n");
        bw.write("      <td>" + respuesta + "</td>" + "\n");
        bw.write("    </tr>" + "\n");
        bw.close();        
    }
    
    /*-------------------------------------------------------------------------
     *  obtiene la ruta del archivo log conteniendo el nombre segun el dia
     *  y la retorna en un string
     -------------------------------------------------------------------------*/
    private String getarchivo() {
        Formatter fmt = new Formatter();
        String directorio = System.getProperty("user.dir");        
        String mes = fmt.format("%02d",cal.get(Calendar.MONTH) + 1).toString();
        String dia = fmt.format("%02d",cal.get(Calendar.DAY_OF_MONTH)).toString();
        String archivo = directorio + "\\logs\\cml" +
                         Integer.toString(cal.get(Calendar.YEAR)) + dia + ".html";
        return archivo;
    }
}
