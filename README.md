# Toscano-post1-u10
**Unidad 10 — Corrección, Pruebas y Verificación**  
Property-Based Testing con jqwik · Ingeniería de Sistemas · UDES 2026

---

## Requisitos

- Java 17+
- Maven 3.8+

---

## Ejecución

```bash
# Desde la raíz del proyecto
mvn test
```

El informe de Surefire queda en `target/surefire-reports/`.

---

## Propiedades implementadas

### SortedList (4 propiedades · 1000 tries cada una)

| # | Nombre | Propiedad algebraica |
|---|--------|----------------------|
| 1 | `alwaysSorted` | Invariante de orden |
| 2 | `preservesAllElements` | Sin pérdidas ni duplicados |
| 3 | `sizeMatchesInput` | Conservación del tamaño |
| 4 | `addingAlreadySortedGivesSameResult` | Idempotencia |

### ConsistentHashRing (3 propiedades · 1000/200 tries)

| # | Nombre | Propiedad algebraica |
|---|--------|----------------------|
| 1 | `getNodeIsDeterministic` | Determinismo |
| 2 | `getNodeReturnsKnownNode` | Pertenencia al anillo |
| 3 | `addingNodeOnlyMigratesSubset` | Monotonía al escalar |

### RoundTripProperties (1 propiedad · 1000 tries)

| # | Nombre | Propiedad algebraica |
|---|--------|----------------------|
| 1 | `jsonRoundTrip` | Round-trip: decode(encode(x)) == x |

---

## Análisis de Shrinking (Paso 6)

**Bug introducido:** en `SortedList.add()` se invirtió la posición de inserción
(`pos = data.size() - pos`) haciendo que los elementos queden en orden inverso.

**Contraejemplo original encontrado por jqwik:**  
`TODO: pegar aquí la lista original que reportó jqwik (ej: [47, -3, 1002, 0, ...])`

**Contraejemplo tras shrinking:**  
`[1, 0]` — lista de solo 2 elementos

**Pasos de shrinking:** `TODO: número de pasos reportados (ej: "Shrunk 7 times")`

**Interpretación:**  
El caso mínimo `[1, 0]` revela que el bug se activa con cualquier par donde el
segundo elemento sea menor que el primero. No se necesita ningún valor especial
ni lista larga para demostrar la violación del invariante. Esto muestra la
ventaja clave del shrinking: transforma un contraejemplo complejo e ilegible en
el caso más pequeño posible, facilitando enormemente el diagnóstico.

**¿Qué bugs encuentra PBT que el testing por ejemplos no detecta?**  
- Casos borde con valores extremos que no se nos ocurre probar manualmente
  (ej: `Integer.MIN_VALUE`, listas de un solo elemento, listas ya ordenadas).
- Violaciones de invariantes que solo se dan en combinaciones específicas de
  tamaño o valores (ej: inserción en posición `size - pos` que es inválida
  cuando `pos == 0`).
- Propiedades de composición: que el orden se mantenga tras *múltiples*
  inserciones, no solo tras una.

---

## Capturas de resultados

### Todos los Test pasaron OK
![Tests Ok](/capturas/Tests-Ok.png)

### Todos Error con introducción de leve BUG
![Leve error de Bug](/capturas/ErrorBug.png)