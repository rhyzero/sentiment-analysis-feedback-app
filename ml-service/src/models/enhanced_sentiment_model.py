"""
Enhanced sentiment analysis model implementation using transformer-based models.

This module provides a more sophisticated sentiment analysis approach using
pre-trained transformer models from Hugging Face.
"""
import os
import torch
import numpy as np
from transformers import AutoTokenizer, AutoModelForSequenceClassification
from src.utils.text_preprocessing import preprocess_text

class EnhancedSentimentAnalyzer:
    """
    A class for sentiment analysis that categorizes text as positive, negative, or neutral
    using a pre-trained transformer model.
    
    Attributes:
        model_name (str): Name of the pre-trained model to use
        labels (list): List of sentiment labels
        tokenizer: The tokenizer for the transformer model
        model: The pre-trained transformer model
    """
    
    def __init__(self, model_name="distilbert-base-uncased-finetuned-sst-2-english"):
        """
        Initialize the enhanced sentiment analyzer.
        
        Args:
            model_name (str): Name of the pre-trained model from Hugging Face
        """
        self.model_name = model_name
        self.labels = ["negative", "neutral", "positive"]
        
        try:
            # Load tokenizer and model
            self.tokenizer = AutoTokenizer.from_pretrained(model_name)
            self.model = AutoModelForSequenceClassification.from_pretrained(model_name)
            
            # Check if we need to map model outputs to our label set
            if hasattr(self.model.config, 'id2label'):
                self.id2label = self.model.config.id2label
                # Map model's labels to our standard labels
                self.label_mapping = {}
                for key, value in self.id2label.items():
                    if "positive" in value.lower():
                        self.label_mapping[key] = "positive"
                    elif "negative" in value.lower():
                        self.label_mapping[key] = "negative"
                    else:
                        self.label_mapping[key] = "neutral"
            else:
                # Default mapping if model doesn't provide labels
                self.id2label = {0: "negative", 1: "positive"}
                self.label_mapping = {0: "negative", 1: "positive"}
                
        except Exception as e:
            print(f"Error loading transformer model: {e}")
            raise
    
    def predict(self, text):
        """
        Predict sentiment for a given text using the transformer model.
        
        Args:
            text (str): The text to analyze
            
        Returns:
            dict: A dictionary with the sentiment analysis results
        """
        try:
            # Preprocess text (optional, transformers handle most preprocessing)
            clean_text = text.lower()
            
            # Encode the text
            inputs = self.tokenizer(clean_text, return_tensors="pt", truncation=True, max_length=512)
            
            # Get model prediction
            # Disables gradients since we aren't model training
            # ** takes all entries in input dictionary and passes them as separate arguments
            # Puts raw prediction score into a numpy array
            with torch.no_grad():
                outputs = self.model(**inputs)
                scores = outputs.logits.squeeze().numpy()
                
                # Apply softmax to turn raw scores into probabilities 
                exp_scores = np.exp(scores - np.max(scores))
                probs = exp_scores / exp_scores.sum()
                
                # Find index of highest probability (winning sentiment)
                # Confidence score is the actual probability value
                predicted_class = int(np.argmax(probs))
                confidence = float(probs[predicted_class])
                
                # Map to our standard label set
                if predicted_class in self.label_mapping:
                    label = self.label_mapping[predicted_class]
                else:
                    # If prediction is 1 (positive)
                    if len(probs) == 2 and predicted_class == 1:
                        label = "positive"
                    # If prediction is 0 (negative)
                    elif len(probs) == 2 and predicted_class == 0:
                        label = "negative"
                    else:
                        # Default case
                        label = "neutral"
                        
                # Stores probabilities for each sentiment in a dictionary 
                label_probs = {}
                if len(probs) == 2:  # Binary model (common case)
                    label_probs = {
                        "negative": float(probs[0]),
                        "neutral": 0.0,
                        "positive": float(probs[1])
                    }
                else:
                    # For models with different label sets, map to our standard labels
                    for i, prob in enumerate(probs):
                        mapped_label = self.label_mapping.get(i, "neutral")
                        if mapped_label in label_probs:
                            label_probs[mapped_label] += float(prob)
                        else:
                            label_probs[mapped_label] = float(prob)
                
                # Ensure we have all our standard labels
                for std_label in self.labels:
                    if std_label not in label_probs:
                        label_probs[std_label] = 0.0
                
                return {
                    "text": clean_text,
                    "label": label,
                    "score": confidence,
                    "probabilities": label_probs
                }
                
        except Exception as e:
            print(f"Error in transformer prediction: {e}")
            # Fall back to simple analysis if transformer fails
            return self._fallback_predict(text)
    
    def _fallback_predict(self, text):
        """
        Simple rule-based sentiment analysis as a fallback.
        Args:
            text (str): The text to analyze
            
        Returns:
            dict: A dictionary with the sentiment analysis results
        """
        text = text.lower()
        
        # Positive and negative word lists
        positive_words = ["good", "great", "excellent", "amazing", "love", "awesome", 
                        "fantastic", "wonderful", "best", "happy", "like", "enjoy"]
        negative_words = ["bad", "terrible", "awful", "hate", "worst", "poor", 
                        "horrible", "disappointing", "useless", "dislike", "wrong"]
        
        # Count word occurrences
        positive_count = sum(1 for word in positive_words if word in text.split())
        negative_count = sum(1 for word in negative_words if word in text.split())
        
        # Determine sentiment based on word counts
        total_count = positive_count + negative_count
        
        if total_count == 0:
            label = "neutral"
            probabilities = {"negative": 0.2, "neutral": 0.6, "positive": 0.2}
        else:
            pos_ratio = positive_count / total_count
            neg_ratio = negative_count / total_count
            
            if pos_ratio > neg_ratio:
                label = "positive"
                probabilities = {"negative": 0.1, "neutral": 0.3, "positive": 0.6}
            elif neg_ratio > pos_ratio:
                label = "negative"
                probabilities = {"negative": 0.6, "neutral": 0.3, "positive": 0.1}
            else:
                label = "neutral"
                probabilities = {"negative": 0.3, "neutral": 0.4, "positive": 0.3}
        
        # Adjust confidence based on the specific text
        if "amazing" in text or "love" in text:
            label = "positive"
            probabilities = {"negative": 0.05, "neutral": 0.15, "positive": 0.8}
        elif "hate" in text or "terrible" in text:
            label = "negative"
            probabilities = {"negative": 0.8, "neutral": 0.15, "positive": 0.05}
        
        # Get the highest probability
        score = probabilities[label]
        
        return {
            "text": text,
            "label": label,
            "score": score,
            "probabilities": probabilities
        }


# Example usage
if __name__ == "__main__":
    try:
        analyzer = EnhancedSentimentAnalyzer()
        
        # Test with some sample texts
        sample_texts = [
            "I really love this product, it's amazing!",
            "This is the worst experience I've ever had.",
            "The product arrived on time, it works as expected."
        ]
        
        for text in sample_texts:
            result = analyzer.predict(text)
            print(f"Text: {text}")
            print(f"Sentiment: {result['label']} (Score: {result['score']:.2f})")
            print(f"Probabilities: {result['probabilities']}")
            print()
    except Exception as e:
        print(f"Error in example: {e}")