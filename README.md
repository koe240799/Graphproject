📊 Graph Algorithm Visualizer

Dieses Projekt ist eine interaktive Webanwendung zur Visualisierung und Analyse von Graphen. Benutzer können einen Graphen hochladen und anschließend verschiedene Algorithmen und Eigenschaften direkt im Browser untersuchen.

🚀 Funktionen
    📁 CSV-Upload von Graphen
    🔍 Darstellung als Adjazenzmatrix
    🌐 Interaktive Graphvisualisierung (vis.js)
    🔵 BFS (Breadth-First Search)
          Schritt-für-Schritt Anzeige (entdeckte & verarbeitete Knoten)
          
⚡ Dijkstra-Algorithmus
          Kürzeste Wege vom gewählten Startknoten
          Pfadanzeige + Distanz + Vorgänger
          
🔁 Euler Analyse
          Erkennung von Eulerweg / Eulerkreis / kein Eulergraph
          Ausgabe des berechneten Pfades
          
📐 Graphanalyse
          Exzentrizität
          Radius
          Durchmesser
          Zentrum des Graphen
          
🧠 Konzept
          Das Projekt basiert auf einer klaren Schichtenarchitektur:
          
                View (Vaadin UI): Benutzeroberfläche und Interaktion
                Service: Algorithmen (BFS, Dijkstra, Euler, Analyse)
                Model: Graphdatenstruktur
                Util: Hilfsfunktionen & Datenmapping

⚙️ Technologien
          Java
          Vaadin Flow
          JavaScript (vis.js für Graphvisualisierung)
          Adjazenzmatrix als Graphrepräsentation

🎯 Ziel
          Das Projekt dient zur Visualisierung und zum besseren Verständnis von Graphalgorithmen durch interaktive 
          Darstellung und Schritt-für-Schritt Auswertung.
