package com.university.pbt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lista ordenada en orden no decreciente.
 * Mantiene el invariante de orden en toda inserción.
 *
 * @param <T> tipo comparable
 */
public class SortedList<T extends Comparable<T>> {

    private final List<T> data = new ArrayList<>();

    /**
     * Inserta un elemento en la posición correcta usando búsqueda binaria.
     *
     * @param element elemento a insertar
     */
    // VERSIÓN CON BUG (temporal — solo para observar shrinking)
    public void add(T element) {
        int pos = Collections.binarySearch(data, element);
        if (pos < 0) pos = -(pos + 1);
        // SIN la línea del bug: pos = data.size() - pos;
        data.add(pos, element);
    }

    /**
     * Inserta todos los elementos de la lista dada.
     *
     * @param elements lista de elementos a insertar
     */
    public void addAll(List<T> elements) {
        elements.forEach(this::add);
    }

    /**
     * Retorna una vista no modificable de los datos internos.
     *
     * @return lista inmutable ordenada
     */
    public List<T> toList() {
        return Collections.unmodifiableList(data);
    }

    /** @return número de elementos almacenados */
    public int size() {
        return data.size();
    }

    /** @return true si la lista no contiene elementos */
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * Verifica el invariante: los elementos están en orden no decreciente.
     *
     * @return true si la lista está ordenada correctamente
     */
    public boolean isSorted() {
        for (int i = 0; i < data.size() - 1; i++) {
            if (data.get(i).compareTo(data.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }
}