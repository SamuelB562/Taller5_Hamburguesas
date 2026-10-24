package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class PedidoTest
{
    private Pedido pedidoVacio;
    private Pedido pedidoLleno;

    @BeforeEach
    void setUp( ) throws Exception
    {
        pedidoVacio = new Pedido( "Ana", "Carrera 1 # 1-1" );

        pedidoLleno = new Pedido( "Samuel", "Calle 1 # 2-3" );
        ProductoMenu corral = new ProductoMenu( "corral", 14000 );

        ArrayList<ProductoMenu> items = new ArrayList<ProductoMenu>( );
        items.add( new ProductoMenu( "corral", 14000 ) );
        items.add( new ProductoMenu( "papas medianas", 5000 ) );
        items.add( new ProductoMenu( "gaseosa", 3000 ) );
        Combo combo = new Combo( "combo corral", 0.1, items );

        pedidoLleno.agregarProducto( corral );
        pedidoLleno.agregarProducto( combo );
    }

    @Test
    void testGetNombreCliente( )
    {
        assertEquals( "Samuel", pedidoLleno.getNombreCliente( ), "El nombre del cliente no es el esperado." );
    }

    @Test
    void testIdsConsecutivos( )
    {
        Pedido primero = new Pedido( "A", "dir A" );
        Pedido segundo = new Pedido( "B", "dir B" );
        assertEquals( primero.getIdPedido( ) + 1, segundo.getIdPedido( ), "Cada pedido nuevo debe tener el id siguiente." );
    }

    @Test
    void testPrecioTotalPedidoVacio( )
    {
        assertEquals( 0, pedidoVacio.getPrecioTotalPedido( ), "Un pedido vacío debe valer 0." );
    }

    @Test
    void testPrecioTotalPedidoLleno( )
    {
        assertEquals( 40222, pedidoLleno.getPrecioTotalPedido( ), "El total debe ser neto (33800) + IVA (6422)." );
    }

    @Test
    void testGenerarTextoFacturaVacio( )
    {
        String esperado = "Cliente: Ana\n"
                + "Dirección: Carrera 1 # 1-1\n"
                + "----------------\n"
                + "----------------\n"
                + "Precio Neto:  0\n"
                + "IVA:          0\n"
                + "Precio Total: 0\n";
        assertEquals( esperado, pedidoVacio.generarTextoFactura( ), "La factura del pedido vacío no es la esperada." );
    }

    @Test
    void testGenerarTextoFacturaLleno( )
    {
        String esperado = "Cliente: Samuel\n"
                + "Dirección: Calle 1 # 2-3\n"
                + "----------------\n"
                + "corral\n" + " ".repeat( 12 ) + "14000\n"
                + "Combo combo corral\n" + " Descuento: 0.1\n" + " ".repeat( 12 ) + "19800\n"
                + "----------------\n"
                + "Precio Neto:  33800\n"
                + "IVA:          6422\n"
                + "Precio Total: 40222\n";
        assertEquals( esperado, pedidoLleno.generarTextoFactura( ), "La factura del pedido no es la esperada." );
    }

    @Test
    void testGuardarFactura( @TempDir Path carpeta ) throws Exception
    {
        File archivo = carpeta.resolve( "factura.txt" ).toFile( );
        pedidoLleno.guardarFactura( archivo );

        assertTrue( archivo.exists( ), "El archivo de la factura debe existir." );
        String contenido = Files.readString( archivo.toPath( ), Charset.defaultCharset( ) );
        assertEquals( pedidoLleno.generarTextoFactura( ), contenido, "El archivo debe contener la factura." );
    }

    @Test
    void testGuardarFacturaArchivoInvalido( @TempDir Path carpeta )
    {
        File noEsArchivo = carpeta.toFile( );
        assertThrows( FileNotFoundException.class, ( ) -> pedidoLleno.guardarFactura( noEsArchivo ),
                "Debe lanzar FileNotFoundException si no se puede crear el archivo." );
    }
}