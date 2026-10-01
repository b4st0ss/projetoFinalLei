import { request } from './index.js';

export const projectApi = {

    getAll() {
        return request('/project/getProjects', 'GET');
    },
    create(title, description, email) {
        const body = {
            title,
            description,
            email
        };
        return request('/project/create', 'POST', null, body);
    },

    getDetails(projectId) {
        return request(`/project/${projectId}/details`, 'GET');
    },

    submitClientForm(projectId, answers, requirements, draft, categories = []) { // <-- Adicionar categories
        const body = {
            projectId: projectId,
            answers: answers,
            requirements: requirements,
            draft: draft,
            categories: categories
        };
        return request('/project/clientForm', 'POST', null, body);
    },

    getTechs(projectId) {
        return request(`/project/${projectId}/getTechs`, 'GET');
    },
    associateTech(projectId, techId) {
        return request('/project/associateTech', 'POST', null, { projectId, techId });
    },
    disassociateTech(projectId, techId) {
        return request('/project/disassociateTech', 'DELETE', null, { projectId, techId });
    },

};