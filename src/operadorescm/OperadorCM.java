/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package operadorescm;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.logging.Level;
import loggercm.*;
/**
 *
 * @author usuario
 */
public class OperadorCM {
    Logger log = new Logger();
    private String[] resultado;
    private String equiporemoto;
    boolean error;
    private java.util.Date fechain = new java.util.Date();
    
    
    /*
     * -------------------------------------------------------------------------
     * ejecuta la operacion indicada
     --------------------------------------------------------------------------
     */
    public String operar(String entrada, String equipo) {
        boolean operado = false;
        String salida = entrada;
        String parametros = entrada.substring(entrada.indexOf(";;") + 2);
        equiporemoto = equipo;
        resultado = entrada.split(";;");
        if (resultado[0].equals("@UPPER")) {
            salida = this.cambiar(parametros);
            operado = true;
        }

        if (resultado[0].equals("@EXECWINCMD")) {
            salida = this.execWinCMD(parametros);
            operado = true;            
        }

        if (resultado[0].equals("@EXISTFOLDER")) {
            salida = this.existFolder(parametros);
            operado = true;            
        }
        
        if (resultado[0].equals("@EXISTFILE")) {
            salida = this.existFile(parametros);
            operado = true;            
        }
        
        if (resultado[0].equals("@COPYFILE")) {
            salida = this.copyFile(parametros);
            operado = true;            
        }
        
        if (resultado[0].equals("@COPYFOLDER")) {
            salida = this.copyFolder(parametros);
            operado = true;            
        }
        
        if (resultado[0].equals("@RENMOVE")) {
            salida = this.renMove(parametros);
            operado = true;            
        }
        
        if (resultado[0].equals("@DELETE")) {
            salida = this.eliminar(parametros);
            operado = true;            
        }
        
        java.util.Date fechaout = new java.util.Date();
        
        try {
            if (operado)
                log.loguear(fechain, resultado[0], parametros, equiporemoto,
                        fechaout, (error == false) ?"REALIZADO" : "ERROR" , salida);                            
            else{
                log.loguear(fechain, resultado[0], "", equiporemoto,
                        fechaout, "ERROR" , "Sentencia desconocida");
                salida = "Sentencia Desconocida";
            }
        } catch (IOException ex) {
        }
        
        return salida;
    }
       
    /*-------------------------------------------------------------------------
     * metodo de prueba, devuelve una cadena indicada en mayusculas
    --------------------------------------------------------------------------- */
    private String cambiar(String entrada) {
        String salida = "0;;" + entrada.toUpperCase();       
        error = false;
        return salida;
    }
    
