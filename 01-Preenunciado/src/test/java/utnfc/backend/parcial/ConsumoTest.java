package utnfc.backend.parcial;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ConsumoTest {
    @Test void construccionYCalculo() {
        Consumo c = Consumo.desdeCampos(new String[]{"C", "Alimentos", "120000", "20000", "APROBADO"});
        assertEquals("C", c.getId()); assertEquals("Alimentos", c.getRubro());
        assertEquals(100000, c.getCentavos()); assertEquals(2000, c.comision());
    }
    @Test void limites() {
        assertEquals(0, new Consumo("C", "V", 1, 0).comision());
        assertEquals(0, new Consumo("C", "V", 49, 0).comision());
        assertEquals(1, new Consumo("C", "V", 50, 0).comision());
        assertEquals(2000, new Consumo("C", "V", 100000, 0).comision());
        assertEquals(2000, new Consumo("C", "V", 100001, 0).comision());
        assertEquals(60000, new Consumo("C", "V", 3000000, 0).comision());
    }
    @Test void invalidos() {
        for (int monto : new int[]{0, -1, 3000001}) assertThrows(IllegalArgumentException.class, () -> new Consumo("C", "V", monto, 0));
        assertThrows(IllegalArgumentException.class, () -> new Consumo(" ", "V", 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Consumo("C", null, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> Consumo.desdeCampos(new String[]{"C","V","100","100","APROBADO"}));
        assertThrows(IllegalArgumentException.class, () -> Consumo.desdeCampos(new String[]{"C","V","100","-1","APROBADO"}));
    }
    @Test void igualdadYToString() {
        var a = new Consumo("C", "Alimentos", 100000, 0);
        var b = new Consumo("C", "Alimentos", 100000, 0);
        assertEquals(a, b); assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new Consumo("D", "Alimentos", 100000, 0));
        assertTrue(a.toString().contains("C")); assertTrue(a.toString().contains("2000"));
    }
}
