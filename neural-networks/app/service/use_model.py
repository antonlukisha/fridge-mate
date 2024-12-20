import torch

from config.config import HIDDEN_SIZE
from service.nn_models import Recommend
from utils.logger import setup_logger

logger = setup_logger("use_model")

def load_model(input_size, output_size, model_weights_path):
    logger.info("Start loading model weights.")
    model = Recommend(input_size, HIDDEN_SIZE, output_size)
    model.load_state_dict(torch.load(model_weights_path))
    model.eval()
    logger.info("Model weights loaded successfully.")
    return model

def suggest_recipes(x_data, input_size, output_size):
    logger.info("Creating recipe suggest.")
    model = load_model(input_size, output_size, "models/recipe_model_weights.pth")
    with torch.no_grad():
        suggests = model(x_data)
    logger.info("Creation of suggest completed.")
    return suggests



