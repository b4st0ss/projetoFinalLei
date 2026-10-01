import { request } from './index.js';

export const formQuestionApi = {

    getAll() {
        return request('/formquestion/getAll', 'GET');
    },

    create(questionText) {
        // Corrigido para "formquestion"
        return request('/formquestion/create', 'POST', null, { question: questionText });
    },

    toggleStatus(questionId) {
        // Corrigido para "formquestion"
        return request(`/formquestion/select/${questionId}`, 'POST');
    }

};