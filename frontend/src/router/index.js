import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../components/dashboard/Dashboard.vue'
import Home from "../components/Home.vue";
import CreateProject from '../components/dashboard/CreateProject.vue'
import AnswerProject from "../components/dashboard/AnswerProject.vue";
import DashboardMain from "../components/dashboard/DashboardMain.vue";
import Profile from "../components/auth/Profile.vue";
import ActivateUsers from "../components/dashboard/ActivateUsers.vue";

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
        redirect: '/dashboard/dashboard-main',
        children: [
            {
                path: 'dashboard-main',
                name: 'DashboardMain',
                component: DashboardMain,
                props: true // to pass role and name
            },
            {
                path: 'create-project',
                name: 'CreateProject',
                component: CreateProject,
                beforeEnter: [isLogged, onlyAdmin]
            },
            {
                path: 'answer-project/:projectId',
                name: 'AnswerProject',
                component: AnswerProject,
                beforeEnter: [isLogged],
                props: true // to pass role, name and project id
            },
            {
                path: 'profile',
                name: 'Profile',
                component: Profile,
            },
            {
                path: 'activate-users',
                name: 'ActivateUsers',
                component: ActivateUsers,
                beforeEnter: [isLogged, onlyAdmin]
            }
        ]
    }]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// to check if user is logged
function isLogged (to, from) {
    const savedUser = localStorage.getItem('user')
    if (savedUser) return true

    console.warn("Access denied, must login first!")
    return '/'
}

// Only Admin
function onlyAdmin (to, from) {
    const user = JSON.parse(localStorage.getItem('user'))
    if (user && user.role === 'ADMIN') return true

    return '/dashboard'
}

// Only Client
function onlyClient (to, from) {
    const user = JSON.parse(localStorage.getItem('user'))
    if (user && user.role === 'CLIENT') return true

    return '/dashboard'
}

// Only TecTeam
function onlyTech(to, from) {
    const user = JSON.parse(localStorage.getItem('user'))
    if (user && user.role === 'TECH_TEAM') return true

    return '/dashboard'
}

// Admin or TecTeam
const onlyAdminTech = (to, from) => {
    const user = JSON.parse(localStorage.getItem('user'))
    if (user && (user.role === 'TECH_TEAM' || user.role === 'ADMIN')) return true

    return '/dashboard'
}

export default router