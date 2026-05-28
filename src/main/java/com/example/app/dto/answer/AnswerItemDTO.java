package com.example.app.dto.answer;

public record AnswerItemDTO(Long answerId ,Long formQuestionId, String questionText,String answerText, String status) {
}
