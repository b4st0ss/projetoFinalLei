import { request } from './index.js';

export const userApi = {
    getAll() {
        return request('/user/all', 'GET');
    },
    changeStatus(userId, status) {
        const body = {
            userId,
            status
        };
        return request('/user/activate', 'PUT', null, body);
    },
    getProfile() {
        return request('/user/profile', 'GET');
    },
    updateProfile(field, newInfo) {
        const body = {
            field,
            newInfo
        };
        return request('/user/update', 'PUT', null, body);
    },
    getActiveTechs() {
        return request('/user/techs/active', 'GET');
    }
};