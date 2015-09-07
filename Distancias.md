# Introduccion #

La base del plugin es añadir a RM nuevas similitudes y distancias entre series.

# QSI #

# Kernel Intervalar #

El Kernel intervalar depende de los valores límite de los intervalos de discretización
(tanto el menor como el mayor) pero en el caso de las discretizaciones los valores
mínimos y máximo de los rangos menor y mayor respectivamente son almacenados como
-infinito y +infinito (o ni siquiera son almacenados).

Para obtener esos valores requeriría accceder al ExampleSet original y obtenerlos de
las estadísticas de los propios atributos. Pero se supone que el conjunto que llega aquí
ya está discretizado por lo que eso se ha perdido. A no ser que se almacene de forma temporal (en el caso de que venga de una validación cruzada) y se recupere posteriormente.

Las soluciones pueden ser:
  * Obligar a la entrada del conjunto original de nuevo.- El procesado será tomar para cada atributo sus límites y en caso de que sean series tomarlos de todos sus elementos.De esta forma se elimina el problema de que no se sepa el tipo de discretización aplicada ni la necesidad de añadir un nuevo parámetro.
  * Realizar un nuevo operador que realice la discretización y almacene los valores
  * Utilizar obligatoriamente los límites del rango en los nombres de los valores nominales en que se discretizan los valores reales. Existen tres mecanismos para denominar las clases de una discreización: corto (rangea, rangeb), largo y con rango (que introduce los límites en el nombre). De todas formas los límites máximos no se almacenan por lo que habría que hacer una estimación o desechar esta solución.

---


# Registro de distancias en RM 4.4 #

Para el registro de distancias definidas en un Plugin aparece en la versión 4.4 la invocación de un método InitPlugin en una clase especial del plugin que es llamada si existe y un método en DistancesMeasures que amplía las matrices con las similitudes disponibles.

```
package com.rapidminer;

import com.rapidminer.tools.math.similarity.DistanceMeasures;
import qbts.distances.*;

public class PluginInit {
  public static void initPlugin(){
		DistanceMeasures.registerMeasure(DistanceMeasures.NOMINAL_MEASURES_TYPE, "QSISimilarity", QSISimilarity.class);	
  }
}
```