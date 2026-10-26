# Habitat+

**Proyecto de Plataformas Móviles — Universidad del Valle de Guatemala**

Repositorio: https://github.com/xituprime/habitat-plus-mobile

## ¿Qué es Habitat+?

Habitat+ es una aplicación Android que estamos desarrollando para facilitar algunas tareas de la administración de un condominio. La idea es que los residentes puedan consultar información y realizar gestiones desde su teléfono, sin tener que comunicarse con administración para todo.

También contemplamos un rol de administrador, que tendrá opciones adicionales para gestionar la información del condominio. Por el momento estamos trabajando con datos de prueba mientras completamos los diferentes módulos.

## Funcionalidades del proyecto

- **Inicio:** resumen de anuncios, parqueos, reportes, reservas y recordatorios de mantenimiento.
- **Autenticación:** inicio de sesión, registro y cierre de sesión. Actualmente usamos una simulación; más adelante integraremos Firebase Authentication.
- **Parqueos:** consulta de espacios asignados y de visitantes, reserva de parqueos, historial, búsqueda y filtros.
- **Reportes:** creación de reportes por categoría, consulta de detalles y seguimiento de estados (pendiente, en proceso y resuelto).
- **Áreas comunes:** consulta de espacios como piscina, cancha, salón social y churrasquera, revisión de horarios y reservas.
- **Anuncios:** consulta de comunicados del condominio y sus detalles.
- **Perfil y configuración:** datos del residente, opciones de configuración y cierre de sesión.

El proyecto está pensado para **residentes y administradores**. Algunas acciones exclusivas del administrador todavía están planificadas para fases posteriores.

## Cómo estamos organizando la aplicación

Desarrollamos la app con **Kotlin y Jetpack Compose**, usando componentes de **Material 3**. Para movernos entre pantallas utilizamos **Navigation Compose con rutas `@Serializable`**, en lugar de rutas escritas como cadenas de texto.

También usamos el patrón **MVI (Model-View-Intent)**. En general, cada módulo organiza su código en:

- `Screen`: muestra la interfaz y envía las acciones del usuario.
- `State`: guarda los datos y estados que necesita mostrar la pantalla.
- `Intent` o `Event`: representa las acciones que puede realizar el usuario.
- `ViewModel`: procesa los eventos y actualiza el estado mediante `StateFlow`.

En algunos flujos, como Parqueos, las pantallas comparten un ViewModel para mantener sincronizados los datos de la reserva y el historial.

## Servicios

Estos son los servicios y fuentes de datos que elegimos para el proyecto. Indicamos cuáles usamos actualmente y cuáles están previstos para las siguientes fases.

| Servicio | ¿Para qué lo usaremos? | Estado |
| --- | --- | --- |
| **Firebase Authentication** | Registrar usuarios, iniciar sesión y mantener una sesión autenticada de residentes y administradores. | Planificado. Actualmente el login y el registro son simulados. |
| **Cloud Firestore** | Guardar y consultar información compartida del condominio, como parqueos, reportes, anuncios y reservas. | Planificado. Actualmente estos módulos utilizan principalmente datos de prueba. |
| **Firebase Storage** | Guardar fotografías que se adjunten a reportes o reservas, si confirmamos este servicio para las siguientes fases. | Propuesto; pendiente de confirmar e integrar. |
| **Room (base de datos local)** | Guardar información en el dispositivo y permitir consultas locales. | Implementado en las reservas de Parqueos. |
| **JSONPlaceholder** | Probar una petición real por internet y comprobar la respuesta y el manejo de errores. No contiene datos reales de condominios. | Implementado como servicio HTTP de demostración. |
| **Datos simulados del proyecto** | Poder navegar y probar los módulos mientras todavía no tenemos un backend completo. | Implementados en distintos módulos. |

