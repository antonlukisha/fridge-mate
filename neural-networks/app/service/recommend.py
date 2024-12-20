import json

from service.data_processing import preprocess_data
from service.use_model import suggest_recipes
from utils.logger import setup_logger

logger = setup_logger("recommend")

def get_suggest_recipes(products_json, recipes_json):
    products = json.loads(products_json)
    recipes = json.loads(recipes_json)

    logger.info(f"Loaded {len(products)} products and {len(recipes)} recipes.")
    input_size = len(products)
    output_size = len(recipes)

    input_data = preprocess_data(products, recipes)
    suggests = suggest_recipes(input_data, input_size, output_size)

    recommendations = [
        {"recipe_id": recipes[i]["id"], "recipe_name": recipes[i]["name"], "suggest": suggests[i][0]}
        for i in range(len(recipes))
    ]

    logger.info("Recommendations generated successfully.")
    return recommendations
