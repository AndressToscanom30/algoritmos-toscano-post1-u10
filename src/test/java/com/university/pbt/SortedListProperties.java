package com.university.pbt;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

/**
 * Suite de propiedades algebraicas para SortedList.
 * Cada @Property se ejecuta 1000 veces con datos generados automáticamente.
 */
class SortedListProperties {

    /**
     * Propiedad 1 — Invariante de orden.
     * Para cualquier lista de enteros, SortedList siempre queda ordenada.
     */
    @Property
    void alwaysSorted(
            @ForAll List<@IntRange(min = -1000, max = 1000) Integer> elements) {

        SortedList<Integer> list = new SortedList<>();
        list.addAll(elements);

        assertThat(list.isSorted())
                .as("La lista debe estar ordenada tras insertar: %s", elements)
                .isTrue();
    }

    /**
     * Propiedad 2 — Preserva todos los elementos (sin pérdidas ni duplicados extras).
     * El multiconjunto de entrada debe coincidir con el de salida.
     */
    @Property
    void preservesAllElements(@ForAll List<Integer> elements) {

        SortedList<Integer> list = new SortedList<>();
        list.addAll(elements);

        assertThat(list.toList())
                .containsExactlyInAnyOrderElementsOf(elements);
    }

    /**
     * Propiedad 3 — El tamaño coincide con el input.
     * No se pierden ni inventan elementos.
     */
    @Property
    void sizeMatchesInput(@ForAll List<Integer> elements) {

        SortedList<Integer> list = new SortedList<>();
        list.addAll(elements);

        assertThat(list.size())
                .isEqualTo(elements.size());
    }

    /**
     * Propiedad 4 — Idempotencia del orden.
     * Insertar una lista ya-ordenada produce exactamente el mismo resultado.
     */
    @Property
    void addingAlreadySortedGivesSameResult(@ForAll List<Integer> elements) {

        SortedList<Integer> list1 = new SortedList<>();
        list1.addAll(elements);

        SortedList<Integer> list2 = new SortedList<>();
        list2.addAll(list1.toList()); // input ya ordenado

        assertThat(list2.toList())
                .isEqualTo(list1.toList());
    }
}