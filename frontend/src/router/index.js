import { createRouter, createWebHistory } from 'vue-router'
import store from '../store'
import Dashboard from '../components/dashboard/Dashboard.vue'
import Home from "../components/Home.vue";
import CreateProject from '../components/dashboard/CreateProject.vue'
import AnswerProject from "../components/dashboard/AnswerProject.vue";
import DashboardMain from "../components/dashboard/DashboardMain.vue";
import Profile from "../components/auth/Profile.vue";
import ActivateUsers from "../components/dashboard/ActivateUsers.vue";
import CreateUsers from "../components/auth/CreateUsers.vue";
import ManageQuestions from "../components/dashboard/ManageQuestions.vue";

const getUser = () => store.getters['auth/currentUser'];
const isAuth = () => store.getters['auth/isAuthenticated'];


function isLogged (to, from) {
    if (isAuth()) return true;

    console.warn("Acesso negado. Por favor inicie sessão primeiro.");
    return '/';
}

// Only Admin
function onlyAdmin (to, from) {
    const user = getUser();
    if (user && user.role === 'ADMIN') return true;

    return '/dashboard';
}

// Only Client
function onlyClient (to, from) {
    const user = getUser();
    if (user && user.role === 'CLIENT') return true;

    return '/dashboard';
}

// Only TecTeam
function onlyTech(to, from) {
    const user = getUser();
    if (user && user.role === 'TECH_TEAM') return true;

    return '/dashboard';
}

// Admin or TecTeam
const onlyAdminTech = (to, from) => {
    const user = getUser();
    if (user && (user.role === 'TECH_TEAM' || user.role === 'ADMIN')) return true;

    return '/dashboard';
}



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
            },
            {
                path: 'create-users',
                name: 'CreateUsers',
                component: CreateUsers,
                beforeEnter: [isLogged, onlyAdmin]
            },
            {
                path: 'manage-questions',
                name: 'manage-questions',
                component: ManageQuestions,
                beforeEnter: [isLogged, onlyAdmin]
            }
        ]
    }]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router