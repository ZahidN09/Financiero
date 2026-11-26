package com.krakedev.financiero.test.IA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.krakedev.financiero.entidades.Cliente;
import com.krakedev.financiero.entidades.Cuenta;
import com.krakedev.financiero.servicios.Banco;

/**
 * Pruebas unitarias para el método Banco.crearCuenta(Cliente cliente).
 *
 * Los casos están construidos a partir de la descripción funcional:
 *  - Recibe un Cliente como entrada.
 *  - Toma el ultimoCodigo del banco, lo convierte a String y lo usa como id de la Cuenta.
 *  - Incrementa en 1 el ultimoCodigo.
 *  - Asigna el Cliente recibido como propietario de la cuenta.
 *  - Retorna la Cuenta creada.
 *
 * Se usan únicamente constructores, getters y setters realmente existentes.
 */
public class TestCrearCuenta {

	private Banco banco;
	private Cliente cliente;

	@BeforeEach
	public void setUp() {
		// Cada prueba parte de un banco nuevo y un cliente válido de entrada.
		banco = new Banco();
		cliente = new Cliente("1712345678", "Ana", "Perez");
	}

	/**
	 * Valida que el método retorne una Cuenta no nula al recibir un Cliente.
	 * Es el caso base: la creación debe producir siempre un objeto.
	 */
	@Test
	public void testCrearCuentaRetornaCuentaNoNula() {
		Cuenta cuenta = banco.crearCuenta(cliente);
		assertNotNull(cuenta, "El método crearCuenta debe retornar una cuenta no nula");
	}

	/**
	 * Valida que el id de la cuenta corresponda al ultimoCodigo inicial (1000)
	 * convertido a String, tal como lo describe la descripción funcional.
	 */
	@Test
	public void testCrearCuentaAsignaIdDesdeUltimoCodigoInicial() {
		Cuenta cuenta = banco.crearCuenta(cliente);
		assertEquals("1000", cuenta.getId(), "El id debe corresponder al ultimoCodigo inicial (1000)");
	}

	/**
	 * Valida que al crear la cuenta se incremente en 1 el ultimoCodigo del banco.
	 * Antes: 1000. Después de crear: 1001.
	 */
	@Test
	public void testCrearCuentaIncrementaUltimoCodigo() {
		int codigoAntes = banco.getUltimoCodigo();
		banco.crearCuenta(cliente);
		int codigoDespues = banco.getUltimoCodigo();
		assertEquals(codigoAntes + 1, codigoDespues, "El ultimoCodigo debe incrementarse en 1 tras crear la cuenta");
	}

	/**
	 * Valida que la cuenta creada tenga como propietario exactamente al cliente
	 * recibido como parámetro (misma referencia).
	 */
	@Test
	public void testCrearCuentaAsignaPropietarioRecibido() {
		Cuenta cuenta = banco.crearCuenta(cliente);
		assertSame(cliente, cuenta.getPropietario(),
				"La cuenta debe tener como propietario al cliente pasado como parámetro");
	}

	/**
	 * Valida que el propietario asignado conserve sus datos originales
	 * (cédula, nombre y apellido) al consultarlo desde la cuenta.
	 */
	@Test
	public void testCrearCuentaMantieneDatosDelPropietario() {
		Cuenta cuenta = banco.crearCuenta(cliente);
		assertEquals("1712345678", cuenta.getPropietario().getCedula(), "La cédula del propietario debe conservarse");
		assertEquals("Ana", cuenta.getPropietario().getNombre(), "El nombre del propietario debe conservarse");
		assertEquals("Perez", cuenta.getPropietario().getApellido(), "El apellido del propietario debe conservarse");
	}

	/**
	 * Valida que la cuenta creada tenga saldo actual 0.0 usando tolerancia,
	 * ya que el saldo es de tipo double.
	 */
	@Test
	public void testCrearCuentaIniciaConSaldoCero() {
		Cuenta cuenta = banco.crearCuenta(cliente);
		assertEquals(0.0, cuenta.getSaldoActual(), 0.0001,
				"Una cuenta recién creada debe iniciar con saldo 0.0");
	}

