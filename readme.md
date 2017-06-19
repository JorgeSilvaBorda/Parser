Parser levantamiento arquitectura
===================


Aplicación de análisis de código fuente para analizar la estructura de las aplicaciones en ejecución de Banco Falabella.

----------
Versión actual: 0.0.2-beta


Características:
-------------

Parseo de archivos Make Tuxedo

> **Detalles:**

> - Obtiene los archivos asociados al MakeFile que construye el servidor Tuxedo relacionado.
> - Obtiene las funciones (servicios) Tuxedo del archivo del servidor.
> - Obtiene las funciones requeridas por el servidor Tuxedo para su funcionamiento.
> - Obtiene las tablas consultadas por los servicios Tuxedo.