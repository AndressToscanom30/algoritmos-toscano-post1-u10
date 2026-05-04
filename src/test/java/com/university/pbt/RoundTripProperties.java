package com.university.pbt;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

/**
 * Propiedad round-trip: verifica que serializar y deserializar
 * una SortedList en JSON conserva exactamente todos los elementos en orden.
 *
 * <p>Propiedad algebraica: decode(encode(x)) == x</p>
 */
class RoundTripProperties {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Round-trip JSON para SortedList de enteros.
     * La lista serializada y luego deserializada debe ser idéntica a la original.
     */
    @Property
    void jsonRoundTrip(@ForAll List<Integer> elements) throws Exception {

        SortedList<Integer> original = new SortedList<>();
        original.addAll(elements);

        // encode: lista ordenada → JSON
        String json = mapper.writeValueAsString(original.toList());

        // decode: JSON → lista de enteros
        List<Integer> deserialized = mapper.readValue(
                json,
                mapper.getTypeFactory()
                      .constructCollectionType(List.class, Integer.class));

        // La round-trip debe preservar exactamente el contenido y orden
        assertThat(deserialized)
                .as("Round-trip JSON debe preservar contenido y orden")
                .isEqualTo(original.toList());
    }
}