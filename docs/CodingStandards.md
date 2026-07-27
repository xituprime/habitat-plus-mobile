# HabitatPlus - Coding Standards

## Objetivo

Mantener un código consistente, legible y fácil de mantener para todos los integrantes del equipo.

---

# Arquitectura

El proyecto utiliza:

- Feature-Based Architecture
- MVI (Model - View - Intent)
- Jetpack Compose
- Navigation Compose

No utilizar Clean Architecture.

---

# Organización

Cada módulo debe seguir la siguiente estructura:

```
feature
│
├── intent
├── model
├── state
├── ui
└── viewmodel
```

No crear carpetas adicionales sin aprobación del equipo.

---

# Convención de nombres

Pantallas

- ParkingScreen
- VisitsScreen
- ReservationsScreen

ViewModels

- ParkingViewModel
- VisitsViewModel

State

- ParkingState
- VisitsState

Intent

- ParkingIntent
- VisitsIntent

Model

- Parking
- Visit
- Reservation

---

# Compose

Las pantallas:

- No contienen lógica de negocio.
- Solo representan el estado recibido.

No utilizar Scaffold directamente.

Siempre utilizar:

AppScaffold()

---

# ViewModel

El ViewModel:

- Procesa Intents.
- Modifica el State.
- Nunca conoce componentes Compose.

---

# State

El State representa toda la información necesaria para dibujar una pantalla.

La UI nunca modifica el State directamente.

---

# Intent

Cada acción del usuario debe representarse mediante un Intent.

Ejemplos:

- Load
- Refresh
- Save
- Delete

---

# Componentes reutilizables

Si un componente será utilizado por dos o más módulos debe ubicarse en:

core/components

---

# Theme

Todos los colores, tipografías y formas deben provenir de:

ui/theme

No definir colores directamente dentro de las pantallas.

---

# Git

Nunca trabajar sobre:

- main
- develop

Siempre trabajar sobre una rama feature.

Ejemplo:

feature/parqueos

---

# Commits

Los mensajes deben ser claros.

Ejemplos:

- Create parking feature structure
- Implement navigation graph
- Add shared scaffold
- Configure project theme

Evitar mensajes como:

- cambios
- prueba
- update
- fix

---

# Pull Requests

Todo cambio debe integrarse mediante Pull Request.

No realizar merge directo hacia develop.

---

# Regla principal

Antes de escribir código preguntarse:

¿Este cambio mantiene la arquitectura del proyecto?

Si la respuesta es NO, discutir el cambio con el equipo antes de implementarlo.