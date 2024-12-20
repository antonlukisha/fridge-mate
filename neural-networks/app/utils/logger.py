import logging
import os
from datetime import datetime


def setup_logger(name):
    os.makedirs("./logs", exist_ok=True)
    file_format = "%(asctime)s - %(name)s - %(levelname)s - %(message)s"
    filename = f"./logs/{name}_{datetime.now().strftime('%Y-%m-%d_%H-%M-%S')}.log"
    logger = logging.getLogger()
    logger.setLevel(logging.INFO)

    file_handler = logging.FileHandler(filename)
    file_handler.setFormatter(logging.Formatter(file_format))

    console_handler = logging.StreamHandler()
    console_handler.setFormatter(logging.Formatter(file_format))

    logger.addHandler(file_handler)
    logger.addHandler(console_handler)
    return logger