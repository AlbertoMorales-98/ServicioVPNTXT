# Uso con Docker

Construccion local:

```bash
docker build -t servicio-vpntxt:local .
```

Ejecucion local:

```bash
docker compose up --build
```

Despliegue offline:

```bash
docker save -o servicio-vpntxt-local.tar servicio-vpntxt:local
tar -czf ServicioVPNTXT-deploy.tar.gz docker-compose.yml docker-compose.offline.yml DOCKER.md storage
```
