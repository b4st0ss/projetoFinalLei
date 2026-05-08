import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../components/dashboard/Dashboard.vue'
import Home from "../components/Home.vue";
import CreateProject from '../components/dashboard/CreateProject.vue'
import AnswerProject from "../components/dashboard/AnswerProject.vue";
import DashboardMain from "../components/dashboard/DashboardMain.vue";

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home
    },
    {
        path: '/dashboard',
        component: Dashboard,
        beforeEnter: [isLogged],
        children: [
            {
                path: 'dashboard-main',
                name: 'DashboardMain',
                component: DashboardMain,
                beforeEnter: [isLogged, onlyAdmin]
            },
            {
                path: 'create-project',
                name: 'CreateProject',
                component: CreateProject,
                beforeEnter: [isLogged, onlyAdmin]
            },
            {
                path: 'answer-project',
                name: 'Answer-Project',
                component: AnswerProject,
                beforeEnter: [isLogged, onlyClient]
            }
        ]
    }]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// to check if user is logged
function isLogged (to, from, next) {
    const savedUser = localStorage.getItem('user')
    if (savedUser) return next()

    console.warn("Access denied, must login first!")
    next('/')
}

// Only Admin
function onlyAdmin (to, from, next) {
    const user = JSON.parse(localStorage.getItem('user'))
    if (user && user.role === 'ADMIN') return next()

    console.warn("Access denied, must be Admin!")
    next('/dashboard')
}

// Only Client
function onlyClient (to, from, next) {
    const user = JSON.parse(localStorage.getItem('user'))
    if (user && user.role === 'CLIENT') return next()

    console.warn("Access denied, must be Client!")
    next('/dashboard')
}

// Only TecTeam
function onlyTech(to, from, next) {
    const user = JSON.parse(localStorage.getItem('user'))
    if (user && user.role === 'TECH_TEAM') return next()

    console.warn("Access denied, must be Tech Team!")
    next('/dashboard')
}

// Admin or TecTeam
const onlyAdminTech = (to, from, next) => {
    const user = JSON.parse(localStorage.getItem('user'))
    if (user && (user.role === 'TECH_TEAM' || user.role === 'ADMIN')) return next()

    console.warn("Access denied, must be Admin or Tech Team!")
    next('/dashboard')
}

export default router