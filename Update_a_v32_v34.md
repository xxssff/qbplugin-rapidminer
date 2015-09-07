Una vez realizada la actualización de la versión 3.4 se analiza el problema de la utilización monolítica del plugin. Se rediseña

En la actualización del plugin hay varias cosas pendientes:

  1. Buscar una versión en que el plugin funcione correctamente empezando por la 3.0 (La validación será con que ofrezca los resultados obtenidos en los diferentes experimentos de Metodología) La versión 2.4.1 se compara con la 3.0: Iguales resultados en Ameva-CAIM. Se comprueba el funcionamiento en la versión 3.1 con resultados correctos.
> 2. buscar la siguiente en que falle. En la 3.2 se produce un error de puntero nulo.
> 3. En la última que funciona correctamente diseñar los test
> 4. Comprobar los test y depurar en la versión que falle




Depuración del error en 3.2

Parece que el problema está en que no existe un atributo especial denominado "prediction" en el conjunto de datos que se está manipulando. La cuestión es porqué el conjunto de datos no tiene dicho atributo; ¿será por la copia de los atributos desde el conjunto de entrada? ¿o no vienen desde la entrada?
La primera vez que entra en el método DiscretizationLearner.learn(eSet) el conjunto de datos eSet ya no tiene el atributo prediction, por tanto habrá que comprobar en 3.1 si el prediction se crea sólo cuando se hace un setPredictionLabel.

Pruebo a utilizar createPredictedLabel con el conjunto de datos de entrada. El problema es que el modelo (a quien corresponde el método create...) no tiene asignada un atributo label que es donde se basa para crear la nueva. Por tanto hay que crear, posiblemente en el momento de crear el modelo, el atributo label que se podría coger del conjunto de datos de entrada al constructor.
Esa es la opción que utilizo, asignar en la creación del modelo el atributo label del conjunto de aprendizaje para que antes de aplicar el modelo a un conjunto de test pueda, comprobando que no existe aún, crearle a dicho conjunto una etiqueta de predicción según la que tiene el modelo.

Finalmente caigo en el problema de la depuración, los pasos son:
> Crear el proyecto del plugin que debe tener enlazada el yale.jar
> Indicar la carpeta con el código fuente del jar anterior.
> Situar la clase principal del proyecto a ....YaleGUI
> Colocar los puntos de ruptura deseados
> Usar la opción Project/DEBUG y no run ....

Al intentar compilar las versiones 3.2  ¿y 3.4? se produce un error por imposibilidad de importar los paquetes de com.**.doclets.**. Se encuentra que hay que utilizar la librería tools.jar del jdk que se encuentra en la carpeta lib. Luego habrá que definir la dependencia entre los dos proyectos y hacer que las construcciones se realicen en la carpeta build, así como que el jar del plugin debe instalarse en el build de Yale. Hay que cambiar el camino del home de YALE.

