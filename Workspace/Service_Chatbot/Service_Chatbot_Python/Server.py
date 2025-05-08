import requests
from flask import Flask, request, jsonify
from flask_cors import CORS
from google.generativeai import types

from google.generativeai import configure, GenerativeModel

configure(api_key="AIzaSyCMJLH9q2Xx9I95e7VIuBiZCngowGJDnzQ")

system_instruction = """
Bạn là một trợ lý ảo của cửa hàng Tinh Tú - một cửa hàng buôn bán trang sức.
Nhiệm vụ của bạn là giúp khách hàng tìm kiếm thông tin về các sản phẩm trang sức của cửa hàng.
Tuyệt đối: Không được trả lời các câu hỏi không liên quan đến trang sức hoặc nằm ngoài phạm vi của cửa hàng.
"""

# function_calling_example = {
#     "name": "",
#     "description": "",
#     "parameters": {
#         "type": "object",
#         "properties": {
#             "product_name": {
#                 "type": "string",
#                 "description": "Tên sản phẩm trang sức mà khách hàng muốn tìm kiếm",
#             },
#             "product_type": {
#                 "type": "string",
#                 "description": "Loại sản phẩm trang sức mà khách hàng muốn tìm kiếm",
#             },
#         },
#         "required": ["product_name", "product_type"],
#     },
# }

introduce_products = {
    "name": "introduce_products",
    "description": "Giới thiệu sản phẩm trang sức của cửa hàng Tinh Tú. Thực hiện khi khách hàng hỏi về sản phẩm trang sức khi chưa rõ thông tin.",
    "parameters": {
        "type": "object",
        "properties": {}
    }
}

specific_product = {
    "name": "specific_product",
    "description": "Giới thiệu sản phẩm trang sức cụ thể của cửa hàng Tinh Tú. Thực hiện khi khách hàng hỏi về sản phẩm trang sức cụ thể.",
    "parameters": {
        "type": "object",
        "properties": {
        }
    },
}

tools = types.Tool(function_declarations=[introduce_products])

model = GenerativeModel(
    model_name="gemini-2.0-flash-001",
    tools=[tools],
    system_instruction=system_instruction,
)
app = Flask(__name__)
CORS(app)

@app.route("/api/v1/response", methods=["POST"])
def respond():
    data = request.get_json(force=True)
    prompt = data.get("prompt", "").strip()

    print("Received prompt:", prompt)

    if not prompt:
        return jsonify({"error": "Missing 'prompt'"}), 400
    ai_response = model.generate_content(contents=prompt)

    print("AI Response:", ai_response)

    if ai_response.candidates[0].content.parts[0].function_call:
        function_name = ai_response.candidates[0].content.parts[0].function_call.name
        if function_name == "introduce_products":
            # Request to http://localhost:8105/api/products to get product information
            response = requests.get("http://localhost:8105/api/products")
            if response.status_code == 200:
                response_prompt = "Dưới đây là danh sách sản phẩm nổi bật của cửa hàng Tinh Tú, hãy giới thiệu cho khách hàng chỉ 5 sản phẩm ngẫu nhiên ở dưới theo dạng ngôn ngữ tự nhiên (không markdown, không dùng '\\n'):\n\n"
                products = response.json()
                if not products:
                    return jsonify({"error": "No products found"}), 404
                response_prompt += f"\n\n{products}"
                ai_response_response = model.generate_content(contents=response_prompt)
                response_text = ai_response_response.candidates[0].content.parts[0].text
                if not response_text:
                    return jsonify({"error": "No response from AI"}), 500
                return jsonify({"response": response_text}), 200
            else:
                return jsonify({"error": "Failed to fetch product data"}), 500

        elif function_name == "specific_product":
            response = requests.get("http://localhost:8105/api/products")
            if response.status_code == 200:
                products = response.json()
                if not products:
                    return jsonify({"error": "No products found"}), 404
                product_list = [product["name"] for product in products]
                response_prompt = f"Khách hàng hỏi: {prompt}\n\nDưới đây là danh sách sản phẩm nổi bật của cửa hàng Tinh Tú, hãy trả về mã sản phẩm có liên quan nhất. TUYỆT ĐỐI CHỈ TRẢ VỀ MÃ"
                response_prompt += f"\n\n{product_list}"
                ai_response_response = model.generate_content(contents=response_prompt)
                response_text = ai_response_response.candidates[0].content.parts[0].text
                if not response_text:
                    return jsonify({"error": "No response from AI"}), 500
                return jsonify({"response": response_text,
                                "redirect": f"http://localhost:8205/product/{response_text}",
                                }), 200
            else:
                return jsonify({"error": "Failed to fetch product data"}), 500
    else:
        response = ai_response.candidates[0].content.parts[0].text
        if not response:
            return jsonify({"error": "No response from AI"}), 500

        return jsonify({"response": response}), 200


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8109)
