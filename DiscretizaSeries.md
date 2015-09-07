# Introduccion #

La idea de ampliar la discretización a series se enfoca por medio de hacer nuevos operadores de discretización olvidando la extensión de RM con las listas de bloques.
Estos operadores se limitan (caso unidimensional) a generar el mismo rango para todos los atributos, es decir, **procesar conjuntamente cada dimensión de una serie generando un bloque por atributo**.

Esta ampliación afecta a los discretizadores incluidos en RM y a los de nueva implementación.

## Operadores Nativos de RM ##
Se decide hacer nuevos operadores para incorporar este comportamiento (agrupar series) copiando el código base de los operadores de RM. Para ello se crea una clase abstracta intermedia que incluye el procesado de series y que limita cada discretizador a la creación de sus parámetros y el cálculo de los puntos de corte.
Los nuevos operadores se llamarán igual que en RM con la palabra **Extended** al final y se agruparán en el paquete **qbts.preprocessing.discretization.rm** .
Ejemplo de jerarquía:
```
  Operator
    \_PreprocessingOperator
        \_Discretizer
            |-BinDiscretizationExtended
            |-FrequencyDiscretizationExtended
            \_ ...
```


---


OTRAS OPCIONES
Se podrían haber reutilizado los discretizadores de RM pero creando un operador que procesase cada serie creando un nuevo conjunto de un único atributo y llamará al discretizador estándar. Con el modelo devuelto hacer una replicación de los esquemas de discretización para cada elemento de la serie. Pros: Igual para cada discretizador. Mayor independencia de los cambios que se realicen. Cons: Espacio de memoria duplicado. Recurrir a la reflexion para acceder a los rangos del discretizador original ().
Ya puestos a crear un nuevo operador se prefiere la solución elegida.





PENDIENTE: Ampliar al caso multidimensional. En ese caso si que será necesario un atributo que diga si se tiene que discretizar todas las dimensiones juntas o cada una por su lado.


