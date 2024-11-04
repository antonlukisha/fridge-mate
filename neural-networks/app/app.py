from flask import Flask, request, jsonify
import torch
import torch.nn as nn

app = Flask(__name__)

class SimpleNN(nn.Module):
    def __init__(self):
        super(SimpleNN, self).__init__()
        self.fc1 = nn.Linear(28 * 28, 128)
        self.fc2 = nn.Linear(128, 64)
        self.fc3 = nn.Linear(64, 10)

    def forward(self, x):
        x = x.view(-1, 28 * 28)
        x = torch.relu(self.fc1(x))
        x = torch.relu(self.fc2(x))
        x = self.fc3(x)
        return x

model = SimpleNN()
model.load_state_dict(torch.load('./models/simple_nn.pth'))
model.eval()

@app.route('/recommend', methods=['POST'])
def recommend():
    data = request.json
    recommendations = {}
    return jsonify(recommendations)

if __name__ == '__main__':
    app.run(port=5000)
