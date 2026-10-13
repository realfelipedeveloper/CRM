import os
from typing import Any

import httpx
from fastapi import FastAPI, Header, HTTPException
from pydantic import BaseModel, Field

app = FastAPI(title="Agentes do CRM Áurea", version="0.1.0")
CRM_API_URL = os.getenv("CRM_API_URL", "http://localhost:8080")


class Pergunta(BaseModel):
    texto: str = Field(min_length=3, max_length=1000)


class Resposta(BaseModel):
    resposta: str
    dados_consultados: list[str]
    limitacoes: list[str]


@app.get("/health")
async def health() -> dict[str, str]:
    return {"status": "saudável", "servico": "agentes"}


@app.post("/agentes/inteligencia-negocios", response_model=Resposta)
async def inteligencia_negocios(pergunta: Pergunta, authorization: str | None = Header(default=None)) -> Resposta:
    if not os.getenv("OPENAI_API_KEY"):
        raise HTTPException(503, "A integração OpenAI ainda não possui credencial configurada.")
    if not authorization:
        raise HTTPException(401, "Autenticação do CRM é obrigatória.")

    async with httpx.AsyncClient(timeout=10) as cliente:
        retorno = await cliente.get(f"{CRM_API_URL}/api/bi/dashboard", headers={"Authorization": authorization})
        if retorno.status_code != 200:
            raise HTTPException(retorno.status_code, "Não foi possível consultar métricas autorizadas.")
        metricas: dict[str, Any] = retorno.json()

    # Importação tardia mantém o healthcheck funcional sem credencial externa.
    from agents import Agent, Runner
    agente = Agent(
        name="Agente de Inteligência de Negócios",
        instructions=(
            "Responda exclusivamente em português brasileiro. Use somente os dados estruturados fornecidos. "
            "Não invente números, não confunda correlação com causalidade e sempre explicite limitações. "
            "Diferencie receita, recebimento, despesas e resultado operacional."
        ),
        output_type=Resposta,
    )
    contexto = f"Pergunta: {pergunta.texto}\nMétricas autorizadas: {metricas}"
    resultado = await Runner.run(agente, contexto, max_turns=3)
    return resultado.final_output

