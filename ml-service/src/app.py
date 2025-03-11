"""
Flask application for sentiment analysis service.

This service provides an API for analyzing the sentiment of text data.
"""
from flask import Flask, request, jsonify
from flask_cors import CORS
import os
import logging
from .models.sentiment_model import SentimentAnalyzer

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Create Flask app
app = Flask(__name__)
CORS(app)  # Enable CORS for all routes

# Initialize sentiment analyzer
model_path = os.path.join(os.path.dirname(__file__), 'models', 'sentiment_model.pkl')
analyzer = SentimentAnalyzer(model_path)

@app.route('/analyze', methods=['POST'])
def analyze_sentiment():
    """
    Analyze the sentiment of text provided in the request.
    Expected JSON input:
    {
        "text": "Text to analyze"
    }
    Returns JSON with the sentiment analysis results.
    """
    try:
        data = request.get_json()
        
        if not data or 'text' not in data:
            return jsonify({'error': 'No text provided'}), 400
            
        text = data['text']
        logger.info(f"Analyzing sentiment for text: {text[:50]}...")
        
        # Analyze sentiment
        result = analyzer.predict(text)
        
        return jsonify(result)
        
    except Exception as e:
        logger.error(f"Error analyzing sentiment: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/health', methods=['GET'])
def health_check():
    """
    Health check endpoint to verify the service is running.
    Returns JSON with status information.
    """
    return jsonify({
        'status': 'healthy',
        'service': 'sentiment-analysis'
    })

@app.route('/', methods=['GET'])
def index():
    """
    Root endpoint with information about the service.
    Returns JSON with service information.
    """
    return jsonify({
        'service': 'Sentiment Analysis API',
        'version': '1.0.0',
        'endpoints': {
            '/analyze': 'POST - Analyze text sentiment',
            '/health': 'GET - Check service health'
        }
    })

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5000))
    logger.info(f"Starting sentiment analysis service on port {port}")
    app.run(debug=True, host='0.0.0.0', port=port)