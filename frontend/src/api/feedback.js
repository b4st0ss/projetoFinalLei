import { request } from './index.js';

export const feedbackApi = {

    getByAnswer(answerId) {
        return request(`/feedback/answer/${answerId}`, 'GET');
    },
    getByRequirement(reqId) {
        return request(`/feedback/requirement/${reqId}`, 'GET');
    },
    create(message, answerId, requirementId) {
        const body = { message, answerId, requirementId };
        return request('/feedback/create', 'POST', null, body);
    }
};