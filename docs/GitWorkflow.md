# HabitatPlus - Flujo de trabajo con Git

## Ramas del proyecto

El proyecto utiliza las siguientes ramas:

- `main`
- `develop`
- `feature/*`

---

## main

Contiene únicamente versiones estables.

Nunca se trabaja directamente sobre esta rama.

---

## develop

Es la rama principal de desarrollo.

Todas las nuevas funcionalidades se integran primero aquí.

---

## feature/*

Cada integrante desarrolla su módulo en una rama independiente.

Ejemplos:

- feature/parqueos
- feature/visitas
- feature/reservas
- feature/perfil

---

# Flujo de trabajo

1. Actualizar develop

```
git checkout develop
git pull
```

2. Cambiar a la rama personal

```
git checkout feature/parqueos
```

3. Desarrollar la funcionalidad

4. Commit

```
git add .
git commit -m "Descripción del cambio"
```

5. Push

```
git push
```

6. Crear Pull Request hacia develop

7. Esperar revisión

8. Merge

---

# Reglas

- Nunca trabajar directamente sobre main.
- No hacer Push directamente a develop.
- Todo cambio debe pasar por Pull Request.
- Resolver conflictos antes de crear el Pull Request.