import httpx
import asyncio
from config.config import BASE_URL

async def fetch_products(token):
    async with httpx.AsyncClient() as api:
        response = await api.get(f"{BASE_URL}/products/all/type?token={token}")
        response.raise_for_status()
        return response.json()

async def fetch_recipes():
    async with httpx.AsyncClient() as api:
        response = await api.get(f"{BASE_URL}/recipes/all")
        response.raise_for_status()
        return response.json()

async def fetch_products_and_recipes(token):
    products, recipes = await asyncio.gather(
        fetch_products(token),
        fetch_recipes()
    )
    return products, recipes