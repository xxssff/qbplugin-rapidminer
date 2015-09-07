## Actualización a versión 4.1-4.2 ##

  * ¿Y si la modificación del KNNLearner y el SimiliarityUtil se hace para añadir una UserBasedSimilarity ? No hace falta porque el KNNLearner puede usar la similitud que venga en la lista de objetos de entrada
  * El KnnLearner incluido en RM4.1 permite la aplicación de una similitud que esté en la cadena de objetos de entrada.

  * Se crea un operador cuya salida sea una similitud.
  * Se crea una clase abstracta de la que deriven todas las similitudes
```
package qbts.distances;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Tools;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.preprocessing.PreprocessingModel;
import com.rapidminer.operator.similarity.attributebased.AbstractValueBasedSimilarity;

public abstract class AbstractExtendedRealValueBasedSimilarity extends
		AbstractValueBasedSimilarity{

	private PreprocessingModel model;
//// SI NO HACE NADA SE ¿PUEDE QUITAR EL METODO COMPLETO? SE SUPONE QUE SE LLAMARA AL PADRE.
	public void init(ExampleSet exampleSet) throws OperatorException {
             // Tools.onlyNumericalAttributes(exampleSet, "value based similarities");
		super.init(exampleSet);
	}
}
```
  * Se crea una Clase para cada similitud. Cada clase tendrá que comprobar los atributos que quiere con una llamada a Tools.only....
```
	public void init(ExampleSet es) throws OperatorException {
		Tools.onlyNominalAttributes(es, "QSI similarity");
		super.init(es);
	}

```

  * El KNN de la versión 4.1 se copia en la 4.2 porque ha cambiado y ya no admite similitudes en la entrada.

---


## Actualización a versión 4.0 ##

La primera opción de rediseñar el plugin para adecuarlo a las nuevas características de RapidMiner es utilizar el nuevo KNNLearner,
La primera idea sería modificarlo creando una nueva clase, algo así como un KNNPreprocessLearner. [UtilizarKNNLearner](UtilizarKNNLearner.md)

Otra posibilidad sería que las similitudes que requieren información de la discretización  se enfoquen desde el lado de la transformación. Así una discretización podría implicar el convertir cada serie unidimensional en otra tridimensional donde además del valor discretizado se incluyesen los valores límite del segmento en que se ha incluido el valor original en el proceso de discretización.LimitesDiscretizacion

Pero si nos vamos a la raiz de la cuestión surge la pregunta ¿porqué no almacenar el modelo de discretización a un fichero y que la distancia lo lea? El problema es que no hay un operador de distancia sino que la distancia es un parámetro del Knnlearner. debido a ésto no se le puede pasar un parámetro (con el nombre del fichero) a la distancia a menos que se modifique la definición del KNNLearner. De todas formas el inconveniente se limita a tener que almacenar el modelo con un nombre fijo conocido a priori.

El único problema es que no hay una distancia DEFINIDA POR EL USUARIO, por lo que hay que hacer una copia del learner y del SimilarityUtil SOLO para incluir una nueva clase que implemente una similitud.

El similarityUtil hay que incluirlo porque además de crear el parámetro partiendo de la array de cadenas donde se identifican las clases, tiene un método resolveSimilarityMeasure que se encarga de compararlas con respecto del Mapper privado donde se almacenan.




## Comentarios Anteriores ##

Otra consideración es el caso de que se use el KNN en una validación cruzada. Como lo que se almacena es un puntero al conjunto de datos de entrada si se realzian modificaciones de los valores entonces se quedan modificados. Revisar la validación cruzada.

La prueba es aplicar una discretización estándar RM en una validación cruzada en modo de depuración.

El utilizar el operador IOMultiplyOperator no es útil porque el método copy del AbstractExampleSet hace una llamada al método clone y por tanto utiliza los mismos punteros. Lo más limpio sería hacer un nuevo operador propio derivado del IOMultiplyOperator que cree una copia completa del objeto de entrada.

En la idea de no tener que modificar las clases de RM se podría hacer que el nuevo operado se encargue directamente de coger un conjunto de datos y crear otro idéntico pero nuevo de forma que las modificaciones vayan al generado.

Sería como un CopyExampleSet con entrada de un exampleSet y salida de otro.

El motivo de no cambiar el IOMultiplyOperator es que implicaría modificar también el AbstractExampleSet para incluir otro método que hiciese la copia completa.

Se podría duplicar o sustituir a elección del usuario.



Los test de RM sólo cubren los casos
  * Comprobar que en la lectura de un conjunto de ejemplos se han obtenido los correspondientes números de ejemplos, atributos y atributos especiales
  * Comprobar que los “learners” producen objetos del tipo correcto.
  * Comprobar un parámetro de un PerformanceEvaluator.



No hay ninguna comprobación de las salidas de los ejemplos. Se podría utilizar el PerformanceEvaluator para crear un ModelParameterEvaluator.

Los datos que son visibles de un operador son el valor de sus parámetros pero el conjunto de cortes almacenado en un modelo no está accesible de forma externa (no es un parámetro). Por tanto para poder hacer una comprobación hay que volver a tirar de la reflexión. El problema ahora es que el resultado es un objeto complejo (o puede serlo en un futuro). Sería mejor dejar una parte principal que se encargue de crear el experimento desde un fichero y dejar la comprobación para un test particular que conozca no sólo el tipo del objeto que va a obtener sino tambien la comparación a realizar para ver si coincide.



Existe en el Helper un método para devolver un campo privado de un operador pero ¿cómo hago un casting si la clase la tengo en una cadena? Ya que puede llegar un objeto complejo habrá que indicar además del nombre del campo el tipo que tiene.



Una forma de crear el objeto es

```
String nomClass="Miclase";

try {

Class clase = Class.forName(nomClass);

      Object pepe = clase.newInstance();

      pepe = clase.cast( llamada que devuelve un Object);

}…
```

Cada vez que quiera utilizar el objeto le puedo hacer un casting con clase.cast(pepe)


De todas forma la opción de acceder a la lista de cortes del modelo me proporciona  una lista de bloques, por lo que para comprobar los valores de los límites tendría que comparar sólo un campo de cada bloque con los valores conocidos. Por tanto sería mejor crear una función en el test que genere de la lista un vector de valores que ya sería completamente comparables de forma automática.


De todas formas todavía habría que comprobar como hacer la validación completa.
  1. Se debería crear una carpeta XML con los experimentos
  1. Para cada test
  1. Cargar el fichero con el experimento y ejecutarlo
  1. Comprobar los resultados

Para lanzar un grupo de experimentos de forma desatendida se puede utilizar un fichero cmd con un contenido como el siguiente
```
start /Dd:\yale-3.0\bin /WAIT  yale d:\yale-3.0\test\gunx\Exp_CEE.xml
start /Dd:\yale-3.0\bin /WAIT  yale d:\yale-3.0\test\gunx\Exp_euclidean.xml
```
El tiempo que se gana con una ejecución en modo carácter frente a una en modo gráfico ronda entre el 5 y el 15% dependiendo del experimento.

---

## Cambios anteriores ##
[Update\_a\_v32\_v34](Update_a_v32_v34.md)
[Diferencias\_241\_30](Diferencias_241_30.md)