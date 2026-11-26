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
 * Pruebas unitarias para el método Banco.retirar(double monto, Cuenta cuenta).
 *
 * Descripción funcional: - Recibe un monto y una Cuenta. - Si el monto es mayor
 * que 0 Y menor o igual al saldo actual de la cuenta, se resta al saldo actual
 * y retorna true. - Si el monto es menor o igual a 0, o es mayor que el saldo
 * actual, NO modifica el saldo y retorna false.
 *
 * Se usan únicamente constructores, getters y setters realmente existentes. Los
 * asserts sobre double usan tolerancia.
 */
public class TestRetirar {

	private static final double DELTA = 0.0001;

	private Banco banco;
	private Cliente cliente;
	private Cuenta cuenta;

	@BeforeEach
	public void setUp() {
		banco = new Banco();
		cliente = new Cliente("1712345678", "Ana", "Perez");
		cuenta = new Cuenta("1000");
		cuenta.setPropietario(cliente);
	}

	// ---------------------------------------------------------------------
	// Casos con monto válido (0 < monto <= saldo)
	// ---------------------------------------------------------------------

	/**
	 * Valida que un retiro con monto positivo y menor al saldo retorne true.
	 */
	@Test
	public void testRetiroValidoRetornaTrue() {
		cuenta.setSaldoActual(500.0);
		boolean resultado = banco.retirar(100.0, cuenta);
		assertTrue(resultado, "Un retiro con monto válido debe retornar true");
	}

	/**
	 * Valida que el retiro reste correctamente al saldo existente.
	 */
	@Test
	public void testRetiroRestaDelSaldoExistente() {
		cuenta.setSaldoActual(500.0);
		banco.retirar(150.0, cuenta);
		assertEquals(350.0, cuenta.getSaldoActual(), DELTA,
				"Tras retirar 150 de un saldo de 500, el saldo debe ser 350");
	}

	/**
	 * Valida que un retiro con monto que deja el saldo en 0 (monto == saldo) sea
	 * aceptado y retorne true. El enunciado dice "menor al saldo actual", pero la
	 * condición del método acepta monto == saldo (monto > saldo → false). Se valida
	 * el comportamiento real: retirar todo el saldo es válido.
	 */
	@Test
	public void testRetiroDeTodoElSaldoRetornaTrueYDejaCero() {
		cuenta.setSaldoActual(300.0);
		boolean resultado = banco.retirar(300.0, cuenta);
		assertTrue(resultado, "Retirar exactamente el saldo disponible debe ser válido");
		assertEquals(0.0, cuenta.getSaldoActual(), DELTA,
				"El saldo debe quedar en 0 al retirar todo el saldo disponible");
	}

	/**
	 * Valida que varios retiros consecutivos se acumulen correctamente.
	 */
	@Test
	public void testMultiplesRetirosAcumulanDescuento() {
		cuenta.setSaldoActual(1000.0);
		banco.retirar(200.0, cuenta);
		banco.retirar(300.0, cuenta);
		banco.retirar(100.0, cuenta);
		assertEquals(400.0, cuenta.getSaldoActual(), DELTA,
				"Tras retirar 200 + 300 + 100 de 1000, el saldo debe ser 400");
	}

	/**
	 * Valida que se acepten montos con decimales y el resultado sea exacto dentro
	 * de la tolerancia.
	 */
	@Test
	public void testRetiroConDecimales() {
		cuenta.setSaldoActual(100.50);
		banco.retirar(0.25, cuenta);
		assertEquals(100.25, cuenta.getSaldoActual(), DELTA, "Tras retirar 0.25 de 100.50, el saldo debe ser 100.25");
	}

	/**
	 * Valida que un retiro de monto muy pequeño pero positivo sea aceptado.
	 */
	@Test
	public void testRetiroConMontoMinimoPositivo() {
		cuenta.setSaldoActual(10.0);
		boolean resultado = banco.retirar(0.0001, cuenta);
		assertTrue(resultado, "Un monto apenas mayor que 0 debe ser aceptado");
		assertEquals(9.9999, cuenta.getSaldoActual(), DELTA, "El saldo debe reflejar el retiro del monto mínimo");
	}

	// ---------------------------------------------------------------------
	// Casos con monto inválido (<= 0)
	// ---------------------------------------------------------------------

	/**
	 * Valida que un retiro con monto 0 retorne false y no modifique el saldo.
	 */
	@Test
	public void testRetiroMontoCeroRetornaFalseYNoModificaSaldo() {
		cuenta.setSaldoActual(500.0);
		boolean resultado = banco.retirar(0.0, cuenta);
		assertFalse(resultado, "Un retiro con monto 0 debe retornar false");
		assertEquals(500.0, cuenta.getSaldoActual(), DELTA, "El saldo no debe modificarse cuando el monto es 0");
	}

