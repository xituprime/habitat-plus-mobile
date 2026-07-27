# HabitatPlus Mobile

Aplicación móvil para la administración inteligente de condominios, desarrollada como proyecto académico utilizando Kotlin, Jetpack Compose y Material 3.

---

# Arquitectura

El proyecto utiliza:

- Feature-Based Architecture
- MVI (Model - View - Intent)
- Navigation Compose
- Material 3
- Kotlin

La arquitectura se encuentra documentada en la carpeta:

```
docs/
```

---

# Tecnologías

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Android Studio
- Git
- GitHub

---

# Estructura

```
com.habitatplus.app
│
├── core
├── features
├── navigation
└── ui
```

Cada módulo implementa la siguiente estructura:

```
feature
│
├── intent
├── model
├── state
├── ui
└── viewmodel
```

---

# Flujo de trabajo

```
feature/*
        │
        ▼
develop
        │
        ▼
main
```

Todo cambio debe realizarse mediante Pull Request.

---

# Documentación

La carpeta `docs` contiene:

- Architecture.md
- GitWorkflow.md
- CodingStandards.md

---

# Integrantes

- Axel Antonio Xitumul Chen
- Junior
- Kennet

---

# Estado del proyecto

🚧 Arquitectura V1.0 completada.

Actualmente se encuentra en desarrollo de funcionalidades.