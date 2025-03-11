import api from "./api";

export const submitFeedback = async (feedbackData) => {
  try {
    const response = await api.post("/feedback", feedbackData);
    return response.data;
  } catch (error) {
    console.error("Error submitting feedback:", error);
    throw error;
  }
};

export const getFeedbackList = async () => {
  try {
    const response = await api.get("/feedback");
    return response.data;
  } catch (error) {
    console.error("Error fetching feedback list:", error);
    throw error;
  }
};
