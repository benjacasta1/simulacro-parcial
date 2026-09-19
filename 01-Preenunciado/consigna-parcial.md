# Parcial práctico: protección de compras

**Tiempo orientativo: 45 minutos.** Partir exactamente del proyecto preparcial. Se entregan este documento, `datos-parcial.csv` y la carpeta `tests-nuevos`. Mantener los tests existentes.

El CSV del parcial contiene 140 filas de datos más encabezado (141 líneas); es un conjunto distinto del preparcial de 60 filas. El volumen no agrega reglas ni tareas de programación.

La liquidación incorpora la **protección de compra** (es un seguro sobre la compra, por ejemplo, se compra un televisor y le paga a la tarjeta para asegurarlo ante rotura/robo/etc.). Se necesita procesar consumos con y sin cobertura en una misma colección y obtener sus comisiones correctas.

## Reglas nuevas

El nuevo encabezado es `id,rubro,importeCentavos,bonificacionCentavos,estado,diasCobertura`. Conservar también el formato original de cinco columnas: todos esos consumos son sin seguro. Cada fila debe tener el ancho indicado por su encabezado; no aceptar filas de cinco campos bajo un encabezado de seis, ni a la inversa.

Para filas APROBADO del nuevo formato, el sexto campo es un entero (tras quitar espacios externos):

- `0` conserva todas las reglas anteriores y su comisión. Es un consumo sin seguro.
- `30`, `90` o `180` son plazos de cobertura. El consumo queda **asegurado**: respeta las validaciones anteriores, admite como máximo **500_000 centavos netos inclusive** y suma una **prima de 200 centavos por cada 30 días** sobre la comisión de un consumo sin seguro del mismo neto. La prima se aplica una sola vez por consumo.
- Un valor vacío, no numérico o un plazo distinto de 0/30/90/180 hace inválida la fila.

Conservar la precedencia del preparcial: verificar ancho y después descartar ANULADO sin validar montos ni días de cobertura. Las filas inválidas se registran y la carga continúa.

Ejemplos (neto y comisión en centavos):

| Neto | Días | Comisión |
|---:|---:|---:|
| 100_000 | 0 (o formato de 5 columnas) | 2000 |
| 100_000 | 30 | 2200 |
| 100_000 | 90 | 2600 |
| 100_000 | 180 | 3200 |
| 500_000 | 90 | 10600 |
| 500_001 | 90 | inválido |

## Resultado requerido

1. Integrar la lectura de ambas versiones del CSV sin duplicar el proceso de carga. La construcción desde campos textuales permanece en el dominio (`desdeCampos` o equivalente); no concentrar la nueva regla solo en el parser.
2. Hacer que `comision()`, `getTotalLiquidacion()`, filtros por comisión y `totalesPorRubro()` reflejen la nueva regla.
3. Incorporar en el colector un conteo por modalidad. Puede elegir el nombre y representación de esa operación; para una colección vacía debe dar un resultado vacío o conteos cero. No se pide agrupar por plazo.
4. Hacer que el main use por defecto el archivo del parcial y muestre resumen, cantidad de objetos, cantidades SIN_SEGURO/ASEGURADO, total y totales por rubro. Seguir delegando cálculos.
5. Conservar el comportamiento anterior y pasar los tests existentes y los nuevos. Puede agregar tests propios.

Se evalúa comportamiento observable, encapsulamiento y distribución de responsabilidades. Puede usar herencia, composición u otra solución razonable. Puede usar Streams o ciclos; no se exige una jerarquía ni una técnica determinada. No se evalúa el nombre concreto de una subclase.

## Preparación y entrega

Desde la raíz de su copia de `01-Preenunciado`, copiar `tests-nuevos/utnfc` a `src/test/java/utnfc` fusionando directorios. Copiar `datos-parcial.csv` a `datos/datos-parcial.csv` y ajustar la ruta predeterminada del main.

```sh
mvn test
java -cp target/classes utnfc.backend.parcial.Main datos/datos-parcial.csv
```

Los nuevos tests usan archivos temporales; compilan sobre el proyecto inicial y algunos deben fallar antes de resolver. El conteo por modalidad y el main se revisan además mediante ejecución, sin exigir un método nuevo con firma fija.

Entregar un único ZIP del proyecto con `pom.xml`, `src/` y `datos/`; excluir `target/`, `.git/` y archivos del IDE. Subirlo en el ítem 10 del cuestionario. Incluir un texto breve `DECISIONES.md` (3–5 líneas) que indique la estrategia elegida y si quedó algo pendiente.

Distribución sugerida: 5 minutos de lectura, 25 de cambios, 10 de pruebas y 5 para revisar y empaquetar.
