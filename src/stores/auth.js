import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { apiFetch } from '@/utils/api'

const AUTH_KEY = 'todo-plus-auth-user'

function readLocalUser() {
  const raw = localStorage.getItem(AUTH_KEY)
  return raw ? JSON.parse(raw) : null
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref(readLocalUser())
  const isLoggedIn = computed(() => !!user.value)

  async function login(username, password) {
    if (!username || !password) {
      throw new Error('用户名和密码不能为空')
    }
    const data = await apiFetch('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({
        username,
        password
      })
    })
    const nextUser = data.user
    user.value = nextUser
    localStorage.setItem(AUTH_KEY, JSON.stringify(nextUser))
  }

  async function logout() {
    try {
      await apiFetch('/api/auth/logout', { method: 'POST' })
    } catch {
      // Ignore API errors and still clear local auth state.
    }
    user.value = null
    localStorage.removeItem(AUTH_KEY)
  }

  return {
    user,
    isLoggedIn,
    login,
    logout
  }
})
