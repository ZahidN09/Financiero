package com.krakedev.financiero.test.IA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.krakedev.financiero.entidades.Cliente;
import com.krakedev.financiero.entidades.Cuenta;
import com.krakedev.financiero.servicios.Banco;

/**
 * Pruebas unitarias para el método Banco.depositar(double monto, Cuenta cuenta).
 *
 * Descripción funcional:
 *  - Recibe un monto y una Cuenta.
 *  - Si el monto es mayor que 0, se suma al saldo actual de la cuenta y retorna true.
 *  - Si el monto es menor o igual a 0, NO modifica el saldo y retorna false.
 *
 * Se usan únicamente constructores, getters y setters realmente existentes.
 * Los asserts sobre double usan tolerancia.
 */
public class TestDepositar {

	private static final double DELTA = 0.0001;

	private Banco banco;
	private Cliente cliente;
	private Cuenta cuenta;

	@BeforeEach
	public void setUp() {
		// Se prepara un banco, un cliente y una cuenta con saldo inicial controlado.
		banco = new Banco();
		cliente = new Cliente("1712345678", "Ana", "Perez");
		cuenta = new Cuenta("1000");
		cuenta.setPropietario(cliente);
	}

	// ---------------------------------------------------------------------
	// Casos con monto válido (> 0)
	// ---------------------------------------------------------------------

	/**
	 * Valida que un depósito con monto positivo retorne true.
	 */
	@Test
	public void testDepositoMontoPositivoRetornaTrue() {
		boolean resultado = banco.depositar(100.0, cuenta);
		assertTrue(resultado, "Un depósito con monto positivo debe retornar true");
	}

	/**
	 * Valida que un depósito sobre una cuenta con saldo 0 deje el saldo igual al monto.
	 */
	@Test
	public void testDepositoSobreCuentaSinSaldoActualizaSaldo() {
		assertEquals(0.0, cuenta.getSaldoActual(), DELTA, "La cuenta debe iniciar con saldo 0");
		banco.depositar(150.50, cuenta);
		assertEquals(150.50, cuenta.getSaldoActual(), DELTA,
				"Tras depositar 150.50 en cuenta con saldo 0, el saldo debe ser 150.50");
	}

	/**
	 * Valida que el depósito se sume al saldo existente (no lo reemplace).
	 */
	@Test
	public void testDepositoSumaAlSaldoExistente() {
		cuenta.setSaldoActual(200.0);
		banco.depositar(50.0, cuenta);
		assertEquals(250.0, cuenta.getSaldoActual(), DELTA,
				"El depósito debe sumarse al saldo existente (200 + 50 = 250)");
	}

	/**
	 * Valida que varios depósitos consecutivos se acumulen correctamente.
	 */
	@Test
	public void testMultiplesDepositosAcumulanSaldo() {
		banco.depositar(100.0, cuenta);
		banco.depositar(250.25, cuenta);
		banco.depositar(49.75, cuenta);
		assertEquals(400.0, cuenta.getSaldoActual(), DELTA,
				"La suma de 100 + 250.25 + 49.75 debe dar 400.0");
	}

	/**
	 * Valida que se acepte un monto con decimales y que el resultado
	 * sea exacto dentro de la tolerancia.
	 */
	@Test
	public void testDepositoConDecimales() {
		banco.depositar(0.01, cuenta);
		assertEquals(0.01, cuenta.getSaldoActual(), DELTA,
				"Un depósito de 0.01 debe reflejarse con tolerancia en el saldo");
	}

	/**
	 * Valida que un monto muy pequeño pero mayor que 0 sea aceptado.
	 */
	@Test
	public void testDepositoConMontoMinimoPositivo() {
		boolean resultado = banco.depositar(0.0001, cuenta);
		assertTrue(resultado, "Un monto apenas mayor que 0 debe ser aceptado");
		assertEquals(0.0001, cuenta.getSaldoActual(), DELTA,
				"El saldo debe reflejar el monto depositado");
	}

	// ---------------------------------------------------------------------
	// Casos con monto inválido (<= 0)
	// ---------------------------------------------------------------------

	/**
	 * Valida que un depósito con monto 0 retorne false y no modifique el saldo.
	 */
	@Test
	public void testDepositoMontoCeroRetornaFalseYNoModificaSaldo() {
		cuenta.setSaldoActual(300.0);
		boolean resultado = banco.depositar(0.0, cuenta);
		assertFalse(resultado, "Un depósito con monto 0 debe retornar false");
		assertEquals(300.0, cuenta.getSaldoActual(), DELTA,
				"El saldo no debe modificarse cuando el monto es 0");
	}

	/**
	 * Valida que un depósito con monto negativo retorne false y no modifique el saldo.
	 */
	@Test
	public void testDepositoMontoNegativoRetornaFalseYNoModificaSaldo() {
		cuenta.setSaldoActual(500.0);
		boolean resultado = banco.depositar(-100.0, cuenta);
		assertFalse(resultado, "Un depósito con monto negativo debe retornar false");
		assertEquals(500.0, cuenta.getSaldoActual(), DELTA,
				"El saldo no debe modificarse cuando el monto es negativo");
	}

	/**
	 * Valida que un depósito negativo sobre una cuenta con saldo 0 no genere
	 * saldo negativo ni modifique el saldo.
	 */
	@Test
	public void testDepositoNegativoSobreCuentaVaciaNoDejaSaldoNegativo() {
		banco.depositar(-50.0, cuenta);
		assertEquals(0.0, cuenta.getSaldoActual(), DELTA,
				"El saldo debe permanecer en 0 tras un depósito inválido");
	}

	/**
	 * Valida que después de un depósito inválido, un depósito válido posterior
	 * funcione normalmente (el estado de la cuenta no se corrompe).
	 */
	@Test
	public void testDepositoInvalidoNoImpideDepositoValidoPosterior() {
		banco.depositar(-10.0, cuenta);         // inválido
		banco.depositar(0.0, cuenta);           // inválido
		boolean resultado = banco.depositar(75.0, cuenta); // válido

		assertTrue(resultado, "Un depósito válido posterior debe retornar true");
		assertEquals(75.0, cuenta.getSaldoActual(), DELTA,
				"Solo debe reflejarse el depósito válido (75.0)");
	}
}