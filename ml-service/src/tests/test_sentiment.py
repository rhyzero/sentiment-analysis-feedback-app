"""
Tests for the sentiment analysis model.
"""
import sys
import os
import pytest

# Add the src directory to the Python path
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from src.models.sentiment_model import SentimentAnalyzer
from src.utils.text_preprocessing import preprocess_text

def test_preprocessing():
    """Test the text preprocessing function."""
    text = "This is a TEST with numbers 123 and punctuation!!!"
    processed = preprocess_text(text)
    
    # Check that text is lowercase
    assert processed.lower() == processed
    
    # Check that numbers and punctuation are removed
    assert not any(c.isdigit() for c in processed)
    assert not any(c in "!?.,;:" for c in processed)

def test_sentiment_analyzer_initialization():
    """Test that the sentiment analyzer initializes correctly."""
    analyzer = SentimentAnalyzer()
    assert analyzer is not None
    assert analyzer.labels == ["negative", "neutral", "positive"]

def test_sentiment_prediction_format():
    """Test that the sentiment prediction has the correct format."""
    analyzer = SentimentAnalyzer()
    result = analyzer.predict("This is a test")
    
    # Check result structure
    assert "sentiment" in result
    assert "confidence" in result
    assert "text" in result
    
    # Check sentiment is one of the expected labels
    assert result["sentiment"] in analyzer.labels
    
    # Check confidence is a float between 0 and 1
    assert isinstance(result["confidence"], float)
    assert 0 <= result["confidence"] <= 1

if __name__ == "__main__":
    # Run tests manually
    test_preprocessing()
    test_sentiment_analyzer_initialization()
    test_sentiment_prediction_format()
    print("All tests passed!")