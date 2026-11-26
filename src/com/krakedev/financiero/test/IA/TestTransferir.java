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
 * Pruebas unitarias para el método Banco.transferir(double monto, Cuenta origen, Cuenta destino).
 *
 * Descripción funcional:
 *  - Recibe un monto, una cuenta origen y una cuenta destino.
 *  - Si el monto es mayor que 0 y se puede retirar de la cuenta origen:
 *      1) se resta el monto al saldo de origen,
 *      2) se deposita el monto en la cuenta destino,
 *      3) se retorna el resultado del depósito.
 *  - Si no se puede retirar (monto <= 0 o fondos insuficientes): retorna false
 *    y NO se modifica ninguna de las dos cuentas.
 *
 * Se usan únicamente constructores, getters y setters realmente existentes.
 * Los asserts sobre double usan tolerancia.
 */
public class TestTransferir {

	private static final double DELTA = 0.0001;

	private Banco banco;
	private Cliente cliente;
	private Cuenta origen;
	private Cuenta destino;

	@BeforeEach
	public void setUp() {
		banco = new Banco();
		cliente = new Cliente("1712345678", "Ana", "Perez");
		origen = new Cuenta("1000");
		origen.setPropietario(cliente);
		destino = new Cuenta("1001");
		destino.setPropietario(cliente);
	}

	// ---------------------------------------------------------------------
	// Casos con transferencia válida
	// ---------------------------------------------------------------------

	/**
	 * Valida que una transferencia válida retorne true.
	 */
	@Test
	public void testTransferenciaValidaRetornaTrue() {
		origen.setSaldoActual(500.0);
		boolean resultado = banco.transferir(100.0, origen, destino);
		assertTrue(resultado, "Una transferencia válida debe retornar true");
	}

	/**
	 * Valida que la cuenta origen disminuya exactamente el monto transferido.
	 */
	@Test
	public void testTransferenciaDescuentaDeOrigen() {
		origen.setSaldoActual(500.0);
		banco.transferir(150.0, origen, destino);
		assertEquals(350.0, origen.getSaldoActual(), DELTA,
				"El saldo de origen debe disminuir en el monto transferido (500 - 150 = 350)");
	}

	/**
	 * Valida que la cuenta destino aumente exactamente el monto transferido.
	 */
	@Test
	public void testTransferenciaAcreditaEnDestino() {
		origen.setSaldoActual(500.0);
		destino.setSaldoActual(100.0);
		banco.transferir(150.0, origen, destino);
		assertEquals(250.0, destino.getSaldoActual(), DELTA,
				"El saldo de destino debe aumentar en el monto transferido (100 + 150 = 250)");
	}

	/**
	 * Valida que origen y destino se actualicen de forma consistente
	 * en una misma transferencia (el monto que sale es el que entra).
	 */
	@Test
	public void testTransferenciaConservaMontoEntreCuentas() {
		origen.setSaldoActual(1000.0);
		destino.setSaldoActual(200.0);
		banco.transferir(300.0, origen, destino);

		assertEquals(700.0, origen.getSaldoActual(), DELTA,
				"Origen debe quedar en 700 tras transferir 300 de 1000");
		assertEquals(500.0, destino.getSaldoActual(), DELTA,
				"Destino debe quedar en 500 tras recibir 300 sobre 200");
	}

	/**
	 * Valida que transferir exactamente todo el saldo disponible sea aceptado.
	 */
	@Test
	public void testTransferenciaDeTodoElSaldoRetornaTrueYDejaOrigenEnCero() {
		origen.setSaldoActual(300.0);
		boolean resultado = banco.transferir(300.0, origen, destino);

		assertTrue(resultado, "Transferir todo el saldo disponible debe ser válido");
		assertEquals(0.0, origen.getSaldoActual(), DELTA, "Origen debe quedar en 0");
		assertEquals(300.0, destino.getSaldoActual(), DELTA, "Destino debe recibir todo el monto");
	}

