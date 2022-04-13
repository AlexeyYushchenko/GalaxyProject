package program;

import model.Universe;

public class Program {
    public static void main(String[] args) throws Exception {
        /*Universe universe = new Universe();
        universe.behavior();*/

//        Planet[] mass = {
//            new Planet("1", 1, 1),
//            new Planet("2", 2, 2)
//        };
//        double v = Planet.averageRadius(mass);
//        System.out.println(v);
//
//
//        Planet planet = new Planet();
//        double v1 = planet.averageRadius(mass);

        //      XSTREAM
//        String fileName = "universeXStream.xml";
//        Universe universe = new Universe();
//        universe.saveAsXmlViaXStream(fileName);
//        Universe universe1 = universe.loadFromXmlViaXStream(fileName);
//        System.out.println(universe1);

//      DOM
        String fileNameDom = "universeDOM.xml";
        Universe universe = Universe.createRandomUniverse();
        universe.saveToXmlViaDom(fileNameDom);
        Universe universe1 = universe.loadFromXmlViaDom(fileNameDom);
        System.out.println(universe1);

//        List<Galaxy> galaxies = GalaxyFactory.createGalaxies();
//        galaxies.get(0).saveAsXmlDoc("galaxy.xml");


//        try(FileInputStream fis = new FileInputStream(new File("./planet.xml"));
//            XMLDecoder decoder = new XMLDecoder(fis))
//        {
//        Planet planet1 = (Planet) decoder.readObject();
//            System.out.println("name=" + planet1.getName());
//
//        }


    }
}
