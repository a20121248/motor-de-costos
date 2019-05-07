package modelo;

public class AsignacionGrupoCuenta {
    private CuentaContable planDeCuenta;
    private Tipo grupo;
    
    public AsignacionGrupoCuenta(CuentaContable planDeCuenta, Tipo grupo) {
        this.planDeCuenta = planDeCuenta;
        this.grupo = grupo;
    }
    
    public CuentaContable getPlanDeCuenta() {
        return planDeCuenta;
    }

    public void setPlanDeCuenta(CuentaContable planDeCuenta) {
        this.planDeCuenta = planDeCuenta;
    }

    public Tipo getGrupo() {
        return grupo;
    }

    public void setGrupo(Tipo grupo) {
        this.grupo = grupo;
    }
}
