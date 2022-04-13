package model;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import factory.GalaxyFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@XStreamAlias("universe")
public class Universe {
    private List<Galaxy> galaxies = new ArrayList<>();

    public List<Galaxy> getGalaxies() {
        return galaxies;
    }

    public void setGalaxies(List<Galaxy> galaxies) {
        this.galaxies = galaxies;
    }

    public static Universe createRandomUniverse(){
        Universe universe = new Universe();
        universe.galaxies.addAll(GalaxyFactory.createGalaxies());
        return universe;
    }

    public Universe(){

    }

    public boolean addGalaxy(Galaxy galaxy){
        if (galaxies.contains(galaxy))
            return false;
        this.galaxies.add(galaxy);
        return true;
    }

    public boolean removeGalaxy(Galaxy galaxy){
        return this.galaxies.remove(galaxy);
    }

    public Galaxy removeGalaxy(String name){
        Galaxy galaxy = findGalaxy(name);
        if (galaxy == null) return null;
        galaxies.remove(findGalaxy(name));
        return galaxy;
    }

    public Galaxy findGalaxy(String galaxyName){
        return galaxies.stream()
                .filter(galaxy -> galaxyName.equals(galaxy.getName()))
                .findFirst()
                .orElse(null);
    }

    public int findGalaxyIndex(Galaxy galaxy){
        if (galaxies.contains(galaxy)){
            for (var i = 0; i < galaxies.size(); i++) {
                if (galaxy.equals(galaxies.get(i))) return i;
            }
        }
        return -1;
    }

    public Planet findPlanet(String planetName){
        for (var i = 0; i < galaxies.size(); i++) {
            Planet planet = galaxies.get(i).findPlanet(planetName);
            if (planet != null)
                return planet;
        }
        return null;
    }

    public int[] findPlanet(Planet planet){
        for (var i = 0; i < galaxies.size(); i++) {
            int idx = galaxies.get(i).findPlanet(planet);
            if (idx != -1)
                return new int[]{i, idx};
        }
        return new int[0];
    }

    public void behavior() {
        while (true){
            this.galaxies.addAll(GalaxyFactory.createGalaxies());
            System.out.println(this);
            try {
                Thread.sleep(30000);
            } catch (InterruptedException ignored) {}
        }
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner("\n");
        sj.add("Universe{");
        for(Galaxy galaxy : galaxies){
            sj.add(galaxy.toString());
        }
        sj.add("}");
        return sj.toString();
    }

    public void saveToXmlViaDom(String fileName) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document doc = builder.newDocument();

        //Root element
        Element universe = doc.createElement("universe");
        doc.appendChild(universe);
        for(Galaxy galaxy : galaxies){
            universe.appendChild(galaxy.getAsElement(doc));
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Source input = new DOMSource(doc);
        Result output = new StreamResult(new File(fileName));
        transformer.transform(input, output);
    }

    public Universe loadFromXmlViaDom(String fileName) throws ParserConfigurationException {
        try {
            Universe universe = new Universe();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));
            NodeList universes = document.getElementsByTagName("universe");
            if (universes.getLength() > 1) throw new Exception("Illegal number of universes exception");

            Element universeElement = (Element) universes.item(0);
            NodeList listOfGalaxies = universeElement.getElementsByTagName("galaxy");
            for (int i = 0; i < listOfGalaxies.getLength(); i++) {
                Element galaxyElement = (Element) listOfGalaxies.item(i);
                NodeList galaxyName = galaxyElement.getElementsByTagName("name");
                Galaxy galaxy = new Galaxy(galaxyName.item(0).getTextContent());

                NodeList planetList = galaxyElement.getElementsByTagName("planet");
                for (int j = 0; j < planetList.getLength(); j++) {
                    Element planetElement = (Element) planetList.item(j);
                    String planetName = planetElement.getElementsByTagName("name").item(0).getTextContent();
                    String radius = planetElement.getElementsByTagName("radius").item(0).getTextContent();
                    String rotationPeriod = planetElement.getElementsByTagName("rotationPeriod").item(0).getTextContent();
                    galaxy.addPlanet(new Planet(planetName, Double.parseDouble(radius), Double.parseDouble(rotationPeriod)));
                }
                universe.galaxies.add(galaxy);
            }
            return universe;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Universe();
    }


    public void saveAsXmlViaXStream(String fileName) throws IOException {
        XStream xStream = new XStream();
        xStream.processAnnotations(Universe.class);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xStream.toXML(this, writer);
        }
    }

    public Universe loadFromXmlViaXStream(String fileName){
        XStream xStream = new XStream();
        xStream.processAnnotations(Universe.class);
        xStream.allowTypes(new Class[]{Universe.class, Planet.class, Galaxy.class});
        return (Universe) xStream.fromXML(new File(fileName));
    }

    public static final void prettyPrint(Document xml) throws Exception {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(xml), new StreamResult(out));
        System.out.println(out.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Universe)) return false;
        Universe universe = (Universe) o;
        return Objects.equals(galaxies, universe.galaxies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(galaxies);
    }

}
