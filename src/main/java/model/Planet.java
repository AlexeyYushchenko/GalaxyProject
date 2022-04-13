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
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Objects;

@XStreamAlias("planet")
public class Planet {
    private String name;
    private double radius;
    private double rotationPeriod;

    public Planet() {
        this.name = "";
    }

    public Planet(String name, double radius, double rotationPeriod) {
        this.name = name;
        this.radius = radius;
        this.rotationPeriod = rotationPeriod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setRotationPeriod(double rotationPeriod) {
        this.rotationPeriod = rotationPeriod;
    }

    public double getRadius() {
        return radius;
    }

    public double getRotationPeriod() {
        return rotationPeriod;
    }

    public double getRotationSpeed() {
        return Math.PI * 2 * radius / rotationPeriod;
    }

    public String behavior() {
        return String.format("%-8s\t Orbital period %.2f days", getName(), getRotationSpeed());
    }

    @Override
    public String toString() {
        return "Planet{" +
                "name='" + name + '\'' +
                ", radius=" + radius +
                ", rotationPeriod=" + rotationPeriod +
                '}';
    }

    public Element getAsElement(Document doc) {
        Element planetElement = doc.createElement("planet");

        Element planetName = doc.createElement("name");
        planetName.setTextContent(this.name);
        planetElement.appendChild(planetName);

        Element planetRadius = doc.createElement("radius");
        planetRadius.setTextContent(String.valueOf(this.radius));
        planetElement.appendChild(planetRadius);

        Element planetRotationPeriod = doc.createElement("rotationPeriod");
        planetRotationPeriod.setTextContent(String.valueOf(this.rotationPeriod));
        planetElement.appendChild(planetRotationPeriod);
        return planetElement;
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

    public void saveAsXml(){
        try(FileOutputStream fos = new FileOutputStream(new File("./planet.xml"));
            XMLEncoder encoder = new XMLEncoder(fos))
        {
            encoder.writeObject(this);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Planet)) return false;
        Planet planet = (Planet) o;
        return Double.compare(planet.radius, radius) == 0 &&
                Double.compare(planet.rotationPeriod, rotationPeriod) == 0 &&
                Objects.equals(name, planet.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, radius, rotationPeriod);
    }

    public static double averageRadius(Planet[] planets){
        /*double res = 0;
        for(Planet planet : planets){
            res += planet.radius;
        }
        return res / planets.length;*/

        return Arrays.stream(planets).mapToDouble(Planet::getRadius).average().getAsDouble();
    }
}
