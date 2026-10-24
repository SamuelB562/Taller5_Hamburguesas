package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.excepciones.IngredienteRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.NoHayPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoFaltanteException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.YaHayUnPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.Restaurante;

public class RestauranteTest
{
    /**
     * Carpeta con los archivos de prueba (relativa a la carpeta del proyecto)
     */
    private static final String RUTA = "./data/pruebas/";

    private Restaurante restaurante;
    private File ingredientesOk;
    private File menuOk;
    private File combosOk;
    private File facturaGenerada;

    @BeforeEach
    void setUp( ) throws Exception
    {
        restaurante = new Restaurante( );
        ingredientesOk = new File( RUTA + "ingredientes_ok.txt" );
        menuOk = new File( RUTA + "menu_ok.txt" );
        combosOk = new File( RUTA + "combos_ok.txt" );
        facturaGenerada = null;
    }

    @AfterEach
    void tearDown( ) throws Exception
    {
        if( facturaGenerada != null )
        {
            facturaGenerada.delete( );
            new File( "./facturas" ).delete( ); // solo se borra si quedó vacía
        }
    }



    @Test
    void testEstadoInicial( )
    {
        assertNull( restaurante.getPedidoEnCurso( ), "No debe haber pedido en curso al inicio." );
        assertTrue( restaurante.getPedidos( ).isEmpty( ), "No debe haber pedidos cerrados al inicio." );
        assertTrue( restaurante.getIngredientes( ).isEmpty( ), "No debe haber ingredientes al inicio." );
        assertTrue( restaurante.getMenuBase( ).isEmpty( ), "El menú debe estar vacío al inicio." );
        assertTrue( restaurante.getMenuCombos( ).isEmpty( ), "No debe haber combos al inicio." );
    }

    @Test
    void testIniciarPedido( ) throws Exception
    {
        restaurante.iniciarPedido( "Samuel", "Calle 1" );
        assertNotNull( restaurante.getPedidoEnCurso( ), "Debe haber un pedido en curso." );
        assertEquals( "Samuel", restaurante.getPedidoEnCurso( ).getNombreCliente( ) );
    }

    @Test
    void testIniciarPedidoYaEnCurso( ) throws Exception
    {
        restaurante.iniciarPedido( "Samuel", "Calle 1" );
        YaHayUnPedidoEnCursoException e = assertThrows( YaHayUnPedidoEnCursoException.class,
                ( ) -> restaurante.iniciarPedido( "Ana", "Calle 2" ) );
        assertEquals( "Ya existe un pedido en curso, para el cliente Samuel así que no se puede crear un pedido para Ana",
                e.getMessage( ) );
        assertEquals( "Samuel", restaurante.getPedidoEnCurso( ).getNombreCliente( ), "El pedido original no debe cambiar." );
    }

    @Test
    void testCerrarSinPedido( )
    {
        NoHayPedidoEnCursoException e = assertThrows( NoHayPedidoEnCursoException.class,
                ( ) -> restaurante.cerrarYGuardarPedido( ) );
        assertEquals( "Actualmente no hay un pedido en curso", e.getMessage( ) );
    }

    @Test
    void testCerrarYGuardarPedido( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( ingredientesOk, menuOk, combosOk );
        restaurante.iniciarPedido( "Samuel", "Calle 1" );
        Pedido pedido = restaurante.getPedidoEnCurso( );
        pedido.agregarProducto( restaurante.getMenuBase( ).get( 0 ) );
        String facturaEsperada = pedido.generarTextoFactura( );
        facturaGenerada = new File( "./facturas/factura_" + pedido.getIdPedido( ) + ".txt" );

        restaurante.cerrarYGuardarPedido( );

        assertNull( restaurante.getPedidoEnCurso( ), "Ya no debe haber pedido en curso." );
        assertEquals( 1, restaurante.getPedidos( ).size( ), "El pedido debe quedar en el histórico." );
        assertSame( pedido, restaurante.getPedidos( ).get( 0 ) );
        assertTrue( facturaGenerada.exists( ), "Debe existir el archivo de la factura." );
        String contenido = Files.readString( facturaGenerada.toPath( ), Charset.defaultCharset( ) );
        assertEquals( facturaEsperada, contenido, "El archivo debe contener la factura del pedido." );
    }



    @Test
    void testCargarInformacion( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( ingredientesOk, menuOk, combosOk );

        assertEquals( 3, restaurante.getIngredientes( ).size( ) );
        assertEquals( "queso", restaurante.getIngredientes( ).get( 2 ).getNombre( ) );
        assertEquals( 2500, restaurante.getIngredientes( ).get( 2 ).getCostoAdicional( ) );

        assertEquals( 3, restaurante.getMenuBase( ).size( ) );
        assertEquals( "corral", restaurante.getMenuBase( ).get( 0 ).getNombre( ) );
        assertEquals( 14000, restaurante.getMenuBase( ).get( 0 ).getPrecio( ) );

        assertEquals( 1, restaurante.getMenuCombos( ).size( ) );
        assertEquals( "combo corral", restaurante.getMenuCombos( ).get( 0 ).getNombre( ) );
        assertEquals( 19800, restaurante.getMenuCombos( ).get( 0 ).getPrecio( ), "22000 con 10% de descuento." );
    }

    @Test
    void testCargarDatosReales( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( new File( "./data/ingredientes.txt" ), new File( "./data/menu.txt" ),
                new File( "./data/combos.txt" ) );
        assertEquals( 15, restaurante.getIngredientes( ).size( ) );
        assertEquals( 22, restaurante.getMenuBase( ).size( ) );
        assertEquals( 4, restaurante.getMenuCombos( ).size( ) );
    }



    @Test
    void testIngredienteRepetido( )
    {
        File repetido = new File( RUTA + "ingredientes_repetido.txt" );
        IngredienteRepetidoException e = assertThrows( IngredienteRepetidoException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( repetido, menuOk, combosOk ) );
        assertEquals( "El ingrediente lechuga está repetido", e.getMessage( ) );
    }

    @Test
    void testProductoRepetido( )
    {
        File repetido = new File( RUTA + "menu_repetido.txt" );
        ProductoRepetidoException e = assertThrows( ProductoRepetidoException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( ingredientesOk, repetido, combosOk ) );
        assertEquals( "El producto corral está repetido", e.getMessage( ) );
    }

    @Test
    void testComboRepetido( )
    {
        File repetido = new File( RUTA + "combos_repetido.txt" );
        ProductoRepetidoException e = assertThrows( ProductoRepetidoException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( ingredientesOk, menuOk, repetido ) );
        assertEquals( "El producto combo corral está repetido", e.getMessage( ) );
    }

    @Test
    void testProductoFaltanteEnCombo( )
    {
        File faltante = new File( RUTA + "combos_faltante.txt" );
        ProductoFaltanteException e = assertThrows( ProductoFaltanteException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( ingredientesOk, menuOk, faltante ) );
        assertEquals( "El producto pizza no aparece en la información del restaurante", e.getMessage( ) );
    }

    @Test
    void testNumeroMalFormado( )
    {
        File malo = new File( RUTA + "ingredientes_malformado.txt" );
        assertThrows( NumberFormatException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( malo, menuOk, combosOk ) );
    }

    @Test
    void testArchivoInexistente( )
    {
        File noExiste = new File( RUTA + "no_existe.txt" );
        assertThrows( FileNotFoundException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( noExiste, menuOk, combosOk ) );
    }
}