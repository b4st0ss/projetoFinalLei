import { useToast } from "vue-toast-notification";

const toast = useToast()

function show(template){
    toast.open(
        {
            message: template,
            position: 'top',
            type: 'default',
            duration: 4000
        }
    )
}

export function successNotification(message){
    show(`
          <div class="fixed top-6 left-1/2 -translate-x-1/2 z-50 flex items-center gap-2 bg-emerald-950 border border-emerald-400 rounded-xl p-3 text-xs text-emerald-400 shadow-2xl min-w-[280px]">
            <!-- https://heroicons.com/outline -->
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5 shrink-0">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75 11.25 15 15 9.75M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Z" />
            </svg>
            
            ${message}
          </div>
    `)
}

export function errorNotification(message){
    show(`  
          <div class="fixed top-6 left-1/2 -translate-x-1/2 z-50 flex items-center gap-2 bg-red-950 border border-red-400 rounded-xl p-3 text-xs text-red-400 shadow-2xl min-w-[280px]">
            <!-- https://heroicons.com/outline -->
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-5 h-5 shrink-0">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126ZM12 15.75h.007v.008H12v-.008Z" />
            </svg>
            
            ${message}
          </div>
    `)
}