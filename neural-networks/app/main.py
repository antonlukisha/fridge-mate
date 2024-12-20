import httpx
from flask import Flask, jsonify, request
from api.api_client import fetch_products_and_recipes
from service.recommend import get_suggest_recipes
from utils.logger import setup_logger

app = Flask(__name__)
logger = setup_logger("main")

@app.route('/recommend/recipes', methods=['POST'])
async def recommend_recipes():
    token = request.args.get('token')
    if not token:
        return jsonify({"error": "Token is required"}), 400
    try:
        products, recipes = await fetch_products_and_recipes(token)
        recommendations = await get_suggest_recipes(products, recipes)
        return jsonify({"recommendations": recommendations})
    except httpx.HTTPStatusError as error:
        logger.error(f"Recommendation API failed with status {error.response.status_code}.")
        return jsonify({"error": f"Recommendation API failed with status {error.response.status_code}"})
    except httpx.HTTPError as error:
        logger.error(f"HTTP error occurred: look at logger file.")
        return jsonify({"error": str(error)})
    except Exception as error:
        logger.error(f"Error: look at logger file.")
        return jsonify({"error": str(error)})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