Para usar Firebase tendremos que crear y configurar un proyecto de Firebase, registrar nuestra aplicación Android y revisar las reglas de acceso de cada servicio. Firestore y Storage también requieren configurar permisos y considerar sus límites de uso y posibles costos. JSONPlaceholder no requiere una cuenta ni una clave API para la consulta de prueba que estamos utilizando.

**Comportamiento sin internet:** la idea final es permitir consultas de información previamente guardada, pero evitar nuevas reservas o reportes cuando no haya conexión, para no generar conflictos. En la versión actual de demostración, las reservas de Parqueos se guardan localmente con Room; todavía no existe sincronización completa con Firebase.

## Librerías y herramientas

| Librería o herramienta | Uso dentro del proyecto |
| --- | --- |
| **Kotlin** | Lenguaje con el que desarrollamos la aplicación. |
| **Jetpack Compose** | Construcción de las pantallas. |
| **Material 3** | Botones, tarjetas, campos de texto y otros componentes visuales. |
| **Navigation Compose** | Navegación entre pantallas con rutas tipadas. |
| **Kotlin Serialization** | Definición de destinos serializables con `@Serializable`. |
| **ViewModel** | Manejo de la lógica y del estado de las pantallas. |
| **StateFlow y Coroutines** | Actualización del estado y ejecución de operaciones asíncronas. |
| **Room** | Persistencia de datos locales; actualmente se utiliza para reservas de parqueos. |
| **KSP** | Procesamiento de anotaciones necesario para generar el código de Room. |
| **HttpURLConnection** | Petición HTTPS de demostración a JSONPlaceholder, sin agregar Retrofit por ahora. |
| **Firebase SDK (previsto)** | Integración futura con Authentication, Firestore y, si se confirma, Storage. |
| **Android Studio y Gradle** | Desarrollo, compilación y generación del APK. |
| **Git y GitHub** | Control de versiones y trabajo en ramas de cada integrante. |

## Qué tenemos funcionando en esta entrega

Actualmente podemos navegar entre las pantallas principales y secundarias, iniciar sesión y registrarnos con datos simulados, realizar reservas de parqueos, consultar su historial y utilizar filtros. También probamos que las reservas guardadas con Room permanecen después de cerrar y volver a abrir la aplicación.

Además, hicimos una petición HTTP de prueba desde el Dashboard. Cuando hay internet, la aplicación confirma que recibió una respuesta; cuando no hay conexión, muestra un error sin cerrar la app.

Todavía tenemos funcionalidades que completar, como la autenticación real con Firebase, la sincronización de los demás módulos y algunas acciones opcionales relacionadas con fotografías y administración.

## Instalación y ejecución

1. Clonar el repositorio: `git clone https://github.com/xituprime/habitat-plus-mobile.git`.
2. Abrir el proyecto en Android Studio y esperar a que Gradle sincronice.
3. Ejecutar la aplicación en un emulador o dispositivo Android compatible.
4. Para generar un APK de pruebas, ejecutar `./gradlew assembleDebug` (en PowerShell: `.\gradlew.bat :app:assembleDebug`).
5. El APK generado se encuentra normalmente en `app/build/outputs/apk/debug/app-debug.apk`. Para esta entrega también colocaremos una copia en la carpeta `APK/` de la raíz del repositorio.

La autenticación de esta versión es **simulada**: no se deben utilizar contraseñas reales. Las reservas locales de Room son datos de demostración y no se sincronizan todavía con otros dispositivos.

## Equipo

- **Axel:** configuración e integración del proyecto; Parqueos, Reservas e Historial.
- **Kennet:** Dashboard, Anuncios, Perfil y Configuración.
- **Junior:** Reportes y Áreas Comunes.

Cada integrante trabaja sus módulos en ramas de Git y después integramos los cambios al proyecto principal.

## Próximos pasos

Continuaremos con Firebase Authentication y Firestore, el manejo de roles, la sincronización de datos locales y las funciones pendientes de los módulos. También seguiremos probando la navegación, los estados MVI y el comportamiento cuando no hay conexión.
