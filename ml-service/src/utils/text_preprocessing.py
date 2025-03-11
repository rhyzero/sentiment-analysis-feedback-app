"""
Text preprocessing utilities for sentiment analysis.

This module contains functions for cleaning and preprocessing
text data before sentiment analysis.
"""
import re
import nltk
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer

# Download required NLTK resources
try:
    nltk.data.find('tokenizers/punkt')
except LookupError:
    nltk.download('punkt')
    
try:
    nltk.data.find('tokenizers/punkt_tab')
except LookupError:
    nltk.download('punkt_tab')
    
try:
    nltk.data.find('corpora/stopwords')
except LookupError:
    nltk.download('stopwords')
    
try:
    nltk.data.find('corpora/wordnet')
except LookupError:
    nltk.download('wordnet')

def clean_text(text):
    """
    Clean the text by removing special characters and converting to lowercase.
    Takes in a string of text.
    Returns cleaned text.
    """
    # Convert to lowercase
    text = text.lower()
    
    # Remove special characters and numbers, keeping only letters and spaces
    text = re.sub(r'[^a-zA-Z\s]', '', text)
    
    # Remove extra whitespace
    text = re.sub(r'\s+', ' ', text).strip()
    
    return text

def remove_stopwords(text):
    """
    Remove common stopwords ('the', 'and', 'a') from text.
    Takes in a string of text.
    Returns text with stopwords removed.
    """
    stop_words = set(stopwords.words('english'))
    word_tokens = word_tokenize(text)
    filtered_text = [word for word in word_tokens if word not in stop_words]
    return ' '.join(filtered_text)

def lemmatize_text(text):
    """
    Lemmatize (revert words to their dictionary form) words in text to their root form.
    Takes in a string of text.
    Returns lemmatized text.
    """
    lemmatizer = WordNetLemmatizer()
    word_tokens = word_tokenize(text)
    lemmatized_text = [lemmatizer.lemmatize(word) for word in word_tokens]
    return ' '.join(lemmatized_text)

def preprocess_text(text):
    """
    Apply full preprocessing pipeline to text.
    Takes in raw input text.
    Returns fully preprocessed text.
    """
    text = clean_text(text)
    text = remove_stopwords(text)
    text = lemmatize_text(text)
    return text

# Testing the preprocessing function
if __name__ == "__main__":
    sample_text = "This is a sample text with UPPERCASE and 123 numbers! How's it going?"
    print("Original:", sample_text)
    print("Preprocessed:", preprocess_text(sample_text))