import { request } from './index.js';

export const authApi = {

    login(username, password){
        const body = {
            username,
            password
        }
        return request('/auth/login', 'POST', null, body);
    },
    register(name, username, email, password){
        const body = {
            name,
            username,
            email,
            password
        }
        return request('/auth/register', 'POST', null, body);
    },
    registerAdmin(name, username, email, password, role) {
        const body = {
            name,
            username,
            email,
            password,
            role };
        return request('/auth/registerAdmin', 'POST', null, body);
    }
};