FROM python:3.12-slim

WORKDIR /app

COPY . .

COPY ./models ./models

RUN python dependencies.py

EXPOSE 5000

CMD ["gunicorn", "-w", "4", "app:app", "-b", "0.0.0.0:5000"]
