package factory;

import model.Galaxy;
import model.Planet;

import java.security.SecureRandom;
import java.util.*;

public class GalaxyFactory {
    private static final Random rand = new SecureRandom();
    private static final Set<String> uniqueNames = new HashSet<>();

    private GalaxyFactory() {
    }

    public static List<Galaxy> createGalaxies() {
        ArrayList<Galaxy> galaxies = new ArrayList<>();
        var min = 1;
        var max = 7;
        int numberOfGalaxiesToCreate = rand.nextInt(min, max + 1);
        for (var i = 0; i < numberOfGalaxiesToCreate; i++) {
            galaxies.add(createGalaxy());
        }
        return galaxies;
    }

    private static Planet createPlanet() {
        boolean isPlanetNameCreated = false;
        String name = "";
        var min = 0;
        var max = 0;
        while (!isPlanetNameCreated) {
            try {
                min = 1;
                max = 999999;
                name = "P" + rand.nextInt(min, max + 1);
                if (uniqueNames.contains(name)) {
                    throw new IllegalArgumentException();
                }
                uniqueNames.add(name);
                isPlanetNameCreated = true;
            } catch (Exception ignored) {
            }
        }
        min = 1500;
        double radius = rand.nextInt(min, max + 1);
        min = 0;
        max = 100;
        double rotationPeriod = rand.nextDouble() + rand.nextInt(min, max + 1);
        return new Planet(name, radius, rotationPeriod);
    }

    private static Galaxy createGalaxy() {
        boolean isGalaxyNameCreated = false;
        Galaxy galaxy = null;
        while (!isGalaxyNameCreated) {
            try {
                var min = 1;
                var max = 999;
                String name = "G" + rand.nextInt(min, max + 1);
                if (uniqueNames.contains(name)) {
                    throw new IllegalArgumentException();
                }
                isGalaxyNameCreated = true;
                uniqueNames.add(name);
                galaxy = new Galaxy(name);
                min = 1;
                max = 7;
                int numberOfPlanetsToCreate = rand.nextInt(min, max + 1);
                for (var i = 0; i < numberOfPlanetsToCreate; i++) {
                    galaxy.addPlanet(createPlanet());
                }
            } catch (Exception ignored) {
            }
        }
        return galaxy;
    }
}