	/**
	 * Valida que la cuenta creada tenga el tipo por defecto esperado ("A"),
	 * tal como lo establece el constructor de Cuenta.
	 */
	@Test
	public void testCrearCuentaIniciaConTipoPorDefecto() {
		Cuenta cuenta = banco.crearCuenta(cliente);
		assertEquals("A", cuenta.getTipo(), "Una cuenta recién creada debe tener el tipo por defecto 'A'");
	}

	/**
	 * Valida la secuencia de creación: al crear varias cuentas,
	 * sus ids deben corresponder a ultimoCodigo, ultimoCodigo+1, ultimoCodigo+2, ...
	 * Comenzando desde 1000: "1000", "1001", "1002".
	 */
	@Test
	public void testCrearVariasCuentasGeneraIdsSecuenciales() {
		Cuenta c1 = banco.crearCuenta(cliente);
		Cuenta c2 = banco.crearCuenta(cliente);
		Cuenta c3 = banco.crearCuenta(cliente);

		assertEquals("1000", c1.getId(), "Primera cuenta debe tener id 1000");
		assertEquals("1001", c2.getId(), "Segunda cuenta debe tener id 1001");
		assertEquals("1002", c3.getId(), "Tercera cuenta debe tener id 1002");
	}

	/**
	 * Valida que, tras crear varias cuentas, el ultimoCodigo del banco
	 * haya aumentado exactamente en la cantidad de cuentas creadas.
	 */
	@Test
	public void testUltimoCodigoIncrementaProporcionalAlNumeroDeCuentas() {
		banco.crearCuenta(cliente);
		banco.crearCuenta(cliente);
		banco.crearCuenta(cliente);

		assertEquals(1003, banco.getUltimoCodigo(),
				"Tras crear 3 cuentas desde el estado inicial, el ultimoCodigo debe ser 1003");
	}

	/**
	 * Valida que si se modifica el ultimoCodigo antes de crear la cuenta
	 * (usando el setter existente), el id de la nueva cuenta refleje ese valor
	 * y luego se incremente.
	 */
	@Test
	public void testCrearCuentaUsaUltimoCodigoConfiguradoManualmente() {
		banco.setUltimoCodigo(5000);

		Cuenta cuenta = banco.crearCuenta(cliente);

		assertEquals("5000", cuenta.getId(), "El id debe reflejar el ultimoCodigo configurado previamente");
		assertEquals(5001, banco.getUltimoCodigo(), "El ultimoCodigo debe incrementarse tras la creación");
	}

	/**
	 * Valida que se pueda crear una cuenta con un cliente distinto al usado
	 * en pruebas previas, y que el propietario asignado sea ese nuevo cliente.
	 * Confirma que el método usa el parámetro recibido y no un cliente fijo.
	 */
	@Test
	public void testCrearCuentaConOtroClienteAsignaEsePropietario() {
		Cliente otroCliente = new Cliente("0987654321", "Luis", "Gomez");

		Cuenta cuenta = banco.crearCuenta(otroCliente);

		assertSame(otroCliente, cuenta.getPropietario(),
				"Debe asignarse el cliente recibido como propietario, no otro");
		assertEquals("0987654321", cuenta.getPropietario().getCedula(),
				"Los datos del nuevo cliente deben conservarse en la cuenta");
	}

	/**
	 * Valida que crear una cuenta no modifique los datos del cliente original.
	 * La cuenta referencia al mismo cliente, por lo que verificamos que sus
	 * atributos siguen intactos.
	 */
	@Test
	public void testCrearCuentaNoModificaAlClienteOriginal() {
		banco.crearCuenta(cliente);

		assertEquals("1712345678", cliente.getCedula(), "La cédula del cliente no debe cambiar");
		assertEquals("Ana", cliente.getNombre(), "El nombre del cliente no debe cambiar");
		assertEquals("Perez", cliente.getApellido(), "El apellido del cliente no debe cambiar");
	}
}