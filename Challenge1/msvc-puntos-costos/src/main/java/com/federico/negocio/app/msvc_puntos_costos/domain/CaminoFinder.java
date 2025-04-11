package com.federico.negocio.app.msvc_puntos_costos.domain;

import java.util.List;
import java.util.stream.Stream;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.springframework.stereotype.Component;

import com.federico.negocio.app.msvc_puntos_costos.domain.dto.CaminoPKRequest;
import com.federico.negocio.libs.commons.libs_msvc_commons.domain.PuntoVenta;
import com.federico.negocio.libs.commons.libs_msvc_commons.exception.NotFoundException;

@Component
public class CaminoFinder {

    public ResultadoCamino caminoMinimo(List<Camino> caminos, CaminoPKRequest caminoPKRequest) {
        Graph<Integer, Camino> grafo = new SimpleDirectedWeightedGraph<>(Camino.class);

        for (Camino camino : caminos) {
            int puntoA = camino.getCaminoPK().getPuntoA().getId();
            int puntoB = camino.getCaminoPK().getPuntoB().getId();
            int costo = camino.getCosto();

            grafo.addVertex(puntoA);
            grafo.addVertex(puntoB);

            grafo.addEdge(puntoA, puntoB, camino);
            grafo.setEdgeWeight(camino, costo);
        }

        DijkstraShortestPath<Integer, Camino> dijkstra = new DijkstraShortestPath<>(grafo);
        GraphPath<Integer, Camino> path = dijkstra.getPath(caminoPKRequest.getPuntoA().getId(),caminoPKRequest.getPuntoB().getId());

        if (path == null) 
            throw new NotFoundException("No se encontró posible para ir de " + caminoPKRequest.getPuntoA().getId() + " a " + caminoPKRequest.getPuntoB().getId());

        return new ResultadoCamino(
                (int) path.getWeight(),
                path.getEdgeList().stream()
                        .map(Camino::getCaminoPK)
                        .flatMap(pk -> Stream.of(pk.getPuntoA(), pk.getPuntoB()))
                        .distinct()
                        .map(PuntoVenta::getPuntoVenta)
                        .toList());
    }

    public record ResultadoCamino(int costoTotal, List<String> camino) {
    }

}
