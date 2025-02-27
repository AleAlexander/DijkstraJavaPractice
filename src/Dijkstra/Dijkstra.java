package Dijkstra;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import java.util.*;

import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Double.min;

enum Steps{UNVISITED_VERTEX_SELECTION, NEAREST_NEIGHBOR_SELECTION, RELAXATION};

public class Dijkstra {
    /**
     * step - метка, указывающая, какой шаг алгоритма должен выполнятся следующим
     * distance - карта вершин и расстояний до них
     * unvisitedVertices - список непросмотренных вершин
     * outgoingEdges - карта вершин и выходящих из них ребер
     * graph - граф, в котором ищутся кратчайшие пути
     * source - начальная вершина в алгоритме Дейкстры
     */
    private Steps step = Steps.UNVISITED_VERTEX_SELECTION;
    private HashMap<Object, Double> distance;
    private ArrayList<Object> unvisitedVertices;
    private HashMap<Object, TreeSet<Object>> outgoingEdges;
    private mxGraph graph;
    private Object source;

    public Dijkstra(mxGraph graph, Object source) {
        distance = new HashMap<>();
        unvisitedVertices = new ArrayList<>();
        outgoingEdges = new HashMap<>();
        this.graph = graph;
        this.source = source;

        /**
         * изначально расстояния до всех вершин равно бесконечности
         */
        for (Object v : graph.getChildVertices(graph.getDefaultParent())) {
            distance.put(v, POSITIVE_INFINITY);
            unvisitedVertices.add(v);
        }

        /**
         * дублирование вершин с выходящими из них ребрами для возможности реализации визуализации
         */
        for (Object v : graph.getChildVertices(graph.getDefaultParent())) {
            TreeSet<Object> set = new TreeSet<>(new EdgeComparator());
            set.addAll(Arrays.asList(graph.getOutgoingEdges(v)));
            outgoingEdges.put(v, set);
        }

    }

    /**
     * главный метод алгоритма Дейкстры разбит на небольшие методы
     * опять же для возможности реализации визуализации
     */
    public void getPaths() {
        /**
         * расстояние от начальной вершины до себя, очевидно, равно 0
         */
        distance.put(source, 0.0);

        Object currVertex = new mxCell();
        Object currEdge = new mxCell();
        while (!unvisitedVertices.isEmpty())
            switch (step) {
                case UNVISITED_VERTEX_SELECTION:
                    currVertex = selectUnvisitedVertex();
                    break;
                case NEAREST_NEIGHBOR_SELECTION:
                    currEdge = selectNearestNeighbor(currVertex);
                    if (currEdge.equals(currVertex))
                        unvisitedVertices.remove(currVertex);
                    break;
                case RELAXATION:
                    relax(currEdge);
                    break;
            }
    }

    /**
     * выбор следующей просматриваемой вершины из еще непросмотренных
     */
    private Object selectUnvisitedVertex() {
        step = Steps.NEAREST_NEIGHBOR_SELECTION;

        /**
         * выбирается непросмотренная вершина с наименьшим текущем расстоянии до начальной вершины
         */
        Object vertex = new mxCell();
        double mindistance = minDistance();
        for (Object v: distance.keySet()) {
            if (unvisitedVertices.contains(v) && distance.get(v).equals(mindistance)) {
                vertex = v;
                break;
            }
        }

        return vertex;
    }

    /**
     * выбор непросмотренной вершины, ближайшей к текущей просматриваемой
     */
    private Object selectNearestNeighbor(Object vertex) {
        if (outgoingEdges.get(vertex).isEmpty()) {
            step = Steps.UNVISITED_VERTEX_SELECTION;
            return vertex;
        }
        else {
            step = Steps.RELAXATION;
            Object result = outgoingEdges.get(vertex).first();
            outgoingEdges.get(vertex).remove(result);
            return result;
        }
    }

    /**
     * обновление расстояния до вершины
     */
    private double relax(Object edge) {
        Object source = ((mxCell) edge).getSource();
        Object target = ((mxCell) edge).getTarget();
        double value = (double)((mxCell) edge).getValue();

        double newDistance = Math.min(distance.get(target), distance.get(source) + value);

        distance.put(target, newDistance);

        step = Steps.NEAREST_NEIGHBOR_SELECTION;

        return newDistance;
    }

    /**
     * поиск кратчайшего расстояния до непросмотренных вершин
     */
    private double minDistance() {
        double result = POSITIVE_INFINITY;
        for (Object v: unvisitedVertices)
            if (distance.get(v) < result)
                result = distance.get(v);

        return result;
    }

    public String toString() {
        StringBuffer builder = new StringBuffer();
        for (Map.Entry e: distance.entrySet()) {
            builder.append("vertex = ");
            builder.append(((mxCell) e.getKey()).getValue().toString());
            builder.append(", distance = ");
            builder.append(e.getValue().toString());
            builder.append("\n");
        }

        return builder.toString();
    }
}
