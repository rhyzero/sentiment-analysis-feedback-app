"""
Flask application for sentiment analysis service with enhanced ML capability.

This service provides an API for analyzing the sentiment of text data using
transformer-based ML models with fallback to simpler approaches.
"""
from flask import Flask, request, jsonify
from flask_cors import CORS
import os
import logging
import time

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Create Flask app
app = Flask(__name__)
CORS(app)  # Enable CORS for all routes

# Lazy load the sentiment analyzer to improve startup time
analyzer = None

def get_analyzer():
    """
    Lazily initialize the sentiment analyzer to avoid slowing down app startup.
    Returns the appropriate analyzer based on environment settings.
    """
    global analyzer
    if analyzer is None:
        # Check if we should use enhanced model
        use_enhanced = os.environ.get("USE_ENHANCED_MODEL", "true").lower() == "true"
        
        if use_enhanced:
            try:
                logger.info("Initializing enhanced transformer-based sentiment analyzer...")
                from src.models.enhanced_sentiment_model import EnhancedSentimentAnalyzer
                analyzer = EnhancedSentimentAnalyzer()
                logger.info("Enhanced sentiment analyzer initialized successfully")
            except Exception as e:
                logger.error(f"Error initializing enhanced model: {str(e)}")
                logger.info("Falling back to basic sentiment analyzer")
                from src.models.sentiment_model import SentimentAnalyzer
                analyzer = SentimentAnalyzer()
        else:
            logger.info("Initializing basic sentiment analyzer...")
            from src.models.sentiment_model import SentimentAnalyzer
            analyzer = SentimentAnalyzer()
    
    return analyzer

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
    start_time = time.time()
    
    try:
        data = request.get_json()
        
        if not data or 'text' not in data:
            return jsonify({'error': 'No text provided'}), 400
            
        text = data['text']
        logger.info(f"Analyzing sentiment for text: {text[:50]}...")
        
        # Get analyzer (lazy initialization)
        current_analyzer = get_analyzer()
        
        # Analyze sentiment
        result = current_analyzer.predict(text)
        
        # Log processing time
        processing_time = time.time() - start_time
        logger.info(f"Sentiment analysis completed in {processing_time:.2f} seconds")
        
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
        'service': 'sentiment-analysis',
        'enhanced_model': os.environ.get("USE_ENHANCED_MODEL", "true").lower() == "true"
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
        'model_type': 'transformer-based' if os.environ.get("USE_ENHANCED_MODEL", "true").lower() == "true" else 'rule-based',
        'endpoints': {
            '/analyze': 'POST - Analyze text sentiment',
            '/health': 'GET - Check service health'
        }
    })

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5000))
    logger.info(f"Starting sentiment analysis service on port {port}")
    app.run(debug=True, host='0.0.0.0', port=port)