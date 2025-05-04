FROM python:3.10-slim

WORKDIR /app

COPY Setup.py setup.py
RUN pip install -e .

COPY . .

RUN adduser --disabled-password --gecos '' appuser
RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8000

CMD ["python", "src/Gateway.py"]
