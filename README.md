Pastas:
- `backend/` Spring Boot
- `frontend/` React (Vite)

## Rodar local com Docker
```bash
docker compose up --build
# Frontend: http://localhost:3000  (env VITE_API_BASE_URL aponta para http://localhost:8080)
# Backend:  http://localhost:8080
```

## Endpoints
- `POST /chat`
- `GET /chat/history/{conversationId}`

## Fixes importantes
- RedisConfig agora usa `GenericJackson2JsonRedisSerializer` + `ObjectMapper` com typing seguro (evita problemas de `setObjectMapper`).
- Dockerfile do backend é multi-stage (não depende de build prévio).
- Sanitização e defesa simples contra prompt-injection.

  ##Para Rodar Local
  - certifique-se que estar rodando o Docker
  - use o bash: cd backend > mvn spring-boot:run
  - use o bash: cd frontend > npm run dev
 
    ##Gostaria de Agradecer especialmente a CloulWalk inc, pelo desafio proposto.##