    /* ------------------------------------------------------------------------
     * Ejecuta comando en sistemas windows
     -----------------------------------------------------------------------*/
    private String execWinCMD(String entrada) {
        String stderr = "";
        String stdout = "";
        String retorno = "";
        entrada = "cmd.exe;;/C;;" + entrada;

        try {
            String[] cmd = entrada.split(";;");
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd);
            // mensajes de error
            stdClear errorGobbler = new stdClear(proc.getErrorStream(), "error");
            
            // salida
            stdClear outputGobbler = new stdClear(proc.getInputStream(), "output");
            
            // borra las entradas
            errorGobbler.start();
            errorGobbler.join();
            stderr = errorGobbler.salida;

            outputGobbler.start();
            outputGobbler.join();
            stdout = outputGobbler.salida;


            int exitVal = proc.waitFor();
            if (exitVal != 0) {
                retorno = "1;;" + stderr;
                error = true;
            } else {
                retorno = "0;;" + stdout;
                error = false;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return retorno;
    }
    
    /*------------------------------------------------------------------------
     *   Verifica si existe una carpeta indicada
     ------------------------------------------------------------------------*/
    public String existFolder(String entrada) {
        error = true;
        File carpeta = new File(entrada);
        if (carpeta.exists()) 
            if (carpeta.isDirectory())
                error = false;
        
        if (!error)
            return "0;;Directorio encontrado";
        else
            return "1;;Directorio NO encontrado";
        
    }
    
    /*------------------------------------------------------------------------
     *   Verifica si existe un archivo indicada
     ------------------------------------------------------------------------*/
    public String existFile(String entrada) {
        error = true;
        File archivo = new File(entrada);
        if (archivo.exists()) {
            if (archivo.isFile()) {
                error = false;
            }
        }

        if (!error) {
            return "0;;Archivo encontrado";
        } else {
            return "1;;Archivo NO encontrado";
        }

    }

    /*
     * --------------------------------------------------------------------------
     * Copia un directorio con todo su contenido
     --------------------------------------------------------------------------*/
    public String copyDir(File srcDir, File dstDir) {
        String retorno = null;
        try {
            if (srcDir.isDirectory()) {
                if (!dstDir.exists()) {
                    dstDir.mkdir();
                }

                String[] children = srcDir.list();
                for (int i = 0; i < children.length; i++) {
                    retorno = copyDir(new File(srcDir, children[i]),
                              new File(dstDir, children[i]));
                    if (retorno != null)
                        return retorno;
                }
                
            } else {
                retorno = copy(srcDir, dstDir);
                if (retorno != null)
                    return retorno;
            }
        } catch (Exception e) {
            return e.toString();
        }
        return retorno;
    }

    /*--------------------------------------------------------------------------
     * Copia un solo archivo
     -------------------------------------------------------------------------*/
    private String copy(File s, File t)
    {
        try{
              FileChannel in = (new FileInputStream(s)).getChannel();
              FileChannel out = (new FileOutputStream(t)).getChannel();
              in.transferTo(0, s.length(), out);
              in.close();
              out.close();
              return null;
        }
        catch(Exception e)
        {
            return e.toString();
        }
    }
    
    /*--------------------------------------------------------------------------
     * Copia archivo segun parametros de mensaje
     -------------------------------------------------------------------------*/
    private String copyFile(String entrada) {
       String[] parms = entrada.split(";;");
       String retorno = this.copy(new File(parms[0]), new File(parms[1]));
       if (retorno == null) {
           error = false;
           return "0;;Archivo Copiado"; }
       else {
           error = true;
           return "1;;" + retorno;
       }
    }
    
    /*--------------------------------------------------------------------------
     * Copia carpetas segun parametros de mensaje
     -------------------------------------------------------------------------*/
    private String copyFolder(String entrada) {
       String[] parms = entrada.split(";;");
       String retorno = this.copyDir(new File(parms[0]), new File(parms[1]));
       if (retorno == null) {
           error = false;
           return "0;;Directorio Copiado"; }
       else {
           error = true;
           return "1;;" + retorno;
       }
    }
    
     /*--------------------------------------------------------------------------
     * renombra o mueve un archivo o directorio indicado 
     -------------------------------------------------------------------------*/
    private String renMove(String entrada) {
       String[] parms = entrada.split(";;");
       File origen = new File(parms[0]);
       File destino = new File(parms[1]);
       if (origen.renameTo(destino)) {
           error = false;
           return "0;;Archivo/Directorio movido"; }
       else {
           error = true;
           return "1;;Archivo/Directorio NO movido";
       }
    }   
    
     /*--------------------------------------------------------------------------
     * elimina un fichero  
     -------------------------------------------------------------------------*/
    private String eliminar(String entrada) {
       File origen = new File(entrada);
        if (origen.delete()) {
           error = false;
           return "0;;Archivo/Directorio eliminado"; }
       else {
           error = true;
           return "1;;Archivo/Directorio NO eliminado";
       }
    } 
}

/*------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------
 -------------------------------------------------------------------------------------*/
class stdClear extends Thread {

    InputStream is;
    String std, salida;
       
    stdClear(InputStream is, String std) {
        this.is = is;
        this.std = std;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            salida = "";
            while ((line = br.readLine()) != null) {
                  salida = salida + line;
            }
           // System.out.println(std + ">"+ salida);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
