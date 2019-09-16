package dataManipulation;

import dataObjects.Route;

import java.util.List;


public interface AddRouteCallback {
    void addRoute(Route route);

    void addRoutes(List<Route> routes);
}
