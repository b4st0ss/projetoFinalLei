export default {
    namespaced: true,

    state: () => ({
        user: null,
        token: null,
    }),

    mutations: {
        SET_AUTH(state, payload) {
            state.user = {
                name: payload.name,
                role: payload.role,
            }

            state.token = payload.token
        },

        CLEAR_AUTH(state) {
            state.user = null
            state.token = null
        },
    },

    actions: {
        loginSuccess({ commit }, payload) {
            commit('SET_AUTH', payload)
        },

        logout({ commit }) {
            commit('CLEAR_AUTH')
        },
    },

    getters: {
        currentUser: (state) => state.user,

        token: (state) => state.token,

        isAuthenticated: (state) => !!state.token,
    },
}