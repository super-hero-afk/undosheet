import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { apiFetch } from '@/utils/api'
import { useAuthStore } from '@/stores/auth'

export const CATEGORY_OPTIONS = ['全部', '工作', '生活', '学习']
const CATEGORY_TO_API = {
  工作: 'WORK',
  生活: 'LIFE',
  学习: 'STUDY'
}
const API_TO_CATEGORY = {
  WORK: '工作',
  LIFE: '生活',
  STUDY: '学习'
}

export const useTodoStore = defineStore('todos', () => {
  const todos = ref([])
  const selectedCategory = ref('全部')
  const keyword = ref('')
  const loading = ref(false)

  const filteredTodos = computed(() => {
    return todos.value.filter((todo) => {
      const byCategory =
        selectedCategory.value === '全部' || todo.category === selectedCategory.value
      const byKeyword =
        !keyword.value || todo.title.toLowerCase().includes(keyword.value.toLowerCase())
      return byCategory && byKeyword
    })
  })

  const stats = computed(() => {
    const total = todos.value.length
    const done = todos.value.filter((todo) => todo.completed).length
    return {
      total,
      done,
      pending: total - done
    }
  })

  function authHeader() {
    const auth = useAuthStore()
    if (!auth.user?.username) throw new Error('请先登录')
    return { 'X-User': auth.user.username }
  }

  function mapApiTodo(item) {
    return {
      id: String(item.id),
      title: item.title,
      category: API_TO_CATEGORY[item.category] || '工作',
      completed: item.completed,
      createdAt: item.createdAt
    }
  }

  async function fetchTodos() {
    loading.value = true
    try {
      const params = new URLSearchParams()
      if (selectedCategory.value !== '全部') {
        params.set('category', CATEGORY_TO_API[selectedCategory.value])
      }
      if (keyword.value.trim()) {
        params.set('keyword', keyword.value.trim())
      }
      const query = params.toString() ? `?${params.toString()}` : ''
      const data = await apiFetch(`/api/todos${query}`, {
        headers: authHeader()
      })
      todos.value = data.map(mapApiTodo)
    } finally {
      loading.value = false
    }
  }

  async function addTodo(title, category) {
    const normalizedTitle = title.trim()
    if (!normalizedTitle) return
    const data = await apiFetch('/api/todos', {
      method: 'POST',
      headers: authHeader(),
      body: JSON.stringify({
        title: normalizedTitle,
        category: CATEGORY_TO_API[category]
      })
    })
    todos.value.unshift(mapApiTodo(data))
  }

  async function removeTodo(id) {
    await apiFetch(`/api/todos/${id}`, {
      method: 'DELETE',
      headers: authHeader()
    })
    todos.value = todos.value.filter((todo) => todo.id !== String(id))
  }

  async function toggleTodo(id) {
    const data = await apiFetch(`/api/todos/${id}/toggle`, {
      method: 'PATCH',
      headers: authHeader()
    })
    const target = todos.value.find((todo) => todo.id === String(id))
    if (!target) return
    target.completed = data.completed
  }

  async function updateTodo(id, payload) {
    const target = todos.value.find((todo) => todo.id === String(id))
    if (!target) return
    const data = await apiFetch(`/api/todos/${id}`, {
      method: 'PUT',
      headers: authHeader(),
      body: JSON.stringify({
        title: payload.title,
        category: CATEGORY_TO_API[payload.category]
      })
    })
    target.title = data.title
    target.category = API_TO_CATEGORY[data.category]
  }

  return {
    todos,
    filteredTodos,
    selectedCategory,
    keyword,
    loading,
    stats,
    fetchTodos,
    addTodo,
    removeTodo,
    toggleTodo,
    updateTodo
  }
})
