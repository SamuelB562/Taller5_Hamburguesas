package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ComboTest
{
    private ArrayList<ProductoMenu> items;
    private Combo combo;
    private Combo comboSinDescuento;

    @BeforeEach
    void setUp( ) throws Exception
    {
        items = new ArrayList<ProductoMenu>( );
        items.add( new ProductoMenu( "corral", 14000 ) );
        items.add( new ProductoMenu( "papas medianas", 5000 ) );
        items.add( new ProductoMenu( "gaseosa", 3000 ) );

        combo = new Combo( "combo corral", 0.1, items );
        comboSinDescuento = new Combo( "combo sin descuento", 0.0, items );
    }

    @Test
    void testGetNombre( )
    {
        assertEquals( "combo corral", combo.getNombre( ), "El nombre del combo no es el esperado." );
    }

    @Test
    void testGetPrecioConDescuento( )
    {
        assertEquals( 19800, combo.getPrecio( ), "El precio debe ser la suma de los items menos el descuento." );
    }

    @Test
    void testGetPrecioSinDescuento( )
    {
        assertEquals( 22000, comboSinDescuento.getPrecio( ), "Sin descuento el precio debe ser la suma de los items." );
    }

    @Test
    void testListaDeItemsSeCopia( )
    {
        items.add( new ProductoMenu( "postre", 6000 ) );
        assertEquals( 19800, combo.getPrecio( ), "Cambiar la lista original no debe afectar al combo." );
    }

    @Test
    void testGenerarTextoFactura( )
    {
        String esperado = "Combo combo corral\n"
                + " Descuento: 0.1\n"
                + " ".repeat( 12 ) + "19800\n";
        assertEquals( esperado, combo.generarTextoFactura( ), "El texto de la factura no es el esperado." );
    }
}