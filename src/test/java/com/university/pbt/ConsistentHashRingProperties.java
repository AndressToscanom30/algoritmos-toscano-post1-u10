package com.university.pbt;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.Size;
import net.jqwik.api.constraints.StringLength;

/**
 * Suite de propiedades para ConsistentHashRing.
 * Usa generadores personalizados con @Provide para producir listas de nodos
 * válidas.
 */
class ConsistentHashRingProperties {

    /** Número de nodos virtuales usado en todas las propiedades. */
    private static final int VIRTUAL_NODES = 150;

    /**
     * Generador personalizado: listas de nodos únicos, alfabéticos,
     * con entre 1 y 8 elementos de longitud 3–10.
     */
    @Provide
    Arbitrary<List<String>> nodeList() {
        return Arbitraries.strings()
                .alpha()
                .ofMinLength(3)
                .ofMaxLength(10)
                .list()
                .ofMinSize(1)
                .ofMaxSize(8)
                .uniqueElements();
    }

    /**
     * Propiedad 1 — Determinismo.
     * La misma clave sobre el mismo anillo siempre retorna el mismo nodo.
     */
    @Property
    void getNodeIsDeterministic(
            @ForAll("nodeList") List<String> nodes,
            @ForAll @StringLength(min = 1, max = 50) String key) {

        ConsistentHashRing ring = new ConsistentHashRing(nodes, VIRTUAL_NODES);

        assertThat(ring.getNode(key))
                .as("El nodo para la clave '%s' debe ser siempre el mismo", key)
                .isEqualTo(ring.getNode(key));
    }

    /**
     * Propiedad 2 — Pertenencia al anillo.
     * El nodo retornado siempre es uno de los nodos conocidos del anillo.
     */
    @Property
    void getNodeReturnsKnownNode(
            @ForAll("nodeList") List<String> nodes,
            @ForAll @StringLength(min = 1) String key) {

        ConsistentHashRing ring = new ConsistentHashRing(nodes, VIRTUAL_NODES);

        assertThat(ring.nodes())
                .as("El resultado de getNode debe pertenecer al anillo")
                .contains(ring.getNode(key));
    }

    /**
     * Propiedad 3 — Monotonía al agregar un nodo.
     * Al añadir un nuevo nodo, la mayoría de claves NO deben migrar.
     * Se usa Assume para descartar casos degenerados donde el hash del
     * nuevo nodo colisiona con los existentes.
     */
    @Property(tries = 200)
    void addingNodeOnlyMigratesSubset(
            @ForAll("nodeList") List<String> nodes,
            @ForAll @StringLength(min = 1, max = 30) String newNode,
            @ForAll @Size(min = 20, max = 50) List<@StringLength(min = 1, max = 20) String> keys) {

        // Descartar si el nuevo nodo ya existe
        Assume.that(!nodes.contains(newNode));

        ConsistentHashRing before = new ConsistentHashRing(nodes, 150);
        ConsistentHashRing after = new ConsistentHashRing(
                new ArrayList<>(nodes) {
                    {
                        add(newNode);
                    }
                }, 150);

        // Solo evaluar si el anillo before tiene al menos 2 nodos distintos
        // y el after tiene más nodos que el before (inserción real ocurrió)
        Assume.that(before.nodes().size() >= 1);
        Assume.that(after.nodes().size() > before.nodes().size());

        long unchanged = keys.stream()
                .filter(k -> before.getNode(k).equals(after.getNode(k)))
                .count();

        // Con N nodos originales, agregar 1 debería migrar ~1/(N+1) claves.
        // Por tanto al menos (N/(N+1)) claves permanecen.
        // Como mínimo garantizamos que más del 30% no migra (umbral conservador).
        long minExpected = Math.max(1L, keys.size() * 3 / 10);

        assertThat(unchanged)
                .as("Al agregar 1 nodo, al menos el 30%% de claves deben permanecer. "
                        + "Nodos antes: %s, nodo nuevo: %s", nodes, newNode)
                .isGreaterThan(minExpected);
    }
}