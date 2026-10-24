package co.edu.notas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * ENTREGA DEL TALLER
 * Nombre completo: Diego Alejandro Pineda Montenegro
 * 1. Bug encontrado (método y línea de CarritoCompras.java):
 *    agregarProducto, línea 24: usaba "cantidad < 0" y permitía agregar productos
 *    con cantidad 0, violando R1 (cantidad mayor a 0). Corregido a "cantidad <= 0".
 * 2. Prueba que lo detectó (nombre del método):
 *    agregarProducto_cantidadCero_lanzaExcepcion
 * 3. Sugerencia de la IA que rechacé o corregí, y por qué
 *    (si todas eran correctas: cómo verifiqué una de ellas con las reglas):
 *    Verifiqué a mano calcularTotal_menorAMinimoEnvioGratis_sumaEnvio con R6:
 *    100.000 × 1,19 = 119.000, que es menor a 150.000, entonces se suma envío
 *    de 12.000 → 131.000. Coincide con el valor esperado de la prueba.
 */
class CarritoComprasTest {

    private CarritoCompras carrito;

    @BeforeEach
    void setUp() { carrito = new CarritoCompras(); }

    @Test
    @DisplayName("Subtotal = precio × cantidad de cada producto")
    void calcularSubtotal_variosProductos_sumaCorrecta() {
        // Arrange
        carrito.agregarProducto("Cuaderno", 8_000, 3);
        carrito.agregarProducto("Esfero", 2_000, 5);
        // Act
        double subtotal = carrito.calcularSubtotal();
        // Assert
        assertEquals(34_000, subtotal, 0.001);
    }

    // ---------- Parte A: R2 (carrito vacío) ----------

    @Test
    @DisplayName("R2: carrito vacío tiene subtotal 0")
    void calcularSubtotal_carritoVacio_retornaCero() {
        assertEquals(0, carrito.calcularSubtotal(), 0.001);
    }

    @Test
    @DisplayName("R2: carrito vacío tiene total 0")
    void calcularTotal_carritoVacio_retornaCero() {
        assertEquals(0, carrito.calcularTotal(), 0.001);
    }

    // ---------- Parte A: R1 (entradas inválidas) ----------

    @Test
    @DisplayName("R1: producto válido se agrega al carrito")
    void agregarProducto_datosValidos_agregaItem() {
        carrito.agregarProducto("Regla", 3_000, 1);
        assertEquals(1, carrito.cantidadDeItems());
    }

    @Test
    @DisplayName("R1: nombre nulo lanza excepción")
    void agregarProducto_nombreNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto(null, 1_000, 1));
    }

    @Test
    @DisplayName("R1: nombre vacío lanza excepción")
    void agregarProducto_nombreVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("", 1_000, 1));
    }

    @Test
    @DisplayName("R1: precio 0 lanza excepción")
    void agregarProducto_precioCero_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", 0, 1));
    }

    @Test
    @DisplayName("R1: precio negativo lanza excepción")
    void agregarProducto_precioNegativo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", -500, 1));
    }

    @Test
    @DisplayName("R1: cantidad 0 lanza excepción (caso límite)")
    void agregarProducto_cantidadCero_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", 1_000, 0));
    }

    @Test
    @DisplayName("R1: cantidad negativa lanza excepción")
    void agregarProducto_cantidadNegativa_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", 1_000, -1));
    }

        // ---------- Parte B: R3 a R6 ----------

    @Test
    @DisplayName("R3: SENA10 descuenta el 10 % del subtotal")
    void calcularDescuento_cuponSena10_diezPorCiento() {
        carrito.agregarProducto("Cuaderno", 8_000, 3);
        carrito.agregarProducto("Esfero", 2_000, 5);
        carrito.aplicarCupon("SENA10");
        assertEquals(3_400, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("R4: BIENVENIDO con subtotal exacto de $50.000 descuenta $5.000")
    void calcularDescuento_bienvenidoSubtotalCincuentaMil_descuentaCincoMil() {
        carrito.agregarProducto("Mochila", 50_000, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(5_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("R4: BIENVENIDO con subtotal de $49.999 no descuenta")
    void calcularDescuento_bienvenidoSubtotalMenorACincuentaMil_noDescuenta() {
        carrito.agregarProducto("Mochila", 49_999, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("Sin cupón no hay descuento")
    void calcularDescuento_sinCupon_retornaCero() {
        carrito.agregarProducto("Mochila", 80_000, 1);
        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("R5: cupón desconocido lanza excepción")
    void aplicarCupon_cuponInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.aplicarCupon("DESCUENTO50"));
    }

    @Test
    @DisplayName("R6: total con IVA + envío pagado ($100.000 → $131.000)")
    void calcularTotal_menorAMinimoEnvioGratis_sumaEnvio() {
        carrito.agregarProducto("Libro", 100_000, 1);
        // 100.000 × 1,19 = 119.000 (< 150.000) + 12.000 = 131.000
        assertEquals(131_000, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("R6: total con envío gratis ($200.000 → $238.000)")
    void calcularTotal_mayorAMinimoEnvioGratis_envioGratis() {
        carrito.agregarProducto("Calculadora", 200_000, 1);
        // 200.000 × 1,19 = 238.000 (≥ 150.000) → envío 0
        assertEquals(238_000, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("R6: total con cupón SENA10 ($500.000 → $535.500)")
    void calcularTotal_conCuponSena10_aplicaDescuentoAntesDeIva() {
        carrito.agregarProducto("Monitor", 500_000, 1);
        carrito.aplicarCupon("SENA10");
        // (500.000 − 50.000) × 1,19 = 535.500 → envío gratis
        assertEquals(535_500, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("R6: total con cupón BIENVENIDO ($50.000 → $65.550)")
    void calcularTotal_conCuponBienvenido_aplicaDescuentoYEnvio() {
        carrito.agregarProducto("Mochila", 50_000, 1);
        carrito.aplicarCupon("BIENVENIDO");
        // (50.000 − 5.000) × 1,19 = 53.550 + 12.000 = 65.550
        assertEquals(65_550, carrito.calcularTotal(), 0.001);
    }

}