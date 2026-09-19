package utnfc.backend.parcial;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class LiquidadorTarjetasTest {
    @Test void totalesYConsultas() {
        var l = new LiquidadorTarjetas(List.of(new Consumo("A","Alimentos",100000,0),new Consumo("B","Alimentos",100001,0),new Consumo("C","Combustible",50,0)));
        assertEquals(4001,l.getTotalLiquidacion()); assertEquals(1333,l.getPromedioComision());
        assertEquals(Map.of("Alimentos",4000,"Combustible",1),l.totalesPorRubro());
        assertEquals(2,l.filtrar(c->c.getCentavos()<=100000).size());
        assertTrue(l.filtrar(c->c.getRubro().equals("X")).isEmpty());
        assertEquals("C", l.getMinimo().orElseThrow().getId());
        assertEquals(2000, l.getMaximo().orElseThrow().comision());
    }
    @Test void vacio() {
        var l=new LiquidadorTarjetas(List.of());
        assertEquals(0,l.getTotalLiquidacion()); assertEquals(0,l.getPromedioComision());
        assertTrue(l.totalesPorRubro().isEmpty()); assertTrue(l.filtrar(c->true).isEmpty());
        assertTrue(l.getMaximo().isEmpty()); assertTrue(l.getMinimo().isEmpty());
    }
    @Test void copiaDefensiva() {
        var lista=new ArrayList<Consumo>(); lista.add(new Consumo("A","Alimentos",100000,0));
        var l=new LiquidadorTarjetas(lista); lista.clear();
        assertEquals(2000,l.getTotalLiquidacion());
        assertThrows(UnsupportedOperationException.class,()->l.filtrar(c->true).clear());
        assertThrows(UnsupportedOperationException.class,()->l.getConsumos().clear());
    }
}