	/**
	 * Valida que se acepten montos con decimales y el resultado sea exacto
	 * dentro de la tolerancia.
	 */
	@Test
	public void testTransferenciaConDecimales() {
		origen.setSaldoActual(100.75);
		destino.setSaldoActual(50.25);
		banco.transferir(25.50, origen, destino);

		assertEquals(75.25, origen.getSaldoActual(), DELTA, "Origen debe quedar en 75.25");
		assertEquals(75.75, destino.getSaldoActual(), DELTA, "Destino debe quedar en 75.75");
	}

	/**
	 * Valida que múltiples transferencias se acumulen correctamente.
	 */
	@Test
	public void testMultiplesTransferenciasAcumulan() {
		origen.setSaldoActual(1000.0);
		destino.setSaldoActual(0.0);
		banco.transferir(100.0, origen, destino);
		banco.transferir(250.0, origen, destino);
		banco.transferir(50.0, origen, destino);

		assertEquals(600.0, origen.getSaldoActual(), DELTA,
				"Origen debe quedar en 600 tras retirar 100 + 250 + 50 de 1000");
		assertEquals(400.0, destino.getSaldoActual(), DELTA,
				"Destino debe acumular 400 por las tres transferencias");
	}

	// ---------------------------------------------------------------------
	// Casos con monto inválido (<= 0)
	// ---------------------------------------------------------------------

	/**
	 * Valida que una transferencia con monto 0 retorne false y no modifique
	 * ninguna de las dos cuentas.
	 */
	@Test
	public void testTransferenciaMontoCeroRetornaFalseYNoModificaSaldos() {
		origen.setSaldoActual(500.0);
		destino.setSaldoActual(200.0);
		boolean resultado = banco.transferir(0.0, origen, destino);

		assertFalse(resultado, "Una transferencia con monto 0 debe retornar false");
		assertEquals(500.0, origen.getSaldoActual(), DELTA, "Origen no debe modificarse");
		assertEquals(200.0, destino.getSaldoActual(), DELTA, "Destino no debe modificarse");
	}

	/**
	 * Valida que una transferencia con monto negativo retorne false y no
	 * modifique ninguna de las dos cuentas.
	 */
	@Test
	public void testTransferenciaMontoNegativoRetornaFalseYNoModificaSaldos() {
		origen.setSaldoActual(500.0);
		destino.setSaldoActual(200.0);
		boolean resultado = banco.transferir(-100.0, origen, destino);

		assertFalse(resultado, "Una transferencia con monto negativo debe retornar false");
		assertEquals(500.0, origen.getSaldoActual(), DELTA, "Origen no debe modificarse");
		assertEquals(200.0, destino.getSaldoActual(), DELTA, "Destino no debe modificarse");
	}

	// ---------------------------------------------------------------------
	// Casos con fondos insuficientes en origen
	// ---------------------------------------------------------------------

	/**
	 * Valida que una transferencia mayor al saldo de origen retorne false
	 * y no modifique ninguna de las dos cuentas.
	 */
	@Test
	public void testTransferenciaMayorAlSaldoOrigenRetornaFalseYNoModificaSaldos() {
		origen.setSaldoActual(100.0);
		destino.setSaldoActual(500.0);
		boolean resultado = banco.transferir(200.0, origen, destino);

		assertFalse(resultado, "Una transferencia mayor al saldo de origen debe retornar false");
		assertEquals(100.0, origen.getSaldoActual(), DELTA, "Origen no debe modificarse");
		assertEquals(500.0, destino.getSaldoActual(), DELTA, "Destino no debe modificarse");
	}

	/**
	 * Valida que una transferencia sobre una cuenta origen con saldo 0 sea rechazada
	 * y no modifique ninguna cuenta.
	 */
	@Test
	public void testTransferenciaDesdeOrigenSinSaldoRetornaFalse() {
		assertEquals(0.0, origen.getSaldoActual(), DELTA, "Origen debe iniciar con saldo 0");
		destino.setSaldoActual(100.0);
		boolean resultado = banco.transferir(50.0, origen, destino);

		assertFalse(resultado, "No debe permitirse transferir desde una cuenta sin saldo");
		assertEquals(0.0, origen.getSaldoActual(), DELTA, "Origen debe permanecer en 0");
		assertEquals(100.0, destino.getSaldoActual(), DELTA, "Destino no debe modificarse");
	}

