package com.krakedev.financiero.test;

import com.krakedev.financiero.entidades.Cliente;
import com.krakedev.financiero.entidades.Cuenta;

public class TestImprimir {

	public static void main(String[] args) {
		
		Cuenta c1 = new Cuenta("007");
		
		//c1.imprimir();
		
		Cliente cl1 = new Cliente("0400000089","Luis","Pozo");
		
		//cl1.imprimir();
		
		c1.setPropietario(cl1);
		c1.imprimir();

	}

}
