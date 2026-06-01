package at.spengergasse.util;

import at.spengergasse.model.EulerConcept;
import java.util.List;

//Theoretische Erklärungen die von EulerConcept abgerufen werden können
public class EulerDataUtil {

    public static List<EulerConcept> getConcepts() {
        return List.of(
                new EulerConcept(
                        "Eulersche Grundlagen",
                        "Ein Graph ist eulersch, wenn er eine eulersche Linie oder einen Zyklus besitzt."
                ),
                new EulerConcept(
                        "Eulerscher Zyklus",
                        "Alle Knoten haben geraden Grad → Start = Ende."
                ),
                new EulerConcept(
                        "Eulersche Linie",
                        "Genau zwei Knoten haben ungeraden Grad → Start ≠ Ende."
                )
        );
    }
}