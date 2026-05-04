package com.university.pbt;

import java.util.*;

/**
 * Anillo de hashing consistente con nodos virtuales.
 * Permite distribución uniforme de claves sobre un conjunto de nodos.
 */
public class ConsistentHashRing {

    private final TreeMap<Integer, String> ring = new TreeMap<>();
    private final int virtualNodes;

    /**
     * Construye el anillo con los nodos dados y la cantidad de nodos virtuales.
     *
     * @param nodes        lista inicial de nodos
     * @param virtualNodes réplicas virtuales por nodo (más = distribución más uniforme)
     */
    public ConsistentHashRing(List<String> nodes, int virtualNodes) {
        this.virtualNodes = virtualNodes;
        nodes.forEach(this::addNode);
    }

    /**
     * Agrega un nodo real al anillo mediante sus nodos virtuales.
     *
     * @param node nombre del nodo
     */
    public void addNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            int hash = hash(node + "#" + i);
            ring.put(hash, node);
        }
    }

    /**
     * Elimina un nodo real y todos sus nodos virtuales del anillo.
     *
     * @param node nombre del nodo a eliminar
     */
    public void removeNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            ring.remove(hash(node + "#" + i));
        }
    }

    /**
     * Retorna el nodo responsable de la clave dada.
     * Utiliza el sucesor más cercano en el anillo (wrap-around incluido).
     *
     * @param key clave a mapear
     * @return nombre del nodo responsable
     * @throws IllegalStateException si el anillo está vacío
     */
    public String getNode(String key) {
        if (ring.isEmpty()) {
            throw new IllegalStateException("El anillo está vacío");
        }
        int hash = hash(key);
        Map.Entry<Integer, String> entry = ring.ceilingEntry(hash);
        // wrap-around: si no hay sucesor, tomar el primer nodo del anillo
        return (entry != null ? entry : ring.firstEntry()).getValue();
    }

    /**
     * Retorna el conjunto de nodos reales actualmente en el anillo.
     *
     * @return conjunto de nombres de nodos
     */
    public Set<String> nodes() {
        return new HashSet<>(ring.values());
    }

    /**
     * Función de hash que siempre retorna un entero no negativo.
     *
     * @param key cadena a hashear
     * @return valor hash positivo
     */
    private int hash(String key) {
        return key.hashCode() & Integer.MAX_VALUE;
    }
}