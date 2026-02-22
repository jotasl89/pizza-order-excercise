# pizza-order

Aplicacion de ejemplo para gestionar pedidos de pizza con Quarkus, LittleHorse (workflow engine), y PostgreSQL.

## Que hace el proyecto

- Expone un API REST para crear pedidos y consultar su estado.
- Ejecuta un workflow con LittleHorse para orquestar el pedido (pago, preparacion, horneado, entrega, completado).
- Persiste pedidos en PostgreSQL con Panache/Hibernate ORM.
- Expone Swagger UI (OpenAPI) para documentar el API.

## Flujo del workflow

1. Crear pedido en base de datos.
2. Confirmar pago (actualmente simula fallo con excepcion para probar cancelacion).
3. Preparar pizza.
4. Hornear pizza.
5. Entregar pizza.
6. Completar pedido.

Si ocurre un error en pago, preparacion, horneado o entrega, el workflow cancela el pedido y marca el estado como `CANCELLED`.

## Endpoints

Base path: `/pizza-order`

### Crear pedido

`POST /pizza-order?type=pepperoni&size=mediana`

Respuesta: texto con el numero de pedido generado.

### Consultar estado

`GET /pizza-order?id=123`

Respuesta JSON:

```json
{
  "id": 1,
  "state": "CANCELLED",
  "description": "Payment failed"
}
```

## Swagger UI y OpenAPI

- Swagger UI: <http://localhost:8081/q/swagger-ui>
- OpenAPI JSON: <http://localhost:8081/q/openapi>

## Configuracion local

El proyecto usa estas propiedades (ver [src/main/resources/application.properties](src/main/resources/application.properties)):

- Puerto HTTP: `8081`
- PostgreSQL: `jdbc:postgresql://localhost:5433/pizza-order`
- Usuario/Password: `postgres` / `postgres`

## Ejecutar en modo dev

```shell script
./mvnw quarkus:dev
```

Dev UI de Quarkus: <http://localhost:8081/q/dev>

## Empaquetado

```shell script
./mvnw package
```

Luego ejecutar:

```shell script
java -jar target/quarkus-app/quarkus-run.jar
```

## Ejecutar tests

```shell script
./mvnw test
```

## Notas importantes

- Cada task simula tiempos con `Thread.sleep` para emular procesos reales.

## Tecnologias

- Quarkus 3.x
- LittleHorse (workflows y tasks)
- Hibernate ORM + Panache
- PostgreSQL
- UnitTest
- Mockito
- OpenAPI / Swagger UI
