# DigitalFix Catalog — Caso 6

Microservicio catálogo: servicios técnicos y repuestos. Spring Boot 4 + JPA + H2 file.

## Modelo

- `Servicio`: nombre único, tarifa, categoría, activo, responsable
- `Repuesto`: sku único, precio, `stock`, `stockMinimo`, `stockBajo = stock <= stockMinimo`, responsable

Gestión Admin + Supervisor (`assertSupervisor`). `update/delete` respeta `responsable` (solo dueño o sin asignar).

## Regla clave

Stock disminuye al asignar la orden: `POST /api/catalog/repuestos/{id}/consumir` descuenta 1, `409` si insuficiente. Lo llama el BFF tras `ASIGNADA`. `descontar/reponer` exigen rol + dueño.

## Endpoints

Base `/api/catalog` (puerto `8081`):

- `GET /api/catalog/servicios?soloActivos=true`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`
- `GET /api/catalog/repuestos`, `GET /stock-bajo`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`
- `POST /{id}/descontar {"cantidad":N}`, `POST /{id}/reponer {"cantidad":N}`, `POST /{id}/consumir`

## Persistencia

H2 file `/tmp/catalogdb` + volumen Docker. Seed: 2 servicios + 2 repuestos (`BATT-ORIG-001` ya en stock bajo).

## Run

```
./mvnw -DskipTests package
docker compose up -d --build
```

## Colección Postman EP1

https://benjamin-1874968.postman.co/workspace/Benjamin's-Workspace~edccb9a7-d9b0-4dac-8429-645e20bcb7f8/collection/45505473-ff1f78c6-e53c-4f7b-b861-e5387a510074?action=share&creator=45505473
