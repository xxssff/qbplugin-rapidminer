## Validación cruzada ##
Lo primero que ha de hacerse es comprobar si alguno de los métodos de validación cruzada de la 3.0 (que tiene 3) coincide exactamente con los de la 2.4.1 . Para ello se debería verificar que las semillas aleatorias son la misma e incluir en los fuentes de los experimentos el volcado de los ficheros de ejemplos intermedios para que se vean los contenidos y poder compararlos.
Se comprueba que el parámetro "Sampling\_type" en un valor "shuffled sampling" proporciona los mismos elementos en la validación cruzada de la versión 3.0 que los que producía la versión 2.4.1.

Este es el experimento utilizado en la versión 3.0 para la comprobación
```
<operator name="RAIZ" class="Experiment">
  <parameter key="resultfile"    value="D:\yale-3.0\test\gunx\EXP_AC.res"/>
  <parameter key="logverbosity"    value="minimum"/>
  <parameter key="logfile"    value="D:\yale-3.0\test\gunx\EXP_AC_debug.log"/>
  <operator name="LeeConjuntoAprendizaje1" class="ExampleSource">
    <parameter key="attributes"    value="D:\yale-3.0\Test\gunx\datos\gunx_Apren.att"/>
  </operator>
  <operator name="ValidacionCruzada1" class="XValidation">
    <parameter key="sampling_type"    value="shuffled sampling"/>
    <operator name="salida" class="ExampleSetWriter">
      <parameter key="example_set_file"    value="D:\yale-3.0\Test\gunx\testcv\Uno_%a.xml"/>
    </operator>
    <operator name="CadenaOp1" class="OperatorChain">
      <operator name="NewOperator" class="ExampleSetWriter">
        <parameter key="example_set_file"    value="D:\yale-3.0\Test\gunx\testcv\DOS_%a.xml"/>
      </operator>
    </operator>
  </operator>
</operator>
```
Los ficheros generados no son exactamente idénticos puesto que al no indicarse formato de atributos la versión 2.4.1 graba la etiqueta en el final de cada ejemplo mientras la 3.0 lo hace al principio.

### Niveles de log ###
Existe diferencia entre los niveles de log existentes entre las versiones 2.4.1 y 3.0

> maximum
> fatal
> error
> exception
> warning
> status            init
> operator         status
> init                 io
> task               minimum
> minimum

Además hay diferencia en la cantidad de información aunque los dos estén en minimum. Esto hay que comprobarlo puesto que puede que el plugin de la versión 3.0 tenga menos salida de objetos que en el caso de la 2.4.1