	/**
	 * Valida que una transferencia apenas mayor al saldo de origen sea rechazada
	 * (por una fracción mínima) y no modifique los saldos.
	 */
	@Test
	public void testTransferenciaApenasMayorAlSaldoOrigenRetornaFalse() {
		origen.setSaldoActual(100.0);
		destino.setSaldoActual(0.0);
		boolean resultado = banco.transferir(100.0001, origen, destino);

		assertFalse(resultado, "Una transferencia que excede apenas el saldo debe rechazarse");
		assertEquals(100.0, origen.getSaldoActual(), DELTA, "Origen no debe modificarse");
		assertEquals(0.0, destino.getSaldoActual(), DELTA, "Destino no debe modificarse");
	}

	// ---------------------------------------------------------------------
	// Casos combinados / secuenciales
	// ---------------------------------------------------------------------

	/**
	 * Valida que una transferencia rechazada por fondos insuficientes no impida
	 * una transferencia válida posterior con un monto menor.
	 */
	@Test
	public void testTransferenciaRechazadaNoImpideTransferenciaValidaPosterior() {
		origen.setSaldoActual(50.0);
		destino.setSaldoActual(0.0);

		banco.transferir(500.0, origen, destino);        // rechazada
		boolean resultado = banco.transferir(30.0, origen, destino); // válida

		assertTrue(resultado, "Una transferencia válida posterior debe retornar true");
		assertEquals(20.0, origen.getSaldoActual(), DELTA, "Solo debe descontarse lo transferido (50 - 30 = 20)");
		assertEquals(30.0, destino.getSaldoActual(), DELTA, "Solo debe acreditarse lo transferido (30)");
	}

	/**
	 * Valida que una transferencia rechazada por monto inválido no impida
	 * una transferencia válida posterior.
	 */
	@Test
	public void testTransferenciaMontoInvalidoNoImpideTransferenciaValidaPosterior() {
		origen.setSaldoActual(100.0);
		destino.setSaldoActual(0.0);

		banco.transferir(-10.0, origen, destino);         // inválida por monto
		banco.transferir(0.0, origen, destino);           // inválida por monto
		boolean resultado = banco.transferir(60.0, origen, destino); // válida

		assertTrue(resultado, "Una transferencia válida posterior debe retornar true");
		assertEquals(40.0, origen.getSaldoActual(), DELTA, "Solo debe descontarse lo transferido (100 - 60 = 40)");
		assertEquals(60.0, destino.getSaldoActual(), DELTA, "Solo debe acreditarse lo transferido (60)");
	}

	/**
	 * Valida que transferir entre la misma cuenta (origen == destino) sea aceptado
	 * por la lógica actual (retira y vuelve a depositar) y deje el saldo intacto.
	 * Es un caso borde que documenta el comportamiento real del método.
	 */
	@Test
	public void testTransferenciaMismaCuentaNoAlteraSaldo() {
		origen.setSaldoActual(500.0);
		boolean resultado = banco.transferir(100.0, origen, origen);

		assertTrue(resultado, "Con la lógica actual, retirar y depositar en la misma cuenta retorna true");
		assertEquals(500.0, origen.getSaldoActual(), DELTA,
				"El saldo final debe permanecer igual (se resta y se vuelve a sumar el mismo monto)");
	}

	/**
	 * Valida que una transferencia de una cuenta a otra no afecte el saldo
	 * de una tercera cuenta ajena a la operación.
	 */
	@Test
	public void testTransferenciaNoAfectaOtrasCuentas() {
		Cuenta tercera = new Cuenta("1002");
		tercera.setPropietario(cliente);
		tercera.setSaldoActual(999.0);

		origen.setSaldoActual(500.0);
		destino.setSaldoActual(100.0);
		banco.transferir(150.0, origen, destino);

		assertEquals(999.0, tercera.getSaldoActual(), DELTA,
				"Una cuenta ajena a la transferencia no debe verse afectada");
	}
}