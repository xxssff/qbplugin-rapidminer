## ¿Generar el serialUID para una clase del plugin? ##

> Hay que ejecutar la utilidad serialver (pertenece al SDK) indicándole en el classpath
> el camino al jar (si es que está compilado) donde está la clase y aquellas que utilice.

> La clase se identifica con su camino completo. Ejemplo, para QSISimilarity
> se ejecuta
```
  serialver -classpath D:\ws\RM\lib\rapidminer.jar;D:\ws\RM\lib\plugins\qbts.jar qbts.distances.QSISimilarity
```
> indicando los caminos correctos a cada comprimido.

> Si se quiere la versión windows se lanza sin indicar la clase y añadiendo el parámetro -show

## Modificar el fichero MANIFEST ##
El fichero MANIFEST.MF se crea en cada compresión de generación del qbts.jar no siendo copia del fichero que se encuentre en el proyecto. Para incluir alguna línea especifíca se debe modificar la sección **dist** del constructor de Ant (build.xml).