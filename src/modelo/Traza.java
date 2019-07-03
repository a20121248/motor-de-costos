/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

public class Traza {
    private String codigoCentroOrigen;
    private int nivelCentroOrigen;
    private String codigoCentroDestino;
    private int nivelCentroDestino;
    private String codigoProducto;
    private String codigoSubcanal;
    private double porcentaje;
    
    public Traza(String codigoOrigen, int nivelOrigen, String codigoDestino, int nivelDestino, double porcentaje){
        this.codigoCentroOrigen = codigoOrigen;
        this.nivelCentroOrigen = nivelOrigen;
        this.codigoCentroDestino = codigoDestino;
        this.nivelCentroDestino = nivelDestino;
        this.porcentaje = porcentaje;
    }
    
    public Traza(String codigoOrigen, int nivelOrigen,String codigoDestino, String codigoProducto, String codigoSubcanal, int nivelDestino, double porcentaje){
        this.codigoCentroOrigen = codigoOrigen;
        this.nivelCentroOrigen = nivelOrigen;
        this.codigoCentroDestino = codigoDestino;
        this.codigoProducto = codigoProducto;
        this.codigoSubcanal = codigoSubcanal;
        this.nivelCentroDestino = nivelDestino;
        this.porcentaje = porcentaje;
    }
    
    public Traza(String codigoDestino, String codigoProducto, String codigoSubcanal,int nivelDestino){
        this.codigoCentroDestino = codigoDestino;
        this.codigoProducto = codigoProducto;
        this.codigoSubcanal = codigoSubcanal;
        this.nivelCentroDestino = nivelDestino;
    }
    
    public void setCodigoCentroOrigen(String origen){
        this.codigoCentroOrigen = origen;
    }
    
    public String getCodigoCentroOrigen(){
        return this.codigoCentroOrigen;
    }
    
    public void setCodigoCentroDestino(String destino){
       this.codigoCentroDestino = destino;
    }
    
    public String getCodigoCentroDestino(){
        return this.codigoCentroDestino;
    }
    
    public void setCodigoProducto(String codigo){
       this.codigoProducto = codigo;
    }
    
    public String getCodigoProducto(){
        return this.codigoProducto;
    }
    
    public void setCodigoSubcanal(String codigo){
       this.codigoSubcanal = codigo;
    }
    
    public String getCodigoSubcanal(){
        return this.codigoSubcanal;
    }
    
    public void setNivelCentroOrigen(int nivel){
        this.nivelCentroOrigen = nivel;
    }
    
    public int getNivelCentroOrigen(){
        return this.nivelCentroOrigen;
    }
    
    public void setNivelCentroDestino(int nivel){
        this.nivelCentroDestino = nivel;
    }
    
    public int getNivelCentroDestino(){
        return this.nivelCentroDestino;
    }
    
    public void setPorcentaje(double porcentaje){
        this.porcentaje = porcentaje;
    }
    
    public double getPorcentaje(){
        return this.porcentaje;
    }
}
