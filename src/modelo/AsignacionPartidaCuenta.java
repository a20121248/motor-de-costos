/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

/**
 *
 * @author briggette.olenka.ro1
 */
public class AsignacionPartidaCuenta {
    private String partida;
    private String cuentaContable;
    
    public AsignacionPartidaCuenta(String codigoCuenta, String codigoPartida) {
        this.partida = codigoPartida;
        this.cuentaContable = codigoCuenta;
    }
    
    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

    public String getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(String cuentaContable) {
        this.cuentaContable = cuentaContable;
    }
}
