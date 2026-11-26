package com.krakedev.financiero.servicios;

import com.krakedev.financiero.entidades.Cliente;
import com.krakedev.financiero.entidades.Cuenta;

public class Banco {

	private int ultimoCodigo = 1000;

	public Banco() {

	}

	public int getUltimoCodigo() {
		return ultimoCodigo;
	}

	public void setUltimoCodigo(int ultimoCodigo) {
		this.ultimoCodigo = ultimoCodigo;
	}

	public Cuenta crearCuenta(Cliente cliente) {

		String codigoStr = this.ultimoCodigo + "";
		this.ultimoCodigo ++;
		
		Cuenta c1 = new Cuenta(codigoStr);
		c1.setPropietario(cliente);
		
		return c1;
		
	}
	
	public boolean depositar(double monto, Cuenta cuenta) {
		if(monto <= 0) return false;
		cuenta.setSaldoActual(monto + cuenta.getSaldoActual());
		return true;
	}
	
	public boolean retirar(double monto, Cuenta cuenta) {
		if(monto <= 0 || monto > cuenta.getSaldoActual()) return false;
		cuenta.setSaldoActual(cuenta.getSaldoActual() - monto);
		return true;
	}
	
	public boolean transferir(double monto, Cuenta origen,Cuenta destino) {
		if(!retirar(monto,origen)) return false;
		return depositar(monto, destino);
	}

}
