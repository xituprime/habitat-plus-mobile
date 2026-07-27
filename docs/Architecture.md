# HabitatPlus - Arquitectura del Proyecto

## Objetivo

Este documento define la arquitectura oficial del proyecto HabitatPlus.

Todos los integrantes del equipo deben seguir esta estructura. No se deben crear nuevas carpetas o cambiar la organización sin antes discutirlo con el equipo.

---

# Arquitectura

El proyecto utiliza una arquitectura **Feature-Based** con el patrón **MVI (Model - View - Intent)**.

No se utiliza Clean Architecture.

---

# Estructura del proyecto

```
com.habitatplus.app
│
├── core
│   ├── components
│   ├── constants
│   └── utils
│
├── features
│   ├── parqueos
│   ├── visitas
│   ├── reservas
│   └── perfil
│
├── navigation
│
└── ui
    ├── components
    └── theme
```

---

# Estructura de una Feature

Cada módulo debe seguir exactamente la misma organización.

Ejemplo:

```
parqueos
│
├── intent
├── model
├── state
├── ui
└── viewmodel
```

---

# Responsabilidad de cada carpeta

## intent

Representa las acciones que realiza el usuario.

Ejemplo:

- LoadParkings
- RefreshParkings
- SelectParking

---

## model

Contiene los modelos propios del módulo.

Ejemplo:

- Parking
- Reservation

---

## state

Representa el estado completo de la pantalla.

Toda la información mostrada por la UI proviene del State.

---

## viewmodel

Contiene la lógica del módulo.

El ViewModel:

- recibe Intents
- modifica el State
- nunca conoce componentes Compose

---

## ui

Contiene únicamente las pantallas del módulo.

No debe contener lógica de negocio.

---

# Core

Todo elemento reutilizable por más de un módulo debe vivir en `core`.

Ejemplos:

- botones
- diálogos
- utilidades
- constantes

---

# Navegación

Toda la navegación de la aplicación debe centralizarse en el paquete:

```
navigation
```

Las pantallas nunca deben conocer rutas directamente.

---

# Theme

Los colores, tipografía y estilos globales pertenecen exclusivamente a:

```
ui/theme
```

---

# Flujo MVI

```
Usuario

↓

Intent

↓

ViewModel

↓

StateFlow

↓

State

↓

UI
```

---

# Reglas del proyecto

- No crear carpetas fuera de esta arquitectura.
- No mover archivos entre módulos.
- No acceder directamente a otra Feature.
- Todo componente reutilizable debe ir a `core`.
- Toda nueva pantalla debe pertenecer a una Feature.

---

Esta arquitectura queda congelada en la versión 1.0 del proyecto.