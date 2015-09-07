# Utilización del KNNLearner #
La nueva versión de RM incluye medidas de similitud listas para aplicar, así como un learner para KNN que se aplica seleccionando una de las medidas existentes.

Las limitaciones de utilizar este operador son actualmente que calcula sobre todos los atributos de cada ejemplo y faltarían las medidas de cadenas.

La primera parte nos obliga a introducir sólo una cadena.

Realmente el KNN llama directamente a la clase que implementa la medida de distancia por lo que es ésta la responsable de decidir como calcular la medida. Las que se incluyen con RM calculan sobre cada atributo. Se podrían hacer nuevas medidas que se aplicasen sobre las series pero el procedimiento de hacer la localización de las series que se incluyen en cada conjunto de datos se debería hacer cada vez que se fuese a realizar una comparación. El cambio de la discretización se realizó en el apply pero en ese caso sólo se ejecuta una vez por discretización. Aquí habría que almacenar de alguna forma la detección de bloques. La solución está en que las medidas derivan de AbstractValueBasedSimilarityMeasure (que implementa ExampleBasedSimilarityMeasure).

Cuando se realiza la instanciación de la clase de la medida se ejecuta un método init() definido en la interfaz al que se le envía el conjunto de ejemplos. Lo que hacen los operadores es almacenar los ids de los ejemplos del conjunto, imagino que para depués recuperar de cada ejemplo por su ID sin que tenga que enviarse el ejemplo completo.

La idea entonces sería aplicar el init() para almacenar el conjunto de bloques de las series que se encuentran en el conjunto de datos. Esto además permitiría modificar las distancias existentes para que se pudiesen aplicar a ejemplos con series de una forma más o menos sencillas, no importaría el método de discretización aplicado.


Lo único que no tiene buena pinta es que el método getValues del AbstractValueBasedSimilarityMeasure devuelve un vector de dobles (double[.md](.md)) en lugar de algo más genérico. Así que la aplicación de una distancia a un conjunto de ejemplos con series debería hacerse por medio múltiples llamadas para obtener cada serie en un vector.

Una consideración para mejorar el tiempo de ejecución sería que el init() almacenara los datos. Sería un inconveniente en la vertiente de espacio pero no en la de tiempo.



La segunda es cuestión de implementación. Pero el mayor inconveniente sería la no disponibilidad de la información de discretización para poder implementar la distancia basada en kernel. Para ello habría que modificar el !KNNLearner para que lo almacenara en el !KNNModel.


En lugar de tener que modificar el !KNN se puede probar si los objetos mantienen el orden en que han sido genereados y qué hace el ModelApplier en el caso de que en la lista de objetos de entrada existan dos objetos del mismo tipo.

La prueba sería hacer que la primera parte de la validación cruzada utilizase un CAIMDiscretizer (que modifique el exampleSet de entrada) y luego un !KNNLearner. La segunda parte tendría dos ModelApplier consecutivos y un performanceEvaluator, el primer ModelApplier haría la !Discretización y el segundo la identificación. Si esto funciona sólo habría que…

Esto no sirve porque el problema deriva de que la identificación que hace el KNN requiere el esquema de discretización que se ha utilizado y ese hay que pasárselo a la función de distancia.



La solución por tanto es modificar el !KNNLearner y el !KNNModel para que el primero coja un modelo de la entrada y lo almacene en el segundo, con eso ya en el applier del !KNNmodel se podría acceder a toda la información.



La verdad es que parece poco elegante.



Otra opción sería que el applier del KNNModel pudiese comprobar si existe un modelo en la entrada y en ese caso lo aplique de forma inmediata. La modificación sería mínima y no demasiado fea, pero ¿cómo garantizo que el que se ejecuta en el ModelApplier el el modelo del KNN y no el otro? Porque si lo que se dice arriba es verdad entonces siempre me encontraría primero el DiscretizationModel. Para evitar esta situación se podría utilizar un IOSelector para hacer que el modelo del KNN se coloque al principio de la lista. De todas formas la modificación aunque sea mínima debe hacerse en el KNNLEarner y el Model ya que la implementación en el model generará otra clase diferente que tendrá que ser generada por el Learner. El learner será una herencia del existente al que se cambia la clase del modelo y el modelo sobreescribe el applier para hacer la nueva comprobación.

Otra opción aunque muy similar a esta sería no cambiar el modelo del KNN sino hacer un operador que cogiese dos modelos y los guardase en otro modelo, como un Mixer. Ese modelo tendría una ejecución que llamaría al primero, cogería el resultado y se lo enviaría al segundo. De esta forma sólo hay que crear un modelo nuevo y un operador (que es en realidad un learner aunque no se defina como tal). Pero parece que estaría más claro haciendo la opción anterior de modificar el KNN en sus dos componentes.
Esta opción no es realizable porque no se puede crear un nuevo modelo partiendo del modelo KNN puesto que no se puede acceder a los componentes que se incluyen en KNNModel. Tampoco en el caso del Learner porque todos los atributos están definidos como privados y por tanto no son visibles a las subclases.


La opción por tanto es crear un nuevo KNNLearner y un KNNModel copiados y modificados.