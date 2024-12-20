import torch

def preprocess_data(products, recipes):
    input_data = []
    for recipe in recipes:
        recipe_list = []
        ingredients = list(recipe["ingredients"].split(", "))
        for product in products:
            recipe_list.append(1 if (product['name'] in ingredients) else 0)
        input_data.append(recipe_list)
    return torch.tensor(input_data, dtype=torch.float32)