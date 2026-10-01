import store from '../store'

const API_URL = "http://localhost:8080";

function getHeaders(customToken) {
    const headers = {
        "Content-Type": "application/json"
    };

    const token = customToken || store.getters['auth/token'];

    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    return headers;
}

export async function request(url, method, customToken = null, body = null) {
    const response = await fetch(`${API_URL}${url}`, {
        method,
        headers: getHeaders(customToken),
        body: body ? JSON.stringify(body) : null
    });

    const textData = await response.text();
    const data = textData ? JSON.parse(textData) : {};

    if (!response.ok) {
        throw new Error(data.message || `Erro de servidor (Status: ${response.status})`);
    }

    return data;
}