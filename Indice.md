# Introduction #


# Estado actual #
Migración de la implementación a Rapidminer 4.4
Se realiza el registro de las distancias/similitudes
Migración de las implementaciones realizadas en YALE 3.4 a RapidMiner 4.2


# Problemas actuales #
Revisar la ampliación de los discretizadores para el soporte de las series. Puede ser mejor eliminar el sistema de parámetros y pasarlo a la discretización de todos los atributos que formen una serie de forma conjunta.

Dependerá del mecanismo que se use para el Kernel Intervalar. Al definir las distancias como clases dentro del paquete tools.math ya no son operadores y por tanto no tienen acceso al conjunto de objetos que se están procesando. Por tanto la información de los límites deberá llegar desde otro lugar. Además la instanciación de la distancia se realiza por parte del operador que la use (p.ej. NearestNeighbors) por lo que no se le puede completar información de forma previa.


---


## Estructura del plugin ##
### qbts.distances ###
  * Se definen aquí todas las distancias, que heredan de AbstractExtendedRealValueBasedSimilarity y el operador que genera la similitud denominado QBSimilaritySetup

### qbts.preprocessing.discretization ###
  * Contiene los discretizadores de RM ampliados a la aplicación de series. Finalmente se opta por añadir a los incluidos en RM4.1 un nuevo parámetro que indique que todos los atributos se discreticen juntos. La opción de añadir también el procesado de series (con deteccion de los atributos de principio y fin) se desecha por ser más compleja por tener que contemplar más situaciones por tener dos parámetros relacionados, es más intrusivo en el código de RM y además incluir la opción seleccionada.
  * También existe un DiscretizationModelSeries que es una herencia directa y casi vacía del DiscretizationModel de RM que se crea sólo por que los constructores de DiscretizationModel son privados y por tanto debe estar en el mismo paquete en que sea accedido.
  * Ver [Explicacion de funcionamiento](Discretizacion.md)
~~qbts.discretization~~

  * ~~yale.operator.learner.lazy Incluye código copiado de RM y ligeramente modificado. Incluye un nuevo KNNLearner y SimiliarityUtil, ver [razonamiento](KNNLearnerSimilarity.md)~~




¿Y si pudiera usar el AttributeSubsetPreprocessing para hacer las discretizaciones?
Si hay varias series sólo tengo que hacer una entrada por cada grupo de atributos y ya está.
ESTUDIAR:

¿como hacer que el BinDiscretization (por ejemplo) actúe sobre todos? Realmente hay que crear un idéntico pero modificado, ¿que ventaja tiene entonces utilizar el AttributeSubsetPreprocessing? Pero lo que es peor,si tengo que tratar varias veces un conjunto de ejemplos entonces me va a quedar un conjunto de modelos uno para cada serie. Supongo que eso no es problema pero puede ser inconveniente en la selección de los modelos.

LECCIONES:
Se comprueba que los atributos son realmente sustituidos. El orden en que quedan es:
  * Si solo se procesan algunos, entonces quedan los atributos especiales, los modificados y el resto.
  * en caso de que la expresión regular afecte a todos, entonces quedan los modificados y los especiales.


---



LECCIONES:
  1. Se puede crear un operador que se encargue de crear una similitud en la que almacene el modelo de discretización.
  1. Aunque en la lista del inputContainer (que es donde están los objetos que entran a un operador) sólo tenga un modelo éste vendrá  dentro de un ~~ModelContainer~~ GroupedModel. El apply() de un ~~ModelContainer~~ GroupedModel realiza la aplicación secuencial de todos los modelos que incluya.
La cuestión es ver como afecta cuando además del modelo de discretización exista uno de aprendizaje en el ~~ModelContainer~~ GroupedModel.
  1. Hay que crear una clase base de la que dependan todas las similitudes por lo que se pueda requerir; en el caso del kernel hay que almacenar los límites del modelo de discretización. La clase debe definirse al nivel de AbstractRealValueBasedSimilarity para evitar la comprobación que se hace en el init de AbstractRealValueBasedSimilarity de que todos los atributos sean numéricos. En nuestro caso tienen que ser nominales.



---




# [Comentarios](Comentarios.md) #



# [FAQ](FAQ.md) #