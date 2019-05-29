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
    private Partida  partida;
    private Tipo cuentaContable;
    
    public AsignacionPartidaCuenta(Partida partida, Tipo cuentaContable) {
        this.partida = partida;
        this.cuentaContable = cuentaContable;
    }
    
    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public Tipo getCuentaContable() {
        return cuentaContable;
    }

    public void setCuentaContable(Tipo cuentaContable) {
        this.cuentaContable = cuentaContable;
    }
}
