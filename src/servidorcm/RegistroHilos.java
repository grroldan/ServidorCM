/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorcm;

/**
 *
 * @author usuario
 */
abstract class RegistroHilos {
    private int cantidadHilos;
    
    
    public void inicializar() {
        this.cantidadHilos = 0;
    }
    
    public void incrementar() {
        this.cantidadHilos += 1;
    }
    
    public int getantidadHilos() {
        return this.cantidadHilos;
    }
            
}
