package utnfc.backend.parcial;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
class ParserLiquidacionesTest {
    @TempDir Path dir;
    @Test void archivoProvisto() throws IOException {
        var r = new ParserLiquidaciones().leer(Path.of("datos/datos.csv"));
        assertEquals(60, r.getLeidas()); assertEquals(39, r.getProcesadas());
        assertEquals(6, r.getDescartadas()); assertEquals(15, r.getInvalidas());
        assertEquals(39, r.getConsumos().size()); assertEquals(100000, r.getConsumos().get(0).getCentavos());
        assertEquals("Leídas: 60 | procesadas: 39 | descartadas: 6 | inválidas: 15 | objetos: 39", r.toString());
        assertTrue(r.getErrores().containsKey(10));
        assertEquals(6, r.getDescartes().size());
        assertEquals(r.getLeidas(), r.getProcesadas()+r.getDescartadas()+r.getInvalidas());
    }
    @Test void agregadosYTestigos() throws IOException {
        var r = new ParserLiquidaciones().leer(Path.of("datos/datos.csv"));
        var l = new LiquidadorTarjetas(r.getConsumos());
        assertEquals(636156, l.getTotalLiquidacion());
        assertEquals(16311, l.getPromedioComision());
        assertEquals(java.util.Map.of("Servicios",238075,"Alimentos",210000,"Combustible",188081), l.totalesPorRubro());
        assertEquals(8, l.filtrar(c -> c.getCentavos() <= 100000).size());
        assertEquals(3000000, l.filtrar(c -> c.getId().equals("C06")).get(0).getCentavos());
        assertEquals(1, l.filtrar(c -> c.getId().equals("I013")).get(0).getCentavos());
        assertTrue(l.filtrar(c -> c.getId().equals("I049")).isEmpty());
        assertTrue(l.panorama().contains("Total: 636156"));
        assertTrue(l.panorama().contains("promedio: 16311"));
        assertTrue(r.getErrores().containsKey(50));
        assertEquals("ANULADO", r.getDescartes().get(47));
    }
    @Test void resultadoConservaCopiasDefensivas() {
        var consumos = new java.util.ArrayList<Consumo>();
        var errores = new java.util.TreeMap<Integer, String>();
        var descartes = new java.util.TreeMap<Integer, String>();
        consumos.add(new Consumo("A", "Alimentos", 1, 0)); errores.put(2, "error"); descartes.put(3, "ANULADO");
        var r = new ResultadoParseo(consumos, 3, errores, descartes);
        consumos.clear(); errores.clear(); descartes.clear();
        assertEquals(1, r.getProcesadas()); assertEquals(1, r.getInvalidas());
        assertEquals("ANULADO", r.getDescartes().get(3));
        assertThrows(UnsupportedOperationException.class, () -> r.getConsumos().clear());
        assertThrows(UnsupportedOperationException.class, () -> r.getErrores().clear());
        assertThrows(UnsupportedOperationException.class, () -> r.getDescartes().clear());
    }
    @Test void estructuraYContinuacion() throws IOException {
        Path p = dir.resolve("x.csv");
        Files.writeString(p, "id,rubro,importeCentavos,bonificacionCentavos,estado\n\nC,Alimentos,10000,0,APROBADO,extra\nC,Alimentos,10000,0,APROBADO\n");
        var r = new ParserLiquidaciones().leer(p);
        assertEquals(3,r.getLeidas()); assertEquals(2,r.getInvalidas()); assertEquals(1,r.getProcesadas());
    }
    @Test void soloCabeceraYErroresDeArchivo() throws IOException {
        Path p=dir.resolve("x.csv");
        Files.writeString(p,"id,rubro,importeCentavos,bonificacionCentavos,estado\n");
        assertEquals(0,new ParserLiquidaciones().leer(p).getLeidas());
        Files.writeString(p,"mal\n");
        assertThrows(IllegalArgumentException.class,()->new ParserLiquidaciones().leer(p));
        assertThrows(IOException.class,()->new ParserLiquidaciones().leer(dir.resolve("ausente")));
    }
}
