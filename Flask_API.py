from flask import Flask, request, jsonify
from flask_cors import CORS
import tensorflow as tf
import numpy as np
from PIL import Image
import base64
import io

app = Flask(__name__)
CORS(app)  # Allow requests from mobile app

# === Hardcoded class names (exact 20 classes used during model training) ===
CLASS_NAMES = [
    'Acne and Rosacea Photos',
    'Actinic Keratosis Basal Cell Carcinoma and other Malignant Lesions',
    'Atopic Dermatitis Photos',
    'Bullous Disease Photos',
    'Cellulitis Impetigo and other Bacterial Infections',
    'Eczema Photos',
    'Exanthems and Drug Eruptions',
    'Herpes HPV and other STDs Photos',
    'Light Diseases and Disorders of Pigmentation',
    'Lupus and other Connective Tissue diseases',
    'Melanoma Skin Cancer Nevi and Moles',
    'Poison Ivy Photos and other Contact Dermatitis',
    'Psoriasis pictures Lichen Planus and related diseases',
    'Seborrheic Keratoses and other Benign Tumors',
    'Systemic Disease',
    'Tinea Ringworm Candidiasis and other Fungal Infections',
    'Urticaria Hives',
    'Vascular Tumors',
    'Vasculitis Photos',
    'Warts Molluscum and other Viral Infections'
]

print(f"Loaded {len(CLASS_NAMES)} class names: {CLASS_NAMES}")

# === Load Trained Model ===
MODEL_PATH = r"C:\Users\Dell\Desktop\New folder\skinvision_cnn.h5"

print("Loading model...")
try:
    model = tf.keras.models.load_model(MODEL_PATH)
    print("Model loaded successfully!")
    model.summary()
    print(f"Model output shape: {model.output_shape}")
    print(f"Number of classes (model expects): {model.output_shape[-1]}")
except Exception as e:
    print(f"Error loading model: {e}")
    model = None

@app.route('/', methods=['GET'])
def home():
    return jsonify({
        'message': 'SkinVision API is running!',
        'model_loaded': model is not None,
        'classes': len(CLASS_NAMES),
        'class_names': CLASS_NAMES
    })

@app.route('/predict', methods=['POST'])
def predict():
    try:
        if model is None:
            return jsonify({'error': 'Model not loaded'}), 500
        
        data = request.get_json()
        if 'image' not in data:
            return jsonify({'error': 'No image provided'}), 400
        
        image_data = data['image']
        
        # Decode base64 image
        try:
            if ',' in image_data:
                image_data = image_data.split(',')[1]
            image_bytes = base64.b64decode(image_data)
            image = Image.open(io.BytesIO(image_bytes))
            if image.mode != 'RGB':
                image = image.convert('RGB')
            image = image.resize((224, 224))
        except Exception as e:
            return jsonify({'error': f'Invalid image data: {str(e)}'}), 400
        
        image_array = np.array(image, dtype=np.float32) / 255.0
        image_array = np.expand_dims(image_array, axis=0)
        
        print(f"Image shape: {image_array.shape}")
        
        prediction = model.predict(image_array)
        print(f"Prediction shape: {prediction.shape}")
        print(f"Prediction values: {prediction[0]}")
        
        predicted_index = np.argmax(prediction[0])
        confidence = float(prediction[0][predicted_index]) * 100
        predicted_class = CLASS_NAMES[predicted_index]
        
        print(f"Predicted: {predicted_class} with {confidence:.2f}% confidence")
        
        return jsonify({
            'success': True,
            'prediction': predicted_class,
            'confidence': round(confidence, 2),
            'all_predictions': {
                CLASS_NAMES[i]: round(float(prediction[0][i]) * 100, 2)
                for i in range(len(CLASS_NAMES))
            }
        })

    except Exception as e:
        print(f"Error in prediction: {str(e)}")
        import traceback
        traceback.print_exc()
        return jsonify({'error': f'Prediction failed: {str(e)}'}), 500

@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'healthy', 'model_loaded': model is not None})

if __name__ == '__main__':
    print("Starting SkinVision API server...")
    print(f"Model path: {MODEL_PATH}")
    print(f"Model loaded: {model is not None}")
    app.run(host='0.0.0.0', port=5000, debug=True)
