from flask import Flask, request, jsonify
import os

from google.generativeai import configure, GenerativeModel

configure(api_key="AIzaSyCMJLH9q2Xx9I95e7VIuBiZCngowGJDnzQ")

system_instruction = """

"""

model = GenerativeModel(
    model_name="gemini-2.0-flash-001",
    system_instruction=system_instruction,
)
app = Flask(__name__)


@app.route("/api/v1/response", methods=["POST"])
def respond():
    data = request.get_json(force=True)
    prompt = data.get("prompt", "").strip()
    if not prompt:
        return jsonify({"error": "Missing 'prompt'"}), 400

    ai_response = model.generate_content(contents=prompt)
    return jsonify({"response": ai_response.text})


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8109)
