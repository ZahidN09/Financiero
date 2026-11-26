package com.krakedev.financiero.entidades;

public class Cuenta {

	private String id;
	private double saldoActual;
	private String tipo;
	private Cliente propietario;
	
	public Cuenta(String id) {
		this.id = id;
		this.saldoActual = 0;
		this.tipo = "A";
		this.propietario = null;
	}
	
	public Cliente getPropietario() {
		return propietario;
	}


	public void setPropietario(Cliente propietario) {
		this.propietario = propietario;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getSaldoActual() {
		return saldoActual;
	}

	public void setSaldoActual(double saldoActual) {
		this.saldoActual = saldoActual;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public void imprimir() {
		System.out.println("********** CUENTA " + this.id + " **********");
		propietario.imprimir();
		System.out.println("Saldo Actual: " + this.saldoActual);
		System.out.println("Tipo: " + this.tipo);
	}
}
