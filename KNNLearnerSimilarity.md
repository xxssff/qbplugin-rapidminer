El único problema es que no hay una distancia DEFINIDA POR EL USUARIO, por lo que hay que hacer una copia del learner y del SimilarityUtil SOLO para incluir una nueva clase que implemente una similitud.

El similarityUtil hay que incluirlo porque además de crear el parámetro partiendo de la array de cadenas donde se identifican las clases, tiene un método resolveSimilarityMeasure que se encarga de compararlas con respecto del Mapper privado donde se almacenan.

En el KNNLearner hay que convertir en un comentario la importación del SimilarityUtil puesto que ya está en el mismo paquete y agregar la del KNNModel que no se ha copiado y por tanto está en otro paquete.