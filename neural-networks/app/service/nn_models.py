import torch
import torch.nn as nn
import torch.optim as optim
from config.config import HIDDEN_SIZE, LEARNING_RATE, EPOCH_NUM
from utils.logger import setup_logger

logger = setup_logger("nn_models")

class Recommend(nn.Module):
    def __init__(self, input_size, hidden_size, output_size):
        super(Recommend, self).__init__()
        self.fc1 = nn.Linear(input_size, hidden_size)
        self.relu = nn.ReLU()
        self.fc2 = nn.Linear(hidden_size, output_size)
        self.softmax = nn.Softmax(dim=1)

    def forward(self, x):
        x = self.fc1(x)
        x = self.relu(x)
        x = self.fc2(x)
        x = self.softmax(x)
        return x

def train_model(x_train, y_train, input_size, output_size, epoch_num = EPOCH_NUM):
    logger.info("Training started.")
    model = Recommend(input_size, HIDDEN_SIZE, output_size)
    criterion = nn.MSELoss()
    optimizer = optim.Adam(model.parameters(), lr=LEARNING_RATE)

    for epoch in range(epoch_num):
        outputs = model(x_train)
        loss = criterion(outputs, y_train)

        optimizer.zero_grad()
        loss.backward()
        optimizer.step()

        if epoch % 10 == 0:
            logger.info(f"Epoch {epoch + 1} from {epoch_num} finished with loss={loss.item():.4f}.")
    torch.save(model.state_dict(), "models/recipe_model_weights.pth")
    logger.info("Model training finished. Weight saved to 'models/recipe_model_weights.pth'.")
    return model