	/**
	 * Valida que un retiro con monto negativo retorne false y no modifique el
	 * saldo.
	 */
	@Test
	public void testRetiroMontoNegativoRetornaFalseYNoModificaSaldo() {
		cuenta.setSaldoActual(500.0);
		boolean resultado = banco.retirar(-100.0, cuenta);
		assertFalse(resultado, "Un retiro con monto negativo debe retornar false");
		assertEquals(500.0, cuenta.getSaldoActual(), DELTA, "El saldo no debe modificarse cuando el monto es negativo");
	}

	// ---------------------------------------------------------------------
	// Casos con monto mayor al saldo (fondos insuficientes)
	// ---------------------------------------------------------------------

	/**
	 * Valida que un retiro mayor al saldo actual retorne false y no modifique el
	 * saldo.
	 */
	@Test
	public void testRetiroMayorAlSaldoRetornaFalseYNoModificaSaldo() {
		cuenta.setSaldoActual(200.0);
		boolean resultado = banco.retirar(500.0, cuenta);
		assertFalse(resultado, "Un retiro mayor al saldo debe retornar false");
		assertEquals(200.0, cuenta.getSaldoActual(), DELTA,
				"El saldo no debe modificarse cuando el monto excede el saldo actual");
	}

	/**
	 * Valida que un retiro con saldo 0 no se permita (cualquier monto > 0 falla).
	 */
	@Test
	public void testRetiroSobreCuentaSinSaldoRetornaFalse() {
		assertEquals(0.0, cuenta.getSaldoActual(), DELTA, "La cuenta debe iniciar con saldo 0");
		boolean resultado = banco.retirar(50.0, cuenta);
		assertFalse(resultado, "No debe permitirse retiro cuando el saldo es 0");
		assertEquals(0.0, cuenta.getSaldoActual(), DELTA, "El saldo debe permanecer en 0 tras un retiro rechazado");
	}

	/**
	 * Valida que un retiro apenas mayor al saldo (por una fracción mínima) retorne
	 * false y no modifique el saldo.
	 */
	@Test
	public void testRetiroApenasMayorAlSaldoRetornaFalse() {
		cuenta.setSaldoActual(100.0);
		boolean resultado = banco.retirar(100.0001, cuenta);
		assertFalse(resultado, "Un retiro que excede apenas el saldo debe retornar false");
		assertEquals(100.0, cuenta.getSaldoActual(), DELTA, "El saldo no debe modificarse al rechazar el retiro");
	}

	// ---------------------------------------------------------------------
	// Casos combinados (secuencialidad)
	// ---------------------------------------------------------------------

	/**
	 * Valida que un retiro inválido por monto negativo no impida un retiro válido
	 * posterior.
	 */
	@Test
	public void testRetiroInvalidoNoImpideRetiroValidoPosterior() {
		cuenta.setSaldoActual(100.0);
		banco.retirar(-10.0, cuenta); // inválido
		boolean resultado = banco.retirar(40.0, cuenta); // válido

		assertTrue(resultado, "Un retiro válido posterior debe retornar true");
		assertEquals(60.0, cuenta.getSaldoActual(), DELTA, "Solo debe descontarse el retiro válido (40 de 100 = 60)");
	}

	/**
	 * Valida que un retiro rechazado por fondos insuficientes no impida un retiro
	 * válido posterior con un monto menor.
	 */
	@Test
	public void testRetiroRechazadoPorFondosNoImpideRetiroValidoPosterior() {
		cuenta.setSaldoActual(50.0);
		banco.retirar(200.0, cuenta); // rechazado por fondos insuficientes
		boolean resultado = banco.retirar(30.0, cuenta); // válido

		assertTrue(resultado, "Un retiro válido posterior debe retornar true");
		assertEquals(20.0, cuenta.getSaldoActual(), DELTA, "Solo debe descontarse el retiro válido (30 de 50 = 20)");
	}

	/**
	 * Valida el flujo completo: depositar y luego retirar, verificando que el saldo
	 * final sea correcto (integración básica entre ambos métodos).
	 */
	@Test
	public void testFlujoDepositoLuegoRetiro() {
		banco.depositar(300.0, cuenta);
		banco.retirar(120.0, cuenta);
		assertEquals(180.0, cuenta.getSaldoActual(), DELTA, "Depositar 300 y retirar 120 debe dejar un saldo de 180");
	}
}