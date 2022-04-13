package model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@XStreamAlias("galaxy")
public class Galaxy {
    private String name;
    private List<Planet> planets;

    public Galaxy() {
        this.name = "";
        this.planets = new ArrayList<>();
    }

    public Galaxy(String name) {
        this.name = name;
        this.planets = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public boolean addPlanet(Planet planet) {
        if (planets.contains(planet))
            return false;
        this.planets.add(planet);
        return true;
    }

    public boolean removePlanet(Planet planet) {
        return this.planets.remove(planet);
    }

    public Planet removePlanet(String name) {
        Planet planet = findPlanet(name);
        if (planet == null)
            return null;
        this.planets.remove(planet);
        return planet;
    }

    public Planet findPlanet(String planetName) {
        return this.planets.stream()
                .filter(planet -> planetName.equals(planet.getName()))
                .findFirst()
                .orElse(null);
    }

    public int findPlanet(Planet planet) {
        for (var i = 0; i < this.planets.size(); i++) {
            if (planet.equals(this.planets.get(i)))
                return i;
        }
        return -1;
    }

    public String behavior() {
        return this.planets.stream()
                .map(Planet::behavior)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Galaxy)) return false;
        Galaxy galaxy = (Galaxy) o;
        return Objects.equals(name, galaxy.name) &&
                Objects.equals(planets, galaxy.planets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, planets);
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner("\n");
        sj.add("Galaxy{");
        sj.add("name=" + this.name + ",");
        for (Planet planet : planets) {
            sj.add(planet.toString());
        }
        sj.add("}");
        return sj.toString();
    }

    public Element getAsElement(Document doc) {
        Element galaxyElement = doc.createElement("galaxy");
        Element name = doc.createElement("name");
        name.setTextContent(this.name);
        galaxyElement.appendChild(name);
        for (Planet planet : planets) {
            galaxyElement.appendChild(planet.getAsElement(doc));
        }
        return galaxyElement;
    }

    public void saveAsXmlDoc(String fileName) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element element = getAsElement(doc);
        doc.appendChild(element);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Source input = new DOMSource(doc);
        Result output = new StreamResult(new File(fileName));
        transformer.transform(input, output);
    }
}
