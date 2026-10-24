package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoAjustadoTest
{
    private ProductoAjustado sinAjustes;
    private ProductoAjustado conAjustes;

    // ProductoAjustado no muestra métodos para agregar o quitar ingredientes por esa razon se usa reflexión para llenar las listas privadas y poder cubrir esas ramas.
    @SuppressWarnings( "unchecked" )
    private void llenarLista( ProductoAjustado producto, String campo, Ingrediente ing ) throws Exception
    {
        Field f = ProductoAjustado.class.getDeclaredField( campo );
        f.setAccessible( true );
        ( (ArrayList<Ingrediente>) f.get( producto ) ).add( ing );
    }

    @BeforeEach
    void setUp( ) throws Exception
    {
        ProductoMenu base = new ProductoMenu( "corral", 14000 );
        sinAjustes = new ProductoAjustado( base );

        conAjustes = new ProductoAjustado( base );
        llenarLista( conAjustes, "agregados", new Ingrediente( "queso", 2500 ) );
        llenarLista( conAjustes, "agregados", new Ingrediente( "huevo", 2500 ) );
        llenarLista( conAjustes, "eliminados", new Ingrediente( "cebolla", 1000 ) );
    }

    @Test
    void testGetNombre( )
    {
        assertEquals( "corral", sinAjustes.getNombre( ), "El nombre debe ser el del producto base." );
        assertEquals( "corral", conAjustes.getNombre( ), "El nombre no cambia con los ajustes." );
    }

    @Test
    void testGetPrecioSinAjustes( )
    {
        assertEquals( 14000, sinAjustes.getPrecio( ), "Sin ajustes el precio debe ser el del producto base." );
    }

    @Test
    void testGetPrecioConAjustes( )
    {
        assertEquals( 19000, conAjustes.getPrecio( ), "Debe sumar solo los agregados; quitar ingredientes no baja el precio." );
    }

    @Test
    void testGenerarTextoFacturaSinAjustes( )
    {
        String esperado = "corral\n" + " ".repeat( 12 ) + "14000\n";
        assertEquals( esperado, sinAjustes.generarTextoFactura( ) );
    }

    @Test
    void testGenerarTextoFacturaConAjustes( )
    {
        String esperado = "corral\n"
                + "    +queso" + " ".repeat( 16 ) + "2500\n"
                + "    +huevo" + " ".repeat( 16 ) + "2500\n"
                + "    -cebolla\n"
                + " ".repeat( 12 ) + "19000\n";
        assertEquals( esperado, conAjustes.generarTextoFactura( ) );
    }
}